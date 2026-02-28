package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class SmartLight implements SmartDevice {
    private final String id;
    private final String name;


    @Getter
    @Setter
    private boolean isOn;
    @Getter
    private int brightness; // 0-100
    @Getter
    private String color;   // HEX ex: "#FFFFFF"

    @Getter
    private final List<String> eventLog;

    public SmartLight(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        brightness = 100;
        color = "#FFFFFF";
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
        eventLog.add("Power toggled: " + (isOn ? "ON" : "OFF"));
    }

    public void setBrightness(int _brightness) {
        if (_brightness < 0 || _brightness > 100)
            throw new IllegalArgumentException("Brightness must be between 0 and 100");
        brightness = _brightness;
        eventLog.add("Brightness set to " + _brightness);
    }

    private boolean validHexaCode(String hexaCode) {
        Pattern pattern = Pattern.compile("^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$");
        Matcher matcher = pattern.matcher(hexaCode);
        return matcher.matches();
    }

    public void setColor(String _color) {
        if(!validHexaCode(_color))
            throw new IllegalArgumentException("Invalid color");
        color = _color;
        eventLog.add("Color set to " + _color);
    }

    // Funcție de programare a unui eveniment viitor
    public void scheduleLighting(String scheduleTime) {
        eventLog.add("Lighting scheduled at " + scheduleTime);
    }

    @Override
    public String getStatus() {
        return String.format("SmartLight[id=%s, name=%s, isOn=%s, brightness=%d, color=%s]",
                id, name, isOn, brightness, color);
    }
}

