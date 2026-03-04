package main.backend.dto.issue;

import java.time.Instant;

public record IssueDto(Long id, Long number, String title, String body, String authorUsername, Instant createdAt, boolean isOpen) {}
