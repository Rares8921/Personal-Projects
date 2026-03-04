package main.backend.dto.comment;

import java.time.Instant;

public record CommentDto(
        Long id,
        String body,
        String authorUsername,
        Instant createdAt,
        String filePath,
        Integer lineNumber
) {}