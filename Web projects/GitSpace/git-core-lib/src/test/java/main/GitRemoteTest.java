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

class GitRemoteTest {

    private MockWebServer mockWebServer;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private String originalUserHome;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8080);

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        originalUserHome = System.getProperty("user.home");
        System.setProperty("user.home", tempDir.toAbsolutePath().toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setProperty("user.home", originalUserHome);
    }

    private void performLogin() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"token\":\"valid-token\",\"username\":\"gituser\"}"));
        System.setIn(new java.io.ByteArrayInputStream("gituser\npass\n".getBytes()));
        Main.main(new String[]{"login"});
        outContent.reset();
        // Consume request
        mockWebServer.takeRequest();
    }

    @Test
    void testPushAuthorized(@TempDir Path tempDir) throws Exception {
        performLogin();

        String repoPath = tempDir.resolve("my-repo").toString();
        Main.main(new String[]{"init", repoPath});
        Files.writeString(Path.of(repoPath).resolve("file.txt"), "content");
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "file.txt"});
        Main.main(new String[]{"-C", repoPath, "write-tree"});
        String output = outContent.toString().trim();
        String tree = output.substring(output.lastIndexOf('\n') + 1);
        Main.main(new String[]{"-C", repoPath, "commit-tree", tree, "-m", "init"});

        mockWebServer.enqueue(new MockResponse()
                .setBody("001f# service=git-receive-pack\n0000")
                .addHeader("Content-Type", "application/x-git-upload-pack-advertisement"));
        mockWebServer.enqueue(new MockResponse()
                .setBody("000eunpack ok\n0000")
                .addHeader("Content-Type", "application/x-git-receive-pack-result"));

        String remoteUrl = mockWebServer.url("/gituser/repo.git").toString();
        outContent.reset();

        Main.main(new String[]{"-C", repoPath, "push", remoteUrl, "master"});

        RecordedRequest discovery = mockWebServer.takeRequest();
        assertEquals("Bearer valid-token", discovery.getHeader("Authorization"));

        RecordedRequest push = mockWebServer.takeRequest();
        assertEquals("Bearer valid-token", push.getHeader("Authorization"));
        assertTrue(outContent.toString().contains("Push completed"));
    }

    @Test
    void testPushUnauthorized(@TempDir Path tempDir) throws Exception {
        // No login performed
        String repoPath = tempDir.resolve("my-repo").toString();
        Main.main(new String[]{"init", repoPath});

        String remoteUrl = mockWebServer.url("/gituser/repo.git").toString();
        Main.main(new String[]{"-C", repoPath, "push", remoteUrl, "master"});

        assertTrue(errContent.toString().contains("You must be logged in to push"));
    }

    @Test
    void testPushForbiddenToUninvitedRepo(@TempDir Path tempDir) throws Exception {
        performLogin();

        String repoPath = tempDir.resolve("my-repo").toString();
        Main.main(new String[]{"init", repoPath});
        Files.writeString(Path.of(repoPath).resolve("f.txt"), "v1");
        Main.main(new String[]{"-C", repoPath, "hash-object", "-w", "f.txt"});
        Main.main(new String[]{"-C", repoPath, "write-tree"});

        mockWebServer.enqueue(new MockResponse().setResponseCode(403).setBody("Forbidden"));

        String remoteUrl = mockWebServer.url("/admin/secret-repo.git").toString();
        outContent.reset();
        errContent.reset();

        Main.main(new String[]{"-C", repoPath, "push", remoteUrl, "master"});

        assertTrue(errContent.toString().contains("Fatal Error"));
    }

    @Test
    void testClonePrivateRepoSuccess(@TempDir Path tempDir) throws Exception {
        performLogin();

        mockWebServer.enqueue(new MockResponse()
                .setBody("001e# service=git-upload-pack\n00000015HEAD\0symref=refs/heads/master\n0000")
                .addHeader("Content-Type", "application/x-git-upload-pack-advertisement"));

        // Sending simplified empty pack response for test
        mockWebServer.enqueue(new MockResponse()
                .setBody("PACK...")
                .setResponseCode(200));

        String remoteUrl = mockWebServer.url("/private/repo.git").toString();
        String targetDir = "cloned-repo";

        try {
            Main.main(new String[]{"-C", tempDir.toString(), "clone", remoteUrl, targetDir});
        } catch (Exception e) {
            // Expected to fail on pack parsing since we sent dummy data,
            // but we check if request had auth
        }

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("Bearer valid-token", request.getHeader("Authorization"));
    }
}