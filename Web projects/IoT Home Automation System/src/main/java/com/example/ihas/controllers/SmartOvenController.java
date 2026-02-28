package com.example.ihas.controllers;

import com.example.ihas.devices.SmartOven;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartOvenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ovens")
public class SmartOvenController {

    private final SmartOvenService ovenService;

    @Autowired
    private AuditService auditService;

    public SmartOvenController(SmartOvenService service) {
        ovenService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOvens(Authentication auth) {
        String user_id = auth.getName();
        List<SmartOven> list = ovenService.getAllOvens(user_id);
        List<Map<String, Object>> response = list.stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart ovens", user_id));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapping(SmartOven oven) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", oven.getId());
        m.put("name", oven.getName());
        m.put("isOn", oven.isOnline());
        m.put("temperature", oven.getTemperature());
        m.put("timer", oven.getTimer());
        m.put("preheat", oven.isPreheat());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOven(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartOven oven = ovenService.getOven(id, user_id);
            Map<String, Object> result = mapping(oven);
            result.put("eventLog", oven.getEventLog());
            auditService.log(String.format("User %s viewed smart oven %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addOven(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();

            SmartOven oven = new SmartOven(id, name);
            if (body.containsKey("temperature")) {
                oven.setTemperature(Double.parseDouble(body.get("temperature").toString()));
            }
            if (body.containsKey("timer")) {
                oven.setTimer(Integer.parseInt(body.get("timer").toString()));
            }
            if (body.containsKey("preheat")) {
                oven.setPreheat(Boolean.parseBoolean(body.get("preheat").toString()));
            }
            if (body.containsKey("isOn")) {
                boolean online = Boolean.parseBoolean(body.get("isOn").toString());
                if (online != oven.isOnline()) {
                    oven.togglePower();
                }
            }

            ovenService.addOven(oven, user_id);
            auditService.log(String.format("User %s added smart oven %s", user_id, id));
            return ResponseEntity.ok("Smart oven added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOven(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            ovenService.deleteOven(id, user_id);
            auditService.log(String.format("User %s deleted smart oven %s", user_id, id));
            return ResponseEntity.ok("SmartOven deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggleOven(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            ovenService.togglePower(id, user_id);
            auditService.log(String.format("User %s toggled power state on smart oven %s", user_id, id));
            return ResponseEntity.ok("SmartOven toggled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling oven: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/updateTemperature")
    public ResponseEntity<String> updateTemperature(@PathVariable String id, @RequestParam double temperature, Authentication auth) {
        try {
            String user_id = auth.getName();
            ovenService.updateTemperature(id, temperature, user_id);
            auditService.log(String.format("User %s updated temperature of smart oven %s to %.1f", user_id, id, temperature));
            return ResponseEntity.ok("Temperature updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating temperature: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/updateTimer")
    public ResponseEntity<String> updateTimer(@PathVariable String id, @RequestParam int timer, Authentication auth) {
        try {
            String user_id = auth.getName();
            ovenService.updateTimer(id, timer, user_id);
            auditService.log(String.format("User %s updated timer of smart oven %s to %d", user_id, id, timer));
            return ResponseEntity.ok("Timer updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating timer: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/setPreheat")
    public ResponseEntity<String> setPreheat(@PathVariable String id, @RequestParam boolean preheat, Authentication auth) {
        try {
            String user_id = auth.getName();
            ovenService.setPreheat(id, preheat, user_id);
            auditService.log(String.format("User %s set preheat of smart oven %s to %s", user_id, id, preheat));
            return ResponseEntity.ok("Preheat state updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error setting preheat: " + e.getMessage());
        }
    }
}
