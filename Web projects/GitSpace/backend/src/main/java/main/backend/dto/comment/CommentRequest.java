package main.backend.dto.comment;

public record CommentRequest(
    String body,
    String filePath,
    Integer lineNumber
) {}