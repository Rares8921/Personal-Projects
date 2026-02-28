package com.example.ihas;

import com.example.ihas.devices.SmartSprinkler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartSprinklerTest {

    @Test
    public void testTogglePower() {
        SmartSprinkler sprinkler = new SmartSprinkler("SP001", "Garden Sprinkler");
        boolean initialState = sprinkler.isOn();
        sprinkler.togglePower();
        Assertions.assertNotEquals(initialState, sprinkler.isOn(), "Toggle should change power state");
    }

    @Test
    public void testSetWateringDurationValid() {
        SmartSprinkler sprinkler = new SmartSprinkler("SP001", "Garden Sprinkler");
        sprinkler.setWateringDuration(15);
        Assertions.assertEquals(15, sprinkler.getWateringDuration());
    }

    @Test
    public void testSetWateringDurationInvalid() {
        SmartSprinkler sprinkler = new SmartSprinkler("SP001", "Garden Sprinkler");
        Assertions.assertThrows(IllegalArgumentException.class, () -> sprinkler.setWateringDuration(0));
    }
}
