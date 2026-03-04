package main.pack;

import main.Git;
import main.domain.Commit;
import main.domain.GitObject;
import main.domain.ObjectType;
import main.domain.Tree;
import main.domain.tree.TreeEntry;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;

/**
 * Generates a Git Packfile containing a set of objects.
 * Used for 'git push' to transfer data to the remote.
 */
public class PackGenerator {

    private final Git git;
    private final Set<String> visited = new HashSet<>();
    private final ByteArrayOutputStream packContent = new ByteArrayOutputStream();
    private int objectCount = 0;

    public PackGenerator(Git git) {
        this.git = git;
    }

    public byte[] createPack(String headHash) throws IOException, NoSuchAlgorithmException {
        collectObject(headHash);

        ByteArrayOutputStream finalOutput = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(finalOutput);

        // "PACK" + Version(4 bytes) + ObjectCount(4 bytes)
        dos.write("PACK".getBytes(StandardCharsets.US_ASCII));
        dos.writeInt(2); // Version 2
        dos.writeInt(objectCount);

        dos.write(packContent.toByteArray());
        dos.flush();

        byte[] packData = finalOutput.toByteArray();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] checksum = md.digest(packData);

        finalOutput.write(checksum);

        return finalOutput.toByteArray();
    }

    private void collectObject(String hash) throws IOException {
        if (visited.contains(hash)) {
            return;
        }
        visited.add(hash);

        try {
            Commit commit = git.readCommit(hash);
            writeObjectToPack(ObjectType.COMMIT, commit);
            collectObject(commit.treeHash());
            for (String parent : commit.parents()) {
                collectObject(parent);
            }
            return;
        } catch (Exception ignored) {}

        try {
            Tree tree = git.readTree(hash);
            writeObjectToPack(ObjectType.TREE, tree);
            for (TreeEntry entry : tree.entries()) {
                collectObject(entry.hash());
            }
            return;
        } catch (Exception ignored) {}

        try {
            main.domain.Blob blob = git.readBlob(hash);
            writeObjectToPack(ObjectType.BLOB, blob);
        } catch (Exception e) {
            throw new IOException("Object not found or unknown type: " + hash);
        }
    }

    private void writeObjectToPack(ObjectType type, GitObject obj) throws IOException {
        byte[] content = type.serializeContent(obj);

        // Format: [1 bit MSB][3 bits Type][4 bits Size] ...
        int typeCode = getTypeCode(type);
        int size = content.length;

        int firstByte = (typeCode << 4) | (size & 0x0F);
        size >>>= 4;

        if (size > 0) firstByte |= 0x80;
        packContent.write(firstByte);

        while (size > 0) {
            int nextByte = size & 0x7F;
            size >>>= 7;
            if (size > 0) nextByte |= 0x80;
            packContent.write(nextByte);
        }

        DeflaterOutputStream deflater = new DeflaterOutputStream(packContent);
        deflater.write(content);
        deflater.finish();

        objectCount++;
    }

    private int getTypeCode(ObjectType type) {
        if (type == ObjectType.COMMIT) return 1;
        if (type == ObjectType.TREE) return 2;
        if (type == ObjectType.BLOB) return 3;
        return 0;
    }
}