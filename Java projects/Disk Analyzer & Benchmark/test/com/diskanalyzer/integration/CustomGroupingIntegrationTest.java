package com.diskanalyzer.integration;

import com.diskanalyzer.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Integration tests: Custom grouping rules → scan → categorize → export pipeline.
 */
class CustomGroupingIntegrationTest {

    @TempDir
    Path tempDir;

    private Path testDataDir;

    @BeforeEach
    void setupTestData() throws IOException {
        testDataDir = tempDir.resolve("project_files");
        Files.createDirectories(testDataDir);

        // Create various file types
        Files.write(testDataDir.resolve("main.java"), "public class Main {}".getBytes());
        Files.write(testDataDir.resolve("style.css"), "body{}".getBytes());
        Files.write(testDataDir.resolve("readme.md"), "# Hello".getBytes());
        Files.write(testDataDir.resolve("data.csv"), "a,b,c".getBytes());
        Files.write(testDataDir.resolve("scene.blend"), "blender data".getBytes());
        Files.write(testDataDir.resolve("model.obj"), "3d model data".getBytes());
        Files.write(testDataDir.resolve("disk.vmdk"), "virtual disk data".getBytes());
        Files.write(testDataDir.resolve("photo.png"), "PNG fake data".getBytes());
    }

    @Test
    @DisplayName("Define custom rules → scan → group → verify categorization")
    void testCustomRuleGrouping() throws IOException {
        // 1. Define custom rules
        List<CustomGroupingRules.GroupRule> rules = new ArrayList<>();
        rules.add(new CustomGroupingRules.GroupRule("Source Code", Arrays.asList(".java", ".css"), "#00FF00"));
        rules.add(new CustomGroupingRules.GroupRule("3D Assets", Arrays.asList(".blend", ".obj"), "#FF0000"));

        // 2. Scan directory
        ParallelScanner.ParallelScanResult scanResult = ParallelScanner.scan(
            testDataDir.toString(), 0, 2, null, null, null);

        // 3. Group with custom rules — returns Map<String, long[]> where long[0]=totalSize, long[1]=count
        Map<String, long[]> grouped = CustomGroupingRules.groupByCustomRules(scanResult.getEntries(), rules);

        assertTrue(grouped.containsKey("Source Code"), "Should have 'Source Code' group");
        assertEquals(2, (int) grouped.get("Source Code")[1]); // 2 files: .java, .css

        assertTrue(grouped.containsKey("3D Assets"), "Should have '3D Assets' group");
        assertEquals(2, (int) grouped.get("3D Assets")[1]); // 2 files: .blend, .obj
    }

    @Test
    @DisplayName("Custom rules with save/load → export grouped results")
    void testCustomRulesSaveLoadAndExport() throws IOException {
        // 1. Define and save custom rules — uses CONFIG_DIR based on user.dir
        String originalDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());

            List<CustomGroupingRules.GroupRule> rules = new ArrayList<>();
            rules.add(new CustomGroupingRules.GroupRule("Data Files", Arrays.asList(".csv", ".md"), "#0000FF"));
            CustomGroupingRules.saveRules(rules);

            // 2. Load rules back
            List<CustomGroupingRules.GroupRule> loaded = CustomGroupingRules.loadRules();
            assertEquals(1, loaded.size());
            assertEquals("Data Files", loaded.get(0).getGroupName());

            // 3. Scan and categorize using loaded rules
            ParallelScanner.ParallelScanResult scanResult = ParallelScanner.scan(
                testDataDir.toString(), 0, 2, null, null, null);

            Map<String, long[]> grouped = CustomGroupingRules.groupByCustomRules(scanResult.getEntries(), loaded);
            assertTrue(grouped.containsKey("Data Files"));
            assertTrue(grouped.get("Data Files")[1] >= 2); // .csv and .md files

            // 4. Export all scanned entries to CSV
            File csvFile = tempDir.resolve("grouped_export.csv").toFile();
            ExportUtil.exportFileEntriesToCsv(scanResult.getEntries(), csvFile);
            assertTrue(csvFile.exists());
            List<String> lines = Files.readAllLines(csvFile.toPath());
            assertTrue(lines.size() > 1, "CSV should have header + data rows");
        } finally {
            System.setProperty("user.dir", originalDir);
        }
    }

    @Test
    @DisplayName("Custom rules override standard categorization")
    void testCustomRulesOverrideStandard() {
        FileEntry javaFile = new FileEntry(testDataDir.resolve("main.java").toString(), 100, false, 0);
        
        // Without custom rules, .java should be "Code"
        String standard = FileCategories.categorize(javaFile.getExtension());
        assertEquals("Code", standard);

        // With custom rule mapping .java to "Project Sources"
        List<CustomGroupingRules.GroupRule> rules = new ArrayList<>();
        rules.add(new CustomGroupingRules.GroupRule("Project Sources", Arrays.asList(".java"), "#00FF00"));
        
        String custom = CustomGroupingRules.categorize(javaFile, rules);
        assertEquals("Project Sources", custom);
    }
}
