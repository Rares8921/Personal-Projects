package main.backend;

import main.Main;
import main.CredentialsManager;
import main.backend.dto.repo.CreateRepoRequest;
import main.backend.dto.repo.GitCommitDto;
import main.backend.dto.repo.GitTreeEntryDto;
import main.backend.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GitDataReadIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;

    private Path clientWorkDir;
    private final String TEST_USER = "git_reader";
    private final String TEST_REPO = "read-test-repo";
    private final String TEST_PASS = "pass123";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setup() throws Exception {
        User user = new User();
        user.setUsername(TEST_USER);
        user.setEmail("reader@test.com");
        user.setPasswordHash(passwordEncoder.encode(TEST_PASS));
        userRepository.save(user);

        CreateRepoRequest req = new CreateRepoRequest(TEST_REPO, "Data Read Repo", false, "None");
        repoService.createRepo(TEST_USER, req);

        clientWorkDir = Files.createTempDirectory("git_client_data_");

        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(TEST_USER, TEST_PASS, new ArrayList<>()), null, new ArrayList<>()));
        CredentialsManager.saveCredentials(token, TEST_USER);

        runCli("init", clientWorkDir.toString());

        Files.writeString(clientWorkDir.resolve("README.md"), "# Hello Read API");
        runCli("-C", clientWorkDir.toString(), "hash-object", "-w", "README.md");
        String tree1 = runCli("-C", clientWorkDir.toString(), "write-tree").trim();
        String commit1 = runCli("-C", clientWorkDir.toString(), "commit-tree", tree1, "-m", "Initial commit").trim();
        updateHead(clientWorkDir, "master", commit1);

        Path srcDir = clientWorkDir.resolve("src").resolve("main");
        Files.createDirectories(srcDir);
        Files.writeString(srcDir.resolve("Main.java"), "public class Main {}");
        runCli("-C", clientWorkDir.toString(), "hash-object", "-w", "src/main/Main.java");
        String tree2 = runCli("-C", clientWorkDir.toString(), "write-tree").trim();
        String commit2 = runCli("-C", clientWorkDir.toString(), "commit-tree", tree2, "-p", commit1, "-m", "Add source code").trim();
        updateHead(clientWorkDir, "master", commit2);

        String remoteUrl = String.format("http://localhost:%d/git/%s/%s.git", port, TEST_USER, TEST_REPO);
        runCli("-C", clientWorkDir.toString(), "push", remoteUrl, "master");
    }

    @AfterEach
    void cleanupClient() throws IOException {
        CredentialsManager.logout();
        forceDelete(clientWorkDir);
    }

    @Test
    void testGetTree_Root() {
        String url = "/api/repos/" + TEST_USER + "/" + TEST_REPO + "/git/tree/master";

        ResponseEntity<List<GitTreeEntryDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                authenticatedRequest(TEST_USER, null),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<GitTreeEntryDto> entries = response.getBody();
        assertNotNull(entries);
        assertEquals(2, entries.size());

        assertTrue(entries.stream().anyMatch(e -> e.name().equals("README.md") && e.type().equals("FILE")));
        assertTrue(entries.stream().anyMatch(e -> e.name().equals("src") && e.type().equals("DIRECTORY")));
    }

    @Test
    void testGetTree_SubFolder() {
        String url = "/api/repos/" + TEST_USER + "/" + TEST_REPO + "/git/tree/master/src/main";

        ResponseEntity<List<GitTreeEntryDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                authenticatedRequest(TEST_USER, null),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<GitTreeEntryDto> entries = response.getBody();
        assertNotNull(entries);
        assertEquals(1, entries.size());
        assertEquals("Main.java", entries.getFirst().name());
    }

    @Test
    void testGetBlob_Content() {
        String url = "/api/repos/" + TEST_USER + "/" + TEST_REPO + "/git/blob/master/README.md";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                authenticatedRequest(TEST_USER, null),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("# Hello Read API", response.getBody());
    }

    @Test
    void testGetCommits_History() {
        String url = "/api/repos/" + TEST_USER + "/" + TEST_REPO + "/git/commits/master";

        ResponseEntity<List<GitCommitDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                authenticatedRequest(TEST_USER, null),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<GitCommitDto> commits = response.getBody();
        assertNotNull(commits);
        assertTrue(commits.size() >= 2);

        assertTrue(commits.stream().anyMatch(c -> c.shortMessage().equals("Initial commit")));
        assertTrue(commits.stream().anyMatch(c -> c.shortMessage().equals("Add source code")));
    }

    private String runCli(String... args) throws Exception {
        outContent.reset();
        System.setOut(new PrintStream(outContent));
        try {
            Main.main(args);
        } finally {
            System.setOut(originalOut);
        }
        return outContent.toString();
    }

    private void updateHead(Path repoDir, String branchName, String commitHash) throws IOException {
        Path branchRef = repoDir.resolve(".git/refs/heads/" + branchName);
        Files.createDirectories(branchRef.getParent());
        Files.writeString(branchRef, commitHash);

        Path headFile = repoDir.resolve(".git/HEAD");
        Files.writeString(headFile, "ref: refs/heads/" + branchName + "\n");
    }
}