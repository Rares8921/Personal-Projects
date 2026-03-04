package main.backend;

import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadIntegrationTest extends BaseIntegrationTest {

    private final String USER = "file_uploader";
    private final String PASS = "pass";

    @BeforeEach
    void setup() {
        User u = new User();
        u.setUsername(USER);
        u.setEmail("uploader@test.com");
        u.setPasswordHash(passwordEncoder.encode(PASS));
        userRepository.save(u);
    }

    @Test
    void testAvatarUpload() {
        String fileContent = "fake image content";
        ByteArrayResource resource = new ByteArrayResource(fileContent.getBytes()) {
            @Override
            public String getFilename() {
                return "avatar.png";
            }
        };

        HttpHeaders headers = getAuthHeaders(USER);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/upload/avatar", requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("/api/upload/files/avatars/"));

        User updatedUser = userRepository.findByUsername(USER).get();
        assertNotNull(updatedUser.getAvatarUrl());
        assertTrue(updatedUser.getAvatarUrl().contains("avatar.png") || updatedUser.getAvatarUrl().contains("avatars"));
    }
}