package main.backend;

import main.Main;
import main.CredentialsManager;
import main.backend.dto.pr.PullRequestDto;
import main.backend.dto.pr.PullRequestFileDto;
import main.backend.dto.repo.CreateRepoRequest;
import main.backend.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PullRequestIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;

    private Path clientWorkDir;
    private final String USER = "pr_master";
    private final String REPO = "pr-repo";
    private final String PASS = "pass123";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setup() throws Exception {
        User user = new User();
        user.setUsername(USER);
        user.setEmail("pr@test.com");
        user.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(user);

        repoService.createRepo(USER, new CreateRepoRequest(REPO, "PR Test Repo", false, "None"));

        clientWorkDir = Files.createTempDirectory("git_pr_client_");

        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(USER, PASS, new ArrayList<>()), null, new ArrayList<>()));

        CredentialsManager.saveCredentials(token, USER);


        // git init
        runCli("init", clientWorkDir.toString());

        // Create file
        Files.writeString(clientWorkDir.resolve("Main.java"), "public class Main {}");

        runCli("-C", clientWorkDir.toString(), "hash-object", "-w", "Main.java");

        String treeHash = runCli("-C", clientWorkDir.toString(), "write-tree").trim();

        String commitHash = runCli("-C", clientWorkDir.toString(), "commit-tree", treeHash, "-m", "Initial_commit").trim();

        updateHead(clientWorkDir, "master", commitHash);

        String remoteUrl = String.format("http://localhost:%d/git/%s/%s.git", port, USER, REPO);
        runCli("-C", clientWorkDir.toString(), "push", remoteUrl, "master");
    }

    @AfterEach
    void cleanupClient() {
        forceDelete(clientWorkDir);
        CredentialsManager.logout();
    }

    @Test
    void testPullRequestFlow() throws Exception {
        String prApiUrl = "/api/repos/" + USER + "/" + REPO + "/pulls";
        String remoteUrl = String.format("http://localhost:%d/git/%s/%s.git", port, USER, REPO);

        runCli("-C", clientWorkDir.toString(), "branch", "feature-login");
        runCli("-C", clientWorkDir.toString(), "checkout", "feature-login");

        Files.writeString(clientWorkDir.resolve("Login.java"), "class Login {}");

        runCli("-C", clientWorkDir.toString(), "hash-object", "-w", "Login.java");
        String treeHash = runCli("-C", clientWorkDir.toString(), "write-tree").trim();

        String parentHash = getHeadHash(clientWorkDir);
        String commitHash = runCli("-C", clientWorkDir.toString(), "commit-tree", treeHash, "-p", parentHash, "-m", "Add_login").trim();

        updateHead(clientWorkDir, "feature-login", commitHash);

        runCli("-C", clientWorkDir.toString(), "push", remoteUrl, "feature-login");

        PullRequestDto createDto = new PullRequestDto(null, 0, "Add Login", null, "feature-login", "master", null, null, false, null);

        ResponseEntity<PullRequestDto> createResp = restTemplate.exchange(
                prApiUrl,
                HttpMethod.POST,
                authenticatedRequest(USER, createDto),
                PullRequestDto.class
        );

        assertEquals(HttpStatus.OK, createResp.getStatusCode());
        Long prNumber = (long) Objects.requireNonNull(createResp.getBody()).prNumber();
        assertEquals(1L, prNumber);

        ResponseEntity<List<PullRequestFileDto>> diffResp = restTemplate.exchange(
                prApiUrl + "/" + prNumber + "/files",
                HttpMethod.GET,
                authenticatedRequest(USER, null),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, diffResp.getStatusCode());
        List<PullRequestFileDto> files = diffResp.getBody();
        assert files != null;
        assertEquals(1, files.size());
        assertEquals("Login.java", files.getFirst().filePath());
        assertEquals("added", files.getFirst().status());

        Map<String, String> reviewBody = Map.of("state", "APPROVED");
        restTemplate.exchange(
                prApiUrl + "/" + prNumber + "/reviews",
                HttpMethod.POST,
                authenticatedRequest(USER, reviewBody),
                String.class
        );

        ResponseEntity<String> mergeResp = restTemplate.exchange(
                prApiUrl + "/" + prNumber + "/merge",
                HttpMethod.POST,
                authenticatedRequest(USER, null),
                String.class
        );
        assertEquals(HttpStatus.OK, mergeResp.getStatusCode());

        String fileUrl = "/api/repos/" + USER + "/" + REPO + "/git/blob/master/Login.java";
        ResponseEntity<String> fileContentResp = restTemplate.exchange(
                fileUrl,
                HttpMethod.GET,
                authenticatedRequest(USER, null),
                String.class
        );

        assertEquals(HttpStatus.OK, fileContentResp.getStatusCode());
        assertEquals("class Login {}", fileContentResp.getBody());
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
        // .git/refs/heads/<branchName>
        Path branchRef = repoDir.resolve(".git/refs/heads/" + branchName);
        Files.createDirectories(branchRef.getParent());
        Files.writeString(branchRef, commitHash);

        // .git/HEAD -> ref: refs/heads/<branchName>
        Path headFile = repoDir.resolve(".git/HEAD");
        Files.writeString(headFile, "ref: refs/heads/" + branchName + "\n");
    }

    private String getHeadHash(Path repoDir) throws IOException {
        Path headFile = repoDir.resolve(".git/HEAD");
        String content = Files.readString(headFile).trim();
        if (content.startsWith("ref: ")) {
            Path refPath = repoDir.resolve(".git/" + content.substring(5));
            return Files.readString(refPath).trim();
        }
        return content;
    }
}