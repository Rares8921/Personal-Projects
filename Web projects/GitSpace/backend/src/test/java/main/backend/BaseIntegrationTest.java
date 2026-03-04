package main.backend;

import main.backend.repository.*;
import main.backend.security.JwtTokenProvider;
import main.backend.service.RepoMetadataService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Foloseste application-test.properties
public abstract class BaseIntegrationTest {

    @Autowired protected TestRestTemplate restTemplate;
    @Autowired protected JwtTokenProvider tokenProvider;

    @Autowired protected UserRepository userRepository;
    @Autowired protected RepositoryRepository repositoryRepository;
    @Autowired protected RepositoryCollaboratorRepository collaboratorRepository;
    @Autowired protected IssueRepository issueRepository;
    @Autowired protected CommentRepository commentRepository;
    @Autowired protected ActivityRepository activityRepository;
    @Autowired protected UserFollowRepository userFollowRepository;
    @Autowired protected PullRequestRepository prRepository;
    @Autowired protected PullRequestReviewRepository reviewRepository;
    @Autowired protected SnippetRepository snippetRepository;

    @Autowired protected PasswordEncoder passwordEncoder;
    @Autowired protected RepoMetadataService repoService;

    @Value("${git.storage.path}")
    protected String serverStoragePath;

    @AfterEach
    public void globalCleanup() {
        reviewRepository.deleteAll();
        prRepository.deleteAll();
        commentRepository.deleteAll();
        issueRepository.deleteAll();
        snippetRepository.deleteAll();
        activityRepository.deleteAll();
        collaboratorRepository.deleteAll();
        repositoryRepository.deleteAll();
        userFollowRepository.deleteAll();
        userRepository.deleteAll();

        forceDelete(Path.of(serverStoragePath));
    }

    protected HttpHeaders getAuthHeaders(String username) {
        UserDetails principal = new User(username, "password", new ArrayList<>());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, new ArrayList<>());

        String token = tokenProvider.generateToken(auth);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    protected <T> HttpEntity<T> authenticatedRequest(String username, T body) {
        return new HttpEntity<>(body, getAuthHeaders(username));
    }

    protected <T> HttpEntity<T> unauthenticatedRequest() {
        return new HttpEntity<>(null, new HttpHeaders());
    }

    protected void runCommand(Path directory, String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(directory.toFile());
        pb.redirectErrorStream(true);

        Process process = pb.start();
        // Citim output-ul pentru debug in caz de eroare
        String output = new String(process.getInputStream().readAllBytes());

        boolean finished = process.waitFor(10, TimeUnit.SECONDS);
        if (!finished) {
            process.destroy();
            throw new RuntimeException("Timeout command: " + String.join(" ", command));
        }

        if (process.exitValue() != 0) {
            System.err.println("Command failed: " + String.join(" ", command));
            System.err.println("Output:\n" + output);
            throw new RuntimeException("Exit code " + process.exitValue());
        }
    }

    protected void forceDelete(Path path) {
        if (path == null || !Files.exists(path)) return;

        int maxRetries = 5;
        for (int i = 0; i < maxRetries; i++) {
            try {
                deleteRecursively(path);
                return;
            } catch (IOException e) {
                if (i == maxRetries - 1) {
                    System.err.println("Failed to delete path after retries: " + path + " " + e.getMessage());
                } else {
                    try { Thread.sleep(200); } catch (InterruptedException ignored) { }
                }
            }
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @NotNull
            @Override
            public FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                file.toFile().setWritable(true);
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @NotNull
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}