package com.diskanalyzer.core;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Export scan results to a PDF report using pure Java (no external libraries).
 * Generates a minimal valid PDF 1.4 file with text-based tables and charts.
 */
public final class PdfReportGenerator {

    private PdfReportGenerator() {}

    private static final float PAGE_WIDTH = 595.28f;  // A4
    private static final float PAGE_HEIGHT = 841.89f;
    private static final float MARGIN = 50;
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;

    /**
     * Generate a full PDF report from scan results.
     */
    public static void generateReport(AnalysisEngine.FullScanResult result, String scanPath,
                                       List<BenchmarkResult> benchmarks, File output) throws IOException {
        PdfWriter w = new PdfWriter();

        w.addLine("DISK ANALYZER REPORT", 20, true);
        w.addLine("", 12, false);
        w.addLine("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 11, false);
        w.addLine("Scan Path: " + scanPath, 11, false);
        w.addLine("", 12, false);

        w.addLine("SUMMARY", 16, true);
        w.addLine("─".repeat(60), 10, false);
        if (result != null) {
            w.addLine("Total Size: " + DiskInfo.formatBytes(result.getTotalSize()), 11, false);
            w.addLine("Total Files: " + String.format("%,d", result.getFileCount()), 11, false);
            w.addLine("Total Entries Scanned: " + String.format("%,d", result.getEntries().size()), 11, false);
            w.addLine("", 10, false);

            if (!result.getExtStats().isEmpty()) {
                w.addLine("FILE TYPE DISTRIBUTION", 14, true);
                w.addLine("─".repeat(60), 10, false);
                w.addLine(String.format("%-15s %15s %10s", "Extension", "Total Size", "Files"), 10, true);
                int count = 0;
                for (AnalysisEngine.ExtensionStat s : result.getExtStats()) {
                    if (count++ >= 25) { w.addLine("  ... and more", 9, false); break; }
                    w.addLine(String.format("%-15s %15s %10s",
                        s.getExt(),
                        DiskInfo.formatBytes(s.getTotalSize()),
                        String.format("%,d", s.getFileCount())), 10, false);
                }
                w.addLine("", 10, false);
            }

            if (!result.getLargestFiles().isEmpty()) {
                w.addLine("LARGEST FILES", 14, true);
                w.addLine("─".repeat(60), 10, false);
                int count = 0;
                for (FileEntry e : result.getLargestFiles()) {
                    if (count++ >= 20) break;
                    w.addLine(String.format("%12s  %s", e.getFormattedSize(), e.getName()), 10, false);
                }
                w.addLine("", 10, false);
            }

            Map<String, long[]> cats = FileCategories.groupByCategory(result.getEntries());
            if (!cats.isEmpty()) {
                w.addLine("CATEGORY BREAKDOWN", 14, true);
                w.addLine("─".repeat(60), 10, false);
                w.addLine(String.format("%-18s %15s %10s", "Category", "Size", "Files"), 10, true);
                for (Map.Entry<String, long[]> catEntry : cats.entrySet()) {
                    w.addLine(String.format("%-18s %15s %10s",
                        catEntry.getKey(),
                        DiskInfo.formatBytes(catEntry.getValue()[0]),
                        String.format("%,d", (int) catEntry.getValue()[1])), 10, false);
                }
                w.addLine("", 10, false);
            }
        }

        if (benchmarks != null && !benchmarks.isEmpty()) {
            w.addLine("BENCHMARK RESULTS", 14, true);
            w.addLine("─".repeat(60), 10, false);
            w.addLine(String.format("%-14s %12s %12s %15s", "Test", "MB/s", "IOPS", "Avg Latency"), 10, true);
            for (BenchmarkResult b : benchmarks) {
                w.addLine(String.format("%-14s %12.2f %12.0f %12.2f ms",
                    b.getType(), b.getThroughputMBps(), b.getIops(), b.getAvgLatencyMs()), 10, false);
            }
            w.addLine("", 10, false);
        }

        w.addLine("─".repeat(60), 10, false);
        w.addLine("End of Report", 10, false);

        w.writeTo(output);
    }

    /**
     * Generate a simple summary report (no benchmarks).
     */
    public static void generateScanReport(AnalysisEngine.FullScanResult result, String scanPath, File output)
            throws IOException {
        generateReport(result, scanPath, null, output);
    }

    private static class PdfWriter {
        private final List<String> lines = new ArrayList<>();
        private final List<Integer> sizes = new ArrayList<>();
        private final List<Boolean> bolds = new ArrayList<>();

        void addLine(String text, int fontSize, boolean bold) {
            lines.add(text);
            sizes.add(fontSize);
            bolds.add(bold);
        }

        void writeTo(File output) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<Long> objOffsets = new ArrayList<>();

            write(baos, "%PDF-1.4\n%âãÏÓ\n");

            // Object 1: Catalog
            objOffsets.add((long) baos.size());
            write(baos, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

            // We need to build pages based on content
            List<List<Integer>> pages = new ArrayList<>();
            List<Integer> currentPage = new ArrayList<>();
            float y = PAGE_HEIGHT - MARGIN;

            for (int i = 0; i < lines.size(); i++) {
                float lineHeight = sizes.get(i) * 1.4f;
                y -= lineHeight;
                if (y < MARGIN) {
                    pages.add(currentPage);
                    currentPage = new ArrayList<>();
                    y = PAGE_HEIGHT - MARGIN - lineHeight;
                }
                currentPage.add(i);
            }
            if (!currentPage.isEmpty()) pages.add(currentPage);

            // Object 2: Pages
            int numPages = pages.size();
            objOffsets.add((long) baos.size());
            StringBuilder pagesKids = new StringBuilder();
            for (int p = 0; p < numPages; p++) {
                if (p > 0) pagesKids.append(" ");
                pagesKids.append((4 + p * 2)).append(" 0 R");
            }
            write(baos, "2 0 obj\n<< /Type /Pages /Kids [" + pagesKids + "] /Count " + numPages + " >>\nendobj\n");

            // Object 3: Font
            objOffsets.add((long) baos.size());
            write(baos, "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>\nendobj\n");

            // Pages and content streams
            for (int p = 0; p < numPages; p++) {
                List<Integer> pageLines = pages.get(p);
                StringBuilder stream = new StringBuilder();
                stream.append("BT\n");
                float yPos = PAGE_HEIGHT - MARGIN;

                for (int idx : pageLines) {
                    String text = lines.get(idx);
                    int fontSize = sizes.get(idx);
                    float lineHeight = fontSize * 1.4f;
                    yPos -= lineHeight;

                    stream.append("/F1 ").append(fontSize).append(" Tf\n");
                    stream.append(String.format("%.2f %.2f Td\n", MARGIN, yPos));
                    stream.append("(").append(escapePdf(text)).append(") Tj\n");
                    // Reset position for next line
                    stream.append(String.format("%.2f %.2f Td\n", -MARGIN, -yPos));
                }
                stream.append("ET\n");

                String streamStr = stream.toString();
                byte[] streamBytes = streamStr.getBytes("ISO-8859-1");

                // Page object
                int pageObjNum = 4 + p * 2;
                int contentObjNum = 5 + p * 2;
                objOffsets.add((long) baos.size());
                write(baos, pageObjNum + " 0 obj\n<< /Type /Page /Parent 2 0 R " +
                    "/MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT + "] " +
                    "/Contents " + contentObjNum + " 0 R /Resources << /Font << /F1 3 0 R >> >> >>\nendobj\n");

                // Content stream object
                objOffsets.add((long) baos.size());
                write(baos, contentObjNum + " 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
                baos.write(streamBytes);
                write(baos, "\nendstream\nendobj\n");
            }

            // Cross-reference table
            long xrefOffset = baos.size();
            int totalObjects = objOffsets.size() + 1; // +1 for object 0
            write(baos, "xref\n0 " + totalObjects + "\n");
            write(baos, String.format("0000000000 65535 f \n"));
            for (long offset : objOffsets) {
                write(baos, String.format("%010d 00000 n \n", offset));
            }

            // Trailer
            write(baos, "trailer\n<< /Size " + totalObjects + " /Root 1 0 R >>\n");
            write(baos, "startxref\n" + xrefOffset + "\n%%EOF\n");

            try (FileOutputStream fos = new FileOutputStream(output)) {
                baos.writeTo(fos);
            }
        }

        private void write(ByteArrayOutputStream baos, String s) throws IOException {
            baos.write(s.getBytes("ISO-8859-1"));
        }

        private String escapePdf(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("\r", "")
                    .replace("\n", " ");
        }
    }
}
