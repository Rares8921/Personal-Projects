package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartWasher implements SmartDevice {
    public enum WashMode { NORMAL, DELICATE, HEAVY, QUICK, ECO }

    private final String id;
    private final String name;

    @Getter
    private boolean isOn;
    @Getter
    private WashMode mode;
    @Getter @Setter
    private int temperature; // wash temperature in °C
    @Getter @Setter
    private int spinSpeed; // RPM
    @Getter @Setter
    private int remainingMinutes;
    @Getter
    private final List<String> eventLog;

    @Getter @Setter
    private String userId;

    public SmartWasher(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        mode = WashMode.NORMAL;
        temperature = 40;
        spinSpeed = 1000;
        remainingMinutes = 0;
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
        if (!isOn) remainingMinutes = 0;
    }

    public void setMode(WashMode _mode) {
        mode = _mode;
        eventLog.add("Wash mode set to " + _mode);
    }

    public void startCycle() {
        if (!isOn) throw new IllegalStateException("Washer must be turned on first");
        remainingMinutes = switch (mode) {
            case QUICK -> 30;
            case DELICATE -> 45;
            case NORMAL -> 60;
            case HEAVY -> 90;
            case ECO -> 120;
        };
        eventLog.add("Wash cycle started: " + mode + " (" + remainingMinutes + " min)");
    }

    @Override
    public String getStatus() {
        return String.format("SmartWasher[id=%s, name=%s, isOn=%s, mode=%s, temp=%d°C, spin=%dRPM, remaining=%dmin]",
                id, name, isOn, mode, temperature, spinSpeed, remainingMinutes);
    }
}
