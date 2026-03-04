package main.domain.serial;

import main.domain.AuthorSignature;
import main.domain.Commit;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles serialization and deserialization for a Git Commit object.
 * This class converts between the in-memory Commit object and
 * the plain-text format Git uses (e.g., "tree <hash>\nparent <hash>\n...").
 */
public class CommitSerializer implements ObjectContentSerializer<Commit> {

    public static final String KEY_TREE = "tree";
    public static final String KEY_PARENT = "parent";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_COMMITTER = "committer";

    private static final byte[] BYTES_TREE = KEY_TREE.getBytes(StandardCharsets.UTF_8);
    private static final byte[] BYTES_PARENT = KEY_PARENT.getBytes(StandardCharsets.UTF_8);
    private static final byte[] BYTES_AUTHOR = KEY_AUTHOR.getBytes(StandardCharsets.UTF_8);
    private static final byte[] BYTES_COMMITTER = KEY_COMMITTER.getBytes(StandardCharsets.UTF_8);
    private static final byte SPACE = ' ';
    private static final byte NEWLINE = '\n';

    /**
     * A regex pattern to parse the "author" or "committer" line
     * (e.g., "Name <email> 123456789 Z|+0000 etc.").
     */
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("^(.*) <(.*)> (\\d+) (.*)$");

    /**
     * Writes the Commit object to the stream in Git's plain-text format.
     *
     * @param commit The Commit object to serialize.
     * @param dataOutputStream The stream to write to.
     */
    @Override
    public void serialize(Commit commit, DataOutputStream dataOutputStream) throws IOException {
        Objects.requireNonNull(commit, "Commit cannot be null");
        Objects.requireNonNull(dataOutputStream, "OutputStream cannot be null");

        // 1. Write Tree
        writeHeader(dataOutputStream, BYTES_TREE, commit.treeHash());

        // 2. Write Parents (Multiple parents allowed)
        for (String parent : commit.parents()) {
            writeHeader(dataOutputStream, BYTES_PARENT, parent);
        }

        serializeAuthor(BYTES_AUTHOR, commit.author(), dataOutputStream);
        serializeAuthor(BYTES_COMMITTER, commit.committer(), dataOutputStream);

        dataOutputStream.write(NEWLINE);

        dataOutputStream.write(commit.message().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * A static helper to serialize an AuthorSignature (author or committer)
     * line to the stream.
     *
     * @param keyBytes The line prefix (e.g., "author".getBytes()).
     * @param author The AuthorSignature object to format and write.
     * @param dataOutputStream The stream to write to.
     */
    public static void serializeAuthor(byte[] keyBytes, AuthorSignature author, DataOutputStream dataOutputStream) throws IOException {
        if (author == null)
            return;
        dataOutputStream.write(keyBytes);
        dataOutputStream.write(SPACE);

        dataOutputStream.write(author.toGitFormat().getBytes(StandardCharsets.UTF_8));
        dataOutputStream.write(NEWLINE);
    }

    /**
     * Reads the plain-text commit format from the stream and
     * constructs a new Commit object.
     *
     * @param dataInputStream The stream to read from.
     * @return The newly deserialized Commit object.
     */
    @Override
    public Commit deserialize(DataInputStream dataInputStream) throws IOException {
        String treeHash = null;
        List<String> parents = new ArrayList<>();
        AuthorSignature author = null;
        AuthorSignature committer = null;

        // Read headers line by line until an empty line is found
        while (true) {
            String line = readLine(dataInputStream).trim();
            if (line.isEmpty()) {
                break; // End of headers
            }

            // Parse header line: "key value"
            int spaceIndex = line.indexOf(' ');
            if (spaceIndex == -1) {
                continue;
            }

            String key = line.substring(0, spaceIndex);
            String value = line.substring(spaceIndex + 1);

            switch (key) {
                case KEY_TREE -> treeHash = value;
                case KEY_PARENT -> parents.add(value);
                case KEY_AUTHOR -> author = parseAuthor(value);
                case KEY_COMMITTER -> committer = parseAuthor(value);

            }
        }

        // Read the rest as message
        byte[] messageBytes = dataInputStream.readAllBytes();
        String message = new String(messageBytes, StandardCharsets.UTF_8);

        if (treeHash == null) throw new IOException("Commit missing tree hash");
        if (author == null) throw new IOException("Commit missing author");
        if (committer == null) throw new IOException("Commit missing committer");

        return new Commit(treeHash, parents, author, committer, message);
    }

    /**
     * Parses a string containing author information
     * (e.g., "Name <email> 123456789 +0000")
     * into an AuthorSignature object.
     *
     * @param content The raw author string.
     * @return A new AuthorSignature object, or null if content is null.
     */
    public AuthorSignature parseAuthor(String content) {
        if (content == null) return null;

        Matcher matcher = AUTHOR_PATTERN.matcher(content);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid author format: " + content);
        }

        String name = matcher.group(1);
        String email = matcher.group(2);
        long timestamp = Long.parseLong(matcher.group(3));
        String timezoneStr = matcher.group(4).trim();

        Instant instant = Instant.ofEpochSecond(timestamp);
        ZoneId zoneId;

        try {
            if ("Z".equals(timezoneStr)) {
                zoneId = ZoneOffset.UTC;
            } else {
                if (timezoneStr.matches("^[+-]\\d{4}$")) {
                    String formattedOffset = timezoneStr.substring(0, 3) + ":" + timezoneStr.substring(3);
                    zoneId = ZoneId.of(formattedOffset);
                } else {
                    zoneId = ZoneId.of(timezoneStr);
                }
            }
        } catch (Exception e) {
            zoneId = ZoneOffset.UTC;
        }

        return new AuthorSignature(name, email, ZonedDateTime.ofInstant(instant, zoneId));
    }

    private void writeHeader(DataOutputStream out, byte[] key, String value) throws IOException {
        out.write(key);
        out.write(SPACE);
        out.write(value.getBytes(StandardCharsets.UTF_8));
        out.write(NEWLINE);
    }

    /**
     * Efficiently reads a line from the input stream (up to '\n').
     * Handles standard byte-by-byte reading to avoid buffering too much.
     */
    private String readLine(DataInputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') {
                break;
            }
            buffer.write(b);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
