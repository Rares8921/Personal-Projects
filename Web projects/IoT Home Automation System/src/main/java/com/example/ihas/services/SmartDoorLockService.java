package com.example.ihas.services;

import com.example.ihas.dao.SmartDoorLockDAO;
import com.example.ihas.devices.SmartDoorLock;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartDoorLockService {

    private final SmartDoorLockDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartDoorLockService(SmartDoorLockDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartDoorLock get(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartDoorLock> getAll(String user_id) {
        return dao.findAll(user_id);
    }

    public void add(SmartDoorLock lock, String user_id) {
        dao.save(lock, user_id);
        updateTelemetry(lock);
    }

    public void update(SmartDoorLock lock, String user_id) {
        dao.update(lock, user_id);
        updateTelemetry(lock);
    }

    public void delete(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void toggleDoorLock(String id, String user_id) {
        SmartDoorLock lock = dao.findById(id, user_id);
        lock.togglePower();
        dao.update(lock, user_id);
    }


    private void updateTelemetry(SmartDoorLock l) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("locked", l.isLocked());
            tbService.updateTelemetry(baseUrl, l.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + l.getId());
        }
    }
}
