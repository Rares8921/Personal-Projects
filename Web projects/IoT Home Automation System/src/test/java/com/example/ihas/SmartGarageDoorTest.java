package com.example.ihas;

import com.example.ihas.devices.SmartGarageDoor;
import com.example.ihas.devices.SmartGarageDoor.DoorState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SmartGarageDoorTest {

    @Test
    public void testDefaultValues() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        Assertions.assertEquals("GD001", door.getId());
        Assertions.assertEquals("Main Garage", door.getName());
        Assertions.assertEquals(DoorState.CLOSED, door.getDoorState());
        Assertions.assertFalse(door.isAutoClose());
        Assertions.assertEquals(300, door.getAutoCloseDelay());
    }

    @Test
    public void testTogglePowerFromClosed() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        door.togglePower(); // should open
        Assertions.assertEquals(DoorState.OPEN, door.getDoorState());
    }

    @Test
    public void testTogglePowerFromOpen() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        door.togglePower(); // open
        door.togglePower(); // close
        Assertions.assertEquals(DoorState.CLOSED, door.getDoorState());
    }

    @Test
    public void testOpen() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        door.open();
        Assertions.assertEquals(DoorState.OPEN, door.getDoorState());
        Assertions.assertTrue(door.isOpen());
    }

    @Test
    public void testClose() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        door.open();
        door.close();
        Assertions.assertEquals(DoorState.CLOSED, door.getDoorState());
        Assertions.assertFalse(door.isOpen());
    }

    @Test
    public void testIsOpen() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        Assertions.assertFalse(door.isOpen());
        door.open();
        Assertions.assertTrue(door.isOpen());
    }

    @Test
    public void testAutoClose() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        door.setAutoClose(true);
        Assertions.assertTrue(door.isAutoClose());
        door.setAutoCloseDelay(60);
        Assertions.assertEquals(60, door.getAutoCloseDelay());
    }

    @Test
    public void testEventLog() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        door.open();
        door.close();
        door.togglePower();
        Assertions.assertEquals(3, door.getEventLog().size());
    }

    @Test
    public void testGetStatus() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        String status = door.getStatus();
        Assertions.assertTrue(status.contains("GD001"));
        Assertions.assertTrue(status.contains("CLOSED"));
    }

    @Test
    public void testIsOnline() {
        SmartGarageDoor door = new SmartGarageDoor("GD001", "Main Garage");
        Assertions.assertTrue(door.isOnline());
    }
}
