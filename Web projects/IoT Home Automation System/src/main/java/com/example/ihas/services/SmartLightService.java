package com.example.ihas.services;

import com.example.ihas.dao.SmartLightDAO;
import com.example.ihas.devices.SmartLight;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartLightService {

    private final SmartLightDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartLightService(SmartLightDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartLight get(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartLight> getAll(String user_id) {
        return dao.findAll(user_id);
    }

    public void add(SmartLight light, String user_id) {
        dao.save(light, user_id);
        updateTelemetry(light);
    }

    public void update(SmartLight light, String user_id) {
        dao.update(light, user_id);
        updateTelemetry(light);
    }

    public void delete(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void togglePower(String id, String user_id) {
        SmartLight light = dao.findById(id, user_id);
        light.togglePower();
        dao.update(light, user_id);
    }

    public void updateBrightness(String id, int brightness, String user_id) {
        SmartLight light = dao.findById(id, user_id);
        light.setBrightness(brightness);
        dao.update(light, user_id);
    }

    public void updateColor(String id, String color, String user_id) {
        SmartLight light = dao.findById(id, user_id);
        light.setColor(color);
        dao.update(light, user_id);
    }


    private void updateTelemetry(SmartLight l) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("isOn", l.isOn());
            t.put("brightness", l.getBrightness());
            t.put("color", l.getColor());
            tbService.updateTelemetry(baseUrl, l.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + l.getId());
        }
    }
}
