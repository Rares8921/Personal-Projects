package com.diskanalyzer.core;

import java.util.*;
import java.util.concurrent.*;

/**
 * Disk space monitoring & scheduled scans.
 */
public class DiskSpaceMonitor {

    public interface AlertCallback {
        void onAlert(String drive, long freeSpace, long totalSpace, double usedPercent);
    }

    private static ScheduledExecutorService scheduler;
    private static volatile boolean running = false;
    private static volatile double alertThreshold = 90.0; // alert when usage exceeds this %

    public static void setAlertThreshold(double pct) {
        alertThreshold = pct;
    }

    public static double getAlertThreshold() {
        return alertThreshold;
    }

    /**
     * Start background monitoring of disk space.
     * Checks every intervalMinutes and fires callback if threshold exceeded.
     */
    public static synchronized void startMonitoring(int intervalMinutes, AlertCallback callback) {
        if (running) return;
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "DiskSpaceMonitor");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkDrives(callback);
            } catch (Exception ignored) {}
        }, 0, intervalMinutes, TimeUnit.MINUTES);
    }

    public static synchronized void stopMonitoring() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    public static boolean isRunning() { return running; }

    /**
     * Check drives and trigger alerts.
     */
    public static void checkDrives(AlertCallback callback) {
        try {
            String[] drives = NativeBridge.getAvailableDrives();
            for (String d : drives) {
                try {
                    DiskInfo info = AnalysisEngine.getDriveInfo(d.charAt(0));
                    if (info != null && info.getTotalBytes() > 0) {
                        double usedPct = (double)(info.getTotalBytes() - info.getFreeBytes()) / info.getTotalBytes() * 100;
                        if (usedPct >= alertThreshold) {
                            callback.onAlert(d + ":\\", info.getFreeBytes(), info.getTotalBytes(), usedPct);
                        }
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
    }

    private static ScheduledExecutorService scanScheduler;
    private static volatile boolean scanRunning = false;

    public interface ScanCallback {
        void onScanComplete(String path, AnalysisEngine.FullScanResult result);
    }

    /**
     * Schedule repeating scans of a directory.
     */
    public static synchronized void scheduleScans(String path, int intervalHours, int maxEntries,
                                                    int maxLargest, ScanCallback callback) {
        if (scanRunning) stopScheduledScans();
        scanRunning = true;
        scanScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ScheduledScan");
            t.setDaemon(true);
            return t;
        });

        scanScheduler.scheduleAtFixedRate(() -> {
            try {
                AnalysisEngine.FullScanResult result = AnalysisEngine.fullScan(path, maxEntries, maxLargest);
                if (result != null) {
                    ScanHistory.saveScanRecord(path, result.getTotalSize(), result.getFileCount());
                    callback.onScanComplete(path, result);
                }
            } catch (Exception ignored) {}
        }, intervalHours, intervalHours, TimeUnit.HOURS);
    }

    public static synchronized void stopScheduledScans() {
        scanRunning = false;
        if (scanScheduler != null) {
            scanScheduler.shutdownNow();
            scanScheduler = null;
        }
    }

    public static boolean isScanScheduled() { return scanRunning; }

    /**
     * Get S.M.A.R.T. health data using wmic.
     */
    public static List<SmartDrive> getSmartData() {
        List<SmartDrive> drives = new ArrayList<>();
        try {
            Process p = new ProcessBuilder("wmic", "diskdrive", "get",
                "Model,Size,Status,MediaType,InterfaceType,SerialNumber", "/format:csv")
                .redirectErrorStream(true).start();

            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                String line;
                boolean headerFound = false;
                String[] headers = null;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    if (!headerFound) {
                        headers = line.split(",");
                        headerFound = true;
                        continue;
                    }
                    String[] vals = line.split(",", -1);
                    if (vals.length < 4) continue;

                    SmartDrive sd = new SmartDrive();
                    for (int i = 0; i < headers.length && i < vals.length; i++) {
                        String h = headers[i].trim();
                        String v = vals[i].trim();
                        switch (h) {
                            case "Model": sd.model = v; break;
                            case "Size": try { sd.size = Long.parseLong(v); } catch (Exception e) {} break;
                            case "Status": sd.status = v; break;
                            case "MediaType": sd.mediaType = v; break;
                            case "InterfaceType": sd.interfaceType = v; break;
                            case "SerialNumber": sd.serialNumber = v; break;
                        }
                    }
                    if (sd.model != null && !sd.model.isEmpty()) {
                        drives.add(sd);
                    }
                }
            } finally {
                p.waitFor();
                p.destroy();
            }
        } catch (Exception ignored) {}

        try {
            Process p2 = new ProcessBuilder("wmic", "/namespace:\\\\root\\wmi", "path",
                "MSStorageDriver_ATAPISmartData", "get", "VendorSpecific", "/format:csv")
                .redirectErrorStream(true).start();
            try {
                p2.waitFor();
            } finally {
                p2.destroy();
            }
        } catch (Exception ignored) {}

        return drives;
    }

    /**
     * Get disk temperature via wmic MSAcpi_ThermalZoneTemperature
     */
    public static Map<String, Integer> getDiskTemperatures() {
        Map<String, Integer> temps = new LinkedHashMap<>();
        try {
            Process p = new ProcessBuilder("wmic", "/namespace:\\\\root\\wmi", "path",
                "MSAcpi_ThermalZoneTemperature", "get", "CurrentTemperature", "/format:csv")
                .redirectErrorStream(true).start();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                String line;
                int idx = 0;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("Node")) continue;
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        try {
                            int kelvin10 = Integer.parseInt(parts[parts.length - 1].trim());
                            int celsius = (kelvin10 / 10) - 273;
                            temps.put("Zone " + (idx++), celsius);
                        } catch (Exception ignored) {}
                    }
                }
            } finally {
                p.waitFor();
                p.destroy();
            }
        } catch (Exception ignored) {}
        return temps;
    }

    public static class SmartDrive {
        public String model = "";
        public long size = 0;
        public String status = "Unknown";
        public String mediaType = "";
        public String interfaceType = "";
        public String serialNumber = "";

        public String getFormattedSize() {
            return DiskInfo.formatBytes(size);
        }

        public boolean isHealthy() {
            return "OK".equalsIgnoreCase(status);
        }
    }
}
