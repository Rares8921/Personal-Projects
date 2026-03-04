package main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthClient {
    private static final String LOGIN_URL = "http://localhost:8080/api/auth/login";
    private final HttpClient client = HttpClient.newHttpClient();

    public void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        String jsonBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            System.out.println("Connecting to server...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Spring returneaza: {"token":"ey...", "username":"..."}
                String responseBody = response.body();

                String token = extractJsonValue(responseBody, "token");
                String returnedUser = extractJsonValue(responseBody, "username");

                if (token != null) {
                    CredentialsManager.saveCredentials(token, returnedUser != null ? returnedUser : username);
                    System.out.println("SUCCESS: Logged in as " + (returnedUser != null ? returnedUser : username));
                } else {
                    System.err.println("Error: Server responded 200 OK but token was missing.");
                }

            } else {
                System.err.println("Login Failed (Code " + response.statusCode() + "): " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Network Error: Is the backend server running on localhost:8080?");
            System.err.println("Details: " + e.getMessage());
        }
    }

    private String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}