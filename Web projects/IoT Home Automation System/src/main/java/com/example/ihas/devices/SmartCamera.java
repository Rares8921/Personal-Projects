package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartCamera implements SmartDevice {
    private final String id;
    private final String name;

    @Getter
    private boolean isRecording;
    @Getter @Setter
    private String resolution; // ex: "1080p", "4K"
    @Getter
    private int detectionSensitivity; // de la 1 la 10
    @Getter
    private final List<String> eventLog;

    public SmartCamera(String _id, String _name, String _resolution) {
        id = _id;
        name = _name;
        isRecording = false;
        resolution = _resolution;
        detectionSensitivity = 5;
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
        isRecording = !isRecording;
        eventLog.add("Recording toggled: " + (isRecording ? "STARTED" : "STOPPED"));
    }

    public void setDetectionSensitivity(int sensitivity) {
        if (sensitivity < 1 || sensitivity > 10)
            throw new IllegalArgumentException("Sensitivity must be between 1 and 10");
        detectionSensitivity = sensitivity;
        eventLog.add("Detection sensitivity set to " + sensitivity);
    }

    public void recordEvent(String event) {
        eventLog.add("Event recorded: " + event);
    }

    @Override
    public String getStatus() {
        return String.format("SmartCamera[id=%s, name=%s, isRecording=%s, resolution=%s, sensitivity=%d]",
                id, name, isRecording, resolution, detectionSensitivity);
    }
}

