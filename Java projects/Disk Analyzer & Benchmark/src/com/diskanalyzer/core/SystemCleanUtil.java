package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.stream.*;

/**
 * Utility for finding and cleaning temporary files, and analyzing recycle bin.
 */
public final class SystemCleanUtil {

    private SystemCleanUtil() {}

    public static class CleanItem {
        public final String path;
        public final long size;
        public final String category;

        public CleanItem(String path, long size, String category) {
            this.path = path;
            this.size = size;
            this.category = category;
        }
    }

    /**
     * Find temporary files in standard temp locations.
     */
    public static List<CleanItem> findTempFiles() {
        List<CleanItem> items = new ArrayList<>();

        // Windows Temp
        String userTemp = System.getenv("TEMP");
        if (userTemp != null) collectFiles(userTemp, "User Temp", items);

        // Windows system temp
        String winDir = System.getenv("WINDIR");
        if (winDir != null) collectFiles(winDir + "\\Temp", "System Temp", items);

        // Browser caches
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null) {
            collectFiles(localAppData + "\\Microsoft\\Windows\\INetCache", "IE Cache", items);
            collectFiles(localAppData + "\\Google\\Chrome\\User Data\\Default\\Cache", "Chrome Cache", items);
            collectFiles(localAppData + "\\Mozilla\\Firefox\\Profiles", "Firefox Cache", items);
            collectFiles(localAppData + "\\Microsoft\\Edge\\User Data\\Default\\Cache", "Edge Cache", items);
        }

        // Recent files
        String appData = System.getenv("APPDATA");
        if (appData != null) {
            collectFiles(appData + "\\Microsoft\\Windows\\Recent", "Recent Files", items);
        }

        // Windows prefetch
        if (winDir != null) {
            collectFiles(winDir + "\\Prefetch", "Prefetch", items);
        }

        items.sort((a, b) -> Long.compare(b.size, a.size));
        return items;
    }

    private static void collectFiles(String dir, String category, List<CleanItem> items) {
        Path path = Paths.get(dir);
        if (!Files.isDirectory(path)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p : stream) {
                try {
                    if (Files.isRegularFile(p)) {
                        items.add(new CleanItem(p.toString(), Files.size(p), category));
                    }
                } catch (IOException ignored) {}
            }
        } catch (IOException ignored) {}
    }

    /**
     * Delete a list of files. Returns count of successfully deleted.
     */
    public static int deleteFiles(List<String> paths) {
        int deleted = 0;
        for (String p : paths) {
            try {
                Files.deleteIfExists(Paths.get(p));
                deleted++;
            } catch (IOException ignored) {}
        }
        return deleted;
    }

    /**
     * Get recycle bin size by scanning $Recycle.Bin on each drive.
     */
    public static long getRecycleBinSize() {
        long total = 0;
        for (File root : File.listRoots()) {
            Path recycleBin = Paths.get(root.toString(), "$Recycle.Bin");
            if (Files.isDirectory(recycleBin)) {
                try (Stream<Path> walkStream = Files.walk(recycleBin)) {
                    total += walkStream
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try { return Files.size(p); }
                            catch (IOException e) { return 0; }
                        })
                        .sum();
                } catch (IOException | UncheckedIOException ignored) {}
            }
        }
        return total;
    }

    /**
     * Detect symbolic links and junction points in a directory.
     */
    public static List<String[]> findSymlinksAndJunctions(String rootPath, int maxDepth) {
        List<String[]> results = new ArrayList<>();
        try (Stream<Path> walkStream = Files.walk(Paths.get(rootPath), maxDepth)) {
            walkStream.forEach(p -> {
                    try {
                        if (Files.isSymbolicLink(p)) {
                            Path target = Files.readSymbolicLink(p);
                            results.add(new String[]{p.toString(), "Symbolic Link", target.toString()});
                        } else if (Files.isDirectory(p)) {
                            // Detect junction points: a junction's real path differs from its actual path
                            try {
                                Path realPath = p.toRealPath();
                                Path normalPath = p.toAbsolutePath().normalize();
                                if (!realPath.equals(normalPath)) {
                                    results.add(new String[]{p.toString(), "Junction Point", realPath.toString()});
                                }
                            } catch (IOException ignored) {}
                        }
                    } catch (IOException ignored) {}
                });
        } catch (IOException ignored) {}
        return results;
    }

    /**
     * Estimate cloud storage paths (OneDrive, Google Drive, Dropbox).
     */
    public static Map<String, String[]> detectCloudStorage() {
        Map<String, String[]> result = new LinkedHashMap<>();
        String userProfile = System.getenv("USERPROFILE");
        if (userProfile == null) return result;

        // OneDrive
        String oneDrive = System.getenv("OneDrive");
        if (oneDrive == null) oneDrive = userProfile + "\\OneDrive";
        if (Files.isDirectory(Paths.get(oneDrive))) {
            long size = getDirSize(oneDrive);
            result.put("OneDrive", new String[]{oneDrive, DiskInfo.formatBytes(size), String.valueOf(size)});
        }

        // Google Drive
        String localAppData = System.getenv("LOCALAPPDATA");
        String[] gdrivePaths = {
            userProfile + "\\Google Drive",
            userProfile + "\\My Drive",
            localAppData != null ? localAppData + "\\Google\\DriveFS" : null
        };
        for (String gp : gdrivePaths) {
            if (gp != null && Files.isDirectory(Paths.get(gp))) {
                long size = getDirSize(gp);
                result.put("Google Drive", new String[]{gp, DiskInfo.formatBytes(size), String.valueOf(size)});
                break;
            }
        }

        // Dropbox
        String dropbox = userProfile + "\\Dropbox";
        if (Files.isDirectory(Paths.get(dropbox))) {
            long size = getDirSize(dropbox);
            result.put("Dropbox", new String[]{dropbox, DiskInfo.formatBytes(size), String.valueOf(size)});
        }

        // iCloud
        String icloud = userProfile + "\\iCloudDrive";
        if (Files.isDirectory(Paths.get(icloud))) {
            long size = getDirSize(icloud);
            result.put("iCloud Drive", new String[]{icloud, DiskInfo.formatBytes(size), String.valueOf(size)});
        }

        return result;
    }

    private static long getDirSize(String path) {
        try (Stream<Path> walkStream = Files.walk(Paths.get(path))) {
            return walkStream
                .filter(Files::isRegularFile)
                .mapToLong(p -> { try { return Files.size(p); } catch (IOException e) { return 0; } })
                .sum();
        } catch (IOException e) { return 0; }
    }
}
