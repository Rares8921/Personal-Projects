package main.backend.dto.auth;

public record PasswordResetRequest(String token, String newPassword) {}