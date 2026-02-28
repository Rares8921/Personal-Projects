package com.example.ihas.services;

import com.example.ihas.dao.SmartCurtainsDAO;
import com.example.ihas.devices.SmartCurtains;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartCurtainsService {

    private final SmartCurtainsDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartCurtainsService(SmartCurtainsDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartCurtains get(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartCurtains> getAll(String user_id) {
        return dao.findAll(user_id);
    }

    public void add(SmartCurtains curtains, String user_id) {
        dao.save(curtains, user_id);
        updateTelemetry(curtains);
    }

    public void update(SmartCurtains curtains, String user_id) {
        dao.update(curtains, user_id);
        updateTelemetry(curtains);
    }

    public void delete(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void toggleCurtains(String id, String user_id) {
        SmartCurtains curtains = dao.findById(id, user_id);
        curtains.togglePower();
        dao.update(curtains, user_id);
    }

    public void updatePosition(String id, int position, String user_id) {
        SmartCurtains curtains = dao.findById(id, user_id);
        curtains.setPosition(position);
        dao.update(curtains, user_id);
    }


    private void updateTelemetry(SmartCurtains c) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("position", c.getPosition());
            tbService.updateTelemetry(baseUrl, c.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + c.getId());
        }
    }

}
