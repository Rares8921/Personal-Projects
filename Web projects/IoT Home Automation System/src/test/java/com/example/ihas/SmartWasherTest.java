package com.example.ihas;

import com.example.ihas.devices.SmartWasher;
import com.example.ihas.devices.SmartWasher.WashMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartWasherTest {

    @Test
    public void testTogglePower() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        Assertions.assertFalse(washer.isOn());
        washer.togglePower();
        Assertions.assertTrue(washer.isOn());
    }

    @Test
    public void testDefaultValues() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        Assertions.assertEquals("W001", washer.getId());
        Assertions.assertEquals("Laundry Washer", washer.getName());
        Assertions.assertEquals(WashMode.NORMAL, washer.getMode());
        Assertions.assertEquals(40, washer.getTemperature());
        Assertions.assertEquals(1000, washer.getSpinSpeed());
        Assertions.assertEquals(0, washer.getRemainingMinutes());
    }

    @Test
    public void testSetMode() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.setMode(WashMode.ECO);
        Assertions.assertEquals(WashMode.ECO, washer.getMode());
    }

    @Test
    public void testStartCycleWhenOff() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        Assertions.assertThrows(IllegalStateException.class, washer::startCycle,
                "Should not start cycle when washer is off");
    }

    @Test
    public void testStartCycleNormal() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.togglePower();
        washer.setMode(WashMode.NORMAL);
        washer.startCycle();
        Assertions.assertEquals(60, washer.getRemainingMinutes());
    }

    @Test
    public void testStartCycleQuick() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.togglePower();
        washer.setMode(WashMode.QUICK);
        washer.startCycle();
        Assertions.assertEquals(30, washer.getRemainingMinutes());
    }

    @Test
    public void testStartCycleEco() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.togglePower();
        washer.setMode(WashMode.ECO);
        washer.startCycle();
        Assertions.assertEquals(120, washer.getRemainingMinutes());
    }

    @Test
    public void testStartCycleHeavy() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.togglePower();
        washer.setMode(WashMode.HEAVY);
        washer.startCycle();
        Assertions.assertEquals(90, washer.getRemainingMinutes());
    }

    @Test
    public void testStartCycleDelicate() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.togglePower();
        washer.setMode(WashMode.DELICATE);
        washer.startCycle();
        Assertions.assertEquals(45, washer.getRemainingMinutes());
    }

    @Test
    public void testToggleOffResetsMinutes() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.togglePower(); // on
        washer.startCycle();
        washer.togglePower(); // off
        Assertions.assertEquals(0, washer.getRemainingMinutes());
    }

    @Test
    public void testSetTemperature() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.setTemperature(60);
        Assertions.assertEquals(60, washer.getTemperature());
    }

    @Test
    public void testSetSpinSpeed() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        washer.setSpinSpeed(1400);
        Assertions.assertEquals(1400, washer.getSpinSpeed());
    }

    @Test
    public void testGetStatus() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        String status = washer.getStatus();
        Assertions.assertTrue(status.contains("W001"));
        Assertions.assertTrue(status.contains("Laundry Washer"));
    }

    @Test
    public void testIsOnline() {
        SmartWasher washer = new SmartWasher("W001", "Laundry Washer");
        Assertions.assertTrue(washer.isOnline(), "Washer should always report online");
    }
}
