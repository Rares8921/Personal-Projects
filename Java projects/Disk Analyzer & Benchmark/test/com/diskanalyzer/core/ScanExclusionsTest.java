package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for ScanExclusions class.
 */
class ScanExclusionsTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("shouldExclude matches exact name")
    void testExactMatch() {
        List<String> patterns = List.of("pagefile.sys");
        assertTrue(ScanExclusions.shouldExclude("C:\\pagefile.sys", patterns));
        assertFalse(ScanExclusions.shouldExclude("C:\\other.sys", patterns));
    }

    @Test
    @DisplayName("shouldExclude matches partial path")
    void testPartialPathMatch() {
        List<String> patterns = List.of("$Recycle.Bin");
        assertTrue(ScanExclusions.shouldExclude("C:\\$Recycle.Bin\\file.txt", patterns));
    }

    @Test
    @DisplayName("shouldExclude handles glob patterns with wildcards")
    void testGlobPattern() {
        List<String> patterns = List.of("*.tmp");
        assertTrue(ScanExclusions.shouldExclude("C:\\Users\\test.tmp", patterns));
        assertFalse(ScanExclusions.shouldExclude("C:\\Users\\test.txt", patterns));
    }

    @Test
    @DisplayName("shouldExclude handles glob with question mark")
    void testGlobQuestionMark() {
        List<String> patterns = List.of("file?.txt");
        assertTrue(ScanExclusions.shouldExclude("C:\\file1.txt", patterns));
        assertTrue(ScanExclusions.shouldExclude("C:\\fileA.txt", patterns));
        assertFalse(ScanExclusions.shouldExclude("C:\\file12.txt", patterns));
    }

    @Test
    @DisplayName("shouldExclude handles regex patterns")
    void testRegexPattern() {
        List<String> patterns = List.of("regex:\\.(bak|tmp|old)$");
        assertTrue(ScanExclusions.shouldExclude("C:\\data.bak", patterns));
        assertTrue(ScanExclusions.shouldExclude("C:\\temp.tmp", patterns));
        assertFalse(ScanExclusions.shouldExclude("C:\\file.txt", patterns));
    }

    @Test
    @DisplayName("shouldExclude is case-insensitive for matches")
    void testCaseInsensitive() {
        List<String> patterns = List.of("pagefile.sys");
        assertTrue(ScanExclusions.shouldExclude("C:\\PAGEFILE.SYS", patterns));
    }

    @Test
    @DisplayName("shouldExclude returns false for null/empty")
    void testNullEmpty() {
        assertFalse(ScanExclusions.shouldExclude(null, List.of("*.tmp")));
        assertFalse(ScanExclusions.shouldExclude("test.txt", null));
        assertFalse(ScanExclusions.shouldExclude("test.txt", Collections.emptyList()));
    }

    @Test
    @DisplayName("filterEntries removes excluded files")
    void testFilterEntries() {
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("C:\\keep.txt", 100, false, 0),
            new FileEntry("C:\\remove.tmp", 200, false, 0),
            new FileEntry("C:\\$Recycle.Bin\\deleted", 300, false, 0)
        );
        List<String> patterns = List.of("*.tmp", "$Recycle.Bin");

        List<FileEntry> filtered = ScanExclusions.filterEntries(entries, patterns);
        assertEquals(1, filtered.size());
        assertEquals("C:\\keep.txt", filtered.get(0).getPath());
    }

    @Test
    @DisplayName("getDefaultExclusions returns known system exclusions")
    void testDefaultExclusions() {
        List<String> defaults = ScanExclusions.getDefaultExclusions();
        assertTrue(defaults.contains("$Recycle.Bin"));
        assertTrue(defaults.contains("pagefile.sys"));
        assertTrue(defaults.contains("System Volume Information"));
    }

    @Test
    @DisplayName("globToRegex converts wildcards correctly")
    void testGlobToRegex() {
        String regex = ScanExclusions.globToRegex("*.txt");
        assertTrue("test.txt".matches(".*" + regex + ".*"));

        String regex2 = ScanExclusions.globToRegex("file?.log");
        assertTrue("file1.log".matches(".*" + regex2 + ".*"));
    }

    @Test
    @DisplayName("loadExclusions includes defaults")
    void testLoadExclusionsDefaults() {
        List<String> exclusions = ScanExclusions.loadExclusions();
        assertTrue(exclusions.size() >= ScanExclusions.getDefaultExclusions().size());
    }

    @Test
    @DisplayName("saveExclusions and loadExclusions roundtrip")
    void testSaveLoadRoundtrip() throws IOException {
        String originalDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());
            List<String> patterns = Arrays.asList("*.log", "regex:\\.cache$", "node_modules");
            ScanExclusions.saveExclusions(patterns);

            Path configFile = tempDir.resolve("config").resolve("exclusions.txt");
            assertTrue(Files.exists(configFile), "Config file should exist after save");

            String content = Files.readString(configFile);
            assertTrue(content.contains("*.log"));
            assertTrue(content.contains("node_modules"));

            // Verify loadExclusions includes our patterns (plus defaults)
            List<String> loaded = ScanExclusions.loadExclusions();
            assertTrue(loaded.contains("*.log"));
            assertTrue(loaded.contains("node_modules"));
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }
}
