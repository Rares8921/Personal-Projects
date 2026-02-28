package com.example.ihas;

import com.example.ihas.devices.SmartAssistant;
import com.example.ihas.devices.SmartHub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartAssistantTest {

    @Test
    public void testProcessVoiceCommand() {
        SmartAssistant assistant = new SmartAssistant("AS001", "Living Room Assistant", "HUB001");
        SmartHub dummyHub = new SmartHub("HUB001", "Test Hub");
        int initialLogSize = assistant.getEventLog().size();
        assistant.processVoiceCommand("Turn on the lights", dummyHub);
        Assertions.assertTrue(assistant.getEventLog().size() > initialLogSize,
                "Event log should grow after processing command");
    }

    @Test
    public void testGetStatus() {
        SmartAssistant assistant = new SmartAssistant("AS001", "Living Room Assistant", "HUB001");
        String status = assistant.getStatus();
        Assertions.assertTrue(status.contains("AS001") && status.contains("HUB001"),
                "Status should contain id and hubId");
    }
}
