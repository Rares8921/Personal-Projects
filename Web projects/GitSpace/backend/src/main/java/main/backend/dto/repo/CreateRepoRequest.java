package main.backend.dto.repo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateRepoRequest(
    @NotBlank(message = "Repository name is required")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Name can only contain letters, numbers, hyphens and underscores")
    String name,
    String description,
    boolean isPublic,
    String license
) {}