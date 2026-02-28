package com.example.ihas.services;

import com.example.ihas.dao.SmartGarageDoorDAO;
import com.example.ihas.devices.SmartGarageDoor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartGarageDoorService {

    private final SmartGarageDoorDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartGarageDoorService(SmartGarageDoorDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartGarageDoor get(String id, String userId) { return dao.findById(id, userId); }
    public List<SmartGarageDoor> getAll(String userId) { return dao.findAll(userId); }

    public void add(SmartGarageDoor door, String userId) {
        dao.save(door, userId);
        updateTelemetry(door);
    }

    public void delete(String id, String userId) { dao.delete(id, userId); }

    public void toggleDoor(String id, String userId) {
        SmartGarageDoor d = dao.findById(id, userId);
        d.togglePower();
        dao.update(d, userId);
        updateTelemetry(d);
    }

    public void openDoor(String id, String userId) {
        SmartGarageDoor d = dao.findById(id, userId);
        d.open();
        dao.update(d, userId);
    }

    public void closeDoor(String id, String userId) {
        SmartGarageDoor d = dao.findById(id, userId);
        d.close();
        dao.update(d, userId);
    }

    private void updateTelemetry(SmartGarageDoor d) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("state", d.getDoorState().name());
            t.put("autoClose", d.isAutoClose());
            tbService.updateTelemetry(baseUrl, d.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + d.getId());
        }
    }
}
