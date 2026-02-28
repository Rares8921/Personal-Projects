package com.example.ihas.services;

import com.example.ihas.dao.SmartOvenDAO;
import com.example.ihas.devices.SmartOven;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartOvenService {

    private final SmartOvenDAO ovenDAO;
    private final ThingsBoardService thingsBoardService;
    private final String thingsBoardBaseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartOvenService(SmartOvenDAO dao, ThingsBoardService tbService) {
        ovenDAO = dao;
        thingsBoardService = tbService;
    }

    public SmartOven getOven(String id, String user_id) {
        return ovenDAO.findById(id, user_id);
    }

    public List<SmartOven> getAllOvens(String user_id) {
        return ovenDAO.findAll(user_id);
    }

    public void addOven(SmartOven oven, String user_id) {
        ovenDAO.save(oven, user_id);
        updateTelemetry(oven);
    }

    public void deleteOven(String id, String user_id) {
        ovenDAO.delete(id, user_id);
    }

    public void updateOven(SmartOven oven, String user_id) {
        ovenDAO.update(oven, user_id);
        updateTelemetry(oven);
    }

    public void toggleOven(String id, String user_id) {
        SmartOven oven = ovenDAO.findById(id, user_id);
        oven.togglePower();
        ovenDAO.update(oven, user_id);
        updateTelemetry(oven);
    }

    public void updateTemperature(String id, double temperature, String user_id) {
        SmartOven oven = ovenDAO.findById(id, user_id);
        oven.setTemperature(temperature);
        ovenDAO.update(oven, user_id);
        updateTelemetry(oven);
    }

    public void updateTimer(String id, int timer, String user_id) {
        SmartOven oven = ovenDAO.findById(id, user_id);
        oven.setTimer(timer);
        ovenDAO.update(oven, user_id);
        updateTelemetry(oven);
    }

    public void setPreheat(String id, boolean preheat, String user_id) {
        SmartOven oven = ovenDAO.findById(id, user_id);
        if (preheat) {
            oven.preheat();
        } else {
            oven.cancelPreheat();
        }
        ovenDAO.update(oven, user_id);
        updateTelemetry(oven);
    }

    public void togglePower(String id, String user_id) {
        SmartOven oven = ovenDAO.findById(id, user_id);
        oven.togglePower();
        ovenDAO.update(oven, user_id);
    }

    private void updateTelemetry(SmartOven oven) {
        try {
            Map<String, Object> telemetry = new HashMap<>();
            telemetry.put("isOn", oven.isOnline());
            telemetry.put("temperature", oven.getTemperature());
            telemetry.put("timer", oven.getTimer());
            telemetry.put("preheat", oven.isPreheat());
            thingsBoardService.updateTelemetry(thingsBoardBaseUrl, oven.getId(), telemetry);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + oven.getId());
        }
    }
}
