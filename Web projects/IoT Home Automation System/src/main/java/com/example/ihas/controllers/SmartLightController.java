package com.example.ihas.controllers;

import com.example.ihas.devices.SmartLight;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartLightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lights")
public class SmartLightController {

    private final SmartLightService lightService;

    @Autowired
    private AuditService auditService;

    public SmartLightController(SmartLightService service) {
        lightService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllLights(Authentication auth) {
        String user_id = auth.getName();
        List<SmartLight> list = lightService.getAll(user_id);
        List<Map<String, Object>> response = list.stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart lights", user_id));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapping(SmartLight light) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", light.getId());
        m.put("name", light.getName());
        m.put("isOn", light.isOn());
        m.put("brightness", light.getBrightness());
        m.put("color", light.getColor());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLight(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartLight light = lightService.get(id, user_id);
            Map<String, Object> result = mapping(light);
            result.put("eventLog", light.getEventLog());
            auditService.log(String.format("User %s viewed smart light %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addLight(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            SmartLight light = new SmartLight(id, name);
            lightService.add(light, user_id);
            auditService.log(String.format("User %s added smart light %s", user_id, id));
            return ResponseEntity.ok("SmartLight added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLight(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            lightService.delete(id, user_id);
            auditService.log(String.format("User %s deleted smart light %s", user_id, id));
            return ResponseEntity.ok("SmartLight deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggleLight(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            lightService.togglePower(id, user_id);
            auditService.log(String.format("User %s toggled power on smart light %s", user_id, id));
            return ResponseEntity.ok("SmartLight toggled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling SmartLight: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/updateBrightness")
    public ResponseEntity<String> updateBrightness(@PathVariable String id, @RequestParam int brightness, Authentication auth) {
        try {
            String user_id = auth.getName();
            lightService.updateBrightness(id, brightness, user_id);
            auditService.log(String.format("User %s updated brightness of smart light %s to %d", user_id, id, brightness));
            return ResponseEntity.ok("Brightness updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating brightness: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/updateColor")
    public ResponseEntity<String> updateColor(@PathVariable String id, @RequestParam String color, Authentication auth) {
        try {
            String user_id = auth.getName();
            lightService.updateColor(id, color, user_id);
            auditService.log(String.format("User %s updated color of smart light %s to %s", user_id, id, color));
            return ResponseEntity.ok("Color updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating color: " + e.getMessage());
        }
    }
}
