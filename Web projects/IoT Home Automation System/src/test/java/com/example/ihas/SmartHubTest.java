package com.example.ihas;

import com.example.ihas.devices.SmartHub;
import com.example.ihas.devices.SmartLight;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartHubTest {

    @Test
    public void testStatus() {
        SmartHub hub = new SmartHub("HUB001", "Main Hub");
        String status = hub.getStatus();
        Assertions.assertTrue(status.contains("HUB001") && status.contains("Main Hub"),
                "Status should contain both id and name");
    }

    @Test
    public void testAddAndRemoveDevice() {
        SmartHub hub = new SmartHub("HUB001", "Main Hub");
        SmartLight dummyDevice = new SmartLight("DUMMY001", "Dummy Device");
        hub.addDevice(dummyDevice);
        Assertions.assertTrue(hub.getDevices().containsKey("DUMMY001"), "Device should be added to hub");
        hub.removeDevice("DUMMY001");
        Assertions.assertFalse(hub.getDevices().containsKey("DUMMY001"), "Device should be removed from hub");
    }
}
