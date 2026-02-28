package com.example.ihas.services;

import com.example.ihas.dao.SmartWasherDAO;
import com.example.ihas.devices.SmartWasher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartWasherService {

    private final SmartWasherDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartWasherService(SmartWasherDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartWasher get(String id, String userId) { return dao.findById(id, userId); }
    public List<SmartWasher> getAll(String userId) { return dao.findAll(userId); }

    public void add(SmartWasher washer, String userId) {
        dao.save(washer, userId);
        updateTelemetry(washer);
    }

    public void delete(String id, String userId) { dao.delete(id, userId); }

    public void togglePower(String id, String userId) {
        SmartWasher w = dao.findById(id, userId);
        w.togglePower();
        dao.update(w, userId);
        updateTelemetry(w);
    }

    public void updateMode(String id, String mode, String userId) {
        SmartWasher w = dao.findById(id, userId);
        w.setMode(SmartWasher.WashMode.valueOf(mode));
        dao.update(w, userId);
    }

    public void startCycle(String id, String userId) {
        SmartWasher w = dao.findById(id, userId);
        w.startCycle();
        dao.update(w, userId);
    }

    private void updateTelemetry(SmartWasher w) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("isOn", w.isOn());
            t.put("mode", w.getMode().name());
            t.put("temperature", w.getTemperature());
            tbService.updateTelemetry(baseUrl, w.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + w.getId());
        }
    }
}
