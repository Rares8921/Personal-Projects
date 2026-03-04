package main.backend.dto.repo;

import java.time.LocalDateTime;

public record RepositoryDto(
    Long id,
    String name,
    String description,
    boolean isPublic,
    String defaultBranch,
    int starsCount,
    int forksCount,
    String ownerUsername,// doar username ul, nu tot obiectul User
    String cloneUrlHttp,// URL git clone
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String license,
    boolean isArchived
) {}