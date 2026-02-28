package com.example.ihas;

import com.example.ihas.devices.SmartDoorLock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartDoorLockTest {

    @Test
    public void testToggleLock() {
        SmartDoorLock doorLock = new SmartDoorLock("DL001", "Front Door");
        boolean initialState = doorLock.isLocked();
        doorLock.togglePower();
        Assertions.assertNotEquals(initialState, doorLock.isLocked(), "Toggle should change lock state");
    }

    @Test
    public void testInitialLockState() {
        SmartDoorLock doorLock = new SmartDoorLock("DL001", "Front Door");
        Assertions.assertTrue(doorLock.isLocked(), "Initial lock state should be locked");
    }
}
