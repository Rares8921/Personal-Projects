package com.example.ihas.controllers;

import com.example.ihas.devices.SmartDoorLock;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartDoorLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doorlocks")
public class SmartDoorLockController {

    private final SmartDoorLockService doorLockService;

    @Autowired
    private AuditService auditService;

    public SmartDoorLockController(SmartDoorLockService service) {
        doorLockService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDoorLocks(Authentication auth) {
        String user_id = auth.getName();
        List<SmartDoorLock> list = doorLockService.getAll(user_id);
        List<Map<String, Object>> response = list.stream().map(lock -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", lock.getId());
            m.put("name", lock.getName());
            m.put("locked", lock.isLocked());
            return m;
        }).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart door locks", user_id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoorLock(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartDoorLock lock = doorLockService.get(id, user_id);
            Map<String, Object> result = new HashMap<>();
            result.put("id", lock.getId());
            result.put("name", lock.getName());
            result.put("locked", lock.isLocked());
            result.put("eventLog", lock.getEventLog());
            auditService.log(String.format("User %s viewed smart door lock %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addDoorLock(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            SmartDoorLock lock = new SmartDoorLock(id, name);
            doorLockService.add(lock, user_id);
            auditService.log(String.format("User %s added smart door lock %s", user_id, id));
            return ResponseEntity.ok("SmartDoorLock added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoorLock(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            doorLockService.delete(id, user_id);
            auditService.log(String.format("User %s deleted smart door lock %s", user_id, id));
            return ResponseEntity.ok("SmartDoorLock deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggleDoorLock(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            doorLockService.toggleDoorLock(id, user_id);
            auditService.log(String.format("User %s toggled smart door lock %s", user_id, id));
            return ResponseEntity.ok("SmartDoorLock toggled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling SmartDoorLock: " + e.getMessage());
        }
    }
}
