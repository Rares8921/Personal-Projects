package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for BenchmarkDatabase class.
 */
class BenchmarkDatabaseTest {

    private List<BenchmarkResult> createSampleResults() {
        List<BenchmarkResult> results = new ArrayList<>();
        String[] types = {"SEQ_READ", "SEQ_WRITE", "RAND_READ", "RAND_WRITE"};
        double[] speeds = {3500, 3000, 400, 350};
        for (int i = 0; i < types.length; i++) {
            String json = String.format(
                "{\"type\":\"%s\",\"throughputMBps\":%.1f,\"avgLatencyUs\":100," +
                "\"minLatencyUs\":50,\"maxLatencyUs\":200,\"iops\":50000," +
                "\"totalBytes\":1000000,\"elapsed\":2.0,\"blockSize\":131072,\"ops\":100}",
                types[i], speeds[i]);
            results.add(BenchmarkResult.fromJson(json));
        }
        return results;
    }

    @Test
    @DisplayName("compare identifies NVMe Gen3 for high-speed results")
    void testCompareNVMe() {
        List<BenchmarkResult> results = createSampleResults();
        BenchmarkDatabase.ComparisonResult comp = BenchmarkDatabase.compare(results);

        assertNotNull(comp);
        assertNotNull(comp.getBestMatchDriveType());
        assertTrue(comp.getBestMatchDriveType().contains("NVMe"));
        assertTrue(comp.getOverallScore() > 0);
    }

    @Test
    @DisplayName("compare identifies HDD for slow results")
    void testCompareHDD() {
        List<BenchmarkResult> results = new ArrayList<>();
        String[] types = {"SEQ_READ", "SEQ_WRITE", "RAND_READ", "RAND_WRITE"};
        double[] speeds = {150, 140, 1.0, 1.0};
        for (int i = 0; i < types.length; i++) {
            String json = String.format(
                "{\"type\":\"%s\",\"throughputMBps\":%.1f,\"avgLatencyUs\":5000," +
                "\"minLatencyUs\":1000,\"maxLatencyUs\":10000,\"iops\":100," +
                "\"totalBytes\":1000000,\"elapsed\":10.0,\"blockSize\":4096,\"ops\":100}",
                types[i], speeds[i]);
            results.add(BenchmarkResult.fromJson(json));
        }

        BenchmarkDatabase.ComparisonResult comp = BenchmarkDatabase.compare(results);
        assertTrue(comp.getBestMatchDriveType().contains("HDD"));
    }

    @Test
    @DisplayName("compare returns percentile between 0 and 100")
    void testPercentileRange() {
        BenchmarkDatabase.ComparisonResult comp = BenchmarkDatabase.compare(createSampleResults());
        assertTrue(comp.getPercentile() >= 0);
        assertTrue(comp.getPercentile() <= 100);
    }

    @Test
    @DisplayName("compare returns test scores and rankings")
    void testScoresAndRankings() {
        BenchmarkDatabase.ComparisonResult comp = BenchmarkDatabase.compare(createSampleResults());
        assertFalse(comp.getTestScores().isEmpty());
        assertFalse(comp.getRankings().isEmpty());
        assertFalse(comp.getReferenceScores().isEmpty());
    }

    @Test
    @DisplayName("getReferenceData returns 10 reference entries")
    void testReferenceData() {
        List<BenchmarkDatabase.ReferenceEntry> refs = BenchmarkDatabase.getReferenceData();
        assertEquals(10, refs.size());
        assertTrue(refs.stream().anyMatch(r -> r.driveType.contains("NVMe")));
        assertTrue(refs.stream().anyMatch(r -> r.driveType.contains("HDD")));
        assertTrue(refs.stream().anyMatch(r -> r.driveType.contains("SATA SSD")));
    }

    @Test
    @DisplayName("overallScore stays between 0 and 100")
    void testOverallScoreRange() {
        BenchmarkDatabase.ComparisonResult comp = BenchmarkDatabase.compare(createSampleResults());
        assertTrue(comp.getOverallScore() >= 0);
        assertTrue(comp.getOverallScore() <= 100);
    }

    @Test
    @DisplayName("compare handles single result type")
    void testCompareSingleResult() {
        String json = "{\"type\":\"SEQ_READ\",\"throughputMBps\":500,\"avgLatencyUs\":100," +
            "\"minLatencyUs\":50,\"maxLatencyUs\":200,\"iops\":50000,\"totalBytes\":1000000," +
            "\"elapsed\":2.0,\"blockSize\":131072,\"ops\":100}";
        List<BenchmarkResult> results = List.of(BenchmarkResult.fromJson(json));

        BenchmarkDatabase.ComparisonResult comp = BenchmarkDatabase.compare(results);
        assertNotNull(comp);
        assertNotNull(comp.getBestMatchDriveType());
    }
}
