package main.backend;

import main.backend.dto.snippet.SnippetDto;
import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class SnippetIntegrationTest extends BaseIntegrationTest {

    private final String USER = "snippet_master";
    private final String PASS = "pass";

    @BeforeEach
    void setup() {
        User u = new User();
        u.setUsername(USER);
        u.setEmail("snippet@test.com");
        u.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(u);
    }

    @Test
    void testSnippetLifecycle() {
        // Create Snippet
        SnippetDto req = new SnippetDto(null, "Test Gist", "algo.java", "public class A {}", true, null, "Java", Instant.now());

        ResponseEntity<SnippetDto> createResp = restTemplate.exchange(
                "/api/snippets",
                HttpMethod.POST,
                authenticatedRequest(USER, req),
                SnippetDto.class
        );

        assertEquals(HttpStatus.OK, createResp.getStatusCode());
        assertNotNull(createResp.getBody().id());

        // Get Public Snippets
        ResponseEntity<SnippetDto[]> listResp = restTemplate.exchange(
                "/api/snippets",
                HttpMethod.GET,
                authenticatedRequest(USER, null),
                SnippetDto[].class
        );

        assertEquals(HttpStatus.OK, listResp.getStatusCode());
        assertTrue(Objects.requireNonNull(listResp.getBody()).length > 0);
        assertEquals("algo.java", listResp.getBody()[0].filename());

        // Get Single Snippet
        Long id = createResp.getBody().id();
        ResponseEntity<SnippetDto> getResp = restTemplate.exchange(
                "/api/snippets/" + id,
                HttpMethod.GET,
                authenticatedRequest(USER, null),
                SnippetDto.class
        );
        assertEquals(HttpStatus.OK, getResp.getStatusCode());
    }
}