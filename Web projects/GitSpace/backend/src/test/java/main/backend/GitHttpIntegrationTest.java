package main.backend;

import main.Main;
import main.CredentialsManager;
import main.backend.dto.repo.CreateRepoRequest;
import main.backend.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GitHttpIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort private int port;

    private Path clientWorkDir;
    private final String TEST_USER = "git_tester";
    private final String TEST_REPO = "integration-repo";
    private final String TEST_PASS = "sanderosport";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setup() throws Exception {
        User user = new User();
        user.setUsername(TEST_USER);
        user.setEmail("dacia@dev.local");
        user.setPasswordHash(passwordEncoder.encode(TEST_PASS));
        userRepository.save(user);

        CreateRepoRequest req = new CreateRepoRequest(TEST_REPO, "Test Repo", false, "None");
        repoService.createRepo(TEST_USER, req);

        clientWorkDir = Files.createTempDirectory("git_client_test_");

        String token = tokenProvider.generateToken(new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(TEST_USER, TEST_PASS, new ArrayList<>()), null, new ArrayList<>()));

        CredentialsManager.saveCredentials(token, TEST_USER);
    }

    @AfterEach
    void cleanup() throws IOException {
        CredentialsManager.logout();
        forceDelete(clientWorkDir);
    }

    @Test
    void testGitPushAndClone() throws Exception {
        System.out.println("Init local git repo...");
        runCli("init", clientWorkDir.toString());

        Files.writeString(clientWorkDir.resolve("README.md"), "Hello World!");

        commitChanges(clientWorkDir, "Initial commit");

        String remoteUrl = String.format("http://localhost:%d/git/%s/%s.git",
                port, TEST_USER, TEST_REPO);

        System.out.println("Push to: " + remoteUrl);

        runCli("-C", clientWorkDir.toString(), "push", remoteUrl, "master");

        Path cloneDir = Files.createTempDirectory("git_clone_check_");
        System.out.println("Cloning to: " + cloneDir);

        try {
            runCli("-C", cloneDir.toString(), "clone", remoteUrl, TEST_REPO);

            Path clonedFile = cloneDir.resolve(TEST_REPO).resolve("README.md");

            assertTrue(Files.exists(clonedFile), "Cloned file must exist");
            assertEquals("Hello World!", Files.readString(clonedFile));
        } finally {
            forceDelete(cloneDir);
        }
    }

    @Test
    void testGitPull() throws Exception {
        System.out.println("Init Push v1");
        Path clientA = Files.createTempDirectory("git_client_A_");
        runCli("init", clientA.toString());

        Files.writeString(clientA.resolve("shared.txt"), "Version 1");
        commitChanges(clientA, "Commit v1");

        String remoteUrl = String.format("http://localhost:%d/git/%s/%s.git",
                port, TEST_USER, TEST_REPO);

        runCli("-C", clientA.toString(), "push", remoteUrl, "master");

        System.out.println("Clone");
        Path clientB = Files.createTempDirectory("git_client_B_");
        runCli("-C", clientB.toString(), "clone", remoteUrl, "repo");

        Path repoB = clientB.resolve("repo");
        assertEquals("Version 1", Files.readString(repoB.resolve("shared.txt")));

        System.out.println("Client Push v2");
        Files.writeString(clientA.resolve("shared.txt"), "Version 2 (Updated)");
        commitChanges(clientA, "Commit v2");
        runCli("-C", clientA.toString(), "push", remoteUrl, "master");

        System.out.println("Other client PULL");

        forceDelete(clientB);
        clientB = Files.createTempDirectory("git_client_B_v2_");

        runCli("-C", clientB.toString(), "clone", remoteUrl, "repo");
        repoB = clientB.resolve("repo");

        String contentB = Files.readString(repoB.resolve("shared.txt"));
        assertEquals("Version 2 (Updated)", contentB, "Second client should ve received the changes.");

        forceDelete(clientA);
        forceDelete(clientB);
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

    private void commitChanges(Path repoDir, String message) throws Exception {
        try (var stream = Files.list(repoDir)) {
            var files = stream.filter(p -> !Files.isDirectory(p) && !p.toString().endsWith(".git")).toList();
            for (Path file : files) {
                runCli("-C", repoDir.toString(), "hash-object", "-w", file.getFileName().toString());
            }
        }

        String treeHash = runCli("-C", repoDir.toString(), "write-tree").trim();

        String parentHash = getHeadHash(repoDir);
        String commitHash;

        if (parentHash != null) {
            commitHash = runCli("-C", repoDir.toString(), "commit-tree", treeHash, "-p", parentHash, "-m", message).trim();
        } else {
            commitHash = runCli("-C", repoDir.toString(), "commit-tree", treeHash, "-m", message).trim();
        }

        updateHead(repoDir, "master", commitHash);
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
        if (!Files.exists(headFile)) return null;

        String content = Files.readString(headFile).trim();
        if (content.startsWith("ref: ")) {
            Path refPath = repoDir.resolve(".git/" + content.substring(5));
            if (Files.exists(refPath)) {
                return Files.readString(refPath).trim();
            }
            return null;
        }
        return content;
    }
}