package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartThermostat implements SmartDevice {
    public enum Mode { COOL, HEAT, AUTO, OFF }

    private final String id;
    private final String name;

    @Getter
    private boolean isOn;

    @Getter
    private double temperature;

    @Getter
    private Mode mode;

    @Getter
    private final List<String> eventLog;

    @Getter @Setter
    private String userId;

    public SmartThermostat(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        temperature = 22.0;
        mode = Mode.AUTO;
        eventLog = new ArrayList<>();
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isOnline() { return true; }

    @Override
    public void togglePower() {
        isOn = !isOn;
        eventLog.add("Power toggled: " + (isOn ? "ON" : "OFF"));
    }

    public void setTemperature(double _temperature) {
        if (_temperature < 15.0 || _temperature > 30.0)
            throw new IllegalArgumentException("Temperature must be between 15°C and 30°C");
        temperature = _temperature;
        eventLog.add("Temperature set to " + _temperature);
    }

    public void setMode(Mode _mode) {
        mode = _mode;
        eventLog.add("Mode set to " + _mode);
    }

    public void autoAdjustTemperature(double ambientTemp) {
        double newTemp = (ambientTemp + temperature) / 2;
        setTemperature(newTemp);
        eventLog.add("Auto-adjusted temperature based on ambient value " + ambientTemp);
    }

    @Override
    public String getStatus() {
        return String.format("SmartThermostat[id=%s, name=%s, isOn=%s, temperature=%.1f, mode=%s]",
                id, name, isOn, temperature, mode);
    }
}
