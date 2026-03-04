package main.backend.dto.repo;

import java.time.Instant;
import java.util.List;

public record GitCommitDto(
        String hash,
        String shortMessage,
        String fullMessage,
        String authorName,
        String authorEmail,
        Instant timestamp,
        List<String> parentHashes
) {}
