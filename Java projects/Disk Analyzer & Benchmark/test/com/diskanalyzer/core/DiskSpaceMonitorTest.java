package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for DiskSpaceMonitor class.
 */
class DiskSpaceMonitorTest {

    @AfterEach
    void cleanup() {
        DiskSpaceMonitor.stopMonitoring();
        DiskSpaceMonitor.stopScheduledScans();
    }

    @Test
    @DisplayName("setAlertThreshold and getAlertThreshold work correctly")
    void testAlertThreshold() {
        DiskSpaceMonitor.setAlertThreshold(85.0);
        assertEquals(85.0, DiskSpaceMonitor.getAlertThreshold(), 0.01);
        DiskSpaceMonitor.setAlertThreshold(90.0); // reset
    }

    @Test
    @DisplayName("startMonitoring sets running state")
    void testStartMonitoring() {
        assertFalse(DiskSpaceMonitor.isRunning());
        DiskSpaceMonitor.startMonitoring(60, (drive, free, total, pct) -> {});
        assertTrue(DiskSpaceMonitor.isRunning());
        DiskSpaceMonitor.stopMonitoring();
        assertFalse(DiskSpaceMonitor.isRunning());
    }

    @Test
    @DisplayName("startMonitoring ignores duplicate calls")
    void testDoubleStart() {
        DiskSpaceMonitor.startMonitoring(60, (drive, free, total, pct) -> {});
        DiskSpaceMonitor.startMonitoring(60, (drive, free, total, pct) -> {}); // should not throw
        assertTrue(DiskSpaceMonitor.isRunning());
        DiskSpaceMonitor.stopMonitoring();
    }

    @Test
    @DisplayName("stopMonitoring is safe when not running")
    void testStopWhenNotRunning() {
        assertFalse(DiskSpaceMonitor.isRunning());
        DiskSpaceMonitor.stopMonitoring(); // should not throw
        assertFalse(DiskSpaceMonitor.isRunning());
    }

    @Test
    @DisplayName("getSmartData returns list without throwing")
    void testGetSmartData() {
        List<DiskSpaceMonitor.SmartDrive> drives = DiskSpaceMonitor.getSmartData();
        assertNotNull(drives);
    }

    @Test
    @DisplayName("SmartDrive class works correctly")
    void testSmartDrive() {
        DiskSpaceMonitor.SmartDrive sd = new DiskSpaceMonitor.SmartDrive();
        sd.model = "Samsung SSD 970 EVO";
        sd.size = 500107862016L;
        sd.status = "OK";
        sd.mediaType = "Fixed hard disk media";
        sd.interfaceType = "SCSI";

        assertEquals("Samsung SSD 970 EVO", sd.model);
        assertTrue(sd.isHealthy());
        assertNotNull(sd.getFormattedSize());
    }

    @Test
    @DisplayName("SmartDrive isHealthy detects unhealthy status")
    void testSmartDriveUnhealthy() {
        DiskSpaceMonitor.SmartDrive sd = new DiskSpaceMonitor.SmartDrive();
        sd.status = "Pred Fail";
        assertFalse(sd.isHealthy());
    }

    @Test
    @DisplayName("getDiskTemperatures returns map without throwing")
    void testGetDiskTemperatures() {
        Map<String, Integer> temps = DiskSpaceMonitor.getDiskTemperatures();
        assertNotNull(temps);
    }

    @Test
    @DisplayName("scheduleScans and stopScheduledScans work")
    void testScheduleScans() {
        assertFalse(DiskSpaceMonitor.isScanScheduled());
        DiskSpaceMonitor.scheduleScans("C:\\", 24, 100, 10, (path, result) -> {});
        assertTrue(DiskSpaceMonitor.isScanScheduled());
        DiskSpaceMonitor.stopScheduledScans();
        assertFalse(DiskSpaceMonitor.isScanScheduled());
    }
}
