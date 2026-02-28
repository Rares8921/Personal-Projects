package com.example.ihas.devices;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class SmartAlarmSystem implements SmartDevice {
    private final String id;
    private final String name;
    @Getter
    private boolean isArmed;

    @Getter
    private boolean alarmTriggered;
    @Getter
    private final List<String> eventLog;

    public SmartAlarmSystem(String _id, String _name) {
        id = _id;
        name = _name;
        isArmed = false;
        alarmTriggered = false;
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
        if (isArmed) {
            disarm();
        } else {
            arm();
        }
    }

    public void arm() {
        isArmed = true;
        alarmTriggered = false;
        eventLog.add("Alarm armed at " + LocalDateTime.now());
        System.out.println(name + " armed.");
    }

    public void disarm() {
        isArmed = false;
        alarmTriggered = false;
        eventLog.add("Alarm disarmed at " + LocalDateTime.now());
        System.out.println(name + " disarmed.");
    }

    public void triggerAlarm() {
        if (isArmed) {
            alarmTriggered = true;
            eventLog.add("Alarm triggered at " + LocalDateTime.now());
            System.out.println(name + " alarm triggered!");
        } else {
            System.out.println(name + " is not armed, cannot trigger alarm.");
        }
    }

    @Override
    public String getStatus() {
        return String.format("SmartAlarmSystem[id=%s, name=%s, isArmed=%s, alarmTriggered=%s]",
                id, name, isArmed, alarmTriggered);
    }
}

