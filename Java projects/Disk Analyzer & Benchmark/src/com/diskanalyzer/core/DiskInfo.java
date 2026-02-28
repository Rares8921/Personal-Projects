package com.diskanalyzer.core;

public final class DiskInfo {
    private final char   drive;
    private final String volumeName;
    private final String fileSystem;
    private final String serialNumber;
    private final long   totalBytes;
    private final long   freeBytes;
    private final long   usedBytes;
    private final long   clusterSize;
    private final long   sectorSize;
    private final String driveType;
    private final Boolean isSSD;

    private DiskInfo(Builder b) {
        this.drive        = b.drive;
        this.volumeName   = b.volumeName;
        this.fileSystem   = b.fileSystem;
        this.serialNumber = b.serialNumber;
        this.totalBytes   = b.totalBytes;
        this.freeBytes    = b.freeBytes;
        this.usedBytes    = b.usedBytes;
        this.clusterSize  = b.clusterSize;
        this.sectorSize   = b.sectorSize;
        this.driveType    = b.driveType;
        this.isSSD        = b.isSSD;
    }

    public char    getDrive()        { return drive; }
    public String  getVolumeName()   { return volumeName; }
    public String  getFileSystem()   { return fileSystem; }
    public String  getSerialNumber() { return serialNumber; }
    public long    getTotalBytes()   { return totalBytes; }
    public long    getFreeBytes()    { return freeBytes; }
    public long    getUsedBytes()    { return usedBytes; }
    public long    getClusterSize()  { return clusterSize; }
    public long    getSectorSize()   { return sectorSize; }
    public String  getDriveType()    { return driveType; }
    public Boolean isSSD()           { return isSSD; }

    public double getUsagePercent() {
        return totalBytes > 0 ? (usedBytes * 100.0 / totalBytes) : 0;
    }

    public static DiskInfo fromJson(String json) {
        Builder b = new Builder();
        b.drive        = extractChar(json, "drive");
        b.volumeName   = extractString(json, "volumeName");
        b.fileSystem   = extractString(json, "fileSystem");
        b.serialNumber = extractString(json, "serialNumber");
        b.totalBytes   = extractLong(json, "totalBytes");
        b.freeBytes    = extractLong(json, "freeBytes");
        b.usedBytes    = extractLong(json, "usedBytes");
        b.clusterSize  = extractLong(json, "clusterSize");
        b.sectorSize   = extractLong(json, "sectorSize");
        b.driveType    = extractString(json, "driveType");
        String ssd     = extractRaw(json, "isSSD");
        b.isSSD        = "null".equals(ssd) ? null : Boolean.valueOf(ssd);
        return new DiskInfo(b);
    }

    @Override
    public String toString() {
        return String.format("[%c:] %s (%s) %s | %s / %s (%.1f%% used) | %s",
            drive,
            volumeName.isEmpty() ? "No Label" : volumeName,
            fileSystem,
            driveType,
            formatBytes(usedBytes),
            formatBytes(totalBytes),
            getUsagePercent(),
            isSSD == null ? "Unknown" : (isSSD ? "SSD" : "HDD"));
    }

    public static String formatBytes(long bytes) {
        if (bytes < 0) return "N/A";
        double val = bytes;
        String[] units = {"B", "KB", "MB", "GB", "TB", "PB"};
        int idx = 0;
        while (val >= 1024 && idx < units.length - 1) { val /= 1024; idx++; }
        return idx == 0 ? String.format("%d %s", bytes, units[0])
                        : String.format("%.2f %s", val, units[idx]);
    }

    private static String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return end < 0 ? "" : json.substring(start, end);
    }

    private static long extractLong(String json, String key) {
        String raw = extractRaw(json, key);
        try { return Long.parseLong(raw); } catch (Exception e) { return 0; }
    }

    private static char extractChar(String json, String key) {
        String s = extractString(json, key);
        return s.isEmpty() ? '?' : s.charAt(0);
    }

    private static String extractRaw(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start < 0) return "";
        start += search.length();
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == ',' || c == '}') break;
            end++;
        }
        return json.substring(start, end).trim();
    }

    static final class Builder {
        char drive;
        String volumeName = "", fileSystem = "", serialNumber = "", driveType = "";
        long totalBytes, freeBytes, usedBytes, clusterSize, sectorSize;
        Boolean isSSD;
    }
}
