package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for ExportUtil class.
 */
class ExportUtilTest {

    @TempDir
    Path tempDir;

    private List<FileEntry> sampleEntries() {
        return Arrays.asList(
            new FileEntry("C:\\docs\\report.pdf", 102400, false, 1700000000L),
            new FileEntry("C:\\images\\photo.jpg", 2048576, false, 1700000001L),
            new FileEntry("C:\\folder", 0, true, 1700000002L)
        );
    }

    @Test
    @DisplayName("exportFileEntriesToCsv creates valid CSV file")
    void testExportCsv() throws IOException {
        File out = tempDir.resolve("test.csv").toFile();
        ExportUtil.exportFileEntriesToCsv(sampleEntries(), out);

        assertTrue(out.exists());
        List<String> lines = Files.readAllLines(out.toPath());
        assertEquals(4, lines.size()); // header + 3 entries
        assertTrue(lines.get(0).startsWith("Name,Path,Size"));
        assertTrue(lines.get(1).contains("report.pdf"));
        assertTrue(lines.get(2).contains("photo.jpg"));
    }

    @Test
    @DisplayName("exportFileEntriesToJson creates valid JSON file")
    void testExportJson() throws IOException {
        File out = tempDir.resolve("test.json").toFile();
        ExportUtil.exportFileEntriesToJson(sampleEntries(), out);

        assertTrue(out.exists());
        String content = Files.readString(out.toPath());
        assertTrue(content.contains("\"totalEntries\": 3"));
        assertTrue(content.contains("report.pdf"));
        assertTrue(content.contains("photo.jpg"));
        assertTrue(content.startsWith("{"));
        assertTrue(content.trim().endsWith("}"));
    }

    @Test
    @DisplayName("exportExtStatsToCsv exports extension statistics")
    void testExportExtStatsCsv() throws IOException {
        List<AnalysisEngine.ExtensionStat> stats = Arrays.asList(
            new AnalysisEngine.ExtensionStat(".pdf", 500000, 10),
            new AnalysisEngine.ExtensionStat(".jpg", 2000000, 50)
        );
        File out = tempDir.resolve("ext_stats.csv").toFile();
        ExportUtil.exportExtStatsToCsv(stats, out);

        assertTrue(out.exists());
        List<String> lines = Files.readAllLines(out.toPath());
        assertEquals(3, lines.size());
        assertTrue(lines.get(1).contains(".pdf"));
    }

    @Test
    @DisplayName("exportExtStatsToJson exports extension stats as JSON")
    void testExportExtStatsJson() throws IOException {
        List<AnalysisEngine.ExtensionStat> stats = Arrays.asList(
            new AnalysisEngine.ExtensionStat(".pdf", 500000, 10)
        );
        File out = tempDir.resolve("ext_stats.json").toFile();
        ExportUtil.exportExtStatsToJson(stats, out);

        String content = Files.readString(out.toPath());
        assertTrue(content.contains("\"totalTypes\": 1"));
        assertTrue(content.contains(".pdf"));
    }

    @Test
    @DisplayName("exportBenchmarkToCsv exports benchmark results")
    void testExportBenchmarkCsv() throws IOException {
        String json = "{\"type\":\"SEQ_READ\",\"throughputMBps\":3500,\"avgLatencyUs\":100," +
            "\"minLatencyUs\":50,\"maxLatencyUs\":200,\"iops\":50000,\"totalBytes\":1000000," +
            "\"elapsed\":2.0,\"blockSize\":131072,\"ops\":100}";
        List<BenchmarkResult> results = List.of(BenchmarkResult.fromJson(json));

        File out = tempDir.resolve("bench.csv").toFile();
        ExportUtil.exportBenchmarkToCsv(results, out);

        assertTrue(out.exists());
        List<String> lines = Files.readAllLines(out.toPath());
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("SEQ_READ"));
    }

    @Test
    @DisplayName("exportBenchmarkToJson exports benchmark results as JSON")
    void testExportBenchmarkJson() throws IOException {
        String json = "{\"type\":\"SEQ_WRITE\",\"throughputMBps\":3000,\"avgLatencyUs\":150," +
            "\"minLatencyUs\":60,\"maxLatencyUs\":250,\"iops\":40000,\"totalBytes\":2000000," +
            "\"elapsed\":3.0,\"blockSize\":131072,\"ops\":200}";
        List<BenchmarkResult> results = List.of(BenchmarkResult.fromJson(json));

        File out = tempDir.resolve("bench.json").toFile();
        ExportUtil.exportBenchmarkToJson(results, out);

        String content = Files.readString(out.toPath());
        assertTrue(content.contains("SEQ_WRITE"));
        assertTrue(content.contains("benchmarkResults"));
    }

    @Test
    @DisplayName("exportFullReport generates complete JSON report")
    void testExportFullReport() throws IOException {
        List<FileEntry> entries = sampleEntries();
        List<FileEntry> largest = List.of(entries.get(1));
        List<AnalysisEngine.ExtensionStat> stats = List.of(
            new AnalysisEngine.ExtensionStat(".jpg", 2048576, 1)
        );
        AnalysisEngine.FullScanResult result = new AnalysisEngine.FullScanResult(
            entries, largest, stats, 2150976, 2);

        File out = tempDir.resolve("full_report.json").toFile();
        ExportUtil.exportFullReport(result, "C:\\test", out);

        assertTrue(out.exists());
        String content = Files.readString(out.toPath());
        assertTrue(content.contains("\"scanPath\""));
        assertTrue(content.contains("\"entries\""));
        assertTrue(content.contains("\"largestFiles\""));
        assertTrue(content.contains("\"extensionStats\""));
    }
}
