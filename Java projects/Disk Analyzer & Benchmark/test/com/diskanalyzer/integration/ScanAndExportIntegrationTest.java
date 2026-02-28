package com.diskanalyzer.integration;

import com.diskanalyzer.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Integration tests: Full scan → export pipeline.
 * Tests the end-to-end flow from scanning to exporting in all formats.
 */
class ScanAndExportIntegrationTest {

    @TempDir
    Path tempDir;

    private Path testDataDir;

    @BeforeEach
    void setupTestData() throws IOException {
        testDataDir = tempDir.resolve("scan_data");
        Files.createDirectories(testDataDir);

        // Create a realistic directory structure
        Files.createDirectories(testDataDir.resolve("documents"));
        Files.createDirectories(testDataDir.resolve("images"));
        Files.createDirectories(testDataDir.resolve("code/src"));
        Files.createDirectories(testDataDir.resolve("temp"));

        Files.write(testDataDir.resolve("documents/report.pdf"), new byte[102400]);
        Files.write(testDataDir.resolve("documents/readme.txt"), "# Readme\nThis is a test file.".getBytes());
        Files.write(testDataDir.resolve("images/photo.jpg"), new byte[2048000]);
        Files.write(testDataDir.resolve("images/icon.png"), new byte[4096]);
        Files.write(testDataDir.resolve("code/src/Main.java"), "public class Main { }".getBytes());
        Files.write(testDataDir.resolve("code/src/Utils.java"), "public class Utils { }".getBytes());
        Files.write(testDataDir.resolve("temp/cache.tmp"), new byte[8192]);
    }

    @Test
    @DisplayName("Full pipeline: ParallelScan → categorize → export CSV")
    void testScanCategorizeExportCsv() throws IOException {
        // 1. Scan
        ParallelScanner.ParallelScanResult scanResult = ParallelScanner.scan(
            testDataDir.toString(), 0, 2, null, null, null);

        assertTrue(scanResult.getFileCount() >= 7);
        assertTrue(scanResult.getTotalSize() > 0);

        // 2. Categorize
        Map<String, long[]> categories = FileCategories.groupByCategory(scanResult.getEntries());
        assertFalse(categories.isEmpty());
        assertTrue(categories.containsKey("Documents") || categories.containsKey("Images"));

        // 3. Export CSV
        File csvOutput = tempDir.resolve("export.csv").toFile();
        ExportUtil.exportFileEntriesToCsv(scanResult.getEntries(), csvOutput);

        assertTrue(csvOutput.exists());
        List<String> lines = Files.readAllLines(csvOutput.toPath());
        assertTrue(lines.size() > 1);
        assertTrue(lines.get(0).contains("Name,Path"));
    }

    @Test
    @DisplayName("Full pipeline: ParallelScan → export JSON")
    void testScanExportJson() throws IOException {
        ParallelScanner.ParallelScanResult scanResult = ParallelScanner.scan(
            testDataDir.toString(), 0, 2, null, null, null);

        File jsonOutput = tempDir.resolve("export.json").toFile();
        ExportUtil.exportFileEntriesToJson(scanResult.getEntries(), jsonOutput);

        assertTrue(jsonOutput.exists());
        String content = Files.readString(jsonOutput.toPath());
        assertTrue(content.contains("\"entries\""));
        assertTrue(content.contains("photo.jpg") || content.contains("report.pdf"));
    }

    @Test
    @DisplayName("Full pipeline: ParallelScan → PDF report")
    void testScanExportPdf() throws IOException {
        ParallelScanner.ParallelScanResult scanResult = ParallelScanner.scan(
            testDataDir.toString(), 0, 2, null, null, null);

        // Convert to FullScanResult format
        AnalysisEngine.FullScanResult fullResult = new AnalysisEngine.FullScanResult(
            scanResult.getEntries(),
            scanResult.getEntries().subList(0, Math.min(5, scanResult.getEntries().size())),
            Collections.emptyList(),
            scanResult.getTotalSize(),
            scanResult.getFileCount()
        );

        File pdfOutput = tempDir.resolve("report.pdf").toFile();
        PdfReportGenerator.generateScanReport(fullResult, testDataDir.toString(), pdfOutput);

        assertTrue(pdfOutput.exists());
        assertTrue(pdfOutput.length() > 100);

        // Verify PDF header
        byte[] header = new byte[5];
        try (FileInputStream fis = new FileInputStream(pdfOutput)) { fis.read(header); }
        assertEquals("%PDF-", new String(header));
    }

    @Test
    @DisplayName("Scan with exclusions → filtered export")
    void testScanWithExclusions() throws IOException {
        List<String> exclusions = List.of("*.tmp", "temp");

        ParallelScanner.ParallelScanResult scanResult = ParallelScanner.scan(
            testDataDir.toString(), 0, 2, exclusions, null, null);

        // The temp directory and .tmp files should be excluded
        for (FileEntry e : scanResult.getEntries()) {
            assertFalse(e.getPath().endsWith(".tmp"), "Should exclude .tmp files");
            assertFalse(e.getPath().contains("temp" + File.separator), "Should exclude temp dir");
        }

        // Export the filtered results
        File csvOutput = tempDir.resolve("filtered.csv").toFile();
        ExportUtil.exportFileEntriesToCsv(scanResult.getEntries(), csvOutput);
        assertTrue(csvOutput.exists());
    }
}
