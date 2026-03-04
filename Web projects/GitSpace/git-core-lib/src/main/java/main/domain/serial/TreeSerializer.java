package main.domain.serial;

import main.domain.Tree;
import main.domain.tree.TreeEntry;
import main.domain.tree.TreeEntryMode;
import main.domain.tree.TreeEntryModeType;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/**
 * Handles serialization and deserialization for a Git Tree object.
 * Format: {@code <mode> <space> <filename> <null_byte> <20_byte_binary_hash>} repeated.
 */
public class TreeSerializer implements ObjectContentSerializer<Tree> {

    private static final byte SPACE = ' ';
    private static final byte NULL_BYTE = 0;
    private static final int HASH_LENGTH = 20;
    private static final HexFormat HEX = HexFormat.of();

    @Override
    public void serialize(Tree tree, DataOutputStream out) throws IOException {
        Objects.requireNonNull(tree, "Tree cannot be null");
        Objects.requireNonNull(out, "OutputStream cannot be null");

        for (TreeEntry entry : tree.entries()) {
            serializeEntry(entry, out);
        }
    }

    public static void serializeEntry(TreeEntry entry, DataOutputStream out) throws IOException {
        out.write(entry.mode().format().getBytes(StandardCharsets.UTF_8));
        out.write(SPACE);

        out.write(entry.name().getBytes(StandardCharsets.UTF_8));
        out.write(NULL_BYTE);

        byte[] hashBytes = HEX.parseHex(entry.hash());
        out.write(hashBytes);
    }

    @Override
    public Tree deserialize(DataInputStream in) throws IOException {
        Objects.requireNonNull(in, "InputStream cannot be null");

        List<TreeEntry> entries = new ArrayList<>();
        TreeEntry entry;

        while ((entry = deserializeEntry(in)) != null) {
            entries.add(entry);
        }

        return new Tree(entries);
    }

    public static TreeEntry deserializeEntry(DataInputStream in) throws IOException {
        String modeStr = readStringUntil(in, SPACE);
        if (modeStr == null) {
            return null;
        }
        TreeEntryMode mode = deserializeEntryMode(modeStr);

        String name = readStringUntil(in, NULL_BYTE);
        if (name == null) {
            throw new EOFException("Unexpected EOF while reading tree entry name");
        }

        byte[] hashBytes = new byte[HASH_LENGTH];

        int bytesRead = in.readNBytes(hashBytes, 0, HASH_LENGTH);
        if (bytesRead != HASH_LENGTH) {
            throw new EOFException("Unexpected EOF while reading tree entry hash");
        }

        String hash = HEX.formatHex(hashBytes);

        return new TreeEntry(mode, name, hash);
    }

    public static TreeEntryMode deserializeEntryMode(String string) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException("Mode string cannot be empty");
        }

        int value = Integer.parseInt(string, 8);

        TreeEntryModeType type = TreeEntryModeType.match(value);
        int permission = value & 01777; // 01777 is octal mask for 9 bits

        return new TreeEntryMode(type, permission);
    }

    /**
     * Reads bytes from the stream until the specific delimiter is found.
     * Returns null ONLY if EOF is reached before reading any data.
     */
    private static String readStringUntil(DataInputStream in, byte delimiter) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        boolean firstRead = true;

        while ((b = in.read()) != -1) {
            if (b == delimiter) {
                return buffer.toString(StandardCharsets.UTF_8);
            }
            buffer.write(b);
            firstRead = false;
        }

        // end of tree
        if (firstRead) {
            return null;
        }

        throw new EOFException("Unexpected EOF while reading string content");
    }
}