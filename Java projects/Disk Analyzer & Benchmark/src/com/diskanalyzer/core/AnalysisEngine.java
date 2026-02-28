package com.diskanalyzer.core;

import java.util.*;
import java.util.stream.*;

public final class AnalysisEngine {

    private AnalysisEngine() {}

    public static List<DiskInfo> getAllDrives() {
        String[] letters = NativeBridge.getAvailableDrives();
        List<DiskInfo> drives = new ArrayList<>();
        for (String l : letters) {
            try {
                String json = NativeBridge.getDiskInfoJson(l.charAt(0));
                drives.add(DiskInfo.fromJson(json));
            } catch (Exception ignored) {}
        }
        return drives;
    }

    public static DiskInfo getDriveInfo(char letter) {
        String json = NativeBridge.getDiskInfoJson(Character.toUpperCase(letter));
        if (json == null) return null;
        return DiskInfo.fromJson(json);
    }

    public static List<FileEntry> scanDirectory(String path, int maxEntries) {
        String json = NativeBridge.scanDirectory(path, maxEntries);
        return parseFileEntryArray(json);
    }

    public static List<FileEntry> findLargestFiles(String path, int count) {
        String json = NativeBridge.findLargestFiles(path, count);
        return parseFileEntryArray(json);
    }

    public static long getDirectorySize(String path) {
        return NativeBridge.getDirectorySize(path);
    }

    public static int getFileCount(String path) {
        return NativeBridge.getFileCount(path);
    }

    public static List<ExtensionStat> getExtensionStats(String path) {
        String json = NativeBridge.getExtensionStats(path);
        return parseExtStats(json);
    }

    public static FullScanResult fullScan(String path, int maxEntries, int maxLargest) {
        String json = NativeBridge.fullScan(path, maxEntries, maxLargest);
        return parseFullScan(json);
    }

    public static BenchmarkResult benchmarkSeqRead(String dir, int blockKB, int sizeMB) {
        return BenchmarkResult.fromJson(NativeBridge.benchmarkSeqRead(dir, blockKB, sizeMB));
    }

    public static BenchmarkResult benchmarkSeqWrite(String dir, int blockKB, int sizeMB) {
        return BenchmarkResult.fromJson(NativeBridge.benchmarkSeqWrite(dir, blockKB, sizeMB));
    }

    public static BenchmarkResult benchmarkRandRead(String dir, int blockKB, int iters) {
        return BenchmarkResult.fromJson(NativeBridge.benchmarkRandRead(dir, blockKB, iters));
    }

    public static BenchmarkResult benchmarkRandWrite(String dir, int blockKB, int iters) {
        return BenchmarkResult.fromJson(NativeBridge.benchmarkRandWrite(dir, blockKB, iters));
    }

    public static List<BenchmarkResult> runFullBenchmark(String dir, int blockKB, int sizeMB, int randIters) {
        List<BenchmarkResult> results = new ArrayList<>();
        results.add(benchmarkSeqRead(dir, blockKB, sizeMB));
        results.add(benchmarkSeqWrite(dir, blockKB, sizeMB));
        results.add(benchmarkRandRead(dir, 4, randIters));
        results.add(benchmarkRandWrite(dir, 4, randIters));
        return results;
    }

    public static long[] getMemoryInfo() {
        return NativeBridge.getMemoryInfo();
    }

    public static FragmentationResult analyzeFragmentation(String path, int maxFiles) {
        String json = NativeBridge.analyzeFragmentation(path, maxFiles);
        return FragmentationResult.fromJson(json);
    }

    private static List<FileEntry> parseFileEntryArray(String json) {
        List<FileEntry> list = new ArrayList<>();
        if (json == null || json.length() < 3) return list;

        int idx = 0;
        while (true) {
            int objStart = json.indexOf('{', idx);
            if (objStart < 0) break;
            int objEnd = json.indexOf('}', objStart);
            if (objEnd < 0) break;

            String obj = json.substring(objStart, objEnd + 1);
            String path = extractJsonString(obj, "path");
            long size = extractJsonLong(obj, "size");
            boolean dir = "true".equals(extractJsonRaw(obj, "dir"));
            long modified = extractJsonLong(obj, "modified");

            list.add(new FileEntry(path, size, dir, modified));
            idx = objEnd + 1;
        }

        return list;
    }

    private static List<ExtensionStat> parseExtStats(String json) {
        List<ExtensionStat> list = new ArrayList<>();
        if (json == null || json.length() < 3) return list;

        int idx = 0;
        while (true) {
            int objStart = json.indexOf('{', idx);
            if (objStart < 0) break;
            int objEnd = json.indexOf('}', objStart);
            if (objEnd < 0) break;

            String obj = json.substring(objStart, objEnd + 1);
            String ext = extractJsonString(obj, "ext");
            long size = extractJsonLong(obj, "size");
            int count = (int) extractJsonLong(obj, "count");

            list.add(new ExtensionStat(ext, size, count));
            idx = objEnd + 1;
        }

        return list;
    }

    private static String extractJsonString(String json, String key) {
        String s = "\"" + key + "\":\"";
        int i = json.indexOf(s);
        if (i < 0) return "";
        i += s.length();
        int e = json.indexOf("\"", i);
        return e < 0 ? "" : json.substring(i, e);
    }

    private static long extractJsonLong(String json, String key) {
        String raw = extractJsonRaw(json, key);
        try { return Long.parseLong(raw); } catch (Exception e) { return 0; }
    }

    private static String extractJsonRaw(String json, String key) {
        String s = "\"" + key + "\":";
        int i = json.indexOf(s);
        if (i < 0) return "0";
        i += s.length();
        int e = i;
        while (e < json.length() && json.charAt(e) != ',' && json.charAt(e) != '}') e++;
        return json.substring(i, e).trim().replace("\"", "");
    }

    public static final class ExtensionStat {
        private final String ext;
        private final long   totalSize;
        private final int    fileCount;

        public ExtensionStat(String ext, long totalSize, int fileCount) {
            this.ext       = ext;
            this.totalSize = totalSize;
            this.fileCount = fileCount;
        }

        public String getExt()       { return ext; }
        public long   getTotalSize() { return totalSize; }
        public int    getFileCount() { return fileCount; }

        @Override
        public String toString() {
            return String.format("  %-12s %10s  %,8d files", ext, DiskInfo.formatBytes(totalSize), fileCount);
        }
    }

    public static final class FullScanResult {
        private final List<FileEntry>    entries;
        private final List<FileEntry>    largestFiles;
        private final List<ExtensionStat> extStats;
        private final long               totalSize;
        private final int                fileCount;

        public FullScanResult(List<FileEntry> entries, List<FileEntry> largestFiles,
                              List<ExtensionStat> extStats, long totalSize, int fileCount) {
            this.entries      = entries;
            this.largestFiles = largestFiles;
            this.extStats     = extStats;
            this.totalSize    = totalSize;
            this.fileCount    = fileCount;
        }

        public List<FileEntry>     getEntries()      { return entries; }
        public List<FileEntry>     getLargestFiles()  { return largestFiles; }
        public List<ExtensionStat> getExtStats()      { return extStats; }
        public long                getTotalSize()     { return totalSize; }
        public int                 getFileCount()     { return fileCount; }
    }

    private static FullScanResult parseFullScan(String json) {
        if (json == null || json.length() < 3) {
            return new FullScanResult(Collections.emptyList(), Collections.emptyList(),
                                     Collections.emptyList(), 0, 0);
        }

        String entriesJson  = extractJsonArray(json, "entries");
        String largestJson  = extractJsonArray(json, "largest");
        String extJson      = extractJsonArray(json, "extStats");
        long totalSize      = extractJsonLong(json, "totalSize");
        int fileCount       = (int) extractJsonLong(json, "fileCount");

        List<FileEntry>    entries = parseFileEntryArray(entriesJson);
        List<FileEntry>    largest = parseFileEntryArray(largestJson);
        List<ExtensionStat> ext    = parseExtStats(extJson);

        return new FullScanResult(entries, largest, ext, totalSize, fileCount);
    }

    private static String extractJsonArray(String json, String key) {
        String marker = "\"" + key + "\":[";
        int start = json.indexOf(marker);
        if (start < 0) return "[]";
        start += marker.length() - 1; // point to '['

        int depth = 0;
        int end = start;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) { end = i; break; }
            }
        }
        return json.substring(start, end + 1);
    }
}
