package main.domain;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a Git reference (a pointer to a commit hash).
 * Examples: "refs/heads/main", "HEAD", "refs/tags/v1.0".
 */
public record Reference(
        String name,
        String hash
) {

    private static final Set<String> SPECIAL_REFS = Set.of(
            "HEAD",
            "FETCH_HEAD",
            "ORIG_HEAD",
            "MERGE_HEAD",
            "CHERRY_PICK_HEAD"
    );

    public Reference {
        Objects.requireNonNull(name, "Reference name cannot be null");
        Objects.requireNonNull(hash, "Reference hash cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Reference name cannot be blank");
        }
        if (hash.isBlank()) {
            throw new IllegalArgumentException("Reference hash cannot be blank");
        }

        if (!name.startsWith("refs/") && !SPECIAL_REFS.contains(name)) {
            throw new IllegalArgumentException(
                    "Invalid reference name: '%s'. Must start with 'refs/' or be a standard special ref (e.g., HEAD).".formatted(name)
            );
        }
    }

    public boolean isSpecial() {
        return SPECIAL_REFS.contains(name);
    }

    //refs/heads/main -> main
    public String shortName() {
        if (name.startsWith("refs/heads/")) {
            return name.substring("refs/heads/".length());
        }
        if (name.startsWith("refs/tags/")) {
            return name.substring("refs/tags/".length());
        }
        if (name.startsWith("refs/remotes/")) {
            return name.substring("refs/remotes/".length());
        }
        return name;
    }
}