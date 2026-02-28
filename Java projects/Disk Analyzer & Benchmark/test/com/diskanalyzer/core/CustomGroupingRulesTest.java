package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for CustomGroupingRules class.
 */
class CustomGroupingRulesTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("GroupRule matches extensions correctly")
    void testGroupRuleMatches() {
        CustomGroupingRules.GroupRule rule = new CustomGroupingRules.GroupRule(
            "TestGroup", Arrays.asList(".abc", ".xyz"), "#ff0000");

        assertTrue(rule.matches(".abc"));
        assertTrue(rule.matches(".xyz"));
        assertTrue(rule.matches(".ABC")); // case-insensitive via toLowerCase
        assertFalse(rule.matches(".txt"));
    }

    @Test
    @DisplayName("GroupRule matches FileEntry")
    void testGroupRuleMatchesFileEntry() {
        CustomGroupingRules.GroupRule rule = new CustomGroupingRules.GroupRule(
            "VMs", Arrays.asList(".vmdk", ".vdi"), "#ff7043");

        FileEntry vmFile = new FileEntry("disk.vmdk", 1000000, false, 0);
        FileEntry txtFile = new FileEntry("readme.txt", 100, false, 0);
        FileEntry dir = new FileEntry("folder", 0, true, 0);

        assertTrue(rule.matches(vmFile));
        assertFalse(rule.matches(txtFile));
        assertFalse(rule.matches(dir));
    }

    @Test
    @DisplayName("categorize uses custom rules first, then falls back to built-in")
    void testCategorizeWithCustomRules() {
        List<CustomGroupingRules.GroupRule> rules = List.of(
            new CustomGroupingRules.GroupRule("My Images", Arrays.asList(".jpg", ".png"), "#00ff00")
        );

        FileEntry jpgFile = new FileEntry("photo.jpg", 5000, false, 0);
        FileEntry pdfFile = new FileEntry("doc.pdf", 3000, false, 0);

        // jpg should match custom rule
        assertEquals("My Images", CustomGroupingRules.categorize(jpgFile, rules));
        // pdf should fall back to built-in "Documents"
        assertEquals("Documents", CustomGroupingRules.categorize(pdfFile, rules));
    }

    @Test
    @DisplayName("categorize returns Directories for directory entries")
    void testCategorizeDirectory() {
        FileEntry dir = new FileEntry("C:\\folder", 0, true, 0);
        assertEquals("Directories", CustomGroupingRules.categorize(dir, Collections.emptyList()));
    }

    @Test
    @DisplayName("groupByCustomRules groups entries correctly")
    void testGroupByCustomRules() {
        List<CustomGroupingRules.GroupRule> rules = List.of(
            new CustomGroupingRules.GroupRule("Projects", Arrays.asList(".sln", ".csproj"), "#4fc3f7")
        );

        List<FileEntry> entries = Arrays.asList(
            new FileEntry("app.sln", 1000, false, 0),
            new FileEntry("lib.csproj", 2000, false, 0),
            new FileEntry("readme.txt", 500, false, 0)
        );

        Map<String, long[]> groups = CustomGroupingRules.groupByCustomRules(entries, rules);
        assertNotNull(groups.get("Projects"));
        assertEquals(3000, groups.get("Projects")[0]);  // total size
        assertEquals(2, groups.get("Projects")[1]);      // count
        assertNotNull(groups.get("Documents"));          // txt falls to Documents
    }

    @Test
    @DisplayName("getAllCategories includes both custom and built-in")
    void testGetAllCategories() {
        List<CustomGroupingRules.GroupRule> rules = List.of(
            new CustomGroupingRules.GroupRule("Custom1", List.of(".x"), "#000"),
            new CustomGroupingRules.GroupRule("Custom2", List.of(".y"), "#fff")
        );

        List<String> cats = CustomGroupingRules.getAllCategories(rules);
        assertTrue(cats.contains("Custom1"));
        assertTrue(cats.contains("Custom2"));
        assertTrue(cats.contains("Documents"));
        assertTrue(cats.contains("Other"));
    }

    @Test
    @DisplayName("createDefaults returns sensible default rules")
    void testCreateDefaults() {
        List<CustomGroupingRules.GroupRule> defaults = CustomGroupingRules.createDefaults();
        assertFalse(defaults.isEmpty());
        assertTrue(defaults.stream().anyMatch(r -> r.getGroupName().equals("Virtual Machines")));
        assertTrue(defaults.stream().anyMatch(r -> r.getGroupName().equals("Project Files")));
    }

    @Test
    @DisplayName("saveRules and loadRules roundtrip correctly")
    void testSaveLoadRoundtrip() {
        String originalDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());

            List<CustomGroupingRules.GroupRule> rules = Arrays.asList(
                new CustomGroupingRules.GroupRule("TestGroup", Arrays.asList(".test", ".tst"), "#abcdef"),
                new CustomGroupingRules.GroupRule("Another", List.of(".xyz"), "#123456")
            );
            CustomGroupingRules.saveRules(rules);

            List<CustomGroupingRules.GroupRule> loaded = CustomGroupingRules.loadRules();
            assertEquals(2, loaded.size());
            assertEquals("TestGroup", loaded.get(0).getGroupName());
            assertTrue(loaded.get(0).getExtensions().contains(".test"));
            assertEquals("#abcdef", loaded.get(0).getColor());
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @Test
    @DisplayName("loadRules returns empty list when no config exists")
    void testLoadRulesNoConfig() {
        String originalDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.resolve("nonexistent").toString());
            List<CustomGroupingRules.GroupRule> rules = CustomGroupingRules.loadRules();
            assertTrue(rules.isEmpty(), "loadRules should return empty when config file does not exist");
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @Test
    @DisplayName("GroupRule toString returns readable format")
    void testGroupRuleToString() {
        CustomGroupingRules.GroupRule rule = new CustomGroupingRules.GroupRule(
            "Archives", Arrays.asList(".zip", ".rar"), "#000");
        String s = rule.toString();
        assertTrue(s.contains("Archives"));
        assertTrue(s.contains(".zip"));
    }
}
