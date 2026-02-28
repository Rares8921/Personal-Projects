package com.example.ihas.devices;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class SmartCurtains implements SmartDevice {
    private final String id;
    private final String name;
    // 0 = complet inchise, 100 = complet deschise
    @Getter
    private int position;
    @Getter
    private final List<String> eventLog;

    public SmartCurtains(String _id, String _name) {
        id = _id;
        name = _name;
        position = 0;
        eventLog = new ArrayList<>();
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isOnline() { return true; }

    // Toggle va deschide sau inchide complet
    @Override
    public void togglePower() {
        if (position == 0) {
            setPosition(100);
        } else {
            setPosition(0);
        }
    }

    public void setPosition(int _position) {
        if (_position < 0 || _position > 100)
            throw new IllegalArgumentException("Position must be between 0 and 100");
        position = _position;
        eventLog.add("Position set to " + _position);
        System.out.println(name + " position set to " + _position);
    }

    @Override
    public String getStatus() {
        return String.format("SmartCurtains[id=%s, name=%s, position=%d]", id, name, position);
    }
}
