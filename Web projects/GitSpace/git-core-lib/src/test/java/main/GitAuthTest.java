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
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GitAuthTest {

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

    @Test
    void testLoginSuccess() throws Exception {
        String jsonResponse = "{\"token\":\"test-jwt-token\",\"username\":\"gituser\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        String input = "gituser\npassword123\n";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));

        Main.main(new String[]{"login"});

        String output = outContent.toString();
        assertTrue(output.contains("SUCCESS: Logged in as gituser"));

        Main.main(new String[]{"whoami"});
        assertTrue(outContent.toString().contains("Logged in as: gituser"));
    }

    @Test
    void testLoginFailure() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"Bad credentials\"}"));

        String input = "wronguser\nwrongpass\n";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));

        Main.main(new String[]{"login"});

        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Login Failed"));

        outContent.reset();
        Main.main(new String[]{"whoami"});
        assertTrue(outContent.toString().contains("Not logged in"));
    }

    @Test
    void testLogout() throws Exception {
        testLoginSuccess();
        outContent.reset();

        Main.main(new String[]{"logout"});
        assertTrue(outContent.toString().contains("Credentials removed"));

        outContent.reset();
        Main.main(new String[]{"whoami"});
        assertTrue(outContent.toString().contains("Not logged in"));
    }
}