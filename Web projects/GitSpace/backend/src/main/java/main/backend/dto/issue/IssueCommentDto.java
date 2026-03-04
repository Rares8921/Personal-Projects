package main.backend.dto.issue;

import java.time.Instant;

public record IssueCommentDto(Long id, String body, String authorUsername, String authorAvatarUrl, Instant createdAt, Instant updatedAt) {}
