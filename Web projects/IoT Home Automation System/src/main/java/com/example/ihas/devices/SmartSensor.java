package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartSensor implements SmartDevice {
    public enum SensorType {
        MOTION, TEMPERATURE, HUMIDITY, SMOKE, LIGHT, GAS, WATERLEAK, DOOR, WINDOW, SOUND
    }

    private final String id;
    private final String name;
    private final SensorType type;
    @Getter @Setter
    private double lastReading;
    @Getter
    private final List<String> eventLog;

    public SmartSensor(String _id, String _name, SensorType _type) {
        id = _id;
        name = _name;
        type = _type;
        lastReading = 0.0;
        eventLog = new ArrayList<>();
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    public SensorType getSensorType() { return type; }

    @Override
    public boolean isOnline() { return true; }

    // TODO: de revizuit functionalitatea
    // Pentru senzori, togglePower poate fi inexistent
    @Override
    public void togglePower() {
        eventLog.add("Toggle power not supported for sensor.");
    }

    public void updateReading(double reading) {
        lastReading = reading;
        eventLog.add("Sensor reading updated to " + reading);
    }

    @Override
    public String getStatus() {
        return String.format("SmartSensor[id=%s, name=%s, type=%s, lastReading=%.2f]",
                id, name, type, lastReading);
    }
}

