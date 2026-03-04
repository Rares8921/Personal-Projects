package main.backend.dto.user;

public record UserSettingsDto(
    String email,
    String fullName,
    String bio,
    String location,
    String website,
    String timezone,
    Boolean isPublic
) {}