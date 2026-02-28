package com.diskanalyzer.integration;

import com.diskanalyzer.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Integration tests: Parallel scanning with exclusions and PDF report generation.
 */
class ParallelScanAndReportIntegrationTest {

    @TempDir
    Path tempDir;

    private Path projectDir;

    @BeforeEach
    void setupProject() throws IOException {
        projectDir = tempDir.resolve("my_project");
        Files.createDirectories(projectDir.resolve("src"));
        Files.createDirectories(projectDir.resolve("build/output"));
        Files.createDirectories(projectDir.resolve("node_modules/pkg"));
        Files.createDirectories(projectDir.resolve("docs"));

        // Source files
        Files.write(projectDir.resolve("src/App.java"), "public class App {}".getBytes());
        Files.write(projectDir.resolve("src/Utils.java"), "public class Utils {}".getBytes());

        // Build output
        Files.write(projectDir.resolve("build/output/app.jar"), new byte[50000]);

        // Node modules (should be excluded)
        Files.write(projectDir.resolve("node_modules/pkg/index.js"), "module.exports = {};".getBytes());

        // Docs
        Files.write(projectDir.resolve("docs/readme.md"), "# My Project\nDocumentation here".getBytes());
        Files.write(projectDir.resolve("docs/guide.pdf"), new byte[30000]);
    }

    @Test
    @DisplayName("Parallel scan with exclusions → categorize → PDF report")
    void testFullScanToPdfPipeline() throws IOException {
        // 1. Define exclusions
        List<String> exclusions = Arrays.asList("node_modules", "*.jar");

        // 2. Parallel scan with exclusions
        ParallelScanner.ParallelScanResult scanResult = ParallelScanner.scan(
            projectDir.toString(), 0, 2, exclusions, null, null);

        // Verify node_modules files were excluded
        for (FileEntry entry : scanResult.getEntries()) {
            assertFalse(entry.getPath().contains("node_modules"),
                "node_modules should be excluded: " + entry.getPath());
            assertFalse(entry.getPath().endsWith(".jar"),
                "JAR files should be excluded: " + entry.getPath());
        }

        assertTrue(scanResult.getFileCount() > 0);

        // 3. Categorize results
        Map<String, long[]> grouped = FileCategories.groupByCategory(scanResult.getEntries());
        assertFalse(grouped.isEmpty());

        // 4. Generate PDF report
        // Build FullScanResult: (entries, largestFiles, extStats, totalSize, fileCount)
        List<FileEntry> largest = scanResult.getEntries().subList(
            0, Math.min(5, scanResult.getEntries().size()));

        AnalysisEngine.FullScanResult fullResult = new AnalysisEngine.FullScanResult(
            scanResult.getEntries(),
            largest,
            Collections.emptyList(), // no ext stats computed here
            scanResult.getTotalSize(),
            scanResult.getFileCount()
        );

        File pdfFile = tempDir.resolve("scan_report.pdf").toFile();
        PdfReportGenerator.generateReport(fullResult, projectDir.toString(), null, pdfFile);
        assertTrue(pdfFile.exists());
        assertTrue(pdfFile.length() > 100, "PDF should have content");

        // Verify PDF structure
        byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
        String pdfStr = new String(pdfBytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        assertTrue(pdfStr.startsWith("%PDF-"), "Should be valid PDF");
        assertTrue(pdfStr.contains("%%EOF"), "Should have proper EOF");
    }

    @Test
    @DisplayName("Scan directory sizes → verify structure")
    void testScanDirectorySizes() throws IOException {
        // scanDirectorySizes(String rootPath, int maxDepth, List<String> exclusions)
        Map<String, Long> dirSizes = ParallelScanner.scanDirectorySizes(
            projectDir.toString(), 5, null);

        assertNotNull(dirSizes);
        assertFalse(dirSizes.isEmpty(), "Should contain subdirectory sizes");
    }

    @Test
    @DisplayName("Parallel scan with cancellation")
    void testParallelScanCancellation() throws IOException {
        // Create many files
        Path bigDir = tempDir.resolve("big_dir");
        Files.createDirectories(bigDir);
        for (int i = 0; i < 100; i++) {
            Files.write(bigDir.resolve("file" + i + ".txt"), ("content " + i).getBytes());
        }

        // Uses boolean[] cancelFlag (not AtomicBoolean)
        boolean[] cancel = {false};
        ParallelScanner.ParallelScanResult result = ParallelScanner.scan(
            bigDir.toString(), 0, 2, null,
            (found, totalSize, currentPath) -> {
                if (found > 10) cancel[0] = true;
            }, cancel);

        // Should have some entries but possibly not all 100
        assertNotNull(result);
        assertTrue(result.getFileCount() > 0);
    }
}
