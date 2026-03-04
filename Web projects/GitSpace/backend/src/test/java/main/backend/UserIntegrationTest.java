package main.backend;

import main.backend.dto.user.UserProfileDto;
import main.backend.dto.user.UserSettingsDto;
import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntegrationTest extends BaseIntegrationTest {

    private final String USER_1310 = "a1310";
    private final String USER_1300 = "b1300";
    private final String PASS = "extrasafe";

    @BeforeEach
    void setup() {
        createTestUser(USER_1310, "alice@test.com");
        createTestUser(USER_1300, "bob@test.com");
    }

    @Test
    void testSearchAndProfile() {
        ResponseEntity<List<User>> searchResp = restTemplate.exchange(
                "/api/users/search?query=a13",
                HttpMethod.GET,
                authenticatedRequest(USER_1310, null),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, searchResp.getStatusCode());
        assertFalse(Objects.requireNonNull(searchResp.getBody()).isEmpty());
        assertEquals(USER_1310, searchResp.getBody().getFirst().getUsername());

        ResponseEntity<UserProfileDto> profileResp = restTemplate.exchange(
                "/api/users/" + USER_1310,
                HttpMethod.GET,
                authenticatedRequest(USER_1310, null),
                UserProfileDto.class
        );

        assertEquals(HttpStatus.OK, profileResp.getStatusCode());
        assertEquals(USER_1310, Objects.requireNonNull(profileResp.getBody()).username());
    }

    @Test
    void testUpdateSettings() {
        UserSettingsDto settings = new UserSettingsDto(
                "a310_new@sergiu.com", "Dacia 1310", "Dev", "Wonderland", "https://empty.com", "UTC", true
        );

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/users/settings",
                HttpMethod.PUT,
                authenticatedRequest(USER_1310, settings),
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        User updatedAlice = userRepository.findByUsername(USER_1310).get();
        assertEquals("Dacia 1310", updatedAlice.getFullName());
        assertEquals("Wonderland", updatedAlice.getLocation());
    }

    @Test
    void testFollowFlow() {
        // A follows B
        restTemplate.exchange(
                "/api/users/" + USER_1300 + "/follow",
                HttpMethod.POST,
                authenticatedRequest(USER_1310, null),
                String.class
        );

        ResponseEntity<UserProfileDto> bobProfile = restTemplate.exchange(
                "/api/users/" + USER_1300,
                HttpMethod.GET,
                authenticatedRequest(USER_1310, null),
                UserProfileDto.class
        );

        assertEquals(1, Objects.requireNonNull(bobProfile.getBody()).followers());

        // A unfollows Bob
        ResponseEntity<String> deleteResp = restTemplate.exchange(
                "/api/users/" + USER_1300 + "/follow",
                HttpMethod.DELETE,
                authenticatedRequest(USER_1310, null),
                String.class
        );

        assertEquals(HttpStatus.OK, deleteResp.getStatusCode());

        // Check again
        bobProfile = restTemplate.exchange(
                "/api/users/" + USER_1300,
                HttpMethod.GET,
                authenticatedRequest(USER_1310, null),
                UserProfileDto.class
        );

        assertEquals(0, Objects.requireNonNull(bobProfile.getBody()).followers());
    }

    private void createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(user);
    }
}