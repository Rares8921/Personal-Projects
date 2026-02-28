package com.diskanalyzer.core;

public final class FileEntry implements Comparable<FileEntry> {
    private final String  path;
    private final long    size;
    private final boolean directory;
    private final long    modifiedTime;

    public FileEntry(String path, long size, boolean directory, long modifiedTime) {
        this.path         = path;
        this.size         = size;
        this.directory    = directory;
        this.modifiedTime = modifiedTime;
    }

    public String  getPath()         { return path; }
    public long    getSize()         { return size; }
    public boolean isDirectory()     { return directory; }
    public long    getModifiedTime() { return modifiedTime; }

    public String getName() {
        int sep = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return sep >= 0 ? path.substring(sep + 1) : path;
    }

    public String getExtension() {
        if (directory) return "";
        String name = getName();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot).toLowerCase() : "(none)";
    }

    public String getFormattedSize() {
        return DiskInfo.formatBytes(size);
    }

    @Override
    public int compareTo(FileEntry o) {
        return Long.compare(o.size, this.size);
    }

    @Override
    public String toString() {
        return String.format("%s %12s  %s",
            directory ? "[DIR]" : "     ",
            getFormattedSize(),
            path);
    }
}
