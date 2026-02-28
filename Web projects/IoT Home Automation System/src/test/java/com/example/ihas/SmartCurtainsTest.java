package com.example.ihas;

import com.example.ihas.devices.SmartCurtains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartCurtainsTest {

    @Test
    public void testTogglePower() {
        SmartCurtains curtains = new SmartCurtains("SC001", "Living Room Curtains");
        int initialPosition = curtains.getPosition();
        curtains.togglePower();

        int expected = (initialPosition == 0) ? 100 : 0;
        Assertions.assertEquals(expected, curtains.getPosition(), "Toggle should set position to " + expected);
    }

    @Test
    public void testSetPositionValid() {
        SmartCurtains curtains = new SmartCurtains("SC001", "Living Room Curtains");
        curtains.setPosition(50);
        Assertions.assertEquals(50, curtains.getPosition());
    }

    @Test
    public void testSetPositionInvalid() {
        SmartCurtains curtains = new SmartCurtains("SC001", "Living Room Curtains");
        Assertions.assertThrows(IllegalArgumentException.class, () -> curtains.setPosition(150));
    }
}
