package main.backend.dto.user;

import main.backend.dto.repo.RepositoryDto;
import java.time.Instant;
import java.util.List;

public record UserProfileDto(
    Long id,
    String username,
    String fullName,
    String avatarUrl,
    String bio,
    String location,
    Boolean isPublic,
    boolean isFollowing,
    boolean isPending,
    String website,
    Instant createdAt,
    long followers,
    long following,
    List<RepositoryDto> repos
) {}