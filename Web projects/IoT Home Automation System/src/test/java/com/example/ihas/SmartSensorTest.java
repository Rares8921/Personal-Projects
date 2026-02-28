package com.example.ihas;

import com.example.ihas.devices.SmartSensor;
import com.example.ihas.devices.SmartSensor.SensorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartSensorTest {

    @Test
    public void testUpdateReading() {
        SmartSensor sensor = new SmartSensor("S001", "Living Room Sensor", SensorType.TEMPERATURE);
        sensor.updateReading(23.5);
        Assertions.assertEquals(23.5, sensor.getLastReading(), 0.01);
    }

    @Test
    public void testTogglePowerNotSupported() {
        SmartSensor sensor = new SmartSensor("S001", "Living Room Sensor", SensorType.MOTION);
        sensor.togglePower();

        Assertions.assertEquals(0.0, sensor.getLastReading(), 0.01);

        Assertions.assertTrue(sensor.getEventLog().stream().anyMatch(msg -> msg.contains("not supported")));
    }
}
