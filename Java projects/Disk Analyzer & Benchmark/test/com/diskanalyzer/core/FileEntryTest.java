package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileEntry class.
 */
class FileEntryTest {

    @Test
    @DisplayName("Constructor stores all fields correctly")
    void testConstructor() {
        FileEntry fe = new FileEntry("C:\\Users\\test\\file.txt", 1024, false, 1700000000L);
        assertEquals("C:\\Users\\test\\file.txt", fe.getPath());
        assertEquals(1024, fe.getSize());
        assertFalse(fe.isDirectory());
        assertEquals(1700000000L, fe.getModifiedTime());
    }

    @Test
    @DisplayName("getName extracts filename from path")
    void testGetName() {
        FileEntry fe = new FileEntry("C:\\Users\\test\\file.txt", 100, false, 0);
        assertEquals("file.txt", fe.getName());
    }

    @Test
    @DisplayName("getName with forward slashes")
    void testGetNameForwardSlash() {
        FileEntry fe = new FileEntry("C:/Users/test/file.txt", 100, false, 0);
        assertEquals("file.txt", fe.getName());
    }

    @Test
    @DisplayName("getName returns path when no separator")
    void testGetNameNoSeparator() {
        FileEntry fe = new FileEntry("file.txt", 100, false, 0);
        assertEquals("file.txt", fe.getName());
    }

    @Test
    @DisplayName("getExtension returns lowercase extension with dot")
    void testGetExtension() {
        FileEntry fe = new FileEntry("test.TXT", 100, false, 0);
        assertEquals(".txt", fe.getExtension());
    }

    @Test
    @DisplayName("getExtension returns (none) for files without extension")
    void testGetExtensionNone() {
        FileEntry fe = new FileEntry("Makefile", 100, false, 0);
        assertEquals("(none)", fe.getExtension());
    }

    @Test
    @DisplayName("getExtension returns empty for directories")
    void testGetExtensionDirectory() {
        FileEntry fe = new FileEntry("C:\\folder", 0, true, 0);
        assertEquals("", fe.getExtension());
    }

    @Test
    @DisplayName("getFormattedSize returns human-readable size")
    void testGetFormattedSize() {
        FileEntry fe = new FileEntry("test", 1048576, false, 0);
        assertEquals("1.00 MB", fe.getFormattedSize());
    }

    @Test
    @DisplayName("compareTo sorts by size descending")
    void testCompareTo() {
        FileEntry small = new FileEntry("a", 100, false, 0);
        FileEntry large = new FileEntry("b", 1000, false, 0);
        // Descending: large comes first, so small.compareTo(large) > 0
        assertTrue(small.compareTo(large) > 0);
        assertTrue(large.compareTo(small) < 0);
        assertEquals(0, small.compareTo(new FileEntry("c", 100, false, 0)));
    }

    @Test
    @DisplayName("toString contains path and size info")
    void testToString() {
        FileEntry fe = new FileEntry("C:\\test.txt", 512, false, 0);
        String s = fe.toString();
        assertTrue(s.contains("C:\\test.txt"));
        assertTrue(s.contains("512"));
    }

    @Test
    @DisplayName("toString shows [DIR] for directories")
    void testToStringDirectory() {
        FileEntry fe = new FileEntry("C:\\folder", 0, true, 0);
        assertTrue(fe.toString().contains("[DIR]"));
    }
}
