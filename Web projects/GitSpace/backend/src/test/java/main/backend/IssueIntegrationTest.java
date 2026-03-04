package main.backend;

import main.backend.dto.issue.CreateIssueRequest;
import main.backend.dto.issue.IssueCommentDto;
import main.backend.dto.issue.IssueDto;
import main.backend.dto.repo.CreateRepoRequest;
import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class IssueIntegrationTest extends BaseIntegrationTest {

    private final String USER = "dacia_tester";
    private final String REPO = "dacia-repo";
    private final String PASS = "stepway123";

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUsername(USER);
        user.setEmail("tester@issues.com");
        user.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(user);

        repoService.createRepo(USER, new CreateRepoRequest(REPO, "Test Repo", false, "None"));
    }

    @Test
    void testIssueLifecycle() {
        String baseUrl = "/api/repos/" + USER + "/" + REPO + "/issues";

        CreateIssueRequest req1 = new CreateIssueRequest("Bug found", "Fix it asap");
        ResponseEntity<IssueDto> resp1 = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                authenticatedRequest(USER, req1),
                IssueDto.class
        );

        assertEquals(HttpStatus.OK, resp1.getStatusCode());
        assertEquals(1L, Objects.requireNonNull(resp1.getBody()).number());
        assertTrue(resp1.getBody().isOpen());

        CreateIssueRequest req2 = new CreateIssueRequest("Feature request", "Add dark mode");
        ResponseEntity<IssueDto> resp2 = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                authenticatedRequest(USER, req2),
                IssueDto.class
        );

        assertEquals(2L, Objects.requireNonNull(resp2.getBody()).number());

        // ISSUES
        ResponseEntity<List<IssueDto>> listResp = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                authenticatedRequest(USER, null),
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(2, Objects.requireNonNull(listResp.getBody()).size());

        Map<String, String> commentBody = Map.of("body", "I am working on it");
        ResponseEntity<IssueCommentDto> commentResp = restTemplate.exchange(
                baseUrl + "/1/comments",
                HttpMethod.POST,
                authenticatedRequest(USER, commentBody),
                IssueCommentDto.class
        );

        assertEquals(HttpStatus.OK, commentResp.getStatusCode());
        assertEquals("I am working on it", Objects.requireNonNull(commentResp.getBody()).body());

        Map<String, Boolean> statusUpdate = Map.of("open", false);
        ResponseEntity<Void> closeResp = restTemplate.exchange(
                baseUrl + "/1/status",
                HttpMethod.PATCH,
                authenticatedRequest(USER, statusUpdate),
                Void.class
        );

        assertEquals(HttpStatus.OK, closeResp.getStatusCode());

        // CLOSED
        ResponseEntity<IssueDto> getResp = restTemplate.exchange(
                baseUrl + "/1",
                HttpMethod.GET,
                authenticatedRequest(USER, null),
                IssueDto.class
        );
        assertFalse(Objects.requireNonNull(getResp.getBody()).isOpen());
    }
}