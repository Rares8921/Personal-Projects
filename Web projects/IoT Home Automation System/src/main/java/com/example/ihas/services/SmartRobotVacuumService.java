package com.example.ihas.services;

import com.example.ihas.dao.SmartRobotVacuumDAO;
import com.example.ihas.devices.SmartRobotVacuum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartRobotVacuumService {

    private final SmartRobotVacuumDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartRobotVacuumService(SmartRobotVacuumDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartRobotVacuum get(String id, String userId) { return dao.findById(id, userId); }
    public List<SmartRobotVacuum> getAll(String userId) { return dao.findAll(userId); }

    public void add(SmartRobotVacuum vacuum, String userId) {
        dao.save(vacuum, userId);
        updateTelemetry(vacuum);
    }

    public void delete(String id, String userId) { dao.delete(id, userId); }

    public void togglePower(String id, String userId) {
        SmartRobotVacuum v = dao.findById(id, userId);
        v.togglePower();
        dao.update(v, userId);
        updateTelemetry(v);
    }

    public void updateMode(String id, String mode, String userId) {
        SmartRobotVacuum v = dao.findById(id, userId);
        v.setMode(SmartRobotVacuum.CleanMode.valueOf(mode));
        dao.update(v, userId);
    }

    public void startCleaning(String id, String userId) {
        SmartRobotVacuum v = dao.findById(id, userId);
        v.startCleaning();
        dao.update(v, userId);
    }

    public void returnToBase(String id, String userId) {
        SmartRobotVacuum v = dao.findById(id, userId);
        v.returnToBase();
        dao.update(v, userId);
    }

    private void updateTelemetry(SmartRobotVacuum v) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("isOn", v.isOn());
            t.put("mode", v.getMode().name());
            t.put("battery", v.getBatteryLevel());
            tbService.updateTelemetry(baseUrl, v.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + v.getId());
        }
    }
}
