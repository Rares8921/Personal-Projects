package com.example.ihas.controllers;

import com.example.ihas.devices.SmartTV;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartTVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tv")
public class SmartTVController {

    private final SmartTVService tvService;
    @Autowired private AuditService auditService;

    public SmartTVController(SmartTVService service) { tvService = service; }

    private Map<String, Object> mapping(SmartTV tv) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", tv.getId());
        m.put("name", tv.getName());
        m.put("isOn", tv.isOn());
        m.put("volume", tv.getVolume());
        m.put("channel", tv.getChannel());
        m.put("inputSource", tv.getInputSource());
        return m;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(Authentication auth) {
        String userId = auth.getName();
        List<Map<String, Object>> response = tvService.getAll(userId).stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart TVs", userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String id, Authentication auth) {
        try {
            String userId = auth.getName();
            SmartTV tv = tvService.get(id, userId);
            Map<String, Object> result = mapping(tv);
            result.put("eventLog", tv.getEventLog());
            return ResponseEntity.ok(result);
        } catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String userId = auth.getName();
            SmartTV tv = new SmartTV(body.get("id").toString(), body.get("name").toString());
            tvService.add(tv, userId);
            auditService.log(String.format("User %s added smart TV %s", userId, tv.getId()));
            return ResponseEntity.ok("SmartTV added");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Authentication auth) {
        try {
            tvService.delete(id, auth.getName());
            return ResponseEntity.ok("SmartTV deleted");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggle(@PathVariable String id, Authentication auth) {
        try {
            tvService.togglePower(id, auth.getName());
            return ResponseEntity.ok("SmartTV toggled");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/volume")
    public ResponseEntity<String> updateVolume(@PathVariable String id, @RequestParam int volume, Authentication auth) {
        try {
            tvService.updateVolume(id, volume, auth.getName());
            return ResponseEntity.ok("Volume updated");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/channel")
    public ResponseEntity<String> updateChannel(@PathVariable String id, @RequestParam String channel, Authentication auth) {
        try {
            tvService.updateChannel(id, channel, auth.getName());
            return ResponseEntity.ok("Channel updated");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }

    @PostMapping("/{id}/input")
    public ResponseEntity<String> updateInput(@PathVariable String id, @RequestParam String input, Authentication auth) {
        try {
            tvService.updateInputSource(id, input, auth.getName());
            return ResponseEntity.ok("Input source updated");
        } catch (Exception e) { return ResponseEntity.badRequest().body("Error: " + e.getMessage()); }
    }
}
