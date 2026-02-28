package com.example.ihas.integration;

import com.example.ihas.dao.SmartGarageDoorDAO;
import com.example.ihas.devices.SmartGarageDoor;
import com.example.ihas.devices.SmartGarageDoor.DoorState;
import com.example.ihas.services.SmartGarageDoorService;
import com.example.ihas.services.ThingsBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SmartGarageDoorServiceTest {

    @Mock private SmartGarageDoorDAO dao;
    @Mock private ThingsBoardService tbService;
    @InjectMocks private SmartGarageDoorService service;

    private SmartGarageDoor door;
    private final String userId = "testUser";

    @BeforeEach
    void setUp() {
        door = new SmartGarageDoor("GD001", "Main Garage");
    }

    @Test
    void testGetAll() {
        when(dao.findAll(userId)).thenReturn(List.of(door));
        assertEquals(1, service.getAll(userId).size());
    }

    @Test
    void testGet() {
        when(dao.findById("GD001", userId)).thenReturn(door);
        assertEquals("Main Garage", service.get("GD001", userId).getName());
    }

    @Test
    void testAdd() {
        service.add(door, userId);
        verify(dao).save(door, userId);
    }

    @Test
    void testDelete() {
        service.delete("GD001", userId);
        verify(dao).delete("GD001", userId);
    }

    @Test
    void testToggleDoor() {
        when(dao.findById("GD001", userId)).thenReturn(door);
        assertEquals(DoorState.CLOSED, door.getDoorState());
        service.toggleDoor("GD001", userId);
        assertEquals(DoorState.OPEN, door.getDoorState());
        verify(dao).update(door, userId);
    }

    @Test
    void testOpenDoor() {
        when(dao.findById("GD001", userId)).thenReturn(door);
        service.openDoor("GD001", userId);
        assertEquals(DoorState.OPEN, door.getDoorState());
        verify(dao).update(door, userId);
    }

    @Test
    void testCloseDoor() {
        door.open(); // set to open first
        when(dao.findById("GD001", userId)).thenReturn(door);
        service.closeDoor("GD001", userId);
        assertEquals(DoorState.CLOSED, door.getDoorState());
        verify(dao).update(door, userId);
    }
}
