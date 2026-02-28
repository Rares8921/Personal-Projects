package com.diskanalyzer.core;

import java.util.*;

public final class FragmentationResult {

    private final int    totalFiles;
    private final int    fragmentedFiles;
    private final int    totalFragments;
    private final double fragmentationPct;
    private final double avgFragmentsPerFile;
    private final List<FragmentedFile> topFragmented;

    public FragmentationResult(int totalFiles, int fragmentedFiles, int totalFragments,
                               double fragmentationPct, double avgFragmentsPerFile,
                               List<FragmentedFile> topFragmented) {
        this.totalFiles         = totalFiles;
        this.fragmentedFiles    = fragmentedFiles;
        this.totalFragments     = totalFragments;
        this.fragmentationPct   = fragmentationPct;
        this.avgFragmentsPerFile = avgFragmentsPerFile;
        this.topFragmented      = Collections.unmodifiableList(topFragmented);
    }

    public int    getTotalFiles()          { return totalFiles; }
    public int    getFragmentedFiles()     { return fragmentedFiles; }
    public int    getTotalFragments()      { return totalFragments; }
    public double getFragmentationPct()    { return fragmentationPct; }
    public double getAvgFragmentsPerFile() { return avgFragmentsPerFile; }
    public List<FragmentedFile> getTopFragmented() { return topFragmented; }

    public String getHealthRating() {
        if (fragmentationPct < 5)  return "Excellent";
        if (fragmentationPct < 15) return "Good";
        if (fragmentationPct < 30) return "Fair";
        if (fragmentationPct < 50) return "Poor";
        return "Critical";
    }

    public static FragmentationResult fromJson(String json) {
        int totalFiles     = (int) extractLong(json, "totalFiles");
        int fragFiles      = (int) extractLong(json, "fragmentedFiles");
        int totalFrags     = (int) extractLong(json, "totalFragments");
        double fragPct     = extractDouble(json, "fragmentationPct");
        double avgFrags    = extractDouble(json, "avgFragmentsPerFile");

        List<FragmentedFile> top = new ArrayList<>();
        int arrStart = json.indexOf("\"topFragmented\":[");
        if (arrStart >= 0) {
            arrStart = json.indexOf('[', arrStart);
            int arrEnd = json.lastIndexOf(']');
            if (arrEnd > arrStart) {
                String arrStr = json.substring(arrStart, arrEnd + 1);
                int idx = 0;
                while (true) {
                    int objStart = arrStr.indexOf('{', idx);
                    if (objStart < 0) break;
                    int objEnd = arrStr.indexOf('}', objStart);
                    if (objEnd < 0) break;
                    String obj = arrStr.substring(objStart, objEnd + 1);
                    String path = extractString(obj, "path");
                    int fragments = (int) extractLong(obj, "fragments");
                    long size = extractLong(obj, "size");
                    top.add(new FragmentedFile(path, fragments, size));
                    idx = objEnd + 1;
                }
            }
        }

        return new FragmentationResult(totalFiles, fragFiles, totalFrags,
            fragPct, avgFrags, top);
    }

    @Override
    public String toString() {
        return String.format("Fragmentation: %.1f%% (%s) - %d/%d files fragmented, %.1f avg fragments",
            fragmentationPct, getHealthRating(), fragmentedFiles, totalFiles, avgFragmentsPerFile);
    }

    private static String extractString(String json, String key) {
        String s = "\"" + key + "\":\"";
        int i = json.indexOf(s);
        if (i < 0) return "";
        i += s.length();
        int e = json.indexOf("\"", i);
        return e < 0 ? "" : json.substring(i, e);
    }

    private static long extractLong(String json, String key) {
        String raw = extractRaw(json, key);
        try { return Long.parseLong(raw); } catch (Exception e) { return 0; }
    }

    private static double extractDouble(String json, String key) {
        String raw = extractRaw(json, key);
        try { return Double.parseDouble(raw); } catch (Exception e) { return 0; }
    }

    private static String extractRaw(String json, String key) {
        String s = "\"" + key + "\":";
        int i = json.indexOf(s);
        if (i < 0) return "0";
        i += s.length();
        int e = i;
        while (e < json.length() && json.charAt(e) != ',' && json.charAt(e) != '}' && json.charAt(e) != ']') e++;
        return json.substring(i, e).trim().replace("\"", "");
    }

    public static final class FragmentedFile {
        private final String path;
        private final int    fragments;
        private final long   size;

        public FragmentedFile(String path, int fragments, long size) {
            this.path      = path;
            this.fragments = fragments;
            this.size      = size;
        }

        public String getPath()      { return path; }
        public int    getFragments() { return fragments; }
        public long   getSize()      { return size; }

        public String getName() {
            int sep = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
            return sep >= 0 ? path.substring(sep + 1) : path;
        }

        public String getFormattedSize() { return DiskInfo.formatBytes(size); }

        @Override
        public String toString() {
            return String.format("%s - %d fragments (%s)", getName(), fragments, getFormattedSize());
        }
    }
}
