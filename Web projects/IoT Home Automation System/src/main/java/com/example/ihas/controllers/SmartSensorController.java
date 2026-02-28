package com.example.ihas.controllers;

import com.example.ihas.devices.SmartSensor;
import com.example.ihas.services.AuditService;
import com.example.ihas.services.SmartSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sensors")
public class SmartSensorController {

    private final SmartSensorService sensorService;

    @Autowired
    private AuditService auditService;

    public SmartSensorController(SmartSensorService service) {
        sensorService = service;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllSensors(Authentication auth) {
        String user_id = auth.getName();
        List<SmartSensor> sensors = sensorService.getAllSensors(user_id);
        List<Map<String, Object>> response = sensors.stream().map(this::mapping).collect(Collectors.toList());
        auditService.log(String.format("User %s listed all smart sensors", user_id));
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> mapping(SmartSensor sensor) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", sensor.getId());
        m.put("name", sensor.getName());
        m.put("sensorType", sensor.getSensorType().name());
        m.put("lastReading", sensor.getLastReading());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSensor(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            SmartSensor sensor = sensorService.getSensor(id, user_id);
            Map<String, Object> result = mapping(sensor);
            result.put("eventLog", sensor.getEventLog());
            auditService.log(String.format("User %s viewed smart sensor %s", user_id, id));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addSensor(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            String id = body.get("id").toString();
            String name = body.get("name").toString();
            SmartSensor.SensorType sensorType = SmartSensor.SensorType.valueOf(body.get("sensorType").toString());
            SmartSensor sensor = new SmartSensor(id, name, sensorType);
            if (body.containsKey("lastReading")) {
                double reading = Double.parseDouble(body.get("lastReading").toString());
                sensor.updateReading(reading);
            }
            sensorService.addSensor(sensor, user_id);
            auditService.log(String.format("User %s added smart sensor %s of type %s", user_id, id, sensorType));
            return ResponseEntity.ok("Sensor added");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/updateReading")
    public ResponseEntity<String> updateReading(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication auth) {
        try {
            String user_id = auth.getName();
            double reading = Double.parseDouble(body.get("lastReading").toString());
            sensorService.updateReading(id, reading, user_id);
            auditService.log(String.format("User %s updated reading of smart sensor %s to %.2f", user_id, id, reading));
            return ResponseEntity.ok("Sensor reading updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSensor(@PathVariable String id, Authentication auth) {
        try {
            String user_id = auth.getName();
            sensorService.deleteSensor(id, user_id);
            auditService.log(String.format("User %s deleted smart sensor %s", user_id, id));
            return ResponseEntity.ok("Sensor deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
