package com.example.ihas;

import com.example.ihas.devices.SmartOven;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartOvenTest {

    @Test
    public void testTogglePower() {
        SmartOven oven = new SmartOven("O001", "Kitchen Oven");
        boolean initial = oven.isOnline();
        oven.togglePower();
        Assertions.assertNotEquals(initial, oven.isOnline(), "Oven power toggled: ON");
    }

    @Test
    public void testSetTemperature() {
        SmartOven oven = new SmartOven("O001", "Kitchen Oven");
        oven.setTemperature(200);
        Assertions.assertEquals(200, oven.getTemperature(), "Temperature should be set to 200");
    }

    @Test
    public void testSetTimer() {
        SmartOven oven = new SmartOven("O001", "Kitchen Oven");
        oven.setTimer(30);
        Assertions.assertEquals(30, oven.getTimer(), "Timer should be set to 30");
    }

    @Test
    public void testPreheat() {
        SmartOven oven = new SmartOven("O001", "Kitchen Oven");
        oven.preheat();
        Assertions.assertTrue(oven.isPreheat(), "Preheat should be activated");
        oven.cancelPreheat();
        Assertions.assertFalse(oven.isPreheat(), "Preheat should be deactivated after cancel");
    }

    @Test
    public void testInvalidTemperature() {
        SmartOven oven = new SmartOven("O001", "Kitchen Oven");
        Assertions.assertThrows(IllegalArgumentException.class, () -> oven.setTemperature(400), "Should throw exception for invalid temperature");
    }
}
