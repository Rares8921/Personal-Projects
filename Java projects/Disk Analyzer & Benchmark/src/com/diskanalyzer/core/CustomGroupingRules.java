package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Custom file grouping rules — allows users to define their own
 * file categorization rules beyond the built-in FileCategories.
 */
public final class CustomGroupingRules {

    private CustomGroupingRules() {}

    private static String getConfigDir() {
        return System.getProperty("user.dir") + File.separator + "config";
    }

    private static String getConfigFile() {
        return getConfigDir() + File.separator + "custom_groups.cfg";
    }

    public static class GroupRule {
        private final String groupName;
        private final List<String> extensions;
        private final String color;  // hex color for UI (optional)

        public GroupRule(String groupName, List<String> extensions, String color) {
            this.groupName = groupName;
            this.extensions = Collections.unmodifiableList(extensions);
            this.color = color;
        }

        public String getGroupName()      { return groupName; }
        public List<String> getExtensions() { return extensions; }
        public String getColor()           { return color; }

        /**
         * Check if a file extension matches this rule.
         */
        public boolean matches(String extension) {
            if (extension == null) return false;
            String lower = extension.toLowerCase();
            if (!lower.startsWith(".")) lower = "." + lower;
            return extensions.contains(lower);
        }

        /**
         * Check if a FileEntry matches this rule.
         */
        public boolean matches(FileEntry entry) {
            if (entry.isDirectory()) return false;
            return matches(entry.getExtension());
        }

        @Override
        public String toString() {
            return groupName + ": " + String.join(", ", extensions);
        }
    }

    /**
     * Load custom grouping rules from config file.
     */
    public static List<GroupRule> loadRules() {
        List<GroupRule> rules = new ArrayList<>();
        Path configFile = Paths.get(getConfigFile());
        if (!Files.exists(configFile)) return rules;

        try {
            List<String> lines = Files.readAllLines(configFile);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Format: GroupName|.ext1,.ext2,.ext3|#color
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    List<String> exts = Arrays.stream(parts[1].split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(s -> s.startsWith(".") ? s.toLowerCase() : "." + s.toLowerCase())
                        .collect(Collectors.toList());
                    String color = parts.length >= 3 ? parts[2].trim() : "#808080";

                    if (!name.isEmpty() && !exts.isEmpty()) {
                        rules.add(new GroupRule(name, exts, color));
                    }
                }
            }
        } catch (IOException ignored) {}
        return rules;
    }

    /**
     * Save custom grouping rules to config file.
     */
    public static void saveRules(List<GroupRule> rules) {
        try {
            Files.createDirectories(Paths.get(getConfigDir()));
            List<String> lines = new ArrayList<>();
            lines.add("# Custom File Grouping Rules");
            lines.add("# Format: GroupName|.ext1,.ext2,.ext3|#hexcolor");
            lines.add("");
            for (GroupRule rule : rules) {
                lines.add(rule.getGroupName() + "|" + String.join(",", rule.getExtensions()) + "|" + rule.getColor());
            }
            Files.write(Paths.get(getConfigFile()), lines);
        } catch (IOException ignored) {}
    }

    /**
     * Categorize a file entry using custom rules first, falling back to built-in categories.
     */
    public static String categorize(FileEntry entry, List<GroupRule> customRules) {
        if (entry.isDirectory()) return "Directories";

        // Check custom rules first (priority)
        for (GroupRule rule : customRules) {
            if (rule.matches(entry)) {
                return rule.getGroupName();
            }
        }

        // Fall back to built-in categories
        return FileCategories.categorize(entry);
    }

    /**
     * Group file entries using custom rules + built-in categories.
     * Returns map of category name -> [totalSize, fileCount].
     */
    public static Map<String, long[]> groupByCustomRules(List<FileEntry> entries, List<GroupRule> customRules) {
        Map<String, long[]> map = new LinkedHashMap<>();
        for (FileEntry e : entries) {
            String cat = categorize(e, customRules);
            long[] stats = map.computeIfAbsent(cat, k -> new long[2]);
            stats[0] += e.getSize();
            stats[1]++;
        }
        return map;
    }

    /**
     * Get all category names (custom + built-in).
     */
    public static List<String> getAllCategories(List<GroupRule> customRules) {
        List<String> cats = new ArrayList<>();
        for (GroupRule r : customRules) {
            cats.add(r.getGroupName());
        }
        cats.addAll(FileCategories.getAllCategories());
        return cats;
    }

    /**
     * Create a default set of example custom rules.
     */
    public static List<GroupRule> createDefaults() {
        List<GroupRule> defaults = new ArrayList<>();
        defaults.add(new GroupRule("Project Files",
            Arrays.asList(".sln", ".csproj", ".vcxproj", ".xcodeproj", ".pbxproj", ".gradle", ".pom"), "#4fc3f7"));
        defaults.add(new GroupRule("Virtual Machines",
            Arrays.asList(".vmdk", ".vdi", ".vhd", ".vhdx", ".ova", ".ovf", ".qcow2"), "#ff7043"));
        defaults.add(new GroupRule("Design Files",
            Arrays.asList(".fig", ".sketch", ".xd", ".blend", ".3ds", ".obj", ".fbx", ".stl"), "#ab47bc"));
        defaults.add(new GroupRule("Disk Images",
            Arrays.asList(".iso", ".img", ".dmg", ".wim", ".bin", ".cue", ".nrg"), "#ffa726"));
        return defaults;
    }
}
