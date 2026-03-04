package main.backend.dto.pr;

public record PullRequestDto(
    Long id,
    int prNumber,
    String title,
    String description,
    String fromBranch,
    String toBranch,
    String authorUsername,
    String state,
    boolean hasConflicts,
    java.time.Instant createdAt
) {}