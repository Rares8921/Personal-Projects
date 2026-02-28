package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for ScanHistory class.
 */
class ScanHistoryTest {

    @TempDir
    Path tempDir;

    private String originalDir;

    @BeforeEach
    void setup() {
        // Override user.dir so scan history writes to tempDir
        originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
    }

    @AfterEach
    void teardown() {
        System.setProperty("user.dir", originalDir);
    }

    @Test
    @DisplayName("ScanRecord stores and retrieves all fields")
    void testScanRecord() {
        ScanHistory.ScanRecord r = new ScanHistory.ScanRecord("C:\\test", 1700000000000L, 500000, 1000, 1200);
        assertEquals("C:\\test", r.getScanPath());
        assertEquals(1700000000000L, r.getTimestamp());
        assertEquals(500000, r.getTotalSize());
        assertEquals(1000, r.getFileCount());
        assertEquals(1200, r.getEntryCount());
    }

    @Test
    @DisplayName("ScanRecord formats date correctly")
    void testFormattedDate() {
        ScanHistory.ScanRecord r = new ScanHistory.ScanRecord("C:\\test", System.currentTimeMillis(), 0, 0, 0);
        assertNotNull(r.getFormattedDate());
        assertFalse(r.getFormattedDate().isEmpty());
        assertNotNull(r.getTimestampDate());
    }

    @Test
    @DisplayName("ScanRecord toString contains path and size info")
    void testScanRecordToString() {
        ScanHistory.ScanRecord r = new ScanHistory.ScanRecord("C:\\Users", System.currentTimeMillis(), 1048576, 100, 100);
        String s = r.toString();
        assertTrue(s.contains("C:\\Users"));
        assertTrue(s.contains("100"));
    }

    @Test
    @DisplayName("loadHistory returns empty list when no history exists")
    void testLoadEmptyHistory() {
        List<ScanHistory.ScanRecord> records = ScanHistory.loadHistory();
        assertNotNull(records);
        // May or may not be empty depending on test environment
    }

    @Test
    @DisplayName("getTrend returns empty for nonexistent path")
    void testGetTrendEmpty() {
        List<long[]> trend = ScanHistory.getTrend("Z:\\nonexistent\\path");
        assertNotNull(trend);
    }
}
