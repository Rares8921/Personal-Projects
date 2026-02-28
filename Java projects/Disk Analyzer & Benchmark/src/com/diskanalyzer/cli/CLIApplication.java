package com.diskanalyzer.cli;

import com.diskanalyzer.core.*;
import java.util.*;

public final class CLIApplication {

    private static final String BANNER =
        "\n" +
        "  ╔══════════════════════════════════════════════════╗\n" +
        "  ║         DISK ANALYZER & BENCHMARK v1.0          ║\n" +
        "  ║         Native Performance Analysis Tool        ║\n" +
        "  ╚══════════════════════════════════════════════════╝\n";

    private static final String USAGE =
        "Usage: diskanalyzer <command> [options]\n\n" +
        "Commands:\n" +
        "  drives                          List all available drives\n" +
        "  info <drive-letter>             Show detailed drive information\n" +
        "  scan <path> [--top N]           Scan directory contents (default top 20)\n" +
        "  largest <path> [--count N]      Find largest files (default 25)\n" +
        "  extensions <path>               Show file extension statistics\n" +
        "  size <path>                     Calculate directory size\n" +
        "  count <path>                    Count files in directory\n" +
        "  benchmark <drive> [options]     Run disk benchmark\n" +
        "    --type <seq|rand|all>           Benchmark type (default: all)\n" +
        "    --block <KB>                    Block size in KB (default: 1024)\n" +
        "    --size <MB>                     Test file size in MB (default: 256)\n" +
        "    --iters <N>                     Random I/O iterations (default: 1000)\n" +
        "  memory                          Show system memory info\n" +
        "  interactive                     Enter interactive mode\n" +
        "  --gui                           Launch graphical interface\n";

    public static void run(String[] args) {
        if (args.length == 0 || "--help".equals(args[0]) || "-h".equals(args[0])) {
            System.out.println(BANNER);
            System.out.println(USAGE);
            if (!NativeBridge.isLoaded()) {
                System.out.println("\n  WARNING: Native library not available. Limited functionality.");
                System.out.println("  Some commands (drives, info, benchmark) will not work.\n");
            }
            return;
        }

        String cmd = args[0].toLowerCase();
        try {
            switch (cmd) {
                case "drives":     cmdDrives(); break;
                case "info":       cmdInfo(args); break;
                case "scan":       cmdScan(args); break;
                case "largest":    cmdLargest(args); break;
                case "extensions": cmdExtensions(args); break;
                case "size":       cmdSize(args); break;
                case "count":      cmdCount(args); break;
                case "benchmark":  cmdBenchmark(args); break;
                case "memory":     cmdMemory(); break;
                case "interactive":cmdInteractive(); break;
                default:
                    System.err.println("Unknown command: " + cmd);
                    System.out.println(USAGE);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void cmdDrives() {
        if (!NativeBridge.isLoaded()) {
            System.err.println("ERROR: This command requires native library support.");
            return;
        }
        List<DiskInfo> drives = AnalysisEngine.getAllDrives();
        System.out.println("\n  Available Drives:");
        System.out.println("  " + "─".repeat(80));
        for (DiskInfo d : drives) {
            System.out.println("  " + d);
            printUsageBar(d.getUsagePercent(), 50);
        }
        System.out.println();
    }

    private static void cmdInfo(String[] args) {
        if (!NativeBridge.isLoaded()) {
            System.err.println("ERROR: This command requires native library support.");
            return;
        }
        if (args.length < 2) { System.err.println("Usage: info <drive-letter>"); return; }
        char letter = Character.toUpperCase(args[1].charAt(0));
        DiskInfo d = AnalysisEngine.getDriveInfo(letter);

        System.out.println("\n  Drive " + letter + ": Information");
        System.out.println("  " + "─".repeat(50));
        System.out.printf("  Volume Name    : %s%n", d.getVolumeName().isEmpty() ? "(none)" : d.getVolumeName());
        System.out.printf("  File System    : %s%n", d.getFileSystem());
        System.out.printf("  Serial Number  : %s%n", d.getSerialNumber());
        System.out.printf("  Drive Type     : %s%n", d.getDriveType());
        System.out.printf("  Storage Type   : %s%n", d.isSSD() == null ? "Unknown" : (d.isSSD() ? "SSD" : "HDD"));
        System.out.printf("  Total Capacity : %s%n", DiskInfo.formatBytes(d.getTotalBytes()));
        System.out.printf("  Used Space     : %s%n", DiskInfo.formatBytes(d.getUsedBytes()));
        System.out.printf("  Free Space     : %s%n", DiskInfo.formatBytes(d.getFreeBytes()));
        System.out.printf("  Usage          : %.1f%%%n", d.getUsagePercent());
        System.out.printf("  Cluster Size   : %s%n", DiskInfo.formatBytes(d.getClusterSize()));
        System.out.printf("  Sector Size    : %s%n", DiskInfo.formatBytes(d.getSectorSize()));
        printUsageBar(d.getUsagePercent(), 50);
        System.out.println();
    }

    private static void cmdScan(String[] args) {
        if (args.length < 2) { System.err.println("Usage: scan <path> [--top N]"); return; }
        String path = args[1];
        int top = getIntOption(args, "--top", 20);

        System.out.println("\n  Scanning: " + path);
        System.out.println("  " + "─".repeat(70));

        List<FileEntry> entries = AnalysisEngine.scanDirectory(path, top);
        entries.sort(null);
        int shown = Math.min(entries.size(), top);
        for (int i = 0; i < shown; i++) {
            System.out.println("  " + entries.get(i));
        }
        System.out.printf("%n  Total entries: %d%n%n", entries.size());
    }

    private static void cmdLargest(String[] args) {
        if (args.length < 2) { System.err.println("Usage: largest <path> [--count N]"); return; }
        String path = args[1];
        int count = getIntOption(args, "--count", 25);

        System.out.println("\n  Largest files in: " + path);
        System.out.println("  " + "─".repeat(70));

        List<FileEntry> files = AnalysisEngine.findLargestFiles(path, count);
        for (int i = 0; i < files.size(); i++) {
            FileEntry f = files.get(i);
            System.out.printf("  %3d. %12s  %s%n", i + 1, f.getFormattedSize(), f.getPath());
        }
        System.out.println();
    }

    private static void cmdExtensions(String[] args) {
        if (args.length < 2) { System.err.println("Usage: extensions <path>"); return; }
        String path = args[1];

        System.out.println("\n  Extension Statistics for: " + path);
        System.out.println("  " + "─".repeat(50));
        System.out.printf("  %-12s %12s  %10s%n", "Extension", "Total Size", "Files");
        System.out.println("  " + "─".repeat(50));

        List<AnalysisEngine.ExtensionStat> stats = AnalysisEngine.getExtensionStats(path);
        for (AnalysisEngine.ExtensionStat s : stats) {
            System.out.println(s);
        }
        System.out.println();
    }

    private static void cmdSize(String[] args) {
        if (args.length < 2) { System.err.println("Usage: size <path>"); return; }
        long size = AnalysisEngine.getDirectorySize(args[1]);
        System.out.printf("%n  Directory size: %s (%,d bytes)%n%n", DiskInfo.formatBytes(size), size);
    }

    private static void cmdCount(String[] args) {
        if (args.length < 2) { System.err.println("Usage: count <path>"); return; }
        int count = AnalysisEngine.getFileCount(args[1]);
        System.out.printf("%n  File count: %,d%n%n", count);
    }

    private static void cmdBenchmark(String[] args) {        if (!NativeBridge.isLoaded()) {
            System.err.println("ERROR: This command requires native library support.");
            return;
        }        if (args.length < 2) { System.err.println("Usage: benchmark <drive-letter> [options]"); return; }
        char drive = Character.toUpperCase(args[1].charAt(0));
        String targetDir = drive + ":\\";

        String type = getStringOption(args, "--type", "all");
        int blockKB  = getIntOption(args, "--block", 1024);
        int sizeMB   = getIntOption(args, "--size", 256);
        int iters    = getIntOption(args, "--iters", 1000);

        System.out.println(BANNER);
        System.out.printf("  Target Drive   : %c:\\%n", drive);
        System.out.printf("  Block Size     : %d KB%n", blockKB);
        System.out.printf("  Test File Size : %d MB%n", sizeMB);
        System.out.printf("  Random Iters   : %d%n", iters);
        System.out.println("  " + "─".repeat(70));

        List<BenchmarkResult> results = new ArrayList<>();

        if ("all".equals(type) || "seq".equals(type)) {
            System.out.print("  Running Sequential Read...   ");
            System.out.flush();
            BenchmarkResult r = AnalysisEngine.benchmarkSeqRead(targetDir, blockKB, sizeMB);
            results.add(r);
            System.out.println("Done");

            System.out.print("  Running Sequential Write...  ");
            System.out.flush();
            r = AnalysisEngine.benchmarkSeqWrite(targetDir, blockKB, sizeMB);
            results.add(r);
            System.out.println("Done");
        }

        if ("all".equals(type) || "rand".equals(type)) {
            System.out.print("  Running Random Read (4K)...  ");
            System.out.flush();
            BenchmarkResult r = AnalysisEngine.benchmarkRandRead(targetDir, 4, iters);
            results.add(r);
            System.out.println("Done");

            System.out.print("  Running Random Write (4K)... ");
            System.out.flush();
            r = AnalysisEngine.benchmarkRandWrite(targetDir, 4, iters);
            results.add(r);
            System.out.println("Done");
        }

        System.out.println("\n  Results:");
        System.out.println("  " + "═".repeat(70));
        for (BenchmarkResult r : results) {
            System.out.println(r.toTableRow());
        }
        System.out.println("  " + "═".repeat(70));
        System.out.println();
    }

    private static void cmdMemory() {
        if (!NativeBridge.isLoaded()) {
            System.err.println("ERROR: This command requires native library support.");
            return;
        }
        long[] mem = AnalysisEngine.getMemoryInfo();
        System.out.println("\n  System Memory Information");
        System.out.println("  " + "─".repeat(40));
        System.out.printf("  Total Physical : %s%n", DiskInfo.formatBytes(mem[0]));
        System.out.printf("  Available      : %s%n", DiskInfo.formatBytes(mem[1]));
        System.out.printf("  Used           : %s%n", DiskInfo.formatBytes(mem[2]));
        System.out.printf("  Load           : %d%%%n", mem[3]);
        System.out.printf("  Page File      : %s%n", DiskInfo.formatBytes(mem[4]));
        printUsageBar(mem[3], 50);
        System.out.println();
    }

    private static void cmdInteractive() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(BANNER);
        System.out.println("  Type 'help' for commands, 'exit' to quit.\n");

        while (true) {
            System.out.print("  diskanalyzer> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            if ("exit".equalsIgnoreCase(line) || "quit".equalsIgnoreCase(line)) break;
            if ("help".equalsIgnoreCase(line)) { System.out.println(USAGE); continue; }

            String[] parts = line.split("\\s+");
            try {
                switch (parts[0].toLowerCase()) {
                    case "drives":     cmdDrives(); break;
                    case "info":       cmdInfo(parts); break;
                    case "scan":       cmdScan(parts); break;
                    case "largest":    cmdLargest(parts); break;
                    case "extensions": cmdExtensions(parts); break;
                    case "size":       cmdSize(parts); break;
                    case "count":      cmdCount(parts); break;
                    case "benchmark":  cmdBenchmark(parts); break;
                    case "memory":     cmdMemory(); break;
                    default:           System.out.println("  Unknown command. Type 'help'.");
                }
            } catch (Exception e) {
                System.err.println("  Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void printUsageBar(double percent, int width) {
        int filled = (int)(percent / 100.0 * width);
        StringBuilder sb = new StringBuilder("  [");
        for (int i = 0; i < width; i++) sb.append(i < filled ? '█' : '░');
        sb.append(String.format("] %.1f%%", percent));
        System.out.println(sb);
    }

    private static int getIntOption(String[] args, String flag, int defaultVal) {
        for (int i = 0; i < args.length - 1; i++) {
            if (flag.equals(args[i])) {
                try { return Integer.parseInt(args[i + 1]); }
                catch (NumberFormatException e) { return defaultVal; }
            }
        }
        return defaultVal;
    }

    private static String getStringOption(String[] args, String flag, String defaultVal) {
        for (int i = 0; i < args.length - 1; i++) {
            if (flag.equals(args[i])) return args[i + 1];
        }
        return defaultVal;
    }
}
