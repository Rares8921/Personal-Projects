package main;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GitIntegrationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws IOException {
        System.setOut(new PrintStream(outContent));
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    public void tearDown() throws IOException {
        System.setOut(originalOut);
        mockWebServer.shutdown();
    }

    private String getAndClearOutput() {
        String output = outContent.toString().trim();
        outContent.reset();
        return output;
    }

    @Test
    void testFullLocalWorkflow(@TempDir Path tempDir) throws Exception {
        String repoPath = tempDir.toAbsolutePath().toString();

        Main.main(new String[]{"init", repoPath});
        assertTrue(Files.exists(tempDir.resolve(".git")), "Repo not initialized");
        getAndClearOutput();

        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "Hello world!");

        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "test.txt"});
        String blobHash = getAndClearOutput();
        assertTrue(blobHash.matches("[a-f0-9]{40}"), "Invalid Blob Hash");

        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String treeHash = getAndClearOutput();
        assertTrue(treeHash.matches("[a-f0-9]{40}"), "Invalid Tree Hash");

        Main.main(new String[]{"-C", repoPath, "commit-tree", treeHash, "-m", "FirstCommit"});
        String commitHash = getAndClearOutput();
        assertTrue(commitHash.matches("[a-f0-9]{40}"), "Invalid Commit Hash");

        Main.main(new String[]{"-C", repoPath, "cat-file", "-p", commitHash});
        String commitContent = getAndClearOutput();

        assertTrue(commitContent.contains("tree " + treeHash), "Commit content missing tree hash");
        assertTrue(commitContent.contains("author User"), "Commit content missing author");
        assertTrue(commitContent.contains("FirstCommit"), "Commit content missing message");
    }

    @Test
    void testClone(@TempDir Path tempDir) throws Exception {
        Path remoteRepoDir = tempDir.resolve("remote-repo");
        Main.main(new String[]{"init", remoteRepoDir.toString()});
        getAndClearOutput();

        Files.writeString(remoteRepoDir.resolve("README.md"), "Hello Remote World");
        Main.main(new String[]{"-C", remoteRepoDir.toString(), "hash-object", "-w", "README.md"});
        getAndClearOutput();
        Main.main(new String[]{"-C", remoteRepoDir.toString(), "write-tree"});
        String treeHash = getAndClearOutput();
        Main.main(new String[]{"-C", remoteRepoDir.toString(), "commit-tree", treeHash, "-m", "InitialRemote"});
        String commitHash = getAndClearOutput(); // This commit is now HEAD of master

        Git remoteGit = Git.open(remoteRepoDir);
        main.pack.PackGenerator generator = new main.pack.PackGenerator(remoteGit);
        byte[] packData = generator.createPack(commitHash);

        // Construct info/refs response
        String refLine1 = commitHash + " HEAD\0symref=refs/heads/master\n";
        String refLine2 = commitHash + " refs/heads/master\n";

        StringBuilder infoRefs = new StringBuilder();
        infoRefs.append("001e# service=git-upload-pack\n");
        infoRefs.append("0000");
        infoRefs.append(packetLine(refLine1));
        infoRefs.append(packetLine(refLine2));
        infoRefs.append("0000");

        mockWebServer.enqueue(new MockResponse()
                .setBody(infoRefs.toString())
                .addHeader("Content-Type", "application/x-git-upload-pack-advertisement"));


        okio.Buffer responseBody = new okio.Buffer();
        responseBody.writeUtf8("0008NAK\n");
        responseBody.write(packData);

        mockWebServer.enqueue(new MockResponse()
                .setBody(responseBody)
                .addHeader("Content-Type", "application/x-git-upload-pack-result"));

        String cloneUrl = mockWebServer.url("/repo.git").toString();
        String targetDir = "cloned-world";

        Main.main(new String[]{"-C", tempDir.toString(), "clone", cloneUrl, targetDir});

        Path clonedRepo = tempDir.resolve(targetDir);
        assertTrue(Files.exists(clonedRepo.resolve(".git")), ".git folder missing after clone");
        assertTrue(Files.exists(clonedRepo.resolve("README.md")), "README missing - checkout failed");

        String content = Files.readString(clonedRepo.resolve("README.md"));
        assertTrue(content.equals("Hello Remote World"), "Content mismatch");
    }

    private String packetLine(String content) {
        int len = content.length() + 4;
        return String.format("%04x%s", len, content);
    }
}