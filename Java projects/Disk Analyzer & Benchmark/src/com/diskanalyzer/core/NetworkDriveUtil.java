package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Network drive support — detects mapped UNC paths,
 * validates network paths, and provides Java-based scanning for
 * UNC paths (since the native DLL only handles local drive letters).
 */
public final class NetworkDriveUtil {

    private NetworkDriveUtil() {}

    public static class NetworkDrive {
        private final String localPath;     // e.g. "Z:\\"
        private final String uncPath;       // e.g. "\\\\server\\share"
        private final String status;
        private final long totalBytes;
        private final long freeBytes;

        public NetworkDrive(String localPath, String uncPath, String status, long totalBytes, long freeBytes) {
            this.localPath = localPath;
            this.uncPath = uncPath;
            this.status = status;
            this.totalBytes = totalBytes;
            this.freeBytes = freeBytes;
        }

        public String getLocalPath()  { return localPath; }
        public String getUncPath()    { return uncPath; }
        public String getStatus()     { return status; }
        public long getTotalBytes()   { return totalBytes; }
        public long getFreeBytes()    { return freeBytes; }
        public long getUsedBytes()    { return totalBytes - freeBytes; }

        public boolean isAvailable() {
            return "OK".equalsIgnoreCase(status) || "Connected".equalsIgnoreCase(status);
        }
    }

    // Cache for network drive detection (avoid spawning process every call)
    private static volatile List<NetworkDrive> cachedDrives = null;
    private static volatile long cacheTimestamp = 0;
    private static final long CACHE_TTL_MS = 30_000; // 30 seconds

    /**
     * Detect all mapped network drives using net use.
     */
    public static List<NetworkDrive> detectNetworkDrives() {
        long now = System.currentTimeMillis();
        List<NetworkDrive> cached = cachedDrives;
        if (cached != null && (now - cacheTimestamp) < CACHE_TTL_MS) {
            return cached;
        }

        List<NetworkDrive> drives = new ArrayList<>();
        try {
            Process p = new ProcessBuilder("net", "use")
                .redirectErrorStream(true).start();
            try (
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    // Lines look like: OK        Z:     \\server\share    Microsoft Windows Network
                    if (line.length() > 10 && line.contains("\\\\")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 3) {
                            String status = parts[0];
                            String local = parts[1];
                            String unc = parts[2];

                            if (local.length() >= 2 && local.charAt(1) == ':') {
                                long totalBytes = 0, freeBytes = 0;
                                try {
                                    File root = new File(local + "\\");
                                    totalBytes = root.getTotalSpace();
                                    freeBytes = root.getFreeSpace();
                                } catch (Exception ignored) {}

                                drives.add(new NetworkDrive(local, unc, status, totalBytes, freeBytes));
                            }
                        }
                    }
                }
            } finally {
                p.waitFor();
                p.destroy();
            }
        } catch (Exception ignored) {}

        cachedDrives = drives;
        cacheTimestamp = now;
        return drives;
    }

    /**
     * Check if a path is a UNC path (\\server\share).
     */
    public static boolean isUncPath(String path) {
        return path != null && path.startsWith("\\\\");
    }

    /**
     * Check if a path is a network mapped drive.
     */
    public static boolean isNetworkDrive(String path) {
        if (path == null || path.length() < 2) return false;
        // Check if drive letter is a mapped network drive
        for (NetworkDrive nd : detectNetworkDrives()) {
            if (path.toUpperCase().startsWith(nd.getLocalPath().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate that a UNC path is accessible.
     */
    public static boolean validateUncPath(String uncPath) {
        if (!isUncPath(uncPath)) return false;
        try {
            return Files.isDirectory(Paths.get(uncPath));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Scan a network/UNC path using the Java parallel scanner
     * (native DLL doesn't support UNC paths).
     */
    public static ParallelScanner.ParallelScanResult scanNetworkPath(String path, int maxEntries,
            List<String> exclusions, ParallelScanner.ScanProgressCallback callback, boolean[] cancelFlag) {
        return ParallelScanner.scan(path, maxEntries, 0, exclusions, callback, cancelFlag);
    }

    /**
     * Get display name for a path (includes UNC server name if applicable).
     */
    public static String getDisplayName(String path) {
        if (isUncPath(path)) {
            // Extract server name from \\server\share
            String stripped = path.substring(2);
            int sep = stripped.indexOf('\\');
            String server = sep >= 0 ? stripped.substring(0, sep) : stripped;
            return "Network: \\\\" + server;
        }
        return path;
    }
}
