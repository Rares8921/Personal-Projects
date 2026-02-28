package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartAirPurifier implements SmartDevice {
    public enum FanSpeed { LOW, MEDIUM, HIGH, AUTO, SLEEP }

    private final String id;
    private final String name;

    @Getter
    private boolean isOn;
    @Getter
    private FanSpeed fanSpeed;
    @Getter @Setter
    private int airQualityIndex; // AQI 0-500
    @Getter @Setter
    private int filterLife; // percentage remaining 0-100
    @Getter
    private final List<String> eventLog;

    @Getter @Setter
    private String userId;

    public SmartAirPurifier(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        fanSpeed = FanSpeed.AUTO;
        airQualityIndex = 50;
        filterLife = 100;
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

    public void setFanSpeed(FanSpeed _fanSpeed) {
        fanSpeed = _fanSpeed;
        eventLog.add("Fan speed set to " + _fanSpeed);
    }

    public boolean needsFilterReplacement() {
        return filterLife < 10;
    }

    @Override
    public String getStatus() {
        return String.format("SmartAirPurifier[id=%s, name=%s, isOn=%s, fan=%s, AQI=%d, filter=%d%%]",
                id, name, isOn, fanSpeed, airQualityIndex, filterLife);
    }
}
