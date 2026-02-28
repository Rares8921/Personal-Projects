package com.example.ihas.controllers;

import com.example.ihas.devices.SmartThermostat;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartThermostatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/thermostats")
public class ThermostatController {

    private final SmartThermostatService thermostatService;

    @Autowired
    private AuditService auditService;

    public ThermostatController(SmartThermostatService service) {
        thermostatService = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllThermostats(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to access thermostats");
        }

        String user_id = auth.getName();
        List<SmartThermostat> list = thermostatService.getAllThermostats(user_id);
        List<Map<String, Object>> response = list.stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart thermostats", user_id));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapping(SmartThermostat st) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", st.getId());
        m.put("name", st.getName());
        m.put("isOn", st.isOn());
        m.put("temperature", st.getTemperature());
        m.put("mode", st.getMode().name());
        m.put("eventLog", st.getEventLog());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getThermostat(@PathVariable String id, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to access thermostat");
        }

        try {
            SmartThermostat st = thermostatService.getThermostat(id, auth.getName());
            auditService.log(String.format("User %s viewed smart thermostat %s", auth.getName(), id));
            return ResponseEntity.ok(mapping(st));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> addThermostat(@RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to add thermostat");
        }

        try {
            if (!body.containsKey("id") || !body.containsKey("name")) {
                return ResponseEntity.badRequest().body("Missing required fields: id and name");
            }

            String id = body.get("id").toString().trim();
            String name = body.get("name").toString().trim();

            if (id.isEmpty() || name.isEmpty()) {
                return ResponseEntity.badRequest().body("ID and Name cannot be empty");
            }

            SmartThermostat st = new SmartThermostat(id, name);
            if (body.containsKey("temperature")) {
                double t = Double.parseDouble(body.get("temperature").toString());
                st.setTemperature(t);
            }
            if (body.containsKey("mode")) {
                SmartThermostat.Mode mode = SmartThermostat.Mode.valueOf(body.get("mode").toString());
                st.setMode(mode);
            }
            thermostatService.addThermostat(st, auth.getName());
            auditService.log(String.format("User %s added smart thermostat %s", auth.getName(), id));
            return ResponseEntity.ok(mapping(st));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteThermostat(@PathVariable String id, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to delete thermostat");
        }

        try {
            thermostatService.deleteThermostat(id, auth.getName());
            auditService.log(String.format("User %s deleted smart thermostat %s", auth.getName(), id));
            return ResponseEntity.ok("Thermostat deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<?> updateThermostat(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to update thermostat");
        }

        try {
            String user_id = auth.getName();
            if (body.containsKey("temperature")) {
                double t = Double.parseDouble(body.get("temperature").toString());
                thermostatService.updateTemperature(id, t, user_id);
                auditService.log(String.format("User %s updated temperature of smart thermostat %s to %.1f", user_id, id, t));
            }
            if (body.containsKey("mode")) {
                SmartThermostat.Mode m = SmartThermostat.Mode.valueOf(body.get("mode").toString());
                thermostatService.updateMode(id, m, user_id);
                auditService.log(String.format("User %s updated mode of smart thermostat %s to %s", user_id, id, m.name()));
            }
            SmartThermostat updated = thermostatService.getThermostat(id, user_id);
            return ResponseEntity.ok(mapping(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<?> togglePower(@PathVariable String id, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to toggle thermostat power");
        }

        try {
            thermostatService.togglePower(id, auth.getName());
            auditService.log(String.format("User %s toggled power state of smart thermostat %s", auth.getName(), id));
            SmartThermostat updated = thermostatService.getThermostat(id, auth.getName());
            return ResponseEntity.ok(mapping(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling power: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/autoAdjust")
    public ResponseEntity<?> autoAdjust(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to auto-adjust thermostat");
        }

        try {
            double ambient = Double.parseDouble(body.get("ambientTemp").toString());
            thermostatService.autoAdjust(id, ambient, auth.getName());
            auditService.log(String.format("User %s auto-adjusted smart thermostat %s based on ambient temperature %.1f", auth.getName(), id, ambient));
            SmartThermostat updated = thermostatService.getThermostat(id, auth.getName());
            return ResponseEntity.ok(mapping(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error auto adjusting: " + e.getMessage());
        }
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getAmbientTemperature(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            String apiKey = "713a7806e8487a3892448a9a6bff55b0";
            String url = "https://api.openweathermap.org/data/2.5/weather?lat=44.3165&lon=23.8180&units=metric&appid=" + apiKey;

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Java-Spring-Client")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());
                double temp = root.get("main").get("temp").asDouble();
                return ResponseEntity.ok(Map.of("temperature", temp));
            } else {
//                return ResponseEntity.status(response.statusCode()).body("Weather API error: " + response.body());
                return ResponseEntity.ok(Map.of("temperature", 22.0));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }


}
