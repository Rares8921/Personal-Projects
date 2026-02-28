package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BenchmarkResult class.
 */
class BenchmarkResultTest {

    private static final String SEQ_READ_JSON =
        "{\"type\":\"SEQ_READ\",\"throughputMBps\":3500.50,\"avgLatencyUs\":125.3," +
        "\"minLatencyUs\":50.1,\"maxLatencyUs\":300.7,\"iops\":50000.0," +
        "\"totalBytes\":1073741824,\"elapsed\":2.5,\"blockSize\":131072,\"ops\":8192}";

    @Test
    @DisplayName("fromJson parses benchmark result correctly")
    void testFromJson() {
        BenchmarkResult r = BenchmarkResult.fromJson(SEQ_READ_JSON);
        assertEquals(BenchmarkResult.Type.SEQ_READ, r.getType());
        assertEquals(3500.50, r.getThroughputMBps(), 0.01);
        assertEquals(125.3, r.getAvgLatencyUs(), 0.01);
        assertEquals(50.1, r.getMinLatencyUs(), 0.01);
        assertEquals(300.7, r.getMaxLatencyUs(), 0.01);
        assertEquals(50000.0, r.getIops(), 0.1);
        assertEquals(1073741824L, r.getTotalBytes());
        assertEquals(2.5, r.getElapsedSeconds(), 0.01);
        assertEquals(131072, r.getBlockSize());
        assertEquals(8192, r.getOperationCount());
    }

    @Test
    @DisplayName("getAvgLatencyMs converts microseconds to milliseconds")
    void testAvgLatencyMs() {
        BenchmarkResult r = BenchmarkResult.fromJson(SEQ_READ_JSON);
        assertEquals(0.1253, r.getAvgLatencyMs(), 0.0001);
    }

    @Test
    @DisplayName("All benchmark types parse correctly")
    void testAllTypes() {
        for (BenchmarkResult.Type type : BenchmarkResult.Type.values()) {
            String json = "{\"type\":\"" + type.name() + "\",\"throughputMBps\":100.0,\"avgLatencyUs\":10," +
                "\"minLatencyUs\":5,\"maxLatencyUs\":20,\"iops\":1000,\"totalBytes\":1000,\"elapsed\":1," +
                "\"blockSize\":4096,\"ops\":100}";
            BenchmarkResult r = BenchmarkResult.fromJson(json);
            assertEquals(type, r.getType());
        }
    }

    @Test
    @DisplayName("toString and toTableRow produce non-empty strings")
    void testStringRepresentations() {
        BenchmarkResult r = BenchmarkResult.fromJson(SEQ_READ_JSON);
        assertNotNull(r.toString());
        assertFalse(r.toString().isEmpty());
        assertNotNull(r.toTableRow());
        assertTrue(r.toTableRow().contains("MB/s"));
    }
}
