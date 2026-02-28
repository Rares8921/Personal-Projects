package com.example.ihas.services;

import com.example.ihas.dao.SmartSensorDAO;
import com.example.ihas.devices.SmartSensor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmartSensorService {

    private final SmartSensorDAO dao;

    public SmartSensorService(SmartSensorDAO _dao) {
        dao = _dao;
    }

    public SmartSensor getSensor(String id, String user_id) {
        return dao.findById(id, user_id);
    }

    public List<SmartSensor> getAllSensors(String user_id) {
        return dao.findAllByUser(user_id);
    }

    public void addSensor(SmartSensor sensor, String user_id) {
        dao.save(sensor, user_id);
    }

    public void deleteSensor(String id, String user_id) {
        dao.delete(id, user_id);
    }

    public void updateReading(String id, double reading, String user_id) {
        SmartSensor sensor = dao.findById(id, user_id);
        sensor.setLastReading(reading);
        dao.update(sensor, user_id);
    }
}
