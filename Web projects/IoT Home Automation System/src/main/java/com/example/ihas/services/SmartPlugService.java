package com.example.ihas.services;

import com.example.ihas.dao.SmartPlugDAO;
import com.example.ihas.devices.SmartPlug;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartPlugService {

    private final SmartPlugDAO plugDAO;
    private final ThingsBoardService thingsBoardService;
    private final String thingsBoardBaseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartPlugService(SmartPlugDAO dao, ThingsBoardService tbService) {
        plugDAO = dao;
        thingsBoardService = tbService;
    }

    public SmartPlug getSmartPlug(String id, String user_id) {
        return plugDAO.findById(id, user_id);
    }

    public List<SmartPlug> getAllSmartPlugs(String user_id) {
        return plugDAO.findAllByUser(user_id);
    }

    public void addSmartPlug(SmartPlug plug, String user_id) {
        plug.setUserId(user_id);
        plugDAO.save(plug, user_id);
        updateTelemetry(plug);
    }

    public void deleteSmartPlug(String id, String user_id) {
        plugDAO.delete(id, user_id);
    }

    public void toggleSmartPlug(String id, String user_id) {
        SmartPlug plug = plugDAO.findById(id, user_id);
        plug.togglePower();
        plugDAO.update(plug, user_id);
        updateTelemetry(plug);
    }

    public void updateSmartPlug(SmartPlug plug, String user_id) {
        plugDAO.update(plug, user_id);
        updateTelemetry(plug);
    }

    private void updateTelemetry(SmartPlug plug) {
        Map<String, Object> telemetry = new HashMap<>();
        telemetry.put("isOn", plug.isOn());
        telemetry.put("currentConsumption", plug.getCurrentConsumption());

        try {
            thingsBoardService.updateTelemetry(thingsBoardBaseUrl, plug.getId(), telemetry);
        } catch (Exception e) {
            System.err.println("ThingsBoard telemetry failed: " + e.getMessage());
        }
    }

}
