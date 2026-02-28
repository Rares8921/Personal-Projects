package com.example.ihas;

import com.example.ihas.devices.SmartLight;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartLightTest {

    @Test
    public void testTogglePower() {
        SmartLight light = new SmartLight("L001", "Living Room Light");
        boolean initial = light.isOn();
        light.togglePower();
        Assertions.assertNotEquals(initial, light.isOn(), "Power state should be toggled");
    }

    @Test
    public void testSetBrightness() {
        SmartLight light = new SmartLight("L001", "Living Room Light");
        light.setBrightness(80);
        Assertions.assertEquals(80, light.getBrightness(), "Brightness should be set to 80");
    }

    @Test
    public void testInvalidColor() {
        SmartLight light = new SmartLight("L001", "Living Room Light");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            light.setColor("invalidColor");
        });
    }

    @Test
    public void testSetColor() {
        SmartLight light = new SmartLight("L001", "Living Room Light");
        light.setColor("#FFAA00");
        Assertions.assertEquals("#FFAA00", light.getColor(), "Color should be set to #FFAA00");
    }
}
