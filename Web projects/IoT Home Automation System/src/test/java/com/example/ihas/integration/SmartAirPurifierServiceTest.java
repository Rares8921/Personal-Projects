package com.example.ihas.integration;

import com.example.ihas.dao.SmartAirPurifierDAO;
import com.example.ihas.devices.SmartAirPurifier;
import com.example.ihas.devices.SmartAirPurifier.FanSpeed;
import com.example.ihas.services.SmartAirPurifierService;
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
public class SmartAirPurifierServiceTest {

    @Mock private SmartAirPurifierDAO dao;
    @Mock private ThingsBoardService tbService;
    @InjectMocks private SmartAirPurifierService service;

    private SmartAirPurifier purifier;
    private final String userId = "testUser";

    @BeforeEach
    void setUp() {
        purifier = new SmartAirPurifier("AP001", "Bedroom Purifier");
    }

    @Test
    void testGetAll() {
        when(dao.findAll(userId)).thenReturn(List.of(purifier));
        assertEquals(1, service.getAll(userId).size());
    }

    @Test
    void testGet() {
        when(dao.findById("AP001", userId)).thenReturn(purifier);
        assertEquals("Bedroom Purifier", service.get("AP001", userId).getName());
    }

    @Test
    void testAdd() {
        service.add(purifier, userId);
        verify(dao).save(purifier, userId);
    }

    @Test
    void testDelete() {
        service.delete("AP001", userId);
        verify(dao).delete("AP001", userId);
    }

    @Test
    void testTogglePower() {
        when(dao.findById("AP001", userId)).thenReturn(purifier);
        assertFalse(purifier.isOn());
        service.togglePower("AP001", userId);
        assertTrue(purifier.isOn());
        verify(dao).update(purifier, userId);
    }

    @Test
    void testUpdateFanSpeed() {
        when(dao.findById("AP001", userId)).thenReturn(purifier);
        service.updateFanSpeed("AP001", "HIGH", userId);
        assertEquals(FanSpeed.HIGH, purifier.getFanSpeed());
        verify(dao).update(purifier, userId);
    }

    @Test
    void testInvalidFanSpeed() {
        when(dao.findById("AP001", userId)).thenReturn(purifier);
        assertThrows(IllegalArgumentException.class, () ->
                service.updateFanSpeed("AP001", "INVALID", userId));
    }
}
