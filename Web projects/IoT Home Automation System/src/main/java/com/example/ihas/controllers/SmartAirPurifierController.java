package com.example.ihas.controllers;

import com.example.ihas.devices.SmartAirPurifier;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartAirPurifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/airpurifier")
public class SmartAirPurifierController {

    private final SmartAirPurifierService purifierService;
    @Autowired private AuditService auditService;

    public SmartAirPurifierController(SmartAirPurifierService service) { purifierService = service; }

    private Map<String, Object> mapping(SmartAirPurifier p) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", p.getId());
        m.put("name", p.getName());
        m.put("isOn", p.isOn());
        m.put("fanSpeed", p.getFanSpeed().name());
        m.put("airQualityIndex", p.getAirQualityIndex());
        m.put("filterLife", p.getFilterLife());
        return m;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(Authentication auth) {
        String userId = auth.getName();
        List<Map<String, Object>> response = purifierService.getAll(userId).stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all air purifiers", userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String id, Authentication auth) {
        try {
            SmartAirPurifier p = purifierService.get(id, auth.getName());
            Map<String, Object> result = mapping(p);
            result.put("eventLog", p.getEventLog());
            return ResponseEntity.ok(result);
        } catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String userId = auth.getName();
            SmartAirPurifier p = new SmartAirPurifier(body.get("id").toString(), body.get("name").toString());
            purifierService.add(p, userId);
            auditService.log(String.format("User %s added air purifier %s", userId, p.getId()));
            return ResponseEntity.ok("SmartAirPurifier added");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Authentication auth) {
        try {
            purifierService.delete(id, auth.getName());
            return ResponseEntity.ok("SmartAirPurifier deleted");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggle(@PathVariable String id, Authentication auth) {
        try {
            purifierService.togglePower(id, auth.getName());
            return ResponseEntity.ok("SmartAirPurifier toggled");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/fanspeed")
    public ResponseEntity<String> updateFanSpeed(@PathVariable String id, @RequestParam String speed, Authentication auth) {
        try {
            purifierService.updateFanSpeed(id, speed, auth.getName());
            return ResponseEntity.ok("Fan speed updated");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }
}
