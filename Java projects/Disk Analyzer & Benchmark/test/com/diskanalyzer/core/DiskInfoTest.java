package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DiskInfo class.
 */
class DiskInfoTest {

    private static final String SAMPLE_JSON =
        "{\"drive\":\"C\",\"volumeName\":\"Windows\",\"fileSystem\":\"NTFS\"," +
        "\"serialNumber\":\"1234-ABCD\",\"totalBytes\":500000000000," +
        "\"freeBytes\":200000000000,\"usedBytes\":300000000000," +
        "\"clusterSize\":4096,\"sectorSize\":512,\"driveType\":\"Fixed\"," +
        "\"isSSD\":true}";

    @Test
    @DisplayName("fromJson parses all fields correctly")
    void testFromJson() {
        DiskInfo info = DiskInfo.fromJson(SAMPLE_JSON);
        assertEquals('C', info.getDrive());
        assertEquals("Windows", info.getVolumeName());
        assertEquals("NTFS", info.getFileSystem());
        assertEquals("1234-ABCD", info.getSerialNumber());
        assertEquals(500000000000L, info.getTotalBytes());
        assertEquals(200000000000L, info.getFreeBytes());
        assertEquals(300000000000L, info.getUsedBytes());
        assertEquals(4096, info.getClusterSize());
        assertEquals(512, info.getSectorSize());
        assertEquals("Fixed", info.getDriveType());
        assertTrue(info.isSSD());
    }

    @Test
    @DisplayName("getUsagePercent calculates correctly")
    void testUsagePercent() {
        DiskInfo info = DiskInfo.fromJson(SAMPLE_JSON);
        assertEquals(60.0, info.getUsagePercent(), 0.01);
    }

    @Test
    @DisplayName("getUsagePercent returns 0 for zero total")
    void testUsagePercentZero() {
        String json = "{\"drive\":\"X\",\"volumeName\":\"\",\"fileSystem\":\"\"," +
            "\"totalBytes\":0,\"freeBytes\":0,\"usedBytes\":0,\"driveType\":\"Unknown\",\"isSSD\":null}";
        DiskInfo info = DiskInfo.fromJson(json);
        assertEquals(0.0, info.getUsagePercent(), 0.001);
    }

    @Test
    @DisplayName("formatBytes converts bytes to human-readable strings")
    void testFormatBytes() {
        assertEquals("0 B", DiskInfo.formatBytes(0));
        assertEquals("512 B", DiskInfo.formatBytes(512));
        assertEquals("1.00 KB", DiskInfo.formatBytes(1024));
        assertEquals("1.00 MB", DiskInfo.formatBytes(1048576));
        assertEquals("1.00 GB", DiskInfo.formatBytes(1073741824));
        assertEquals("1.00 TB", DiskInfo.formatBytes(1099511627776L));
    }

    @Test
    @DisplayName("formatBytes handles negative values")
    void testFormatBytesNegative() {
        assertEquals("N/A", DiskInfo.formatBytes(-1));
    }

    @Test
    @DisplayName("isSSD returns null when unknown")
    void testIsSSDNull() {
        String json = "{\"drive\":\"D\",\"volumeName\":\"\",\"fileSystem\":\"NTFS\"," +
            "\"totalBytes\":1000,\"freeBytes\":500,\"usedBytes\":500,\"driveType\":\"Fixed\",\"isSSD\":null}";
        DiskInfo info = DiskInfo.fromJson(json);
        assertNull(info.isSSD());
    }

    @Test
    @DisplayName("toString contains drive info")
    void testToString() {
        DiskInfo info = DiskInfo.fromJson(SAMPLE_JSON);
        String s = info.toString();
        assertTrue(s.contains("C"));
        assertTrue(s.contains("NTFS"));
        assertTrue(s.contains("60.0%"));
    }
}
