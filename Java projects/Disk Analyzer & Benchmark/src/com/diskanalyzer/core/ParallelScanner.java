package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Multi-threaded parallel file scanner using Java NIO and a ForkJoinPool.
 * Supplements the native single-pass scan for pure-Java scanning scenarios
 * (e.g., network drives, UNC paths, or when the native DLL is unavailable).
 */
public final class ParallelScanner {

    private ParallelScanner() {}

    public interface ScanProgressCallback {
        void onProgress(int filesScanned, long totalBytes, String currentPath);
    }

    public static class ParallelScanResult {
        private final List<FileEntry> entries;
        private final long totalSize;
        private final int fileCount;
        private final int dirCount;
        private final long scanTimeMs;

        public ParallelScanResult(List<FileEntry> entries, long totalSize, int fileCount, int dirCount, long scanTimeMs) {
            this.entries = entries;
            this.totalSize = totalSize;
            this.fileCount = fileCount;
            this.dirCount = dirCount;
            this.scanTimeMs = scanTimeMs;
        }

        public List<FileEntry> getEntries()  { return entries; }
        public long getTotalSize()            { return totalSize; }
        public int getFileCount()             { return fileCount; }
        public int getDirCount()              { return dirCount; }
        public long getScanTimeMs()           { return scanTimeMs; }
    }

    /**
     * Perform a multi-threaded parallel scan of the given directory.
     *
     * @param rootPath         The directory to scan
     * @param maxEntries       Maximum entries to collect (0 = unlimited)
     * @param threadCount      Number of threads (0 = auto based on CPU cores)
     * @param exclusions       Exclusion patterns (null for none)
     * @param callback         Progress callback (null for none)
     * @param cancelFlag       Cancel flag (null for none)
     * @return ParallelScanResult with all collected data
     */
    public static ParallelScanResult scan(String rootPath, int maxEntries, int threadCount,
                                           List<String> exclusions, ScanProgressCallback callback,
                                           boolean[] cancelFlag) {
        long startTime = System.currentTimeMillis();
        Path root = Paths.get(rootPath);

        if (!Files.isDirectory(root)) {
            return new ParallelScanResult(Collections.emptyList(), 0, 0, 0, 0);
        }

        int threads = threadCount > 0 ? threadCount : Math.max(2, Runtime.getRuntime().availableProcessors());
        ForkJoinPool pool = new ForkJoinPool(threads);

        ConcurrentLinkedQueue<FileEntry> entries = new ConcurrentLinkedQueue<>();
        AtomicLong totalSize = new AtomicLong(0);
        AtomicInteger fileCount = new AtomicInteger(0);
        AtomicInteger dirCount = new AtomicInteger(0);
        AtomicInteger entryCount = new AtomicInteger(0);

        try {
            pool.submit(() -> {
                try {
                    Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
                        new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                                if (cancelFlag != null && cancelFlag[0]) return FileVisitResult.TERMINATE;
                                if (maxEntries > 0 && entryCount.get() >= maxEntries) return FileVisitResult.SKIP_SUBTREE;

                                String pathStr = dir.toString();
                                if (exclusions != null && ScanExclusions.shouldExclude(pathStr, exclusions)) {
                                    return FileVisitResult.SKIP_SUBTREE;
                                }

                                dirCount.incrementAndGet();
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                                if (cancelFlag != null && cancelFlag[0]) return FileVisitResult.TERMINATE;
                                if (maxEntries > 0 && entryCount.get() >= maxEntries) return FileVisitResult.SKIP_SIBLINGS;

                                String pathStr = file.toString();
                                if (exclusions != null && ScanExclusions.shouldExclude(pathStr, exclusions)) {
                                    return FileVisitResult.CONTINUE;
                                }

                                long size = attrs.size();
                                long modified = attrs.lastModifiedTime().toMillis() / 1000;
                                entries.add(new FileEntry(pathStr, size, false, modified));
                                totalSize.addAndGet(size);
                                int fc = fileCount.incrementAndGet();
                                entryCount.incrementAndGet();

                                if (callback != null && fc % 1000 == 0) {
                                    callback.onProgress(fc, totalSize.get(), pathStr);
                                }

                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                                return FileVisitResult.CONTINUE;
                            }
                        });
                } catch (IOException ignored) {}
            }).get(30, TimeUnit.MINUTES); // Timeout safety
        } catch (Exception ignored) {}
        finally {
            pool.shutdown();
        }

        long scanTime = System.currentTimeMillis() - startTime;
        List<FileEntry> sortedEntries = new ArrayList<>(entries);
        sortedEntries.sort(Comparator.comparingLong(FileEntry::getSize).reversed());

        return new ParallelScanResult(sortedEntries, totalSize.get(), fileCount.get(), dirCount.get(), scanTime);
    }

    /**
     * Quick scan that only collects directory sizes (for treemap/sunburst at top level).
     */
    public static Map<String, Long> scanDirectorySizes(String rootPath, int maxDepth, List<String> exclusions) {
        Map<String, Long> dirSizes = new ConcurrentHashMap<>();
        Path root = Paths.get(rootPath);
        if (!Files.isDirectory(root)) return dirSizes;

        try {
            Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), maxDepth,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        String pathStr = dir.toString();
                        if (exclusions != null && ScanExclusions.shouldExclude(pathStr, exclusions)) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        // Attribute size to parent directory
                        Path parent = file.getParent();
                        if (parent != null) {
                            dirSizes.merge(parent.toString(), attrs.size(), Long::sum);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                });
        } catch (IOException ignored) {}

        return dirSizes;
    }
}
