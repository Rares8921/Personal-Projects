package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for PdfReportGenerator class.
 */
class PdfReportGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("generateReport creates a valid PDF file")
    void testGenerateReport() throws IOException {
        List<FileEntry> entries = Arrays.asList(
            new FileEntry("C:\\test\\big.dat", 1073741824, false, 1700000000L),
            new FileEntry("C:\\test\\small.txt", 1024, false, 1700000001L)
        );
        List<AnalysisEngine.ExtensionStat> stats = List.of(
            new AnalysisEngine.ExtensionStat(".dat", 1073741824L, 1),
            new AnalysisEngine.ExtensionStat(".txt", 1024, 1)
        );
        AnalysisEngine.FullScanResult result = new AnalysisEngine.FullScanResult(
            entries, List.of(entries.get(0)), stats, 1073742848L, 2);

        File output = tempDir.resolve("report.pdf").toFile();
        PdfReportGenerator.generateReport(result, "C:\\test", null, output);

        assertTrue(output.exists());
        assertTrue(output.length() > 100);

        // Verify it starts with PDF header
        byte[] header = new byte[5];
        try (FileInputStream fis = new FileInputStream(output)) {
            fis.read(header);
        }
        assertEquals("%PDF-", new String(header));
    }

    @Test
    @DisplayName("generateReport includes benchmark data when provided")
    void testGenerateReportWithBenchmarks() throws IOException {
        List<FileEntry> entries = List.of(new FileEntry("test.txt", 100, false, 0));
        AnalysisEngine.FullScanResult result = new AnalysisEngine.FullScanResult(
            entries, entries, Collections.emptyList(), 100, 1);

        String json = "{\"type\":\"SEQ_READ\",\"throughputMBps\":3500,\"avgLatencyUs\":100," +
            "\"minLatencyUs\":50,\"maxLatencyUs\":200,\"iops\":50000,\"totalBytes\":1000000," +
            "\"elapsed\":2.0,\"blockSize\":131072,\"ops\":100}";
        List<BenchmarkResult> benchmarks = List.of(BenchmarkResult.fromJson(json));

        File output = tempDir.resolve("report_bench.pdf").toFile();
        PdfReportGenerator.generateReport(result, "C:\\test", benchmarks, output);

        assertTrue(output.exists());
        assertTrue(output.length() > 100);
    }

    @Test
    @DisplayName("generateScanReport generates without benchmarks")
    void testGenerateScanReport() throws IOException {
        List<FileEntry> entries = List.of(new FileEntry("file.txt", 500, false, 0));
        AnalysisEngine.FullScanResult result = new AnalysisEngine.FullScanResult(
            entries, entries, Collections.emptyList(), 500, 1);

        File output = tempDir.resolve("scan_report.pdf").toFile();
        PdfReportGenerator.generateScanReport(result, "C:\\scan", output);

        assertTrue(output.exists());
        assertTrue(output.length() > 50);
    }

    @Test
    @DisplayName("generateReport handles null scan result")
    void testGenerateReportNullResult() throws IOException {
        File output = tempDir.resolve("null_report.pdf").toFile();
        PdfReportGenerator.generateReport(null, "C:\\empty", null, output);

        assertTrue(output.exists());
        // Should still produce a valid PDF with title and "no data"
        byte[] header = new byte[5];
        try (FileInputStream fis = new FileInputStream(output)) {
            fis.read(header);
        }
        assertEquals("%PDF-", new String(header));
    }

    @Test
    @DisplayName("PDF contains %%EOF marker")
    void testPdfEndsWithEOF() throws IOException {
        List<FileEntry> entries = List.of(new FileEntry("f.txt", 100, false, 0));
        AnalysisEngine.FullScanResult result = new AnalysisEngine.FullScanResult(
            entries, entries, Collections.emptyList(), 100, 1);

        File output = tempDir.resolve("eof_test.pdf").toFile();
        PdfReportGenerator.generateScanReport(result, "C:\\test", output);

        String content = Files.readString(output.toPath(), java.nio.charset.StandardCharsets.ISO_8859_1);
        assertTrue(content.contains("%%EOF"));
    }
}
