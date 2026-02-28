package com.example.ihas;

import com.example.ihas.devices.SmartAirPurifier;
import com.example.ihas.devices.SmartAirPurifier.FanSpeed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartAirPurifierTest {

    @Test
    public void testTogglePower() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        Assertions.assertFalse(purifier.isOn());
        purifier.togglePower();
        Assertions.assertTrue(purifier.isOn());
        purifier.togglePower();
        Assertions.assertFalse(purifier.isOn());
    }

    @Test
    public void testDefaultValues() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        Assertions.assertEquals("AP001", purifier.getId());
        Assertions.assertEquals("Bedroom Purifier", purifier.getName());
        Assertions.assertEquals(FanSpeed.AUTO, purifier.getFanSpeed());
        Assertions.assertEquals(50, purifier.getAirQualityIndex());
        Assertions.assertEquals(100, purifier.getFilterLife());
    }

    @Test
    public void testSetFanSpeed() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        purifier.setFanSpeed(FanSpeed.HIGH);
        Assertions.assertEquals(FanSpeed.HIGH, purifier.getFanSpeed());
    }

    @Test
    public void testSetAirQualityIndex() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        purifier.setAirQualityIndex(150);
        Assertions.assertEquals(150, purifier.getAirQualityIndex());
    }

    @Test
    public void testSetFilterLife() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        purifier.setFilterLife(50);
        Assertions.assertEquals(50, purifier.getFilterLife());
    }

    @Test
    public void testNeedsFilterReplacementTrue() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        purifier.setFilterLife(5);
        Assertions.assertTrue(purifier.needsFilterReplacement());
    }

    @Test
    public void testNeedsFilterReplacementFalse() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        purifier.setFilterLife(50);
        Assertions.assertFalse(purifier.needsFilterReplacement());
    }

    @Test
    public void testNeedsFilterReplacementBoundary() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        purifier.setFilterLife(10);
        Assertions.assertFalse(purifier.needsFilterReplacement(), "10% should not need replacement");
        purifier.setFilterLife(9);
        Assertions.assertTrue(purifier.needsFilterReplacement(), "9% should need replacement");
    }

    @Test
    public void testEventLog() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        purifier.togglePower();
        purifier.setFanSpeed(FanSpeed.SLEEP);
        Assertions.assertEquals(2, purifier.getEventLog().size());
    }

    @Test
    public void testGetStatus() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        String status = purifier.getStatus();
        Assertions.assertTrue(status.contains("AP001"));
        Assertions.assertTrue(status.contains("Bedroom Purifier"));
        Assertions.assertTrue(status.contains("AUTO"));
    }

    @Test
    public void testIsOnline() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        Assertions.assertTrue(purifier.isOnline());
    }

    @Test
    public void testAllFanSpeeds() {
        SmartAirPurifier purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
        for (FanSpeed speed : FanSpeed.values()) {
            purifier.setFanSpeed(speed);
            Assertions.assertEquals(speed, purifier.getFanSpeed());
        }
    }
}
