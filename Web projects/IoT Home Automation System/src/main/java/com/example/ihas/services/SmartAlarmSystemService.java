package com.example.ihas.services;

import com.example.ihas.dao.SmartAlarmSystemDAO;
import com.example.ihas.devices.SmartAlarmSystem;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartAlarmSystemService {

    private final SmartAlarmSystemDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartAlarmSystemService(SmartAlarmSystemDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartAlarmSystem get(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartAlarmSystem> getAll(String user_id) {
        return dao.findAll(user_id);
    }

    public void add(SmartAlarmSystem alarm, String user_id) {
        dao.save(alarm, user_id);
        updateTelemetry(alarm);
    }

    public void update(SmartAlarmSystem alarm, String user_id) {
        dao.update(alarm, user_id);
        updateTelemetry(alarm);
    }

    public void delete(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void armAlarmSystem(String id, String user_id) {
        SmartAlarmSystem alarm = get(id, user_id);
        alarm.arm();
        dao.update(alarm, user_id);
    }

    public void disarmAlarmSystem(String id, String user_id) {
        SmartAlarmSystem alarm = get(id, user_id);
        alarm.disarm();
        dao.update(alarm, user_id);
    }

    public void triggerAlarm(String id, String user_id) {
        SmartAlarmSystem alarm = get(id, user_id);
        alarm.triggerAlarm();
        dao.update(alarm, user_id);
    }

    private void updateTelemetry(SmartAlarmSystem a) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("isArmed", a.isArmed());
            t.put("alarmTriggered", a.isAlarmTriggered());
            tbService.updateTelemetry(baseUrl, a.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + a.getId());
        }
    }
}
