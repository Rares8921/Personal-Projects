package main.backend.dto.snippet;

import java.time.Instant;

public record SnippetDto(
    Long id,
    String description,
    String filename,
    String content,
    Boolean isPublic,
    String authorUsername,
    String language,
    Instant createdAt
) {}