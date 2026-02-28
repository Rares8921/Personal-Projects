package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Finds duplicate files by size + MD5 hash comparison.
 */
public final class DuplicateFileFinder {

    private DuplicateFileFinder() {}

    public static class DuplicateGroup {
        private final String hash;
        private final long size;
        private final List<String> paths;

        public DuplicateGroup(String hash, long size, List<String> paths) {
            this.hash = hash;
            this.size = size;
            this.paths = Collections.unmodifiableList(paths);
        }

        public String getHash() { return hash; }
        public long getSize() { return size; }
        public List<String> getPaths() { return paths; }
        public int getCount() { return paths.size(); }
        public long getWastedSpace() { return size * (paths.size() - 1); }
    }

    public interface ProgressCallback {
        void onProgress(int filesProcessed, int totalFiles, String currentFile);
    }

    /**
     * Find duplicate files under the given path.
     * Uses two-pass approach: first groups by size, then hashes only size-duplicates.
     */
    public static List<DuplicateGroup> findDuplicates(String rootPath, long minSize,
            ProgressCallback callback, boolean[] cancelFlag) {

        // Pass 1: Collect all files grouped by size (using walkFileTree to skip access-denied dirs)
        Map<Long, List<Path>> bySize = new HashMap<>();
        try {
            java.nio.file.Files.walkFileTree(Paths.get(rootPath), new java.nio.file.SimpleFileVisitor<Path>() {
                @Override
                public java.nio.file.FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) {
                    if (cancelFlag != null && cancelFlag[0]) return java.nio.file.FileVisitResult.TERMINATE;
                    if (!attrs.isRegularFile()) return java.nio.file.FileVisitResult.CONTINUE;
                    try {
                        long size = attrs.size();
                        if (size >= minSize) {
                            bySize.computeIfAbsent(size, k -> new ArrayList<>()).add(file);
                        }
                    } catch (Exception ignored) {}
                    return java.nio.file.FileVisitResult.CONTINUE;
                }

                @Override
                public java.nio.file.FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return java.nio.file.FileVisitResult.CONTINUE;
                }

                @Override
                public java.nio.file.FileVisitResult preVisitDirectory(Path dir, java.nio.file.attribute.BasicFileAttributes attrs) {
                    if (cancelFlag != null && cancelFlag[0]) return java.nio.file.FileVisitResult.TERMINATE;
                    return java.nio.file.FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            return Collections.emptyList();
        }
        if (cancelFlag != null && cancelFlag[0]) return Collections.emptyList();

        // Only keep sizes with 2+ files
        List<Path> toHash = new ArrayList<>();
        Map<Path, Long> sizeMap = new HashMap<>();
        for (Map.Entry<Long, List<Path>> entry : bySize.entrySet()) {
            if (entry.getValue().size() > 1) {
                for (Path p : entry.getValue()) {
                    toHash.add(p);
                    sizeMap.put(p, entry.getKey());
                }
            }
        }

        // Pass 2: Hash potential duplicates
        Map<String, List<String>> byHash = new LinkedHashMap<>();
        Map<String, Long> hashToSize = new HashMap<>();
        int total = toHash.size();
        int processed = 0;

        for (Path p : toHash) {
            if (cancelFlag != null && cancelFlag[0]) break;
            processed++;
            if (callback != null) {
                callback.onProgress(processed, total, p.getFileName().toString());
            }
            try {
                String hash = computeHash(p);
                byHash.computeIfAbsent(hash, k -> new ArrayList<>()).add(p.toString());
                hashToSize.put(hash, sizeMap.get(p));
            } catch (Exception ignored) {}
        }

        // Build result
        return byHash.entrySet().stream()
            .filter(e -> e.getValue().size() > 1)
            .map(e -> new DuplicateGroup(e.getKey(), hashToSize.getOrDefault(e.getKey(), 0L), e.getValue()))
            .sorted((a, b) -> Long.compare(b.getWastedSpace(), a.getWastedSpace()))
            .collect(Collectors.toList());
    }

    private static String computeHash(Path path) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        long fileSize = Files.size(path);
        if (fileSize > 256 * 1024) {
            // For large files, hash first 64KB + last 64KB for speed
            try (java.nio.channels.FileChannel fc = java.nio.channels.FileChannel.open(path, java.nio.file.StandardOpenOption.READ)) {
                java.nio.ByteBuffer buf = java.nio.ByteBuffer.allocate(65536);
                // Hash first 64KB
                while (buf.hasRemaining()) {
                    if (fc.read(buf) <= 0) break;
                }
                buf.flip();
                md.update(buf);
                // Hash last 64KB
                buf.clear();
                fc.position(Math.max(0, fileSize - 65536));
                while (buf.hasRemaining()) {
                    if (fc.read(buf) <= 0) break;
                }
                buf.flip();
                md.update(buf);
            }
            // Include file size for extra uniqueness
            md.update(Long.toString(fileSize).getBytes());
        } else {
            try (InputStream is = Files.newInputStream(path)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) > 0) {
                    md.update(buf, 0, n);
                }
            }
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
