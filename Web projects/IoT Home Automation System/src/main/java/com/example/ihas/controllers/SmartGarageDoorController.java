package com.example.ihas.controllers;

import com.example.ihas.devices.SmartGarageDoor;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartGarageDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/garagedoor")
public class SmartGarageDoorController {

    private final SmartGarageDoorService doorService;
    @Autowired private AuditService auditService;

    public SmartGarageDoorController(SmartGarageDoorService service) { doorService = service; }

    private Map<String, Object> mapping(SmartGarageDoor d) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", d.getId());
        m.put("name", d.getName());
        m.put("doorState", d.getDoorState().name());
        m.put("autoClose", d.isAutoClose());
        m.put("autoCloseDelay", d.getAutoCloseDelay());
        return m;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(Authentication auth) {
        String userId = auth.getName();
        List<Map<String, Object>> response = doorService.getAll(userId).stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all garage doors", userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String id, Authentication auth) {
        try {
            SmartGarageDoor d = doorService.get(id, auth.getName());
            Map<String, Object> result = mapping(d);
            result.put("eventLog", d.getEventLog());
            return ResponseEntity.ok(result);
        } catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String userId = auth.getName();
            SmartGarageDoor d = new SmartGarageDoor(body.get("id").toString(), body.get("name").toString());
            doorService.add(d, userId);
            auditService.log(String.format("User %s added garage door %s", userId, d.getId()));
            return ResponseEntity.ok("SmartGarageDoor added");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Authentication auth) {
        try {
            doorService.delete(id, auth.getName());
            return ResponseEntity.ok("SmartGarageDoor deleted");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggle(@PathVariable String id, Authentication auth) {
        try {
            doorService.toggleDoor(id, auth.getName());
            return ResponseEntity.ok("Garage door toggled");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/open")
    public ResponseEntity<String> open(@PathVariable String id, Authentication auth) {
        try {
            doorService.openDoor(id, auth.getName());
            return ResponseEntity.ok("Garage door opened");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<String> close(@PathVariable String id, Authentication auth) {
        try {
            doorService.closeDoor(id, auth.getName());
            return ResponseEntity.ok("Garage door closed");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }
}
