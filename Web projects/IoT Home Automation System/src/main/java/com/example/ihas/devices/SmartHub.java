package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public final class SmartHub implements SmartDevice {
    private final String id;
    private final String name;
    @Getter
    private final List<String> eventLog;

    // Map pentru stocarea dispozitivelor conectate la hub
    @Getter @Setter
    private final Map<String, SmartDevice> devices = new HashMap<>();

    public SmartHub(String _id, String _name) {
        id = _id;
        name = _name;
        eventLog = new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public void togglePower() {
        eventLog.add("Ping received at hub " + id);
    }

    @Override
    public String getStatus() {
        return String.format("SmartHub[id=%s, name=%s, connectedDevices=%d, events=%d]",
                id, name, devices.size(), eventLog.size());
    }


    public void addDevice(SmartDevice device) {
        devices.put(device.getId(), device);
        eventLog.add("Device added: " + device.getName());
    }

    public void removeDevice(String deviceId) {
        SmartDevice removed = devices.remove(deviceId);
        if (removed != null) {
            eventLog.add("Device removed: " + removed.getName());
        }
    }

    public SmartDevice getDevice(String deviceId) {
        return devices.get(deviceId);
    }

    public Collection<SmartDevice> listDevices() {
        return devices.values();
    }

    // TODO: sa fie setat de utilizator, eventual la tastatura sau prin selectie dintr-un dropdown
    // Un scenariu predefenit
    public void executeScene(String sceneName) {
        //System.out.println("Executing scene: " + sceneName);
        devices.values().forEach(device -> {
            if (device instanceof SmartLight light) {
                light.togglePower();
            }
        });
    }
}
