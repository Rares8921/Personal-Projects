package com.example.ihas;

import com.example.ihas.devices.SmartRobotVacuum;
import com.example.ihas.devices.SmartRobotVacuum.CleanMode;
import com.example.ihas.devices.SmartRobotVacuum.VacuumStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartRobotVacuumTest {

    @Test
    public void testTogglePower() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        Assertions.assertFalse(vacuum.isOn());
        vacuum.togglePower();
        Assertions.assertTrue(vacuum.isOn());
        vacuum.togglePower();
        Assertions.assertFalse(vacuum.isOn());
    }

    @Test
    public void testDefaultValues() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        Assertions.assertEquals("RV001", vacuum.getId());
        Assertions.assertEquals("Floor Cleaner", vacuum.getName());
        Assertions.assertEquals(CleanMode.AUTO, vacuum.getMode());
        Assertions.assertEquals(VacuumStatus.IDLE, vacuum.getVacuumStatus());
        Assertions.assertEquals(100, vacuum.getBatteryLevel());
        Assertions.assertEquals(0, vacuum.getCleanedArea());
    }

    @Test
    public void testSetMode() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        vacuum.setMode(CleanMode.TURBO);
        Assertions.assertEquals(CleanMode.TURBO, vacuum.getMode());
    }

    @Test
    public void testStartCleaningWhenOff() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        Assertions.assertThrows(IllegalStateException.class, vacuum::startCleaning,
                "Should not start cleaning when off");
    }

    @Test
    public void testStartCleaningLowBattery() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        vacuum.togglePower();
        vacuum.setBatteryLevel(5);
        Assertions.assertThrows(IllegalStateException.class, vacuum::startCleaning,
                "Should not start cleaning with low battery");
    }

    @Test
    public void testStartCleaning() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        vacuum.togglePower();
        vacuum.startCleaning();
        Assertions.assertEquals(VacuumStatus.CLEANING, vacuum.getVacuumStatus());
    }

    @Test
    public void testReturnToBase() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        vacuum.togglePower();
        vacuum.startCleaning();
        vacuum.returnToBase();
        Assertions.assertEquals(VacuumStatus.RETURNING, vacuum.getVacuumStatus());
    }

    @Test
    public void testSetBatteryLevel() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        vacuum.setBatteryLevel(45);
        Assertions.assertEquals(45, vacuum.getBatteryLevel());
    }

    @Test
    public void testSetCleanedArea() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        vacuum.setCleanedArea(25);
        Assertions.assertEquals(25, vacuum.getCleanedArea());
    }

    @Test
    public void testGetStatus() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        String status = vacuum.getStatus();
        Assertions.assertTrue(status.contains("RV001"));
        Assertions.assertTrue(status.contains("Floor Cleaner"));
    }

    @Test
    public void testTogglePowerSetsIdle() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        vacuum.togglePower();
        vacuum.startCleaning();
        vacuum.togglePower(); // off
        Assertions.assertEquals(VacuumStatus.IDLE, vacuum.getVacuumStatus());
    }

    @Test
    public void testEventLog() {
        SmartRobotVacuum vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
        vacuum.togglePower();
        vacuum.setMode(CleanMode.SPOT);
        vacuum.startCleaning();
        vacuum.returnToBase();
        Assertions.assertEquals(4, vacuum.getEventLog().size());
    }
}
