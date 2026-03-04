package main.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.backend.dto.auth.ForgotPasswordRequest;
import main.backend.dto.auth.LoginRequest;
import main.backend.dto.auth.PasswordResetRequest;
import main.backend.dto.auth.RegisterRequest;
import main.backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private final String TEST_USERNAME = "testuser";
    private final String TEST_EMAIL = "fmi@default.com";
    private final String TEST_PASSWORD = "parola123";

    private void createTestUser() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPasswordHash(passwordEncoder.encode(TEST_PASSWORD));
        userRepository.save(user);
    }

    @Test
    void testRegister() throws Exception {
        RegisterRequest registerReq = new RegisterRequest("newuser", "new@example.com", "pass123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        assertTrue(userRepository.findByUsername("newuser").isPresent());
    }

    @Test
    void testLoginSuccess() throws Exception {
        createTestUser();
        LoginRequest loginReq = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME));
    }

    @Test
    void testLoginFailure_WrongPassword() throws Exception {
        createTestUser();
        LoginRequest badLogin = new LoginRequest(TEST_USERNAME, "parolaGRESITA");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testForgotPassword_GeneratesToken() throws Exception {
        createTestUser();
        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest(TEST_EMAIL);

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk());

        User user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        assertNotNull(user.getResetPasswordToken(), "Tokenul de resetare nu e in baza de date.");
    }

    @Test
    void testResetPassword_Flow() throws Exception {
        createTestUser();
        User user = userRepository.findByUsername(TEST_USERNAME).get();
        String simToken = "token-special-resetare-123";
        user.setResetPasswordToken(simToken);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String newPassword = "parolanoua456";
        PasswordResetRequest resetReq = new PasswordResetRequest(simToken, newPassword);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully"));

        LoginRequest newLogin = new LoginRequest(TEST_USERNAME, newPassword);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}