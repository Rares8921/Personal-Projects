package main.protocol;

import main.domain.Reference;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class to read Git Protocol "Packet Lines" from any InputStream.
 */
public class PacketLineReader {

    private static final int HASH_LENGTH = 40;
    private final InputStream in;

    public PacketLineReader(InputStream in) {
        this.in = Objects.requireNonNull(in);
    }

    /**
     * Reads the advertised references (initial handshake) from the stream.
     * Parses lines like: "<hash> <refname>\0capabilities"
     */
    public List<Reference> readAdvertisedRefs() throws IOException {
        List<Reference> refs = new ArrayList<>();

        while (true) {
            PacketLine line = readLine();
            if (line == null) break; // EOF
            if (line instanceof PacketLine.Flush) {
                if(refs.isEmpty()) {
                    continue; // separator
                } else {
                    break; // End of refs list
                }
            }

            if (line instanceof PacketLine.Data data) {
                if (data.isComment()) continue;

                String content = new String(data.content(), StandardCharsets.UTF_8).trim();

                // Parse: "hash refname"
                int spaceIndex = content.indexOf(' ');
                if (spaceIndex == -1) continue;

                String hash = content.substring(0, spaceIndex);
                String rest = content.substring(spaceIndex + 1);

                // Strip capabilities (separated by null byte)
                int nullIndex = rest.indexOf('\0');
                String name = (nullIndex != -1) ? rest.substring(0, nullIndex) : rest;

                if (hash.length() == HASH_LENGTH) {
                    refs.add(new Reference(name, hash));
                }
            }
        }
        return refs;
    }

    /**
     * Reads a single PacketLine (Data or Flush).
     */
    public PacketLine readLine() throws IOException {
        byte[] lenBytes = new byte[4];
        int read = in.readNBytes(lenBytes, 0, 4);

        if (read < 4) return null; // Clean EOF

        String lenStr = new String(lenBytes, StandardCharsets.US_ASCII);
        int len;
        try {
            len = Integer.parseInt(lenStr, 16);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid packet length: " + lenStr);
        }

        if (len == 0) return PacketLine.flush();

        int payloadLen = len - 4;
        if (payloadLen == 0) return PacketLine.data(new byte[0]);

        byte[] payload = in.readNBytes(payloadLen);
        if (payload.length != payloadLen) {
            throw new IOException("Unexpected EOF reading packet payload");
        }

        return PacketLine.data(payload);
    }

    /**
     * Consumes negotiation packets (ACK/NAK) until ready for Packfile.
     */
    public void consumeNegotiation() throws IOException {
        while (true) {
            PacketLine line = readLine();
            if (line == null) throw new IOException("Unexpected EOF during negotiation");

            if (line instanceof PacketLine.Data data) {
                String s = new String(data.content(), StandardCharsets.US_ASCII);
                if (s.startsWith("NAK") || (s.startsWith("ACK") && s.contains("ready"))) {
                    return;
                }
                if (s.startsWith("ERR")) {
                    throw new IOException("Remote Error: " + s);
                }
            }
        }
    }
}