package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartPlug implements SmartDevice {
    private final String id;
    private final String name;

    @Getter
    private boolean isOn;
    @Getter
    @Setter
    private double currentConsumption; // calculat in wati
    @Getter
    private final List<String> eventLog;

    @Getter @Setter
    private String userId;

    public SmartPlug(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        currentConsumption = 0.0;
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
        // TODO: De modificat sa calculeze consumul real
        currentConsumption = isOn ? Math.round(Math.random() * 100.0 * 100.0) / 100.0 : 0.0;
    }

    @Override
    public String getStatus() {
        return String.format("SmartPlug[id=%s, name=%s, isOn=%s, consumption=%.2fW]",
                id, name, isOn, currentConsumption);
    }
}