package com.example.ihas.controllers;

import com.example.ihas.devices.SmartCamera;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartCameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cameras")
public class SmartCameraController {

    private final SmartCameraService cameraService;

    @Autowired
    private AuditService auditService;

    public SmartCameraController(SmartCameraService service) {
        cameraService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCameras(Principal principal) {
        List<SmartCamera> list = cameraService.getAll(principal.getName());
        List<Map<String, Object>> response = list.stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart cameras", principal.getName()));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapping(SmartCamera cam) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", cam.getId());
        m.put("name", cam.getName());
        m.put("isRecording", cam.isRecording());
        m.put("resolution", cam.getStatus().contains("4K") ? "4K" : "1080p");
        m.put("detectionSensitivity", cam.getDetectionSensitivity());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCamera(@PathVariable String id, Principal principal) {
        try {
            SmartCamera cam = cameraService.get(id, principal.getName());
            Map<String, Object> result = mapping(cam);
            result.put("eventLog", cam.getEventLog());
            auditService.log(String.format("User %s viewed smart camera %s", principal.getName(), id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addCamera(@RequestBody Map<String, Object> body, Principal principal) {
        try {
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            String resolution = body.get("resolution").toString();
            SmartCamera cam = new SmartCamera(id, name, resolution);
            if (body.containsKey("detectionSensitivity")) {
                int sensitivity = Integer.parseInt(body.get("detectionSensitivity").toString());
                cam.setDetectionSensitivity(sensitivity);
            }
            cameraService.add(cam, principal.getName());
            auditService.log(String.format("User %s added smart camera %s", principal.getName(), id));
            return ResponseEntity.ok("Camera added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCamera(@PathVariable String id, Principal principal) {
        try {
            cameraService.delete(id, principal.getName());
            auditService.log(String.format("User %s deleted smart camera %s", principal.getName(), id));
            return ResponseEntity.ok("Camera deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<String> toggleCamera(@PathVariable String id, Principal principal) {
        try {
            cameraService.toggleRecording(id, principal.getName());
            auditService.log(String.format("User %s toggled recording on smart camera %s", principal.getName(), id));
            return ResponseEntity.ok("Camera recording toggled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error toggling camera: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/updateSensitivity")
    public ResponseEntity<String> updateSensitivity(@PathVariable String id, @RequestBody Map<String, Object> body, Principal principal) {
        try {
            if (body.containsKey("detectionSensitivity")) {
                int sensitivity = Integer.parseInt(body.get("detectionSensitivity").toString());
                cameraService.updateDetectionSensitivity(id, sensitivity, principal.getName());
                auditService.log(String.format("User %s updated detection sensitivity on smart camera %s to %d", principal.getName(), id, sensitivity));
                return ResponseEntity.ok("Detection sensitivity updated");
            } else {
                return ResponseEntity.badRequest().body("Detection sensitivity is required");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
