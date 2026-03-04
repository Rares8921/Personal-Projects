package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class CredentialsManager {

    private static Path getConfigDir() {
        return Paths.get(System.getProperty("user.home"), ".dacia-git");
    }

    private static Path getConfigFile() {
        return getConfigDir().resolve("credentials");
    }

    public static void saveCredentials(String token, String username) {
        try {
            Path dir = getConfigDir();
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Properties props = new Properties();
            props.setProperty("token", token);
            props.setProperty("username", username);

            try (var writer = Files.newBufferedWriter(getConfigFile())) {
                props.store(writer, "Dacia Git CLI Credentials");
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save credentials locally: " + e.getMessage());
        }
    }

    public static String getToken() {
        return getProperty("token");
    }

    public static String getUsername() {
        return getProperty("username");
    }

    private static String getProperty(String key) {
        Path file = getConfigFile();
        if (!Files.exists(file)) return null;
        try (var reader = Files.newBufferedReader(file)) {
            Properties props = new Properties();
            props.load(reader);
            return props.getProperty(key);
        } catch (IOException e) {
            return null;
        }
    }

    public static void logout() {
        try {
            Files.deleteIfExists(getConfigFile());
            System.out.println("Credentials removed.");
        } catch (IOException e) {
            System.err.println("Error clearing credentials: " + e.getMessage());
        }
    }
}