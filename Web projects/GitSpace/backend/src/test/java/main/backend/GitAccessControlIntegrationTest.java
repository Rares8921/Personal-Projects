package main.backend;

import main.backend.dto.repo.CreateRepoRequest;
import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class GitAccessControlIntegrationTest extends BaseIntegrationTest {

    private final String OWNER = "git_owner";
    private final String STRANGER = "git_stranger";
    private final String PASS = "pass";

    @BeforeEach
    void setup() {
        createUser(OWNER);
        createUser(STRANGER);

        repoService.createRepo(OWNER, new CreateRepoRequest("public-git-repo", "Public repo", true, "None"));

        repoService.createRepo(OWNER, new CreateRepoRequest("private-git-repo", "Private repo", false, "None"));
    }

    @Test
    void testGitInfoRefs_PublicRepo_UnauthenticatedAccess() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/git/" + OWNER + "/public-git-repo.git/info/refs?service=git-upload-pack",
                HttpMethod.GET,
                unauthenticatedRequest(),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(),
            "Unauthenticated users should access public repo for cloning");
    }

    @Test
    void testGitInfoRefs_PrivateRepo_UnauthenticatedAccess() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/git/" + OWNER + "/private-git-repo.git/info/refs?service=git-upload-pack",
                HttpMethod.GET,
                unauthenticatedRequest(),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
            "Unauthenticated users should NOT access private repo");
    }

    @Test
    void testGitInfoRefs_PrivateRepo_OwnerAccess() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/git/" + OWNER + "/private-git-repo.git/info/refs?service=git-upload-pack",
                HttpMethod.GET,
                authenticatedRequest(OWNER, null),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(),
            "Owner should access their private repo");
    }

    @Test
    void testGitInfoRefs_PrivateRepo_StrangerAccess() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/git/" + OWNER + "/private-git-repo.git/info/refs?service=git-upload-pack",
                HttpMethod.GET,
                authenticatedRequest(STRANGER, null),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
            "Stranger should NOT access private repo");
    }

    @Test
    void testGitPush_PublicRepo_StrangerAccess() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/git/" + OWNER + "/public-git-repo.git/info/refs?service=git-receive-pack",
                HttpMethod.GET,
                authenticatedRequest(STRANGER, null),
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
            "Stranger should NOT have write access to public repo");
    }

    @Test
    void testGitPush_PublicRepo_OwnerAccess() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/git/" + OWNER + "/public-git-repo.git/info/refs?service=git-receive-pack",
                HttpMethod.GET,
                authenticatedRequest(OWNER, null),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(),
            "Owner should have write access to their public repo");
    }

    @Test
    void testGitPush_PrivateRepo_UnauthenticatedAccess() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/git/" + OWNER + "/private-git-repo.git/info/refs?service=git-receive-pack",
                HttpMethod.GET,
                unauthenticatedRequest(),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
            "Unauthenticated users should NOT access private repo for push");
    }

    private void createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(username + "@git.test");
        u.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(u);
    }
}
