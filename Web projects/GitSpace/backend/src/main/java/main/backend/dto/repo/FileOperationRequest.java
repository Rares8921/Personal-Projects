package main.backend.dto.repo;

public record FileOperationRequest(
    String branch,
    String path, // ex: "src/Main.java"
    String content,
    String message // Commit message
) {}