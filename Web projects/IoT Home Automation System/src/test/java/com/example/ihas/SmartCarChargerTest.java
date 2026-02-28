package com.example.ihas;

import com.example.ihas.devices.SmartCarCharger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartCarChargerTest {

    @Test
    public void testToggleCharging() {
        SmartCarCharger charger = new SmartCarCharger("CC001", "Home Car Charger");
        boolean initialState = charger.isCharging();
        charger.togglePower();
        Assertions.assertNotEquals(initialState, charger.isCharging(), "Toggle should change charging state");
    }

    @Test
    public void testInitialValues() {
        SmartCarCharger charger = new SmartCarCharger("CC001", "Home Car Charger");
        Assertions.assertFalse(charger.isCharging());
        Assertions.assertEquals(0.0, charger.getCurrent(), 0.01);
        Assertions.assertEquals(0.0, charger.getVoltage(), 0.01);
    }
}
