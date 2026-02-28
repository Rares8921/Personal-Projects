package com.example.ihas.services;

import com.example.ihas.dao.SmartCarChargerDAO;
import com.example.ihas.devices.SmartCarCharger;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartCarChargerService {

    private final SmartCarChargerDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartCarChargerService(SmartCarChargerDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartCarCharger get(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartCarCharger> getAll(String user_id) {
        return dao.findAll(user_id);
    }

    public void add(SmartCarCharger charger, String user_id) {
        dao.save(charger, user_id);
        updateTelemetry(charger);
    }

    public void update(SmartCarCharger charger, String user_id) {
        dao.update(charger, user_id);
        updateTelemetry(charger);
    }

    public void delete(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void toggleCharging(String id, String user_id) {
        SmartCarCharger charger = dao.findById(id, user_id);
        charger.togglePower();
        dao.update(charger, user_id);
    }


    private void updateTelemetry(SmartCarCharger ch) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("isCharging", ch.isCharging());
            t.put("current", ch.getCurrent());
            t.put("voltage", ch.getVoltage());
            tbService.updateTelemetry(baseUrl, ch.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + ch.getId());
        }
    }
}
