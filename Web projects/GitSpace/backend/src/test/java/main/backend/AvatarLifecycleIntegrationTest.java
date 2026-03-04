package main.backend;

import main.backend.model.Role;
import main.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AvatarLifecycleIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;

    private final String USER_NAME = "avatar_user";
    private final String ADMIN_NAME = "avatar_admin";
    private final String PASS = "pass123";

    private User regularUser;
    private User adminUser;

    @BeforeEach
    void setup() {
        regularUser = new User();
        regularUser.setUsername(USER_NAME);
        regularUser.setEmail("user@avatar.com");
        regularUser.setPasswordHash(passwordEncoder.encode(PASS));
        regularUser.setRole(Role.ROLE_USER);
        userRepository.save(regularUser);

        adminUser = new User();
        adminUser.setUsername(ADMIN_NAME);
        adminUser.setEmail("admin@avatar.com");
        adminUser.setPasswordHash(passwordEncoder.encode(PASS));
        adminUser.setRole(Role.ROLE_ADMIN);
        userRepository.save(adminUser);
    }

    @Test
    void testUserCanCreateUpdateAndDeleteAvatar() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "me.jpg", MediaType.IMAGE_JPEG_VALUE, "image_content".getBytes()
        );

        mockMvc.perform(multipart("/api/upload/avatar")
                        .file(file)
                        .header("Authorization", "Bearer " + tokenProvider.generateToken(createAuth(USER_NAME))))
                .andExpect(status().isOk());

        User u = userRepository.findById(regularUser.getId()).orElseThrow();
        assertNotNull(u.getAvatarUrl(), "Avatar should be set");

        MockMultipartFile newFile = new MockMultipartFile(
                "file", "new_me.png", MediaType.IMAGE_PNG_VALUE, "new_content".getBytes()
        );

        mockMvc.perform(multipart("/api/upload/avatar")
                        .file(newFile)
                        .header("Authorization", "Bearer " + tokenProvider.generateToken(createAuth(USER_NAME))))
                .andExpect(status().isOk());

        u = userRepository.findById(regularUser.getId()).orElseThrow();
        assertTrue(u.getAvatarUrl().contains("new_me.png") || u.getAvatarUrl().contains("avatars"), "Avatar URL should change");

        mockMvc.perform(delete("/api/upload/avatar")
                        .header("Authorization", "Bearer " + tokenProvider.generateToken(createAuth(USER_NAME))))
                .andExpect(status().isNoContent());

        u = userRepository.findById(regularUser.getId()).orElseThrow();
        assertNull(u.getAvatarUrl(), "Avatar should be deleted");
    }

    @Test
    void testAdminCanDeleteAnyUserAvatar() throws Exception {
        regularUser.setAvatarUrl("http://storage/some_avatar.jpg");
        userRepository.save(regularUser);

        mockMvc.perform(delete("/api/admin/users/" + regularUser.getId() + "/avatar")
                        .header("Authorization", "Bearer " + tokenProvider.generateToken(createAuth(ADMIN_NAME))))
                .andExpect(status().isNoContent());

        User u = userRepository.findById(regularUser.getId()).orElseThrow();
        assertNull(u.getAvatarUrl(), "Admin should have removed the avatar");
    }

    @Test
    void testUserCannotDeleteOtherUserAvatar() throws Exception {
        mockMvc.perform(delete("/api/admin/users/" + adminUser.getId() + "/avatar")
                        .header("Authorization", "Bearer " + tokenProvider.generateToken(createAuth(USER_NAME))))
                .andExpect(status().isForbidden()); // 403
    }

    private org.springframework.security.core.Authentication createAuth(String username) {
        org.springframework.security.core.userdetails.UserDetails principal =
                new org.springframework.security.core.userdetails.User(username, PASS, new java.util.ArrayList<>());
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(principal, null);
    }
}