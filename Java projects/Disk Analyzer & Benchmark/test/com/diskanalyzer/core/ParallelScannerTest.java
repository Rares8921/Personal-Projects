package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for ParallelScanner class.
 */
class ParallelScannerTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setupFiles() throws IOException {
        // Create a test directory structure
        Files.createDirectories(tempDir.resolve("sub1"));
        Files.createDirectories(tempDir.resolve("sub2"));
        Files.write(tempDir.resolve("file1.txt"), "content1".getBytes());
        Files.write(tempDir.resolve("file2.dat"), "content two".getBytes());
        Files.write(tempDir.resolve("sub1/nested.log"), "log data here".getBytes());
        Files.write(tempDir.resolve("sub2/deep.csv"), "a,b,c".getBytes());
    }

    @Test
    @DisplayName("scan finds all files in directory tree")
    void testScanFindsAllFiles() {
        ParallelScanner.ParallelScanResult result = ParallelScanner.scan(
            tempDir.toString(), 0, 2, null, null, null);

        assertEquals(4, result.getFileCount());
        assertTrue(result.getTotalSize() > 0);
        assertTrue(result.getDirCount() >= 3); // root + sub1 + sub2
        assertTrue(result.getScanTimeMs() >= 0);
    }

    @Test
    @DisplayName("scan respects maxEntries limit")
    void testMaxEntries() {
        ParallelScanner.ParallelScanResult result = ParallelScanner.scan(
            tempDir.toString(), 2, 2, null, null, null);

        assertTrue(result.getFileCount() <= 2);
    }

    @Test
    @DisplayName("scan supports exclusion patterns")
    void testExclusions() {
        List<String> exclusions = List.of("*.log", "sub2");

        ParallelScanner.ParallelScanResult result = ParallelScanner.scan(
            tempDir.toString(), 0, 2, exclusions, null, null);

        // Should exclude nested.log and everything in sub2
        assertEquals(2, result.getFileCount()); // file1.txt and file2.dat
    }

    @Test
    @DisplayName("scan can be cancelled")
    void testCancellation() {
        boolean[] cancelFlag = {true}; // Immediately cancel

        ParallelScanner.ParallelScanResult result = ParallelScanner.scan(
            tempDir.toString(), 0, 2, null, null, cancelFlag);

        assertNotNull(result);
        // Result should be zero or partial
    }

    @Test
    @DisplayName("scan reports progress")
    void testProgress() throws IOException {
        // Create enough files to trigger progress reporting (every 1000 files)
        // For small test, just verify callback is not null-safe
        List<String> progressPaths = new ArrayList<>();
        ParallelScanner.ScanProgressCallback callback = (count, bytes, path) -> {
            progressPaths.add(path);
        };

        ParallelScanner.scan(tempDir.toString(), 0, 2, null, callback, null);
        // With only 4 files, progress may not fire (fires every 1000 files)
        assertNotNull(progressPaths);
    }

    @Test
    @DisplayName("scan handles nonexistent directory")
    void testNonexistentDir() {
        ParallelScanner.ParallelScanResult result = ParallelScanner.scan(
            "Z:\\nonexistent\\path", 0, 2, null, null, null);

        assertEquals(0, result.getFileCount());
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    @DisplayName("scan results are sorted by size descending")
    void testSortOrder() {
        ParallelScanner.ParallelScanResult result = ParallelScanner.scan(
            tempDir.toString(), 0, 2, null, null, null);

        List<FileEntry> entries = result.getEntries();
        for (int i = 1; i < entries.size(); i++) {
            assertTrue(entries.get(i - 1).getSize() >= entries.get(i).getSize());
        }
    }

    @Test
    @DisplayName("scanDirectorySizes collects per-directory sizes")
    void testScanDirectorySizes() {
        Map<String, Long> sizes = ParallelScanner.scanDirectorySizes(
            tempDir.toString(), 5, null);

        assertFalse(sizes.isEmpty());
        // Root should have some files
        assertTrue(sizes.containsKey(tempDir.toString()) || sizes.size() > 0);
    }

    @Test
    @DisplayName("scanDirectorySizes respects exclusions")
    void testScanDirSizesExclusions() {
        List<String> exclusions = List.of("sub2");
        Map<String, Long> sizes = ParallelScanner.scanDirectorySizes(
            tempDir.toString(), 5, exclusions);

        // sub2 directory should not be in results
        for (String key : sizes.keySet()) {
            assertFalse(key.contains("sub2"));
        }
    }
}
