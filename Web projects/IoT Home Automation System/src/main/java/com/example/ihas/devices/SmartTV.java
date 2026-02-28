package com.example.ihas.devices;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class SmartTV implements SmartDevice {
    private final String id;
    private final String name;

    @Getter
    private boolean isOn;
    @Getter @Setter
    private int volume; // 0-100
    @Getter @Setter
    private String channel;
    @Getter @Setter
    private String inputSource; // HDMI1, HDMI2, USB, STREAMING
    @Getter
    private final List<String> eventLog;

    @Getter @Setter
    private String userId;

    public SmartTV(String _id, String _name) {
        id = _id;
        name = _name;
        isOn = false;
        volume = 30;
        channel = "1";
        inputSource = "HDMI1";
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

    public void setVolume(int _volume) {
        if (_volume < 0 || _volume > 100)
            throw new IllegalArgumentException("Volume must be between 0 and 100");
        volume = _volume;
        eventLog.add("Volume set to " + _volume);
    }

    public void mute() {
        volume = 0;
        eventLog.add("TV muted");
    }

    @Override
    public String getStatus() {
        return String.format("SmartTV[id=%s, name=%s, isOn=%s, volume=%d, channel=%s, input=%s]",
                id, name, isOn, volume, channel, inputSource);
    }
}
