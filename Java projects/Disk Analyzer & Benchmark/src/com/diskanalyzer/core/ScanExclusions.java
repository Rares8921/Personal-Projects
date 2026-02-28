package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

/**
 * Scan exclusion patterns — allows users to define glob/regex patterns
 * to exclude files and folders from scans.
 */
public final class ScanExclusions {

    private ScanExclusions() {}

    private static String getConfigDir() {
        return System.getProperty("user.dir") + File.separator + "config";
    }

    private static String getConfigFile() {
        return getConfigDir() + File.separator + "exclusions.txt";
    }

    /**
     * Default system exclusions that are always applied.
     */
    private static final List<String> DEFAULT_EXCLUSIONS = Arrays.asList(
        "$Recycle.Bin",
        "System Volume Information",
        "$WINDOWS.~BT",
        "$WINDOWS.~WS",
        "pagefile.sys",
        "hiberfil.sys",
        "swapfile.sys"
    );

    /**
     * Load user-defined exclusion patterns from config file.
     */
    public static List<String> loadExclusions() {
        List<String> patterns = new ArrayList<>(DEFAULT_EXCLUSIONS);
        Path configFile = Paths.get(getConfigFile());
        if (Files.exists(configFile)) {
            try {
                List<String> lines = Files.readAllLines(configFile);
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        patterns.add(line);
                    }
                }
            } catch (IOException ignored) {}
        }
        return patterns;
    }

    /**
     * Save user-defined exclusion patterns to config file.
     */
    public static void saveExclusions(List<String> userPatterns) {
        try {
            Files.createDirectories(Paths.get(getConfigDir()));
            List<String> lines = new ArrayList<>();
            lines.add("# Disk Analyzer Exclusion Patterns");
            lines.add("# One pattern per line. Supports glob patterns (*, ?) and regex (prefix with regex:)");
            lines.add("# Lines starting with # are comments");
            lines.add("");
            lines.addAll(userPatterns);
            Files.write(Paths.get(getConfigFile()), lines);
        } catch (IOException ignored) {}
    }

    /**
     * Check if a path should be excluded based on the given patterns.
     */
    public static boolean shouldExclude(String filePath, List<String> patterns) {
        if (filePath == null || patterns == null) return false;

        String normalizedPath = filePath.replace('/', '\\');
        String fileName = Paths.get(filePath).getFileName().toString();

        for (String pattern : patterns) {
            if (pattern == null || pattern.isEmpty()) continue;

            if (pattern.startsWith("regex:")) {
                // Regex pattern
                String regex = pattern.substring(6).trim();
                try {
                    if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(normalizedPath).find()) {
                        return true;
                    }
                } catch (PatternSyntaxException ignored) {}
            } else if (pattern.contains("*") || pattern.contains("?")) {
                // Glob pattern
                String regex = globToRegex(pattern);
                try {
                    if (Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(normalizedPath).find()) {
                        return true;
                    }
                } catch (PatternSyntaxException ignored) {}
            } else {
                // Exact name match (case-insensitive)
                if (fileName.equalsIgnoreCase(pattern) ||
                    normalizedPath.toLowerCase().contains(pattern.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Filter a list of FileEntry objects, removing excluded ones.
     */
    public static List<FileEntry> filterEntries(List<FileEntry> entries, List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) return entries;
        return entries.stream()
            .filter(e -> !shouldExclude(e.getPath(), patterns))
            .collect(Collectors.toList());
    }

    /**
     * Get default system exclusions.
     */
    public static List<String> getDefaultExclusions() {
        return Collections.unmodifiableList(DEFAULT_EXCLUSIONS);
    }

    /**
     * Convert a glob pattern to a regex pattern.
     */
    static String globToRegex(String glob) {
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            switch (c) {
                case '*':
                    if (i + 1 < glob.length() && glob.charAt(i + 1) == '*') {
                        regex.append(".*");
                        i++; // skip second *
                    } else {
                        regex.append("[^\\\\]*");
                    }
                    break;
                case '?':  regex.append("[^\\\\]"); break;
                case '.':  regex.append("\\."); break;
                case '(':  regex.append("\\("); break;
                case ')':  regex.append("\\)"); break;
                case '{':  regex.append("\\{"); break;
                case '}':  regex.append("\\}"); break;
                case '[':  regex.append("\\["); break;
                case ']':  regex.append("\\]"); break;
                case '+':  regex.append("\\+"); break;
                case '^':  regex.append("\\^"); break;
                case '$':  regex.append("\\$"); break;
                case '|':  regex.append("\\|"); break;
                case '\\': regex.append("\\\\"); break;
                default:   regex.append(c); break;
            }
        }
        return regex.toString();
    }
}
