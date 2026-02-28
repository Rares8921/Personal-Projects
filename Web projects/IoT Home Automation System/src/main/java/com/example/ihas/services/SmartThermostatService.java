package com.example.ihas.services;

import com.example.ihas.dao.SmartThermostatDAO;
import com.example.ihas.devices.SmartThermostat;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmartThermostatService {

    private final SmartThermostatDAO thermostatDAO;
    private final ThingsBoardService thingsBoardService;
    private final String thingsBoardBaseUrl = "https://eu.thingsboard.cloud/api/v1/";

    public SmartThermostatService(SmartThermostatDAO dao, ThingsBoardService tbService) {
        thermostatDAO = dao;
        thingsBoardService = tbService;
    }

    public SmartThermostat getThermostat(String id, String user_id) {
        return thermostatDAO.findById(id, user_id);
    }

    public List<SmartThermostat> getAllThermostats(String user_id) {
        return thermostatDAO.findAllByUser(user_id);
    }

    public void addThermostat(SmartThermostat thermostat, String user_id) {
        thermostat.setUserId(user_id);
        thermostatDAO.save(thermostat, user_id);

        try {
            Map<String, Object> telemetry = new HashMap<>();
            telemetry.put("temperature", thermostat.getTemperature());
            telemetry.put("mode", thermostat.getMode().name());
            telemetry.put("isOn", thermostat.isOn());
            thingsBoardService.updateTelemetry(thingsBoardBaseUrl, thermostat.getId(), telemetry);
        } catch (Exception e) {
            System.err.println("ThingsBoard failed (add): " + e.getMessage());
        }
    }


    public void deleteThermostat(String id, String user_id) {
        thermostatDAO.delete(id, user_id);
    }

    public void updateTemperature(String id, double newTemp, String user_id) {
        SmartThermostat st = thermostatDAO.findById(id, user_id);
        st.setTemperature(newTemp);
        thermostatDAO.update(st, user_id);

        try {
            Map<String, Object> telemetry = new HashMap<>();
            telemetry.put("temperature", st.getTemperature());
            thingsBoardService.updateTelemetry(thingsBoardBaseUrl, st.getId(), telemetry);
        } catch (Exception e) {
            System.err.println("ThingsBoard failed (temp): " + e.getMessage());
        }
    }

    public void updateMode(String id, SmartThermostat.Mode mode, String user_id) {
        SmartThermostat st = thermostatDAO.findById(id, user_id);
        st.setMode(mode);
        thermostatDAO.update(st, user_id);

        try {
            Map<String, Object> telemetry = new HashMap<>();
            telemetry.put("mode", st.getMode().name());
            thingsBoardService.updateTelemetry(thingsBoardBaseUrl, st.getId(), telemetry);
        } catch (Exception e) {
            System.err.println("ThingsBoard failed (mode): " + e.getMessage());
        }
    }

    public void togglePower(String id, String user_id) {
        SmartThermostat st = thermostatDAO.findById(id, user_id);
        st.togglePower();
        thermostatDAO.update(st, user_id);

        try {
            Map<String, Object> telemetry = new HashMap<>();
            telemetry.put("isOn", st.isOn());
            thingsBoardService.updateTelemetry(thingsBoardBaseUrl, st.getId(), telemetry);
        } catch (Exception e) {
            System.err.println("ThingsBoard failed (toggle): " + e.getMessage());
        }
    }

    public void autoAdjust(String id, double ambientTemp, String user_id) {
        SmartThermostat st = thermostatDAO.findById(id, user_id);
        st.autoAdjustTemperature(ambientTemp);
        thermostatDAO.update(st, user_id);

        try {
            Map<String, Object> telemetry = new HashMap<>();
            telemetry.put("temperature", st.getTemperature());
            telemetry.put("mode", st.getMode().name());
            thingsBoardService.updateTelemetry(thingsBoardBaseUrl, st.getId(), telemetry);
        } catch (Exception e) {
            System.err.println("ThingsBoard failed (autoAdjust): " + e.getMessage());
        }
    }
}
