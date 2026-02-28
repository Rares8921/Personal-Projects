package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.config.annotation.web.SecurityMarker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class SmartOven implements SmartDevice {
    private final String id;
    private final String name;
    private boolean isOn;

    @Getter
    private double temperature;
    @Getter
    private int timer; // minute
    @Getter @Setter
    private boolean preheat;

    @Getter
    private final List<String> eventLog;

    public SmartOven(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        temperature = 0;
        timer = 0;
        preheat = false;
        eventLog = new ArrayList<>();
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isOnline() { return isOn; }

    @Override
    public void togglePower() {
        isOn = !isOn;
        eventLog.add("Oven power toggled: " + (isOn ? "ON" : "OFF"));
    }

    public void setTemperature(double _temperature) {
        if (_temperature < 50 || _temperature > 300)
            throw new IllegalArgumentException("Temperature must be between 50°C and 300°C");
        temperature = _temperature;
        eventLog.add("Temperature set to " + _temperature);
    }

    public void setTimer(int minutes) {
        if (minutes < 0)
            throw new IllegalArgumentException("Timer cannot be negative");
        timer = minutes;
        eventLog.add("Timer set to " + minutes + " minutes");
    }

    public void preheat() {
        preheat = true;
        eventLog.add("Preheat activated at " + LocalDateTime.now());
    }

    public void cancelPreheat() {
        preheat = false;
        eventLog.add("Preheat cancelled at " + LocalDateTime.now());
    }

    @Override
    public String getStatus() {
        return String.format("SmartOven[id=%s, name=%s, isOn=%s, temperature=%.1f, timer=%d, preheat=%s]",
                id, name, isOn, temperature, timer, preheat);
    }
}

