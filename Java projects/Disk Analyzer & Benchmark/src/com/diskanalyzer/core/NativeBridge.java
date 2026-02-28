package com.diskanalyzer.core;

public final class NativeBridge {

    private static boolean loaded = false;
    private static String loadError = null;

    static {
        try {
            System.loadLibrary("diskanalyzer");
            loaded = true;
        } catch (UnsatisfiedLinkError e) {
            loadError = e.getMessage();
            try {
                String dir = System.getProperty("user.dir");
                System.load(dir + "\\diskanalyzer.dll");
                loaded = true;
                loadError = null;
            } catch (UnsatisfiedLinkError e2) {
                loadError = e2.getMessage();
            }
        }
    }

    private NativeBridge() {}

    public static boolean isLoaded() { return loaded; }
    public static String getLoadError() { return loadError; }

    public static native String[]   getAvailableDrives();
    public static native String     getDiskInfoJson(char driveLetter);
    public static native long[]     getDiskSpace(char driveLetter);

    public static native String     benchmarkSeqRead(String targetDir, int blockSizeKB, int totalSizeMB);
    public static native String     benchmarkSeqWrite(String targetDir, int blockSizeKB, int totalSizeMB);
    public static native String     benchmarkRandRead(String targetDir, int blockSizeKB, int iterations);
    public static native String     benchmarkRandWrite(String targetDir, int blockSizeKB, int iterations);

    public static native long       getDirectorySize(String path);
    public static native int        getFileCount(String path);
    public static native String     scanDirectory(String path, int maxEntries);
    public static native String     findLargestFiles(String path, int maxResults);
    public static native String     getExtensionStats(String path);

    public static native String     fullScan(String path, int maxEntries, int maxLargest);

    public static native long[]     getMemoryInfo();

    public static native String     analyzeFragmentation(String path, int maxFiles);
}
