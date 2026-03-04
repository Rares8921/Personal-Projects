package main.backend.dto.pr;

public record PullRequestFileDto(String filePath, String status, String diff) {} // Status: "modified", "added", "deleted"
