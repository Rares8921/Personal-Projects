package main.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Represents a single line in the Git Packet-Line protocol.
 * This is the core data format for git's protocol over HTTP/SSH.
 */
public sealed interface PacketLine permits PacketLine.Data, PacketLine.Flush {

    /**
     * Writes the packet (including its length prefix or flush marker)
     * to the given output stream.
     *
     * @param out The stream to write to. Cannot be null.
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Represents a packet line that contains data.
     * The Git protocol prefixes this with a 4-byte hex length.
     * Note: The length includes the 4 bytes of the prefix itself.
     * Example: "hello\n" (6 bytes) -> length is 10 (0x000a) -> Prefix "000a".
     */
    record Data(byte[] content) implements PacketLine {

        // Pre-calculated hex formatter for performance
        private static final HexFormat HEX = HexFormat.of();

        public Data {
            Objects.requireNonNull(content, "Packet content cannot be null");
            // Git protocol limit: max packet size is 65520 bytes
            if (content.length > 65516) {
                throw new IllegalArgumentException("Packet content too large: " + content.length);
            }
        }

        @Override
        public void serialize(OutputStream out) throws IOException {
            Objects.requireNonNull(out);

            // content + 4 header bytes
            int totalLen = content.length + 4;

            String hexLen = HEX.toHexDigits(totalLen, 4);

            out.write(hexLen.getBytes(StandardCharsets.US_ASCII));
            out.write(content);
        }

        /**
         * Checks if this data packet is a protocol comment (starts with '#').
         */
        public boolean isComment() {
            return content.length > 0 && content[0] == '#';
        }
    }

    /**
     * Represents a "flush" packet ("0000").
     * This is used by Git to signal the end of a request.
     */
    enum Flush implements PacketLine {
        INSTANCE;

        private static final byte[] ZERO_BYTES = "0000".getBytes(StandardCharsets.US_ASCII);

        @Override
        public void serialize(OutputStream out) throws IOException {
            Objects.requireNonNull(out);
            out.write(ZERO_BYTES);
        }
    }

    static Data data(byte[] content) {
        return new Data(content);
    }

    /**
     * Automatically appends a newline if strictly required by specific protocol phases,
     * but raw string wrapping is safer generally.
     */
    static Data data(String content) {
        Objects.requireNonNull(content);
        return new Data(content.getBytes(StandardCharsets.UTF_8));
    }

    static Flush flush() {
        return Flush.INSTANCE;
    }
}