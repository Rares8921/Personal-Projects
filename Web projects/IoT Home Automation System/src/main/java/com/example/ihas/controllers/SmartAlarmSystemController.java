package com.example.ihas.controllers;

import com.example.ihas.devices.SmartAlarmSystem;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartAlarmSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alarm")
public class SmartAlarmSystemController {

    private final SmartAlarmSystemService alarmService;

    @Autowired
    private AuditService auditService;

    public SmartAlarmSystemController(SmartAlarmSystemService service) {
        alarmService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllAlarmSystems(Authentication auth) {
        String user_id = auth.getName();
        List<SmartAlarmSystem> list = alarmService.getAll(user_id);
        List<Map<String, Object>> response = list.stream().map(this::mapping).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapping(SmartAlarmSystem alarm) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", alarm.getId());
        m.put("name", alarm.getName());
        m.put("isArmed", alarm.isArmed());
        m.put("alarmTriggered", alarm.isAlarmTriggered());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAlarmSystem(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartAlarmSystem alarm = alarmService.get(id, user_id);
            Map<String, Object> result = mapping(alarm);
            result.put("eventLog", alarm.getEventLog());
            auditService.log(String.format("User %s viewed alarm system %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addAlarmSystem(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            String user_id = auth.getName();
            SmartAlarmSystem alarm = new SmartAlarmSystem(id, name);
            alarmService.add(alarm, user_id);
            auditService.log(String.format("User %s added alarm system %s", user_id, id));
            return ResponseEntity.ok("Alarm system added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlarmSystem(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            alarmService.delete(id, user_id);
            auditService.log(String.format("User %s deleted alarm system %s", user_id, id));
            return ResponseEntity.ok("Alarm system deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/arm")
    public ResponseEntity<String> armAlarm(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            alarmService.armAlarmSystem(id, user_id);
            auditService.log(String.format("User %s armed alarm system %s", user_id, id));
            return ResponseEntity.ok("Alarm armed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error arming alarm: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/disarm")
    public ResponseEntity<String> disarmAlarm(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            alarmService.disarmAlarmSystem(id, user_id);
            auditService.log(String.format("User %s disarmed alarm system %s", user_id, id));
            return ResponseEntity.ok("Alarm disarmed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error disarming alarm: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/trigger")
    public ResponseEntity<String> triggerAlarm(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            alarmService.triggerAlarm(id, user_id);
            auditService.log(String.format("User %s triggered alarm system %s", user_id, id));
            return ResponseEntity.ok("Alarm triggered");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering alarm: " + e.getMessage());
        }
    }
}
