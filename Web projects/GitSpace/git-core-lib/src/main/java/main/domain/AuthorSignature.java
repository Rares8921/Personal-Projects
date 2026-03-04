package main.domain;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents the identity of a person associated with a Git commit or tag.
 * A signature consists of a name, an email address, and the timestamp
 * (including timezone) when the action occurred.
 * Format conforms to: {@code "Name <email> timestamp offset"}
 */
public record AuthorSignature(
        String name,
        String email,
        ZonedDateTime timestamp
) {

    // Git expects timezone offsets like +0200 or -0500. "XX" pattern produces this.
    private static final DateTimeFormatter TIMEZONE_FORMATTER = DateTimeFormatter.ofPattern("XX");

    /**
     * Canonical constructor with strict validation.
     * Enforces that name and email do not contain characters that would break
     * the standard Git serialization format (e.g., '<', '>', or newlines).
     *
     * @param name      The display name of the author. Cannot be null or blank.
     * @param email     The email address. Cannot be null.
     * @param timestamp The time of the action. Cannot be null.
     */
    public AuthorSignature {
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");

        name = sanitizeInput(name, "Name");
        email = sanitizeInput(email, "Email");

        validateFormatSafety(name);

        if (email.contains(">")) {
            throw new IllegalArgumentException("Email cannot contain '>' character");
        }
    }

    /**
     * Creates a signature with the current system time.
     *
     * @param name  The author's name.
     * @param email The author's email.
     * @return A new AuthorSignature instance.
     */
    public static AuthorSignature now(String name, String email) {
        return new AuthorSignature(name, email, ZonedDateTime.now());
    }

    /**
     * Serializes the signature to the standard Git byte format.
     * Example: {@code John Doe <john@example.com> 1678886400 +0200}
     *
     * @return The formatted string ready for object writing.
     */
    public String toGitFormat() {
        final long epochSeconds = timestamp.toEpochSecond();
        final String timezoneOffset = timestamp.format(TIMEZONE_FORMATTER);

        return name +
                " <" +
                email +
                "> " +
                epochSeconds +
                " " +
                timezoneOffset;
    }

    @Override
    public String toString() {
        return toGitFormat();
    }

    private static String sanitizeInput(String input, String fieldName) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return input.trim();
    }

    private static void validateFormatSafety(String input) {
        if (input.indexOf('<') >= 0 || input.indexOf('>') >= 0 || input.indexOf('\n') >= 0) {
            throw new IllegalArgumentException("Name contains illegal characters (<, >, or newline): " + input);
        }
    }
}