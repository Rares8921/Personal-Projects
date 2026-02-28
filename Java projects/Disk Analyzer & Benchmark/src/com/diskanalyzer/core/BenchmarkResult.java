package com.diskanalyzer.core;

public final class BenchmarkResult {
    public enum Type { SEQ_READ, SEQ_WRITE, RAND_READ, RAND_WRITE }

    private final Type   type;
    private final double throughputMBps;
    private final double avgLatencyUs;
    private final double minLatencyUs;
    private final double maxLatencyUs;
    private final double iops;
    private final long   totalBytes;
    private final double elapsedSeconds;
    private final int    blockSize;
    private final int    operationCount;

    private BenchmarkResult(Type type, double throughputMBps, double avgLatencyUs,
                            double minLatencyUs, double maxLatencyUs, double iops,
                            long totalBytes, double elapsedSeconds, int blockSize, int operationCount) {
        this.type           = type;
        this.throughputMBps = throughputMBps;
        this.avgLatencyUs   = avgLatencyUs;
        this.minLatencyUs   = minLatencyUs;
        this.maxLatencyUs   = maxLatencyUs;
        this.iops           = iops;
        this.totalBytes     = totalBytes;
        this.elapsedSeconds = elapsedSeconds;
        this.blockSize      = blockSize;
        this.operationCount = operationCount;
    }

    public Type   getType()           { return type; }
    public double getThroughputMBps() { return throughputMBps; }
    public double getAvgLatencyUs()   { return avgLatencyUs; }
    public double getMinLatencyUs()   { return minLatencyUs; }
    public double getMaxLatencyUs()   { return maxLatencyUs; }
    public double getIops()           { return iops; }
    public long   getTotalBytes()     { return totalBytes; }
    public double getElapsedSeconds() { return elapsedSeconds; }
    public int    getBlockSize()      { return blockSize; }
    public int    getOperationCount() { return operationCount; }

    public double getAvgLatencyMs()   { return avgLatencyUs / 1000.0; }

    public static BenchmarkResult fromJson(String json) {
        if (json == null) throw new IllegalArgumentException("Benchmark JSON is null — native call likely failed");
        Type t = Type.valueOf(extractString(json, "type"));
        return new BenchmarkResult(
            t,
            extractDouble(json, "throughputMBps"),
            extractDouble(json, "avgLatencyUs"),
            extractDouble(json, "minLatencyUs"),
            extractDouble(json, "maxLatencyUs"),
            extractDouble(json, "iops"),
            extractLong(json, "totalBytes"),
            extractDouble(json, "elapsed"),
            (int) extractLong(json, "blockSize"),
            (int) extractLong(json, "ops")
        );
    }

    @Override
    public String toString() {
        return String.format("%-10s | %8.2f MB/s | %8.2f IOPS | Avg: %8.2f us | Min: %8.2f us | Max: %8.2f us",
            type, throughputMBps, iops, avgLatencyUs, minLatencyUs, maxLatencyUs);
    }

    public String toTableRow() {
        return String.format("  %-14s %10.2f MB/s  %10.0f IOPS  %10.2f ms avg latency",
            type, throughputMBps, iops, getAvgLatencyMs());
    }

    private static String extractString(String json, String key) {
        String s = "\"" + key + "\":\"";
        int i = json.indexOf(s);
        if (i < 0) return "SEQ_READ";
        i += s.length();
        int e = json.indexOf("\"", i);
        return e < 0 ? "SEQ_READ" : json.substring(i, e);
    }

    private static double extractDouble(String json, String key) {
        String raw = extractRaw(json, key);
        try { return Double.parseDouble(raw); } catch (Exception e) { return 0; }
    }

    private static long extractLong(String json, String key) {
        String raw = extractRaw(json, key);
        try { return Long.parseLong(raw); } catch (Exception e) { return 0; }
    }

    private static String extractRaw(String json, String key) {
        String s = "\"" + key + "\":";
        int i = json.indexOf(s);
        if (i < 0) return "0";
        i += s.length();
        int e = i;
        while (e < json.length() && json.charAt(e) != ',' && json.charAt(e) != '}') e++;
        return json.substring(i, e).trim();
    }
}
