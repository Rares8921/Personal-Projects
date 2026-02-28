package com.example.ihas.devices;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class SmartDoorLock implements SmartDevice {
    private final String id;
    private final String name;
    @Getter
    private boolean locked;
    @Getter
    private final List<String> eventLog;

    public SmartDoorLock(String _id, String _name) {
        id = _id;
        name = _name;
        locked = true; // Initial e setata pe inchis
        eventLog = new ArrayList<>();
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isOnline() { return true; }

    // togglePower va fi interpretat ca lock/unlock
    @Override
    public void togglePower() {
        if (locked) {
            unlock();
        } else {
            lock();
        }
    }

    public void lock() {
        locked = true;
        eventLog.add("Locked at " + LocalDateTime.now());
        System.out.println(name + " locked.");
    }

    public void unlock() {
        locked = false;
        eventLog.add("Unlocked at " + LocalDateTime.now());
        System.out.println(name + " unlocked.");
    }

    @Override
    public String getStatus() {
        return String.format("SmartDoorLock[id=%s, name=%s, locked=%s]", id, name, locked);
    }
}

