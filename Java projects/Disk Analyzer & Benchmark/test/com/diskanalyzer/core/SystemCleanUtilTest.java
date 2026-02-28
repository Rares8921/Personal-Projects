package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for SystemCleanUtil class.
 */
class SystemCleanUtilTest {

    @Test
    @DisplayName("findTempFiles returns a list (may vary by system)")
    void testFindTempFiles() {
        List<SystemCleanUtil.CleanItem> items = SystemCleanUtil.findTempFiles();
        assertNotNull(items);
        // Can't guarantee temp files exist, but the method shouldn't throw
    }

    @Test
    @DisplayName("CleanItem stores fields correctly")
    void testCleanItem() {
        SystemCleanUtil.CleanItem item = new SystemCleanUtil.CleanItem("C:\\temp\\file.tmp", 4096, "User Temp");
        assertEquals("C:\\temp\\file.tmp", item.path);
        assertEquals(4096, item.size);
        assertEquals("User Temp", item.category);
    }

    @Test
    @DisplayName("getRecycleBinSize returns non-negative value")
    void testRecycleBinSize() {
        long size = SystemCleanUtil.getRecycleBinSize();
        assertTrue(size >= 0);
    }

    @Test
    @DisplayName("detectCloudStorage returns a map")
    void testDetectCloudStorage() {
        Map<String, String[]> cloud = SystemCleanUtil.detectCloudStorage();
        assertNotNull(cloud);
        // Each entry should have 3 elements: path, formatted size, raw size
        for (Map.Entry<String, String[]> entry : cloud.entrySet()) {
            assertEquals(3, entry.getValue().length);
        }
    }

    @Test
    @DisplayName("deleteFiles returns 0 for empty list")
    void testDeleteFilesEmpty() {
        int deleted = SystemCleanUtil.deleteFiles(Collections.emptyList());
        assertEquals(0, deleted);
    }

    @Test
    @DisplayName("findSymlinksAndJunctions returns list for valid path")
    void testFindSymlinks() {
        List<String[]> results = SystemCleanUtil.findSymlinksAndJunctions(System.getProperty("user.dir"), 2);
        assertNotNull(results);
    }

    @Test
    @DisplayName("findSymlinksAndJunctions handles nonexistent path")
    void testFindSymlinksInvalid() {
        List<String[]> results = SystemCleanUtil.findSymlinksAndJunctions("Z:\\nonexistent", 2);
        assertTrue(results.isEmpty());
    }
}
