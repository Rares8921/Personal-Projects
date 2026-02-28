package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartGarageDoor implements SmartDevice {
    public enum DoorState { OPEN, CLOSED, OPENING, CLOSING }

    private final String id;
    private final String name;

    @Getter
    private DoorState doorState;
    @Getter @Setter
    private boolean autoClose;
    @Getter @Setter
    private int autoCloseDelay; // in seconds
    @Getter
    private final List<String> eventLog;

    @Getter @Setter
    private String userId;

    public SmartGarageDoor(String _id, String _name) {
        id = _id;
        name = _name;
        doorState = DoorState.CLOSED;
        autoClose = false;
        autoCloseDelay = 300;
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
        if (doorState == DoorState.CLOSED || doorState == DoorState.CLOSING) {
            doorState = DoorState.OPEN;
            eventLog.add("Garage door opened");
        } else {
            doorState = DoorState.CLOSED;
            eventLog.add("Garage door closed");
        }
    }

    public void open() {
        doorState = DoorState.OPEN;
        eventLog.add("Garage door opened");
    }

    public void close() {
        doorState = DoorState.CLOSED;
        eventLog.add("Garage door closed");
    }

    public boolean isOpen() {
        return doorState == DoorState.OPEN || doorState == DoorState.OPENING;
    }

    @Override
    public String getStatus() {
        return String.format("SmartGarageDoor[id=%s, name=%s, state=%s, autoClose=%s, delay=%ds]",
                id, name, doorState, autoClose, autoCloseDelay);
    }
}
