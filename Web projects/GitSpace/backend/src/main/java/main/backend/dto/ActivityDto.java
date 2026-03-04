package main.backend.dto;

public record ActivityDto(String description, java.time.Instant timestamp) {} // e.g., "user created repo X"