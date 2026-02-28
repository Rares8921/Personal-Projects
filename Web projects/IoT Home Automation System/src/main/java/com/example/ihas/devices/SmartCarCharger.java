package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.config.annotation.web.SecurityMarker;

import java.util.ArrayList;
import java.util.List;

public final class SmartCarCharger implements SmartDevice {
    private final String id;
    private final String name;
    @Getter
    private boolean isCharging;
    
    @Getter
    @Setter
    private double current;  // amperi
    @Getter
    @Setter
    private double voltage;  // volți
    @Getter
    private final List<String> eventLog;

    public SmartCarCharger(String _id, String _name) {
        id = _id;
        name = _name;
        isCharging = false;
        current = 0;
        voltage = 0;
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
        isCharging = !isCharging;
        eventLog.add("Charging toggled: " + (isCharging ? "STARTED" : "STOPPED"));
        if (isCharging) {
            // Valori default, tipice
            current = 32.0;
            voltage = 220.0;
        } else {
            current = 0;
            voltage = 0;
        }
    }

    @Override
    public String getStatus() {
        return String.format("SmartCarCharger[id=%s, name=%s, isCharging=%s, current=%.1fA, voltage=%.1fV]",
                id, name, isCharging, current, voltage);
    }
}
