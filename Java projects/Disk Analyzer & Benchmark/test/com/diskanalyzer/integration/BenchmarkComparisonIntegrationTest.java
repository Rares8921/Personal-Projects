package com.diskanalyzer.integration;

import com.diskanalyzer.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Integration tests: Benchmark → Compare with DB → Export pipeline.
 * BenchmarkResult can only be created via fromJson() (private constructor).
 */
class BenchmarkComparisonIntegrationTest {

    @TempDir
    Path tempDir;

    private String originalDir;

    @BeforeEach
    void setup() {
        originalDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
    }

    @AfterEach
    void teardown() {
        System.setProperty("user.dir", originalDir);
    }

    private BenchmarkResult makeBenchmark(String type, double mbps) {
        return BenchmarkResult.fromJson(String.format(
            "{\"type\":\"%s\",\"throughputMBps\":%.1f,\"avgLatencyUs\":100," +
            "\"minLatencyUs\":50,\"maxLatencyUs\":200,\"iops\":50000," +
            "\"totalBytes\":1000000,\"elapsed\":2.0,\"blockSize\":131072,\"ops\":100}", type, mbps));
    }

    @Test
    @DisplayName("Benchmark results → compare with reference DB → export report")
    void testBenchmarkCompareAndExport() throws IOException {
        // 1. Create benchmark results (simulating a fast NVMe drive)
        List<BenchmarkResult> results = Arrays.asList(
            makeBenchmark("SEQ_READ", 3200.0),
            makeBenchmark("SEQ_WRITE", 2800.0),
            makeBenchmark("RAND_READ", 450.0),
            makeBenchmark("RAND_WRITE", 380.0)
        );

        // 2. Compare with reference DB
        BenchmarkDatabase.ComparisonResult comparison = BenchmarkDatabase.compare(results);
        assertNotNull(comparison);
        assertNotNull(comparison.getBestMatchDriveType());
        assertTrue(comparison.getOverallScore() > 0);
        assertTrue(comparison.getPercentile() >= 0 && comparison.getPercentile() <= 100);

        // 3. Export benchmark CSV
        File csvFile = tempDir.resolve("bench_results.csv").toFile();
        ExportUtil.exportBenchmarkToCsv(results, csvFile);
        assertTrue(csvFile.exists());
        List<String> csvLines = Files.readAllLines(csvFile.toPath());
        assertTrue(csvLines.size() >= 5); // header + 4 results

        // 4. Export benchmark JSON
        File jsonFile = tempDir.resolve("bench_results.json").toFile();
        ExportUtil.exportBenchmarkToJson(results, jsonFile);
        String json = Files.readString(jsonFile.toPath());
        assertTrue(json.contains("SEQ_READ"));
    }

    @Test
    @DisplayName("HDD-like results → lower score and percentile")
    void testHddBenchmarkComparison() {
        List<BenchmarkResult> hddResults = Arrays.asList(
            makeBenchmark("SEQ_READ", 120.0),
            makeBenchmark("SEQ_WRITE", 100.0),
            makeBenchmark("RAND_READ", 0.8),
            makeBenchmark("RAND_WRITE", 0.6)
        );

        BenchmarkDatabase.ComparisonResult comparison = BenchmarkDatabase.compare(hddResults);
        assertTrue(comparison.getOverallScore() < 50, "HDD score should be below 50");
    }

    @Test
    @DisplayName("Save and load benchmark history DB")
    void testBenchmarkLocalDatabase() throws IOException {
        // saveToDatabase(String driveName, List<BenchmarkResult> results)
        // uses hardcoded DB_DIR based on user.dir
        List<BenchmarkResult> results = Arrays.asList(
            makeBenchmark("SEQ_READ", 550.0),
            makeBenchmark("SEQ_WRITE", 500.0)
        );

        BenchmarkDatabase.saveToDatabase("C:\\TestDrive", results);

        // loadDatabase() returns List<Map<String, String>>
        List<Map<String, String>> records = BenchmarkDatabase.loadDatabase();
        assertFalse(records.isEmpty());
        assertEquals("C:\\TestDrive", records.get(0).get("drive"));
    }

    @Test
    @DisplayName("Reference database has all expected drive types")
    void testReferenceDatabaseContent() {
        List<BenchmarkDatabase.ReferenceEntry> refs = BenchmarkDatabase.getReferenceData();
        assertEquals(10, refs.size());

        // Verify key drive types exist (using public fields)
        Set<String> types = new HashSet<>();
        for (BenchmarkDatabase.ReferenceEntry ref : refs) {
            types.add(ref.driveType);
        }
        assertTrue(types.contains("NVMe SSD Gen4"));
        assertTrue(types.contains("SATA SSD"));
        assertTrue(types.contains("HDD 7200RPM"));
    }
}
