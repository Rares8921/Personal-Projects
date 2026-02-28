package com.example.ihas;

import com.example.ihas.devices.SmartThermostat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartThermostatTest {

    @Test
    public void testSetTemperatureValid() {
        SmartThermostat st = new SmartThermostat("T001", "Living Room");
        st.setTemperature(25.0);
        Assertions.assertEquals(25.0, st.getTemperature());
    }

    @Test
    public void testSetTemperatureInvalid() {
        SmartThermostat st = new SmartThermostat("T001", "Living Room");
        Assertions.assertThrows(IllegalArgumentException.class, () -> st.setTemperature(10.0));
    }

    @Test
    public void testTogglePower() {
        SmartThermostat st = new SmartThermostat("T001", "Living Room");
        Assertions.assertFalse(st.isOn());
        st.togglePower();
        Assertions.assertTrue(st.isOn());
    }

    @Test
    public void testAutoAdjustTemperature() {
        SmartThermostat st = new SmartThermostat("T001", "Living Room");
        st.setTemperature(22.0);
        st.autoAdjustTemperature(26.0); // newTemp = (22 + 26)/2 = 24
        Assertions.assertEquals(24.0, st.getTemperature());
    }

    @Test
    public void testModeChange() {
        SmartThermostat st = new SmartThermostat("T001", "Living Room");
        st.setMode(SmartThermostat.Mode.HEAT);
        Assertions.assertEquals(SmartThermostat.Mode.HEAT, st.getMode());
    }
}
