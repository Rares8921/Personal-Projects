package com.example.ihas.devices;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class SmartSprinkler implements SmartDevice {
    private final String id;
    private final String name;

    @Getter
    private boolean isOn;
    @Getter
    private int wateringDuration; // minute
    @Getter
    private final List<String> eventLog;

    public SmartSprinkler(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        wateringDuration = 10;
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
        eventLog.add("Sprinkler toggled: " + (isOn ? "ON" : "OFF"));
        System.out.println(name + " is now " + (isOn ? "ON" : "OFF"));
    }

    public void setWateringDuration(int minutes) {
        if (minutes <= 0)
            throw new IllegalArgumentException("Duration must be positive");
        wateringDuration = minutes;
        eventLog.add("Watering duration set to " + minutes + " minutes");
    }

    @Override
    public String getStatus() {
        return String.format("SmartSprinkler[id=%s, name=%s, isOn=%s, duration=%d mins]",
                id, name, isOn, wateringDuration);
    }
}

