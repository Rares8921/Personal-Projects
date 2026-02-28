package com.example.ihas.integration;

import com.example.ihas.dao.SmartRobotVacuumDAO;
import com.example.ihas.devices.SmartRobotVacuum;
import com.example.ihas.devices.SmartRobotVacuum.CleanMode;
import com.example.ihas.devices.SmartRobotVacuum.VacuumStatus;
import com.example.ihas.services.SmartRobotVacuumService;
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
public class SmartRobotVacuumServiceTest {

    @Mock private SmartRobotVacuumDAO dao;
    @Mock private ThingsBoardService tbService;
    @InjectMocks private SmartRobotVacuumService service;

    private SmartRobotVacuum vacuum;
    private final String userId = "testUser";

    @BeforeEach
    void setUp() {
        vacuum = new SmartRobotVacuum("RV001", "Floor Cleaner");
    }

    @Test
    void testGetAll() {
        when(dao.findAll(userId)).thenReturn(List.of(vacuum));
        assertEquals(1, service.getAll(userId).size());
        verify(dao).findAll(userId);
    }

    @Test
    void testGet() {
        when(dao.findById("RV001", userId)).thenReturn(vacuum);
        assertEquals("Floor Cleaner", service.get("RV001", userId).getName());
    }

    @Test
    void testAdd() {
        service.add(vacuum, userId);
        verify(dao).save(vacuum, userId);
    }

    @Test
    void testDelete() {
        service.delete("RV001", userId);
        verify(dao).delete("RV001", userId);
    }

    @Test
    void testTogglePower() {
        when(dao.findById("RV001", userId)).thenReturn(vacuum);
        service.togglePower("RV001", userId);
        assertTrue(vacuum.isOn());
        verify(dao).update(vacuum, userId);
    }

    @Test
    void testUpdateMode() {
        when(dao.findById("RV001", userId)).thenReturn(vacuum);
        service.updateMode("RV001", "TURBO", userId);
        assertEquals(CleanMode.TURBO, vacuum.getMode());
        verify(dao).update(vacuum, userId);
    }

    @Test
    void testStartCleaning() {
        vacuum.togglePower();
        when(dao.findById("RV001", userId)).thenReturn(vacuum);
        service.startCleaning("RV001", userId);
        assertEquals(VacuumStatus.CLEANING, vacuum.getVacuumStatus());
        verify(dao).update(vacuum, userId);
    }

    @Test
    void testStartCleaningWhenOff() {
        when(dao.findById("RV001", userId)).thenReturn(vacuum);
        assertThrows(IllegalStateException.class, () -> service.startCleaning("RV001", userId));
    }

    @Test
    void testReturnToBase() {
        vacuum.togglePower();
        when(dao.findById("RV001", userId)).thenReturn(vacuum);
        service.returnToBase("RV001", userId);
        assertEquals(VacuumStatus.RETURNING, vacuum.getVacuumStatus());
        verify(dao).update(vacuum, userId);
    }
}
