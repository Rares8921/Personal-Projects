package com.example.ihas.controllers;

import com.example.ihas.devices.SmartCarCharger;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartCarChargerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carchargers")
public class SmartCarChargerController {

    private final SmartCarChargerService chargerService;

    @Autowired
    private AuditService auditService;

    public SmartCarChargerController(SmartCarChargerService service) {
        chargerService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCarChargers(Authentication auth) {
        String user_id = auth.getName();
        List<SmartCarCharger> list = chargerService.getAll(user_id);
        List<Map<String, Object>> response = list.stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart car chargers", user_id));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapping(SmartCarCharger charger) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", charger.getId());
        m.put("name", charger.getName());
        m.put("isCharging", charger.isCharging());
        m.put("current", charger.getCurrent());
        m.put("voltage", charger.getVoltage());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCarCharger(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartCarCharger charger = chargerService.get(id, user_id);
            Map<String, Object> result = mapping(charger);
            result.put("eventLog", charger.getEventLog());
            auditService.log(String.format("User %s viewed smart car charger %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addCarCharger(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            SmartCarCharger charger = new SmartCarCharger(id, name);
            chargerService.add(charger, user_id);
            auditService.log(String.format("User %s added smart car charger %s", user_id, id));
            return ResponseEntity.ok("SmartCarCharger added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCarCharger(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            chargerService.delete(id, user_id);
            auditService.log(String.format("User %s deleted smart car charger %s", user_id, id));
            return ResponseEntity.ok("SmartCarCharger deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggleCarCharger(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            chargerService.toggleCharging(id, user_id);
            auditService.log(String.format("User %s toggled charging state on smart car charger %s", user_id, id));
            return ResponseEntity.ok("SmartCarCharger toggled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling SmartCarCharger: " + e.getMessage());
        }
    }
}
