package com.example.ihas.controllers;

import com.example.ihas.devices.SmartAssistant;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assistant")
public class SmartAssistantController {

    private final SmartAssistantService assistantService;

    @Autowired
    private AuditService auditService;

    public SmartAssistantController(SmartAssistantService _service) {
        assistantService = _service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllAssistants(Authentication auth) {
        String user_id = auth.getName();
        List<SmartAssistant> list = assistantService.getAll(user_id);
        List<Map<String, Object>> response = list.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("name", a.getName());
            m.put("hubId", a.getHubId());
            m.put("eventLogSize", a.getEventLog().size());
            return m;
        }).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart assistants", user_id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAssistant(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartAssistant assistant = assistantService.get(id, user_id);
            Map<String, Object> result = new HashMap<>();
            result.put("id", assistant.getId());
            result.put("name", assistant.getName());
            result.put("hubId", assistant.getHubId());
            result.put("eventLog", assistant.getEventLog());
            auditService.log(String.format("User %s viewed smart assistant %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addAssistant(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            String hubId = body.get("hubId").toString();
            SmartAssistant assistant = new SmartAssistant(id, name, hubId);
            assistantService.add(assistant, user_id);
            auditService.log(String.format("User %s added smart assistant %s", user_id, id));
            return ResponseEntity.ok("SmartAssistant added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssistant(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            assistantService.delete(id, user_id);
            auditService.log(String.format("User %s deleted smart assistant %s", user_id, id));
            return ResponseEntity.ok("SmartAssistant deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<String> processCommand(@PathVariable String id,
                                                 @RequestParam String command,
                                                 Authentication auth) {
        try {
            String user_id = auth.getName();
            assistantService.processVoiceCommand(id, command, user_id);
            auditService.log(String.format("User %s sent command to smart assistant %s: %s", user_id, id, command));
            return ResponseEntity.ok("Command processed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing command: " + e.getMessage());
        }
    }
}
