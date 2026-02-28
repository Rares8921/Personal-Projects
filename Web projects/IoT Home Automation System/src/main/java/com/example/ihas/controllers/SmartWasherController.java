package com.example.ihas.controllers;

import com.example.ihas.devices.SmartWasher;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartWasherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/washer")
public class SmartWasherController {

    private final SmartWasherService washerService;
    @Autowired private AuditService auditService;

    public SmartWasherController(SmartWasherService service) { washerService = service; }

    private Map<String, Object> mapping(SmartWasher w) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", w.getId());
        m.put("name", w.getName());
        m.put("isOn", w.isOn());
        m.put("mode", w.getMode().name());
        m.put("temperature", w.getTemperature());
        m.put("spinSpeed", w.getSpinSpeed());
        m.put("remainingMinutes", w.getRemainingMinutes());
        return m;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(Authentication auth) {
        String userId = auth.getName();
        List<Map<String, Object>> response = washerService.getAll(userId).stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart washers", userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String id, Authentication auth) {
        try {
            SmartWasher w = washerService.get(id, auth.getName());
            Map<String, Object> result = mapping(w);
            result.put("eventLog", w.getEventLog());
            return ResponseEntity.ok(result);
        } catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String userId = auth.getName();
            SmartWasher w = new SmartWasher(body.get("id").toString(), body.get("name").toString());
            washerService.add(w, userId);
            auditService.log(String.format("User %s added smart washer %s", userId, w.getId()));
            return ResponseEntity.ok("SmartWasher added");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Authentication auth) {
        try {
            washerService.delete(id, auth.getName());
            return ResponseEntity.ok("SmartWasher deleted");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggle(@PathVariable String id, Authentication auth) {
        try {
            washerService.togglePower(id, auth.getName());
            return ResponseEntity.ok("SmartWasher toggled");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/mode")
    public ResponseEntity<String> updateMode(@PathVariable String id, @RequestParam String mode, Authentication auth) {
        try {
            washerService.updateMode(id, mode, auth.getName());
            return ResponseEntity.ok("Wash mode updated");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startCycle(@PathVariable String id, Authentication auth) {
        try {
            washerService.startCycle(id, auth.getName());
            return ResponseEntity.ok("Wash cycle started");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }
}
