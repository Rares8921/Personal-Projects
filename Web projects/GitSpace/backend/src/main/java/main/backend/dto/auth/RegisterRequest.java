package main.backend.dto.auth;

public record RegisterRequest(String username, String email, String password) {}