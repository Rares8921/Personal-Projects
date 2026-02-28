package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartRobotVacuum implements SmartDevice {
    public enum CleanMode { AUTO, SPOT, EDGE, TURBO, QUIET }
    public enum VacuumStatus { IDLE, CLEANING, RETURNING, CHARGING, ERROR }

    private final String id;
    private final String name;

    @Getter
    private boolean isOn;
    @Getter
    private CleanMode mode;
    @Getter
    private VacuumStatus vacuumStatus;
    @Getter @Setter
    private int batteryLevel; // 0-100
    @Getter @Setter
    private int cleanedArea; // in square meters
    @Getter
    private final List<String> eventLog;

    @Getter @Setter
    private String userId;

    public SmartRobotVacuum(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        mode = CleanMode.AUTO;
        vacuumStatus = VacuumStatus.IDLE;
        batteryLevel = 100;
        cleanedArea = 0;
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
        if (isOn) {
            vacuumStatus = VacuumStatus.IDLE;
        } else {
            vacuumStatus = VacuumStatus.IDLE;
        }
        eventLog.add("Power toggled: " + (isOn ? "ON" : "OFF"));
    }

    public void setMode(CleanMode _mode) {
        mode = _mode;
        eventLog.add("Clean mode set to " + _mode);
    }

    public void startCleaning() {
        if (!isOn) throw new IllegalStateException("Vacuum must be turned on first");
        if (batteryLevel < 10) throw new IllegalStateException("Battery too low");
        vacuumStatus = VacuumStatus.CLEANING;
        eventLog.add("Cleaning started in " + mode + " mode");
    }

    public void returnToBase() {
        vacuumStatus = VacuumStatus.RETURNING;
        eventLog.add("Returning to charging base");
    }

    @Override
    public String getStatus() {
        return String.format("SmartRobotVacuum[id=%s, name=%s, isOn=%s, mode=%s, status=%s, battery=%d%%, area=%dm²]",
                id, name, isOn, mode, vacuumStatus, batteryLevel, cleanedArea);
    }
}
