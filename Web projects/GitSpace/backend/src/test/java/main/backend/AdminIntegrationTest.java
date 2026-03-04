package main.backend;

import main.backend.model.Role;
import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class AdminIntegrationTest extends BaseIntegrationTest {

    private final String ADMIN_USER = "admin_boss";
    private final String NORMAL_USER = "regular_joe";
    private final String VICTIM_USER = "user_to_delete";
    private final String PASS = "pass123";

    @BeforeEach
    void setup() {
        createAdmin(ADMIN_USER);
        createUser(NORMAL_USER);
        createUser(VICTIM_USER);
    }

    @Test
    void testAdminCanDeleteUser() {
        User victim = userRepository.findByUsername(VICTIM_USER).orElseThrow();
        Long victimId = victim.getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/users/" + victimId,
                HttpMethod.DELETE,
                authenticatedRequest(ADMIN_USER, null),
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Admin should be able to delete users (204 No Content)");
        assertTrue(userRepository.findById(victimId).isEmpty(), "User should be removed from DB");
    }

    @Test
    void testRegularUserCannotDeleteUser() {
        User victim = userRepository.findByUsername(VICTIM_USER).orElseThrow();
        Long victimId = victim.getId();

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + victimId,
                HttpMethod.DELETE,
                authenticatedRequest(NORMAL_USER, null),
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Regular user should get 403 Forbidden");
        assertTrue(userRepository.findById(victimId).isPresent(), "User should NOT be removed from DB");
    }

    @Test
    void testAdminAccessToSensitiveEndpoints() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/admin/stats",
                HttpMethod.GET,
                authenticatedRequest(ADMIN_USER, null),
                String.class
        );

        assertNotEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private void createAdmin(String username) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(username + "@admin.com");
        u.setPasswordHash(passwordEncoder.encode(PASS));
        u.setRole(Role.ROLE_ADMIN);
        userRepository.save(u);
    }

    private void createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(username + "@user.com");
        u.setPasswordHash(passwordEncoder.encode(PASS));
        u.setRole(Role.ROLE_USER);
        userRepository.save(u);
    }
}