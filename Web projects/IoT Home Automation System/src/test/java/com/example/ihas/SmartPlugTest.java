package com.example.ihas;

import com.example.ihas.devices.SmartPlug;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartPlugTest {

    @Test
    public void testTogglePower() {
        SmartPlug plug = new SmartPlug("P001", "Living Room Plug");
        boolean initialState = plug.isOn();
        plug.togglePower();
        Assertions.assertNotEquals(initialState, plug.isOn(), "Toggle should change power state");
    }

    @Test
    public void testCurrentConsumptionOn() {
        SmartPlug plug = new SmartPlug("P001", "Living Room Plug");
        plug.togglePower(); // pornește plug-ul
        Assertions.assertTrue(plug.isOn());

        Assertions.assertTrue(plug.getCurrentConsumption() > 0, "Consumption should be > 0 when on");
    }

    @Test
    public void testCurrentConsumptionOff() {
        SmartPlug plug = new SmartPlug("P001", "Living Room Plug");

        Assertions.assertFalse(plug.isOn());
        Assertions.assertEquals(0.0, plug.getCurrentConsumption(), 0.01);
    }
}
