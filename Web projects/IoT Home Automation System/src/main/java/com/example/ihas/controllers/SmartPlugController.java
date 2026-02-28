package com.example.ihas.controllers;

import com.example.ihas.devices.SmartPlug;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartPlugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/smartplugs")
public class SmartPlugController {

    private final SmartPlugService plugService;

    @Autowired
    private AuditService auditService;

    public SmartPlugController(SmartPlugService service) {
        plugService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllSmartPlugs(Authentication auth) {
        String user_id = auth.getName();
        List<SmartPlug> list = plugService.getAllSmartPlugs(user_id);
        List<Map<String, Object>> response = list.stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart plugs", user_id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSmartPlug(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartPlug plug = plugService.getSmartPlug(id, user_id);
            auditService.log(String.format("User %s viewed smart plug %s", user_id, id));
            return ResponseEntity.ok(mapping(plug));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Map<String, Object> mapping(SmartPlug plug) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", plug.getId());
        result.put("name", plug.getName());
        result.put("isOn", plug.isOn());
        result.put("currentConsumption", plug.getCurrentConsumption());
        result.put("eventLog", plug.getEventLog());
        return result;
    }

    @PostMapping
    public ResponseEntity<String> addSmartPlug(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            SmartPlug plug = new SmartPlug(id, name);
            plugService.addSmartPlug(plug, user_id);
            auditService.log(String.format("User %s added smart plug %s", user_id, id));
            return ResponseEntity.ok("SmartPlug added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSmartPlug(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            plugService.deleteSmartPlug(id, user_id);
            auditService.log(String.format("User %s deleted smart plug %s", user_id, id));
            return ResponseEntity.ok("SmartPlug deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<?> toggleSmartPlug(@PathVariable String id, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }

        try {
            String user_id = auth.getName();
            plugService.toggleSmartPlug(id, user_id);
            SmartPlug updated = plugService.getSmartPlug(id, user_id);
            auditService.log(String.format("User %s toggled smart plug %s", user_id, id));
            return ResponseEntity.ok(mapping(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling SmartPlug: " + e.getMessage());
        }
    }

}
