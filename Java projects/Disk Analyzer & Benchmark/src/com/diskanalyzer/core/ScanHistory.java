package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Persists scan results and benchmark history for trend tracking.
 */
public final class ScanHistory {

    private static String getHistoryDir() {
        return System.getProperty("user.dir") + File.separator + "scan_history";
    }

    private ScanHistory() {}

    public static class ScanRecord {
        private final String scanPath;
        private final long timestamp;
        private final long totalSize;
        private final int fileCount;
        private final int entryCount;

        public ScanRecord(String scanPath, long timestamp, long totalSize, int fileCount, int entryCount) {
            this.scanPath = scanPath;
            this.timestamp = timestamp;
            this.totalSize = totalSize;
            this.fileCount = fileCount;
            this.entryCount = entryCount;
        }

        public String getScanPath()   { return scanPath; }
        public long   getTimestamp()  { return timestamp; }
        public long   getTotalSize()  { return totalSize; }
        public int    getFileCount()  { return fileCount; }
        public int    getEntryCount() { return entryCount; }

        public java.time.LocalDateTime getTimestampDate() {
            return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        public String getFormattedDate() {
            return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        @Override
        public String toString() {
            return String.format("[%s] %s - Size: %s, Files: %,d",
                getFormattedDate(), scanPath, DiskInfo.formatBytes(totalSize), fileCount);
        }
    }

    public static void saveScanRecord(String scanPath, long totalSize, int fileCount) {
        saveScanRecord(scanPath, totalSize, fileCount, 0);
    }

    public static void saveScanRecord(String scanPath, long totalSize, int fileCount, int entryCount) {
        try {
            Files.createDirectories(Paths.get(getHistoryDir()));
            Path file = Paths.get(getHistoryDir(), "scan_history.csv");
            boolean exists = Files.exists(file);

            try (PrintWriter pw = new PrintWriter(new FileWriter(file.toFile(), true))) {
                if (!exists) {
                    pw.println("timestamp,scanPath,totalSize,fileCount,entryCount");
                }
                pw.printf("%d,\"%s\",%d,%d,%d%n",
                    System.currentTimeMillis(), scanPath.replace("\"", "\"\""),
                    totalSize, fileCount, entryCount);
            }
        } catch (IOException ignored) {}
    }

    public static List<ScanRecord> loadHistory() {
        List<ScanRecord> records = new ArrayList<>();
        Path file = Paths.get(getHistoryDir(), "scan_history.csv");
        if (!Files.exists(file)) return records;

        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                try {
                    // Parse CSV: timestamp,"scanPath",totalSize,fileCount,entryCount
                    int firstComma = line.indexOf(',');
                    long ts = Long.parseLong(line.substring(0, firstComma));

                    int pathStart = line.indexOf('"', firstComma) + 1;
                    // Handle escaped double quotes in path ("" → ")
                    int pathEnd = pathStart;
                    while (pathEnd < line.length()) {
                        pathEnd = line.indexOf('"', pathEnd);
                        if (pathEnd < 0) { pathEnd = line.length(); break; }
                        if (pathEnd + 1 < line.length() && line.charAt(pathEnd + 1) == '"') {
                            pathEnd += 2; // skip escaped ""
                        } else {
                            break;
                        }
                    }
                    String path = line.substring(pathStart, pathEnd).replace("\"\"", "\"");

                    String rest = line.substring(pathEnd + 2); // skip ","
                    String[] parts = rest.split(",");
                    long totalSize = Long.parseLong(parts[0]);
                    int fc = Integer.parseInt(parts[1]);
                    int ec = Integer.parseInt(parts[2]);

                    records.add(new ScanRecord(path, ts, totalSize, fc, ec));
                } catch (Exception ignored) {}
            }
        } catch (IOException ignored) {}
        return records;
    }

    public static void saveBenchmarkRecord(String drive, List<BenchmarkResult> results) {
        try {
            Files.createDirectories(Paths.get(getHistoryDir()));
            Path file = Paths.get(getHistoryDir(), "benchmark_history.csv");
            boolean exists = Files.exists(file);

            try (PrintWriter pw = new PrintWriter(new FileWriter(file.toFile(), true))) {
                if (!exists) {
                    pw.println("timestamp,drive,type,throughputMBps,iops,avgLatencyUs,blockSize");
                }
                long ts = System.currentTimeMillis();
                for (BenchmarkResult r : results) {
                    pw.printf("%d,%s,%s,%.2f,%.2f,%.2f,%d%n",
                        ts, drive, r.getType(), r.getThroughputMBps(), r.getIops(),
                        r.getAvgLatencyUs(), r.getBlockSize());
                }
            }
        } catch (IOException ignored) {}
    }

    /**
     * Get trend data: returns list of [timestamp, totalSize] for a given scan path.
     */
    public static List<long[]> getTrend(String scanPath) {
        List<long[]> trend = new ArrayList<>();
        for (ScanRecord r : loadHistory()) {
            if (r.scanPath.equalsIgnoreCase(scanPath)) {
                trend.add(new long[]{r.timestamp, r.totalSize});
            }
        }
        return trend;
    }
}
