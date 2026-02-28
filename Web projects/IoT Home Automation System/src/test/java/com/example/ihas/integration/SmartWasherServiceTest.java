package com.example.ihas.integration;

import com.example.ihas.dao.SmartWasherDAO;
import com.example.ihas.devices.SmartWasher;
import com.example.ihas.devices.SmartWasher.WashMode;
import com.example.ihas.services.SmartWasherService;
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
public class SmartWasherServiceTest {

    @Mock private SmartWasherDAO dao;
    @Mock private ThingsBoardService tbService;
    @InjectMocks private SmartWasherService service;

    private SmartWasher washer;
    private final String userId = "testUser";

    @BeforeEach
    void setUp() {
        washer = new SmartWasher("W001", "Laundry Washer");
    }

    @Test
    void testGetAll() {
        when(dao.findAll(userId)).thenReturn(List.of(washer));
        List<SmartWasher> result = service.getAll(userId);
        assertEquals(1, result.size());
        verify(dao).findAll(userId);
    }

    @Test
    void testGet() {
        when(dao.findById("W001", userId)).thenReturn(washer);
        SmartWasher result = service.get("W001", userId);
        assertEquals("Laundry Washer", result.getName());
    }

    @Test
    void testAdd() {
        service.add(washer, userId);
        verify(dao).save(washer, userId);
    }

    @Test
    void testDelete() {
        service.delete("W001", userId);
        verify(dao).delete("W001", userId);
    }

    @Test
    void testTogglePower() {
        when(dao.findById("W001", userId)).thenReturn(washer);
        service.togglePower("W001", userId);
        assertTrue(washer.isOn());
        verify(dao).update(washer, userId);
    }

    @Test
    void testUpdateMode() {
        when(dao.findById("W001", userId)).thenReturn(washer);
        service.updateMode("W001", "ECO", userId);
        assertEquals(WashMode.ECO, washer.getMode());
        verify(dao).update(washer, userId);
    }

    @Test
    void testStartCycle() {
        washer.togglePower(); // must be on first
        when(dao.findById("W001", userId)).thenReturn(washer);
        service.startCycle("W001", userId);
        assertTrue(washer.getRemainingMinutes() > 0);
        verify(dao).update(washer, userId);
    }

    @Test
    void testStartCycleWhileOff() {
        when(dao.findById("W001", userId)).thenReturn(washer);
        assertThrows(IllegalStateException.class, () -> service.startCycle("W001", userId));
    }
}
