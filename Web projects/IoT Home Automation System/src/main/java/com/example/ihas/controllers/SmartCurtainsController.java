package com.example.ihas.controllers;

import com.example.ihas.devices.SmartCurtains;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartCurtainsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/curtains")
public class SmartCurtainsController {

    private final SmartCurtainsService curtainsService;

    @Autowired
    private AuditService auditService;

    public SmartCurtainsController(SmartCurtainsService service) {
        curtainsService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCurtains(Authentication auth) {
        String user_id = auth.getName();
        List<SmartCurtains> list = curtainsService.getAll(user_id);
        List<Map<String, Object>> response = list.stream().map(curtain -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", curtain.getId());
            m.put("name", curtain.getName());
            m.put("position", curtain.getPosition());
            return m;
        }).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart curtains", user_id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCurtains(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartCurtains curtain = curtainsService.get(id, user_id);
            Map<String, Object> result = new HashMap<>();
            result.put("id", curtain.getId());
            result.put("name", curtain.getName());
            result.put("position", curtain.getPosition());
            result.put("eventLog", curtain.getEventLog());
            auditService.log(String.format("User %s viewed smart curtains %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addCurtains(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            int position = body.containsKey("position") ? Integer.parseInt(body.get("position").toString()) : 0;
            SmartCurtains curtain = new SmartCurtains(id, name);
            curtain.setPosition(position);
            curtainsService.add(curtain, user_id);
            auditService.log(String.format("User %s added smart curtains %s", user_id, id));
            return ResponseEntity.ok("SmartCurtains added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCurtains(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            curtainsService.delete(id, user_id);
            auditService.log(String.format("User %s deleted smart curtains %s", user_id, id));
            return ResponseEntity.ok("SmartCurtains deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggleCurtains(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            curtainsService.toggleCurtains(id, user_id);
            auditService.log(String.format("User %s toggled smart curtains %s", user_id, id));
            return ResponseEntity.ok("SmartCurtains toggled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling curtains: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/updatePosition")
    public ResponseEntity<String> updatePosition(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication auth) {
        try {
            if (body.containsKey("position")) {
                int pos = Integer.parseInt(body.get("position").toString());
                String user_id = auth.getName();
                curtainsService.updatePosition(id, pos, user_id);
                auditService.log(String.format("User %s updated position on smart curtains %s to %d", user_id, id, pos));
                return ResponseEntity.ok("Position updated");
            } else {
                return ResponseEntity.badRequest().body("Position value is required");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
