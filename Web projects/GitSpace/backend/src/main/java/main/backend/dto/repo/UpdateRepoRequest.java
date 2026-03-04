package main.backend.dto.repo;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRepoRequest(
    @Size(min = 1, max = 255)
    String description,
    Boolean isPublic,
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Name can only contain letters, numbers, hyphens and underscores")
    String newName,

    String defaultBranch,
    Boolean isArchived
) {}