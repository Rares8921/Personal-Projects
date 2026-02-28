package com.diskanalyzer.core;

import java.util.*;

/**
 * Auto-categorize files by their extension into human-readable groups.
 */
public final class FileCategories {

    private FileCategories() {}

    public static final String CAT_DOCUMENTS   = "Documents";
    public static final String CAT_IMAGES      = "Images";
    public static final String CAT_VIDEO       = "Video";
    public static final String CAT_AUDIO       = "Audio";
    public static final String CAT_ARCHIVES    = "Archives";
    public static final String CAT_CODE        = "Code";
    public static final String CAT_EXECUTABLES = "Executables";
    public static final String CAT_DATABASES   = "Databases";
    public static final String CAT_FONTS       = "Fonts";
    public static final String CAT_SYSTEM      = "System";
    public static final String CAT_TEMP        = "Temporary";
    public static final String CAT_OTHER       = "Other";

    private static final Map<String, String> EXT_MAP = new HashMap<>();

    static {
        // Documents
        for (String e : new String[]{".doc",".docx",".pdf",".txt",".rtf",".odt",".xls",".xlsx",".ppt",".pptx",
                ".csv",".xml",".html",".htm",".md",".tex",".epub",".pages",".numbers",".key"})
            EXT_MAP.put(e, CAT_DOCUMENTS);

        // Images
        for (String e : new String[]{".jpg",".jpeg",".png",".gif",".bmp",".svg",".ico",".tiff",".tif",
                ".webp",".raw",".cr2",".nef",".psd",".ai",".eps",".heic",".heif",".avif"})
            EXT_MAP.put(e, CAT_IMAGES);

        // Video
        for (String e : new String[]{".mp4",".avi",".mkv",".mov",".wmv",".flv",".webm",".m4v",
                ".mpg",".mpeg",".3gp",".ts",".vob"})
            EXT_MAP.put(e, CAT_VIDEO);

        // Audio
        for (String e : new String[]{".mp3",".wav",".flac",".aac",".ogg",".wma",".m4a",".opus",
                ".aiff",".mid",".midi"})
            EXT_MAP.put(e, CAT_AUDIO);

        // Archives
        for (String e : new String[]{".zip",".rar",".7z",".tar",".gz",".bz2",".xz",".cab",".iso",
                ".dmg",".img",".wim",".zst",".lz4"})
            EXT_MAP.put(e, CAT_ARCHIVES);

        // Code
        for (String e : new String[]{".java",".py",".js",".ts",".c",".cpp",".h",".cs",".go",".rs",
                ".rb",".php",".swift",".kt",".scala",".r",".lua",".sh",".bat",".ps1",
                ".json",".yaml",".yml",".toml",".ini",".cfg",".conf",".sql",".css",".scss"})
            EXT_MAP.put(e, CAT_CODE);

        // Executables
        for (String e : new String[]{".exe",".msi",".dll",".sys",".drv",".ocx",".com",".scr",
                ".app",".deb",".rpm",".apk",".jar",".war"})
            EXT_MAP.put(e, CAT_EXECUTABLES);

        // Databases
        for (String e : new String[]{".db",".sqlite",".sqlite3",".mdb",".accdb",".dbf",".ldf",".mdf",".ndf"})
            EXT_MAP.put(e, CAT_DATABASES);

        // Fonts
        for (String e : new String[]{".ttf",".otf",".woff",".woff2",".eot",".fon"})
            EXT_MAP.put(e, CAT_FONTS);

        // System
        for (String e : new String[]{".log",".dat",".reg",".inf",".lnk",".url",
                ".etl",".dmp",".evt",".evtx",".cat",".mui"})
            EXT_MAP.put(e, CAT_SYSTEM);

        // Temporary
        for (String e : new String[]{".tmp",".temp",".cache",".bak",".swp",".~",".old",".orig"})
            EXT_MAP.put(e, CAT_TEMP);
    }

    public static String categorize(String extension) {
        if (extension == null || extension.isEmpty()) return CAT_OTHER;
        String lower = extension.toLowerCase();
        return EXT_MAP.getOrDefault(lower, CAT_OTHER);
    }

    public static String categorize(FileEntry entry) {
        if (entry.isDirectory()) return "Directories";
        return categorize(entry.getExtension());
    }

    /**
     * Group a list of file entries by category, returning category -> (total size, count).
     */
    public static Map<String, long[]> groupByCategory(List<FileEntry> entries) {
        Map<String, long[]> map = new LinkedHashMap<>();
        for (FileEntry e : entries) {
            String cat = categorize(e);
            long[] stats = map.computeIfAbsent(cat, k -> new long[2]);
            stats[0] += e.getSize();
            stats[1]++;
        }
        return map;
    }

    public static List<String> getAllCategories() {
        return Arrays.asList(CAT_DOCUMENTS, CAT_IMAGES, CAT_VIDEO, CAT_AUDIO, CAT_ARCHIVES,
            CAT_CODE, CAT_EXECUTABLES, CAT_DATABASES, CAT_FONTS, CAT_SYSTEM, CAT_TEMP, CAT_OTHER);
    }
}
