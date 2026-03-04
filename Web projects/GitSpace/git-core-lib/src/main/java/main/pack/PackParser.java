package main.pack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


/**
 * Parses a Git "packfile" (a compressed file containing multiple objects).
 * This class reads a byte buffer, validates the pack header,
 * and extracts the individual objects (deltified or undeltified).
 */
public class PackParser {

    public static final int TYPE_MASK = 0b01110000;
    public static final int SIZE_4_MASK = 0b00001111;
    public static final int SIZE_7_MASK = 0b0111_1111;
    public static final int SIZE_CONTINUE_MASK = 0b1000_0000;

    private static final int HASH_LENGTH = 20;

    /**
     * The raw byte buffer containing the entire packfile.
     */
    private final ByteBuffer buffer;

    /**
     * Creates a new parser for a specific packfile buffer.
     * @param buffer The ByteBuffer containing the packfile data.
     */
    public PackParser(ByteBuffer buffer) {
        this.buffer = Objects.requireNonNull(buffer, "Buffer cannot be null");
    }

    /**
     * The main entry point. Parses the entire packfile buffer.
     * @return A List of PackObject (which can be Deltified or Undeltified).
     * @throws IOException, DataFormatException
     */
    public List<PackObject> parse() throws IOException, DataFormatException {
        parseSignature();
        parseVersion();

        final int objectCount = buffer.getInt();
        final List<PackObject> objects = new ArrayList<>(objectCount);

        for (int i = 0; i < objectCount; i++) {
            final PackObjectHeader header = parseObjectHeader();

            PackObject object = switch (header.type()) {
                case COMMIT, TREE, BLOB -> {
                    byte[] content = inflate(header.size());
                    yield PackObject.undeltified(header.type().nativeType(), content);
                }
                case REF_DELTA -> parseRefDelta(header);
                case OFS_DELTA -> throw new UnsupportedOperationException("OFS_DELTA is not yet supported in this implementation.");
                case TAG -> throw new UnsupportedOperationException("TAG objects in packfiles are not yet supported.");
                default -> throw new IllegalStateException("Unexpected pack object type: " + header.type());
            };

            objects.add(object);
        }

        return objects;
    }

    private PackObject parseRefDelta(PackObjectHeader header) throws DataFormatException {
        byte[] hashBytes = new byte[HASH_LENGTH];
        buffer.get(hashBytes);
        String baseHash = HexFormat.of().formatHex(hashBytes);

        byte[] deltaContent = inflate(header.size());
        ByteBuffer deltaBuffer = ByteBuffer.wrap(deltaContent);

        long baseObjectSize = parseVariableLengthIntegerLittleEndian(deltaBuffer); // Unused but must be consumed
        int newObjectSize = parseVariableLengthIntegerLittleEndian(deltaBuffer);

        List<DeltaInstruction> instructions = parseDeltaInstructions(deltaBuffer);

        return PackObject.deltified(baseHash, newObjectSize, instructions);
    }

    /**
     * Reads and validates the 4-byte "PACK" signature at the start.
     */
    public void parseSignature() {
        byte[] sigBytes = new byte[4];
        buffer.get(sigBytes);
        String signature = new String(sigBytes, StandardCharsets.US_ASCII);

        if (!"PACK".equals(signature)) {
            throw new IllegalStateException("Invalid packfile signature: " + signature);
        }
    }

    /**
     * Reads and validates the packfile version (must be 2).
     */
    public void parseVersion() {
        int version = buffer.getInt();
        if (version != 2) {
            throw new IllegalStateException("Unsupported packfile version: " + version + ". Only version 2 is supported.");
        }
    }

    /**
     * Parses the variable-length header for a single object.
     * This header contains the object type and its *uncompressed* size.
     */
    public PackObjectHeader parseObjectHeader() {
        int b = Byte.toUnsignedInt(buffer.get());

        int typeCode = (b & TYPE_MASK) >> 4;
        long size = (b & SIZE_4_MASK);
        int shift = 4;

        while ((b & SIZE_CONTINUE_MASK) != 0) {
            b = Byte.toUnsignedInt(buffer.get());
            size |= ((long) (b & SIZE_7_MASK)) << shift;
            shift += 7;
        }

        return new PackObjectHeader(PackObjectType.valueOf(typeCode), (int) size);
    }

    /**
     * Reads the compressed (zlib) data for an object from the buffer
     * and inflates it to its full size.
     * @param expectedOutputSize The expected *uncompressed* size (from the header).
     * @return The raw, uncompressed byte[] data for the object.
     */
    public byte[] inflate(int expectedOutputSize) throws DataFormatException {
        Inflater inflater = new Inflater();

        if (buffer.hasArray()) {
            inflater.setInput(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
        } else {
            byte[] temp = new byte[buffer.remaining()];
            buffer.get(temp);
            // Reset position because we just want to peek, we will advance properly later
            buffer.position(buffer.position() - temp.length);
            inflater.setInput(temp);
        }

        byte[] output = new byte[expectedOutputSize];
        int bytesInflated = inflater.inflate(output);

        if (bytesInflated != expectedOutputSize) {
            throw new DataFormatException(
                    "Inflated size mismatch. Expected: " + expectedOutputSize + " bytes, but got: " + bytesInflated + " bytes."
            );
        }

        if (!inflater.finished()) {
            throw new DataFormatException("Inflater did not finish processing the compressed stream.");
        }

        int bytesConsumed = (int) inflater.getBytesRead();
        buffer.position(buffer.position() + bytesConsumed);

        inflater.end();
        return output;
    }

    /**
     * Parses the instruction set for a deltified object (OFS_DELTA or REF_DELTA).
     * @param buffer A buffer containing only the delta instructions.
     * @return A List of DeltaInstruction (Copy or Insert).
     */
    public List<DeltaInstruction> parseDeltaInstructions(ByteBuffer buffer) {
        List<DeltaInstruction> instructions = new ArrayList<>();

        while (buffer.hasRemaining()) {
            int command = Byte.toUnsignedInt(buffer.get());

            // MSB = 1 -> COPY command
            // MSB = 0 -> INSERT command
            if ((command & 0b1000_0000) != 0) {
                // COPY Instruction
                // Bits 0-3: offset bytes presence
                // Bits 4-6: size bytes presence

                int offset = parseVariableLengthInteger(buffer,
                        (command & 0x01) != 0, // byte 1
                        (command & 0x02) != 0, // byte 2
                        (command & 0x04) != 0, // byte 3
                        (command & 0x08) != 0  // byte 4
                );

                int size = parseVariableLengthInteger(buffer,
                        (command & 0x10) != 0, // byte 1
                        (command & 0x20) != 0, // byte 2
                        (command & 0x40) != 0  // byte 3
                );

                // spec special: If size is 0, it means 0x10000 (65536)
                if (size == 0) {
                    size = 0x10000;
                }

                instructions.add(DeltaInstruction.copy(offset, size));

            } else {
                // INSERT Instruction
                // The remaining 7 bits are the size of data to insert
                int size = command & 0b0111_1111;

                if (size == 0) {
                    throw new IllegalStateException("Reserved instruction code 0 used in delta");
                }

                byte[] data = new byte[size];
                buffer.get(data);
                instructions.add(DeltaInstruction.insert(data));
            }
        }

        return instructions;
    }

    /**
     * A static helper to parse a variable-length integer used in delta instructions.
     * @param buffer The buffer to read from.
     * @param enabledStates A dynamic list of booleans indicating which bytes to read.
     * @return The parsed integer.
     */
    public static int parseVariableLengthInteger(ByteBuffer buffer, boolean... enabledStates) {
        int value = 0;
        int shift = 0;

        for (boolean isBytePresent : enabledStates) {
            if (isBytePresent) {
                int read = Byte.toUnsignedInt(buffer.get());
                value |= (read << shift);
            }
            shift += 8;
        }

        return value;
    }

    /**
     * A static helper to parse the standard Git variable-length integer (LEB128-like).
     * Used for object sizes and delta offsets.
     * @param buffer The buffer to read from.
     * @return The parsed integer.
     */
    public static int parseVariableLengthIntegerLittleEndian(ByteBuffer buffer) {
        int value = 0;
        int shift = 0;

        while(true) {
            int b = Byte.toUnsignedInt(buffer.get());
            value |= (b & SIZE_7_MASK) << shift;

            if((b & SIZE_CONTINUE_MASK) == 0) {
                break;
            }
            shift += 7;
        }

        return value;
    }

}