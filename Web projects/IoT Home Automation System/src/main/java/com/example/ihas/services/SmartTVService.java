package com.example.ihas.services;

import com.example.ihas.dao.SmartTVDAO;
import com.example.ihas.devices.SmartTV;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartTVService {

    private final SmartTVDAO dao;
    private final ThingsBoardService tbService;
    private final String baseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartTVService(SmartTVDAO _dao, ThingsBoardService _tbService) {
        dao = _dao;
        tbService = _tbService;
    }

    public SmartTV get(String id, String userId) { return dao.findById(id, userId); }
    public List<SmartTV> getAll(String userId) { return dao.findAll(userId); }

    public void add(SmartTV tv, String userId) {
        dao.save(tv, userId);
        updateTelemetry(tv);
    }

    public void delete(String id, String userId) { dao.delete(id, userId); }

    public void togglePower(String id, String userId) {
        SmartTV tv = dao.findById(id, userId);
        tv.togglePower();
        dao.update(tv, userId);
        updateTelemetry(tv);
    }

    public void updateVolume(String id, int volume, String userId) {
        SmartTV tv = dao.findById(id, userId);
        tv.setVolume(volume);
        dao.update(tv, userId);
    }

    public void updateChannel(String id, String channel, String userId) {
        SmartTV tv = dao.findById(id, userId);
        tv.setChannel(channel);
        dao.update(tv, userId);
    }

    public void updateInputSource(String id, String input, String userId) {
        SmartTV tv = dao.findById(id, userId);
        tv.setInputSource(input);
        dao.update(tv, userId);
    }

    private void updateTelemetry(SmartTV tv) {
        try {
            Map<String, Object> t = new HashMap<>();
            t.put("isOn", tv.isOn());
            t.put("volume", tv.getVolume());
            t.put("channel", tv.getChannel());
            tbService.updateTelemetry(baseUrl, tv.getId(), t);
        } catch (Exception ignored) {
            System.err.println("Failed to update telemetry for " + tv.getId());
        }
    }
}
