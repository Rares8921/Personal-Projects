package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Export scan results to CSV and JSON formats.
 */
public final class ExportUtil {

    private ExportUtil() {}

    public static void exportFileEntriesToCsv(List<FileEntry> entries, File output) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(output))) {
            pw.println("Name,Path,Size (bytes),Size (formatted),Type,Modified");
            for (FileEntry e : entries) {
                pw.printf("\"%s\",\"%s\",%d,\"%s\",\"%s\",%d%n",
                    escapeCsv(e.getName()),
                    escapeCsv(e.getPath()),
                    e.getSize(),
                    e.getFormattedSize(),
                    e.isDirectory() ? "Directory" : e.getExtension(),
                    e.getModifiedTime());
            }
        }
    }

    public static void exportExtStatsToCsv(List<AnalysisEngine.ExtensionStat> stats, File output) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(output))) {
            pw.println("Extension,Total Size (bytes),Total Size (formatted),File Count,Avg Size (bytes)");
            for (AnalysisEngine.ExtensionStat s : stats) {
                long avg = s.getFileCount() > 0 ? s.getTotalSize() / s.getFileCount() : 0;
                pw.printf("\"%s\",%d,\"%s\",%d,%d%n",
                    escapeCsv(s.getExt()),
                    s.getTotalSize(),
                    DiskInfo.formatBytes(s.getTotalSize()),
                    s.getFileCount(),
                    avg);
            }
        }
    }

    public static void exportBenchmarkToCsv(List<BenchmarkResult> results, File output) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(output))) {
            pw.println("Test,Throughput (MB/s),IOPS,Avg Latency (us),Min Latency (us),Max Latency (us),Block Size,Operations,Duration (s)");
            for (BenchmarkResult r : results) {
                pw.printf("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%d,%d,%.4f%n",
                    r.getType(), r.getThroughputMBps(), r.getIops(),
                    r.getAvgLatencyUs(), r.getMinLatencyUs(), r.getMaxLatencyUs(),
                    r.getBlockSize(), r.getOperationCount(), r.getElapsedSeconds());
            }
        }
    }

    public static void exportFileEntriesToJson(List<FileEntry> entries, File output) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(output))) {
            pw.println("{");
            pw.printf("  \"exportDate\": \"%s\",%n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pw.printf("  \"totalEntries\": %d,%n", entries.size());
            pw.println("  \"entries\": [");
            for (int i = 0; i < entries.size(); i++) {
                FileEntry e = entries.get(i);
                pw.printf("    {\"name\":\"%s\",\"path\":\"%s\",\"size\":%d,\"formattedSize\":\"%s\",\"type\":\"%s\",\"modified\":%d}%s%n",
                    escapeJson(e.getName()),
                    escapeJson(e.getPath()),
                    e.getSize(),
                    e.getFormattedSize(),
                    e.isDirectory() ? "Directory" : e.getExtension(),
                    e.getModifiedTime(),
                    i < entries.size() - 1 ? "," : "");
            }
            pw.println("  ]");
            pw.println("}");
        }
    }

    public static void exportExtStatsToJson(List<AnalysisEngine.ExtensionStat> stats, File output) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(output))) {
            pw.println("{");
            pw.printf("  \"exportDate\": \"%s\",%n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pw.printf("  \"totalTypes\": %d,%n", stats.size());
            pw.println("  \"extensionStats\": [");
            for (int i = 0; i < stats.size(); i++) {
                AnalysisEngine.ExtensionStat s = stats.get(i);
                long avg = s.getFileCount() > 0 ? s.getTotalSize() / s.getFileCount() : 0;
                pw.printf("    {\"extension\":\"%s\",\"totalSize\":%d,\"formattedSize\":\"%s\",\"fileCount\":%d,\"avgSize\":%d}%s%n",
                    escapeJson(s.getExt()),
                    s.getTotalSize(),
                    DiskInfo.formatBytes(s.getTotalSize()),
                    s.getFileCount(),
                    avg,
                    i < stats.size() - 1 ? "," : "");
            }
            pw.println("  ]");
            pw.println("}");
        }
    }

    public static void exportBenchmarkToJson(List<BenchmarkResult> results, File output) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(output))) {
            pw.println("{");
            pw.printf("  \"exportDate\": \"%s\",%n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pw.println("  \"benchmarkResults\": [");
            for (int i = 0; i < results.size(); i++) {
                BenchmarkResult r = results.get(i);
                pw.printf("    {\"type\":\"%s\",\"throughputMBps\":%.2f,\"iops\":%.2f,\"avgLatencyUs\":%.2f,\"minLatencyUs\":%.2f,\"maxLatencyUs\":%.2f,\"blockSize\":%d,\"ops\":%d,\"elapsed\":%.4f}%s%n",
                    r.getType(), r.getThroughputMBps(), r.getIops(),
                    r.getAvgLatencyUs(), r.getMinLatencyUs(), r.getMaxLatencyUs(),
                    r.getBlockSize(), r.getOperationCount(), r.getElapsedSeconds(),
                    i < results.size() - 1 ? "," : "");
            }
            pw.println("  ]");
            pw.println("}");
        }
    }

    public static void exportFullReport(AnalysisEngine.FullScanResult result, String scanPath, File output) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(output))) {
            pw.println("{");
            pw.printf("  \"exportDate\": \"%s\",%n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pw.printf("  \"scanPath\": \"%s\",%n", escapeJson(scanPath));
            pw.printf("  \"totalSize\": %d,%n", result.getTotalSize());
            pw.printf("  \"formattedSize\": \"%s\",%n", DiskInfo.formatBytes(result.getTotalSize()));
            pw.printf("  \"fileCount\": %d,%n", result.getFileCount());

            pw.printf("  \"entries\": [%n");
            List<FileEntry> entries = result.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                FileEntry e = entries.get(i);
                pw.printf("    {\"name\":\"%s\",\"path\":\"%s\",\"size\":%d,\"type\":\"%s\"}%s%n",
                    escapeJson(e.getName()), escapeJson(e.getPath()), e.getSize(),
                    e.isDirectory() ? "Directory" : e.getExtension(),
                    i < entries.size() - 1 ? "," : "");
            }
            pw.println("  ],");

            pw.printf("  \"largestFiles\": [%n");
            List<FileEntry> largest = result.getLargestFiles();
            for (int i = 0; i < largest.size(); i++) {
                FileEntry e = largest.get(i);
                pw.printf("    {\"name\":\"%s\",\"path\":\"%s\",\"size\":%d,\"formattedSize\":\"%s\"}%s%n",
                    escapeJson(e.getName()), escapeJson(e.getPath()), e.getSize(),
                    e.getFormattedSize(), i < largest.size() - 1 ? "," : "");
            }
            pw.println("  ],");

            pw.printf("  \"extensionStats\": [%n");
            List<AnalysisEngine.ExtensionStat> stats = result.getExtStats();
            for (int i = 0; i < stats.size(); i++) {
                AnalysisEngine.ExtensionStat s = stats.get(i);
                pw.printf("    {\"ext\":\"%s\",\"totalSize\":%d,\"fileCount\":%d}%s%n",
                    escapeJson(s.getExt()), s.getTotalSize(), s.getFileCount(),
                    i < stats.size() - 1 ? "," : "");
            }
            pw.println("  ]");
            pw.println("}");
        }
    }

    private static String escapeCsv(String s) {
        return s == null ? "" : s.replace("\"", "\"\"");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
