package main.backend;

import main.backend.dto.issue.CreateIssueRequest;
import main.backend.dto.repo.CreateRepoRequest;
import main.backend.model.Activity;
import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityIntegrationTest extends BaseIntegrationTest {

    private final String ALICE = "alice_act";
    private final String BOB = "bob_act";
    private final String PASS = "pass";

    @BeforeEach
    void setup() {
        createUser(ALICE);
        createUser(BOB);
    }

    @Test
    void testActivityFeed() {
        restTemplate.exchange(
                "/api/users/" + BOB + "/follow",
                HttpMethod.POST,
                authenticatedRequest(ALICE, null),
                String.class
        );

        repoService.createRepo(BOB, new CreateRepoRequest("bob-repo", "desc", false, "None"));

        String issueUrl = "/api/repos/" + BOB + "/bob-repo/issues";
        CreateIssueRequest issueReq = new CreateIssueRequest("Help me", "Bug here");

        restTemplate.exchange(
                issueUrl,
                HttpMethod.POST,
                authenticatedRequest(BOB, issueReq),
                String.class
        );

        ResponseEntity<Activity[]> feedResp = restTemplate.exchange(
                "/api/activity",
                HttpMethod.GET,
                authenticatedRequest(ALICE, null),
                Activity[].class
        );

        assertEquals(HttpStatus.OK, feedResp.getStatusCode());
        Activity[] activities = feedResp.getBody();

        boolean foundIssueActivity = false;
        if (activities != null) {
            for (Activity a : activities) {
                if (a.getUser().getUsername().equals(BOB) && a.getDescription().contains("opened issue")) {
                    foundIssueActivity = true;
                    break;
                }
            }
        }
        assertTrue(foundIssueActivity, "Alice's feed should contain Bob's issue activity");
    }

    private void createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(username + "@test.com");
        u.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(u);
    }
}