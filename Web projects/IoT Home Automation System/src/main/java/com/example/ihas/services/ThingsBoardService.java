package com.example.ihas.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class ThingsBoardService {

    private final RestTemplate restTemplate;

    public ThingsBoardService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void updateTelemetry(String baseUrl, String deviceToken, Map<String, Object> telemetry) {
        String url = baseUrl + deviceToken + "/telemetry";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(telemetry, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("ThingsBoard telemetry update failed: " + response.getStatusCode());
        }
    }
}
