package com.example.ihas;

import com.example.ihas.devices.SmartAlarmSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartAlarmSystemTest {

    @Test
    public void testArmAndDisarm() {
        SmartAlarmSystem alarm = new SmartAlarmSystem("A001", "Main Entrance Alarm");

        Assertions.assertFalse(alarm.isArmed(), "Initial state should be disarmed");
        alarm.arm();
        Assertions.assertTrue(alarm.isArmed(), "Should be armed after calling arm()");
        alarm.disarm();
        Assertions.assertFalse(alarm.isArmed(), "Should be disarmed after calling disarm()");
    }

    @Test
    public void testTriggerAlarmWhenArmed() {
        SmartAlarmSystem alarm = new SmartAlarmSystem("A001", "Main Entrance Alarm");
        alarm.arm();
        alarm.triggerAlarm();
        Assertions.assertTrue(alarm.isAlarmTriggered(), "Alarm should be triggered when armed and triggerAlarm() is called");
    }

    @Test
    public void testTriggerAlarmWhenNotArmed() {
        SmartAlarmSystem alarm = new SmartAlarmSystem("A001", "Main Entrance Alarm");
        alarm.triggerAlarm();
        Assertions.assertFalse(alarm.isAlarmTriggered(), "Alarm should not be triggered when not armed");
    }
}
