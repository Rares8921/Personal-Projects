package com.example.ihas.controllers;

import com.example.ihas.devices.SmartRobotVacuum;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartRobotVacuumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/robotvacuum")
public class SmartRobotVacuumController {

    private final SmartRobotVacuumService vacuumService;
    @Autowired private AuditService auditService;

    public SmartRobotVacuumController(SmartRobotVacuumService service) { vacuumService = service; }

    private Map<String, Object> mapping(SmartRobotVacuum v) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", v.getId());
        m.put("name", v.getName());
        m.put("isOn", v.isOn());
        m.put("mode", v.getMode().name());
        m.put("status", v.getVacuumStatus().name());
        m.put("batteryLevel", v.getBatteryLevel());
        m.put("cleanedArea", v.getCleanedArea());
        return m;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(Authentication auth) {
        String userId = auth.getName();
        List<Map<String, Object>> response = vacuumService.getAll(userId).stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all robot vacuums", userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String id, Authentication auth) {
        try {
            SmartRobotVacuum v = vacuumService.get(id, auth.getName());
            Map<String, Object> result = mapping(v);
            result.put("eventLog", v.getEventLog());
            return ResponseEntity.ok(result);
        } catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String userId = auth.getName();
            SmartRobotVacuum v = new SmartRobotVacuum(body.get("id").toString(), body.get("name").toString());
            vacuumService.add(v, userId);
            auditService.log(String.format("User %s added robot vacuum %s", userId, v.getId()));
            return ResponseEntity.ok("SmartRobotVacuum added");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Authentication auth) {
        try {
            vacuumService.delete(id, auth.getName());
            return ResponseEntity.ok("SmartRobotVacuum deleted");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggle(@PathVariable String id, Authentication auth) {
        try {
            vacuumService.togglePower(id, auth.getName());
            return ResponseEntity.ok("SmartRobotVacuum toggled");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/mode")
    public ResponseEntity<String> updateMode(@PathVariable String id, @RequestParam String mode, Authentication auth) {
        try {
            vacuumService.updateMode(id, mode, auth.getName());
            return ResponseEntity.ok("Clean mode updated");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startCleaning(@PathVariable String id, Authentication auth) {
        try {
            vacuumService.startCleaning(id, auth.getName());
            return ResponseEntity.ok("Cleaning started");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/dock")
    public ResponseEntity<String> returnToBase(@PathVariable String id, Authentication auth) {
        try {
            vacuumService.returnToBase(id, auth.getName());
            return ResponseEntity.ok("Returning to base");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }
}
