package com.example.ihas.services;

import com.example.ihas.dao.SmartSprinklerDAO;
import com.example.ihas.devices.SmartSprinkler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartSprinklerService {

    private final SmartSprinklerDAO dao;
    private final ThingsBoardService thingsBoardService;

    public SmartSprinklerService(SmartSprinklerDAO _dao, ThingsBoardService _thingsBoardService) {
        dao = _dao;
        thingsBoardService = _thingsBoardService;
    }

    public SmartSprinkler getSprinkler(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartSprinkler> getAllSprinklers(String user_id) {
        return dao.findAllByUser(user_id);
    }

    public void addSprinkler(SmartSprinkler sprinkler, String user_id) {
        dao.save(sprinkler, user_id);
    }

    public void deleteSprinkler(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void setDuration(String id, int duration, String user_id) {
        SmartSprinkler sprinkler = dao.findById(id, user_id);
        sprinkler.setWateringDuration(duration);
        dao.update(sprinkler, user_id);
    }

    public void togglePower(String id, String user_id) {
        SmartSprinkler sprinkler = dao.findById(id, user_id);
        sprinkler.togglePower();
        dao.update(sprinkler, user_id);
    }

    public void updateWateringDuration(String id, int duration, String user_id) {
        SmartSprinkler sprinkler = dao.findById(id, user_id);
        sprinkler.setWateringDuration(duration);
        dao.update(sprinkler, user_id);
        try {
            Map<String, Object> telemetry = new HashMap<>();
            telemetry.put("wateringDuration", duration);
            thingsBoardService.updateTelemetry("https://eu.thingsboard.cloud/api/v1/", id, telemetry);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + sprinkler.getId());
        }
    }

}
