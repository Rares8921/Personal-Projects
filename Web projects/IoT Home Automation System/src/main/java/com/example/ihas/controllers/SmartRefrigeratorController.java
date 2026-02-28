package com.example.ihas.controllers;

import com.example.ihas.devices.SmartRefrigerator;
import com.example.ihas.devices.SmartRefrigeratorItem;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartRefrigeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/refrigerators")
public class SmartRefrigeratorController {

    private final SmartRefrigeratorService refrigeratorService;

    @Autowired
    private AuditService auditService;

    public SmartRefrigeratorController(SmartRefrigeratorService service) {
        refrigeratorService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllRefrigerators(Authentication auth) {
        String user_id = auth.getName();
        List<SmartRefrigerator> list = refrigeratorService.getAllRefrigerators(user_id);
        List<Map<String, Object>> response = list.stream().map(ref -> {
            Map<String, Object> m = mapping(ref);
            m.put("inventorySize", ref.getInventory().size());
            return m;
        }).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart refrigerators", user_id));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapping(SmartRefrigerator ref) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ref.getId());
        m.put("name", ref.getName());
        m.put("temperature", ref.getTemperature());
        m.put("doorOpen", ref.isDoorOpen());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRefrigerator(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartRefrigerator ref = refrigeratorService.getRefrigerator(id, user_id);
            Map<String, Object> result = mapping(ref);
            result.put("inventory", ref.getInventory());
            result.put("eventLog", ref.getEventLog());
            auditService.log(String.format("User %s viewed smart refrigerator %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addRefrigerator(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            SmartRefrigerator ref = new SmartRefrigerator(id, name);
            if (body.containsKey("temperature")) {
                double t = Double.parseDouble(body.get("temperature").toString());
                ref.setTemperature(t);
            }
            refrigeratorService.addRefrigerator(ref, user_id);
            auditService.log(String.format("User %s added smart refrigerator %s", user_id, id));
            return ResponseEntity.ok("Refrigerator added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRefrigerator(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            refrigeratorService.deleteRefrigerator(id, user_id);
            auditService.log(String.format("User %s deleted smart refrigerator %s", user_id, id));
            return ResponseEntity.ok("Refrigerator deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/updateTemperature")
    public ResponseEntity<String> updateTemperature(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication auth) {
        try {
            if (body.containsKey("temperature")) {
                double t = Double.parseDouble(body.get("temperature").toString());
                String user_id = auth.getName();
                refrigeratorService.updateTemperature(id, t, user_id);
                auditService.log(String.format("User %s updated temperature of smart refrigerator %s to %.1f", user_id, id, t));
                return ResponseEntity.ok("Temperature updated");
            } else {
                return ResponseEntity.badRequest().body("Temperature value is required");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggleDoor")
    public ResponseEntity<String> toggleDoor(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            refrigeratorService.toggleDoor(id, user_id);
            auditService.log(String.format("User %s toggled door state on smart refrigerator %s", user_id, id));
            return ResponseEntity.ok("Door toggled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling door: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/items")
    public List<SmartRefrigeratorItem> getItems(@PathVariable String id) {
        return refrigeratorService.getItems(id);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<String> addItem(@PathVariable String id, @RequestBody Map<String, String> payload, Authentication auth) {
        try {
            String user_id = auth.getName();
            String name = payload.get("name");
            LocalDate expiryDate = LocalDate.parse(payload.get("expiryDate"));
            refrigeratorService.addItem(id, name, expiryDate);
            auditService.log(String.format("User %s added item %s with expiry %s to smart refrigerator %s", user_id, name, expiryDate, id));
            return ResponseEntity.ok("Item added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding item: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable String id, @PathVariable Long itemId, Authentication auth) {
        try {
            refrigeratorService.deleteItem(itemId);
            auditService.log(String.format("User %s deleted item %d from smart refrigerator %s", auth.getName(), itemId, id));
            return ResponseEntity.ok("Item deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting item: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/items/expired")
    public ResponseEntity<String> deleteExpired(@PathVariable String id, Authentication auth) {
        try {
            refrigeratorService.deleteExpiredItems(id);
            auditService.log(String.format("User %s deleted expired items from smart refrigerator %s", auth.getName(), id));
            return ResponseEntity.ok("Expired items removed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting expired items: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/checkExpired")
    public ResponseEntity<String> checkExpired(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            String result = refrigeratorService.checkExpiredItems(id, user_id);
            auditService.log(String.format("User %s checked for expired items in smart refrigerator %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking expired items: " + e.getMessage());
        }
    }
}
