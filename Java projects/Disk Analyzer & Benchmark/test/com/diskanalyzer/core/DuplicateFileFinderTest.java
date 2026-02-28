package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for DuplicateFileFinder class.
 */
class DuplicateFileFinderTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("findDuplicates detects files with same content")
    void testFindDuplicates() throws IOException {
        // Create duplicate files
        byte[] content = "This is duplicate content for testing purposes.".getBytes();
        Files.write(tempDir.resolve("file1.txt"), content);
        Files.write(tempDir.resolve("file2.txt"), content);
        Files.write(tempDir.resolve("unique.txt"), "Unique file".getBytes());

        List<DuplicateFileFinder.DuplicateGroup> groups =
            DuplicateFileFinder.findDuplicates(tempDir.toString(), 0, null, null);

        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).getCount());
        assertEquals(content.length, groups.get(0).getSize());
    }

    @Test
    @DisplayName("findDuplicates respects minSize filter")
    void testMinSizeFilter() throws IOException {
        byte[] small = "sm".getBytes();
        Files.write(tempDir.resolve("a.txt"), small);
        Files.write(tempDir.resolve("b.txt"), small);

        List<DuplicateFileFinder.DuplicateGroup> groups =
            DuplicateFileFinder.findDuplicates(tempDir.toString(), 1000, null, null);

        assertTrue(groups.isEmpty()); // files too small
    }

    @Test
    @DisplayName("findDuplicates returns empty for no duplicates")
    void testNoDuplicates() throws IOException {
        Files.write(tempDir.resolve("a.txt"), "content A".getBytes());
        Files.write(tempDir.resolve("b.txt"), "content B".getBytes());
        Files.write(tempDir.resolve("c.txt"), "content C".getBytes());

        List<DuplicateFileFinder.DuplicateGroup> groups =
            DuplicateFileFinder.findDuplicates(tempDir.toString(), 0, null, null);

        assertTrue(groups.isEmpty());
    }

    @Test
    @DisplayName("findDuplicates can be cancelled")
    void testCancellation() throws IOException {
        byte[] content = "duplicate data".getBytes();
        for (int i = 0; i < 10; i++) {
            Files.write(tempDir.resolve("file" + i + ".txt"), content);
        }

        boolean[] cancelFlag = {true}; // immediately cancelled
        List<DuplicateFileFinder.DuplicateGroup> groups =
            DuplicateFileFinder.findDuplicates(tempDir.toString(), 0, null, cancelFlag);

        // May be empty or partial due to immediate cancellation
        assertNotNull(groups);
    }

    @Test
    @DisplayName("DuplicateGroup calculates wasted space correctly")
    void testWastedSpace() {
        DuplicateFileFinder.DuplicateGroup group = new DuplicateFileFinder.DuplicateGroup(
            "abc123", 1000, Arrays.asList("file1.txt", "file2.txt", "file3.txt"));

        assertEquals(1000, group.getSize());
        assertEquals(3, group.getCount());
        assertEquals(2000, group.getWastedSpace()); // 1000 * (3-1)
        assertEquals("abc123", group.getHash());
    }

    @Test
    @DisplayName("findDuplicates reports progress")
    void testProgressCallback() throws IOException {
        byte[] content = "progress test content".getBytes();
        Files.write(tempDir.resolve("p1.txt"), content);
        Files.write(tempDir.resolve("p2.txt"), content);

        List<String> progressFiles = new ArrayList<>();
        DuplicateFileFinder.ProgressCallback cb = (processed, total, file) -> {
            progressFiles.add(file);
        };

        DuplicateFileFinder.findDuplicates(tempDir.toString(), 0, cb, null);
        assertFalse(progressFiles.isEmpty());
    }

    @Test
    @DisplayName("findDuplicates handles empty directory")
    void testEmptyDirectory() throws IOException {
        Path emptyDir = tempDir.resolve("empty");
        Files.createDirectory(emptyDir);

        List<DuplicateFileFinder.DuplicateGroup> groups =
            DuplicateFileFinder.findDuplicates(emptyDir.toString(), 0, null, null);

        assertTrue(groups.isEmpty());
    }
}
