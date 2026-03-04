package main;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GitPushTest {

    private MockWebServer mockWebServer;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        System.setOut(new PrintStream(outContent));
        // Simulam login-ul pentru a trece de verificarea din push
        CredentialsManager.saveCredentials("dummy_token", "test_user");
    }

    @AfterEach
    void tearDown() throws IOException {
        CredentialsManager.logout();
        mockWebServer.shutdown();
        System.setOut(originalOut);
    }

    @Test
    void testPushWorkflow(@TempDir Path tempDir) throws Exception {
        String repoPath = tempDir.toAbsolutePath().toString();
        String remoteUrl = mockWebServer.url("/repo.git").toString();

        Main.main(new String[]{"init", repoPath});
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "Content for push");

        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "file.txt"});

        outContent.reset();
        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String outputTree = outContent.toString().trim();
        String treeHash = outputTree.substring(outputTree.lastIndexOf('\n') + 1);

        outContent.reset();
        Main.main(new String[]{"-C", repoPath, "commit-tree", treeHash, "-m", "PushCommit"});
        String outputCommit = outContent.toString().trim();
        String commitHash = outputCommit.substring(outputCommit.lastIndexOf('\n') + 1);

        // Update manual HEAD/master ref deoarece commit-tree nu o face (fiind plumbing command)
        // Fara asta, 'push master' nu gaseste branch-ul local.
        Path masterRef = tempDir.resolve(".git/refs/heads/master");
        Files.createDirectories(masterRef.getParent());
        Files.writeString(masterRef, commitHash);

        // Discovery(GET /info/refs) -> server da empty
        mockWebServer.enqueue(new MockResponse()
                .setBody("001f# service=git-receive-pack\n0000")
                .addHeader("Content-Type", "application/x-git-upload-pack-advertisement"));

        // Push(POST /git-receive-pack) -> server da 200
        mockWebServer.enqueue(new MockResponse()
                .setBody("000eunpack ok\n0019ok refs/heads/master\n0000")
                .addHeader("Content-Type", "application/x-git-receive-pack-result"));

        // git push <remote_url> <branch>
        outContent.reset();
        Main.main(new String[]{"-C", repoPath, "push", remoteUrl, "master"});

        RecordedRequest discoveryRequest = mockWebServer.takeRequest();
        assertEquals("GET", discoveryRequest.getMethod());
        assertTrue(discoveryRequest.getPath().contains("info/refs"));

        RecordedRequest pushRequest = mockWebServer.takeRequest();
        assertEquals("POST", pushRequest.getMethod());
        assertEquals("application/x-git-receive-pack-request", pushRequest.getHeader("Content-Type"));

        byte[] body = pushRequest.getBody().readByteArray();

        assertTrue(body.length > 0, "Push body should not be empty");

        boolean containsPackSignature = false;
        for (int i = 0; i < body.length - 4; i++) {
            if (body[i] == 'P' && body[i+1] == 'A' && body[i+2] == 'C' && body[i+3] == 'K') {
                containsPackSignature = true;
                break;
            }
        }
        assertTrue(containsPackSignature, "Body must contain binary PACK signature");

        System.err.println("Test Push Passed: Server received valid Packfile stream.");
    }
}