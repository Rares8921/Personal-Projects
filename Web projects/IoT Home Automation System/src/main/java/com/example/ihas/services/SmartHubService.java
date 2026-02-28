package com.example.ihas.services;

import com.example.ihas.dao.SmartHubDAO;
import com.example.ihas.devices.SmartHub;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SmartHubService {

    private final SmartHubDAO dao;

    public SmartHubService(SmartHubDAO _dao) {
        dao = _dao;
    }

    public SmartHub get(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartHub> getAll(String user_id) {
        return dao.findAll(user_id);
    }

    public void add(SmartHub hub, String user_id) {
        dao.save(hub, user_id);
    }

    public void update(SmartHub hub, String user_id) {
        dao.update(hub, user_id);
    }

    public void delete(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void togglePower(String id, String user_id) {
        SmartHub hub = dao.findById(id, user_id);
        hub.togglePower();
        dao.update(hub, user_id);
    }

}
