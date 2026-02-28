package com.diskanalyzer.integration;

import com.diskanalyzer.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Integration tests: Scan History persistence pipeline.
 * Tests saving scan results, loading history, and trend analysis.
 * ScanHistory now uses dynamic getHistoryDir() respecting user.dir changes.
 */
class ScanHistoryIntegrationTest {

    @TempDir
    Path tempDir;

    private String originalDir;

    @BeforeEach
    void setup() {
        originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
    }

    @AfterEach
    void teardown() {
        System.setProperty("user.dir", originalDir);
    }

    @Test
    @DisplayName("Multiple scans → save history → load → verify trend")
    void testMultipleScanHistoryTracking() throws Exception {
        // Save 3 scan records using the actual API
        ScanHistory.saveScanRecord("C:\\Users\\test", 1000000L, 500, 50);
        ScanHistory.saveScanRecord("C:\\Users\\test", 1100000L, 520, 52);
        ScanHistory.saveScanRecord("C:\\Users\\test", 1200000L, 550, 55);

        // Load
        List<ScanHistory.ScanRecord> loaded = ScanHistory.loadHistory();
        assertEquals(3, loaded.size());

        // Verify trend data
        List<long[]> trend = ScanHistory.getTrend("C:\\Users\\test");
        assertNotNull(trend);
        assertEquals(3, trend.size());

        // Verify growth: each entry's totalSize should increase
        assertTrue(trend.get(0)[1] <= trend.get(1)[1]);
        assertTrue(trend.get(1)[1] <= trend.get(2)[1]);
    }

    @Test
    @DisplayName("Scan record formatted date display")
    void testScanRecordFormatting() {
        // Constructor: (scanPath, timestamp, totalSize, fileCount, entryCount)
        ScanHistory.ScanRecord record = new ScanHistory.ScanRecord(
            "D:\\Data", System.currentTimeMillis(), 5368709120L, 10000, 1000);

        assertNotNull(record.getFormattedDate());
        assertFalse(record.getFormattedDate().isEmpty());
        assertEquals("D:\\Data", record.getScanPath());
        assertEquals(10000, record.getFileCount());
        assertTrue(record.toString().contains("D:\\Data"));
    }

    @Test
    @DisplayName("Empty history file handling")
    void testEmptyHistoryFile() throws IOException {
        // No records saved yet — loadHistory returns empty
        List<ScanHistory.ScanRecord> loaded = ScanHistory.loadHistory();
        assertTrue(loaded.isEmpty());

        // getTrend for nonexistent path returns empty list
        List<long[]> trend = ScanHistory.getTrend("Z:\\nonexistent");
        assertNotNull(trend);
        assertTrue(trend.isEmpty());
    }
}
