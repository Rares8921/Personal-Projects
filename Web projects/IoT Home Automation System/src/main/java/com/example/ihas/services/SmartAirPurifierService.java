package com.example.ihas.services;

import com.example.ihas.dao.SmartAirPurifierDAO;
import com.example.ihas.devices.SmartAirPurifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartAirPurifierService {

    private final SmartAirPurifierDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartAirPurifierService(SmartAirPurifierDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartAirPurifier get(String id, String userId) { return dao.findById(id, userId); }
    public List<SmartAirPurifier> getAll(String userId) { return dao.findAll(userId); }

    public void add(SmartAirPurifier purifier, String userId) {
        dao.save(purifier, userId);
        updateTelemetry(purifier);
    }

    public void delete(String id, String userId) { dao.delete(id, userId); }

    public void togglePower(String id, String userId) {
        SmartAirPurifier p = dao.findById(id, userId);
        p.togglePower();
        dao.update(p, userId);
        updateTelemetry(p);
    }

    public void updateFanSpeed(String id, String speed, String userId) {
        SmartAirPurifier p = dao.findById(id, userId);
        p.setFanSpeed(SmartAirPurifier.FanSpeed.valueOf(speed));
        dao.update(p, userId);
    }

    private void updateTelemetry(SmartAirPurifier p) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("isOn", p.isOn());
            t.put("fanSpeed", p.getFanSpeed().name());
            t.put("aqi", p.getAirQualityIndex());
            t.put("filterLife", p.getFilterLife());
            tbService.updateTelemetry(baseUrl, p.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + p.getId());
        }
    }
}
