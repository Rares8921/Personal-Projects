package com.example.ihas.integration;

import com.example.ihas.dao.SmartTVDAO;
import com.example.ihas.devices.SmartTV;
import com.example.ihas.services.SmartTVService;
import com.example.ihas.services.ThingsBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SmartTVServiceTest {

    @Mock private SmartTVDAO tvDAO;
    @Mock private ThingsBoardService tbService;
    @InjectMocks private SmartTVService tvService;

    private SmartTV testTV;
    private final String userId = "testUser";

    @BeforeEach
    void setUp() {
        testTV = new SmartTV("TV001", "Living Room TV");
    }

    @Test
    void testGetAll() {
        when(tvDAO.findAll(userId)).thenReturn(List.of(testTV));
        List<SmartTV> result = tvService.getAll(userId);
        assertEquals(1, result.size());
        assertEquals("TV001", result.get(0).getId());
        verify(tvDAO).findAll(userId);
    }

    @Test
    void testGet() {
        when(tvDAO.findById("TV001", userId)).thenReturn(testTV);
        SmartTV result = tvService.get("TV001", userId);
        assertEquals("Living Room TV", result.getName());
        verify(tvDAO).findById("TV001", userId);
    }

    @Test
    void testAdd() {
        tvService.add(testTV, userId);
        verify(tvDAO).save(testTV, userId);
    }

    @Test
    void testDelete() {
        tvService.delete("TV001", userId);
        verify(tvDAO).delete("TV001", userId);
    }

    @Test
    void testTogglePower() {
        when(tvDAO.findById("TV001", userId)).thenReturn(testTV);
        assertFalse(testTV.isOn());
        tvService.togglePower("TV001", userId);
        assertTrue(testTV.isOn());
        verify(tvDAO).update(testTV, userId);
    }

    @Test
    void testUpdateVolume() {
        when(tvDAO.findById("TV001", userId)).thenReturn(testTV);
        tvService.updateVolume("TV001", 75, userId);
        assertEquals(75, testTV.getVolume());
        verify(tvDAO).update(testTV, userId);
    }

    @Test
    void testUpdateChannel() {
        when(tvDAO.findById("TV001", userId)).thenReturn(testTV);
        tvService.updateChannel("TV001", "HBO", userId);
        assertEquals("HBO", testTV.getChannel());
        verify(tvDAO).update(testTV, userId);
    }

    @Test
    void testUpdateInputSource() {
        when(tvDAO.findById("TV001", userId)).thenReturn(testTV);
        tvService.updateInputSource("TV001", "STREAMING", userId);
        assertEquals("STREAMING", testTV.getInputSource());
        verify(tvDAO).update(testTV, userId);
    }
}
