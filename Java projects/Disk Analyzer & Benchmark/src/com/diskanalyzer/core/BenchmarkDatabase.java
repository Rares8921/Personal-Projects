package com.diskanalyzer.core;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Benchmark comparison with online database.
 * Stores local results and provides percentile/ranking comparison
 * against a local aggregate database (simulating online comparison).
 * Also exports shareable benchmark cards.
 */
public final class BenchmarkDatabase {

    private BenchmarkDatabase() {}

    private static final String DB_DIR = System.getProperty("user.dir") + File.separator + "benchmark_db";
    private static final String DB_FILE = DB_DIR + File.separator + "benchmark_aggregate.csv";

    /**
     * Reference benchmark data for comparison (typical real-world values).
     */
    private static final List<ReferenceEntry> REFERENCE_DATA = new ArrayList<>();

    static {
        // Typical benchmark values for different drive types
        // driveType, seqRead, seqWrite, randRead, randWrite (MB/s)
        REFERENCE_DATA.add(new ReferenceEntry("HDD 7200RPM",    180,  170,    1.5,   1.5));
        REFERENCE_DATA.add(new ReferenceEntry("HDD 5400RPM",    120,  110,    0.8,   0.8));
        REFERENCE_DATA.add(new ReferenceEntry("SATA SSD",       560,  530,   50.0,  45.0));
        REFERENCE_DATA.add(new ReferenceEntry("NVMe SSD Gen3", 3500, 3000,  400.0, 350.0));
        REFERENCE_DATA.add(new ReferenceEntry("NVMe SSD Gen4", 7000, 5500,  700.0, 600.0));
        REFERENCE_DATA.add(new ReferenceEntry("NVMe SSD Gen5",12000,10000, 1200.0,1000.0));
        REFERENCE_DATA.add(new ReferenceEntry("USB 3.0 Flash",   90,   30,    5.0,   2.0));
        REFERENCE_DATA.add(new ReferenceEntry("USB 3.1 SSD",    450,  400,   35.0,  30.0));
        REFERENCE_DATA.add(new ReferenceEntry("SD Card UHS-I",   90,   60,    3.0,   2.0));
        REFERENCE_DATA.add(new ReferenceEntry("SD Card UHS-II", 300,  250,   10.0,   8.0));
    }

    public static class ReferenceEntry {
        public final String driveType;
        public final double seqReadMBps;
        public final double seqWriteMBps;
        public final double randReadMBps;
        public final double randWriteMBps;

        public ReferenceEntry(String driveType, double seqRead, double seqWrite,
                              double randRead, double randWrite) {
            this.driveType = driveType;
            this.seqReadMBps = seqRead;
            this.seqWriteMBps = seqWrite;
            this.randReadMBps = randRead;
            this.randWriteMBps = randWrite;
        }
    }

    public static class ComparisonResult {
        private final String bestMatchDriveType;
        private final double overallScore;
        private final int percentile;
        private final Map<String, Double> testScores;      // test -> your MB/s
        private final Map<String, Double> referenceScores;  // test -> reference MB/s
        private final Map<String, String> rankings;         // test -> "above/below average"
        private final List<ReferenceEntry> allReferences;

        public ComparisonResult(String bestMatch, double overallScore, int percentile,
                                Map<String, Double> testScores, Map<String, Double> refScores,
                                Map<String, String> rankings, List<ReferenceEntry> allRefs) {
            this.bestMatchDriveType = bestMatch;
            this.overallScore = overallScore;
            this.percentile = percentile;
            this.testScores = testScores;
            this.referenceScores = refScores;
            this.rankings = rankings;
            this.allReferences = allRefs;
        }

        public String getBestMatchDriveType() { return bestMatchDriveType; }
        public double getOverallScore()       { return overallScore; }
        public int getPercentile()            { return percentile; }
        public Map<String, Double> getTestScores()      { return testScores; }
        public Map<String, Double> getReferenceScores() { return referenceScores; }
        public Map<String, String> getRankings()        { return rankings; }
        public List<ReferenceEntry> getAllReferences()   { return allReferences; }
    }

    /**
     * Compare benchmark results against the reference database.
     */
    public static ComparisonResult compare(List<BenchmarkResult> results) {
        Map<String, Double> testScores = new LinkedHashMap<>();
        for (BenchmarkResult r : results) {
            testScores.put(r.getType().name(), r.getThroughputMBps());
        }

        String bestMatch = "Unknown";
        double bestDist = Double.MAX_VALUE;
        Map<String, Double> bestRefScores = new LinkedHashMap<>();

        for (ReferenceEntry ref : REFERENCE_DATA) {
            double dist = 0;
            int count = 0;
            for (BenchmarkResult r : results) {
                double refVal = getRefValue(ref, r.getType());
                if (refVal > 0) {
                    double ratio = r.getThroughputMBps() / refVal;
                    dist += Math.abs(Math.log(ratio)); // log-scale distance
                    count++;
                }
            }
            if (count > 0) {
                dist /= count;
                if (dist < bestDist) {
                    bestDist = dist;
                    bestMatch = ref.driveType;
                    bestRefScores.put("SEQ_READ", ref.seqReadMBps);
                    bestRefScores.put("SEQ_WRITE", ref.seqWriteMBps);
                    bestRefScores.put("RAND_READ", ref.randReadMBps);
                    bestRefScores.put("RAND_WRITE", ref.randWriteMBps);
                }
            }
        }

        Map<String, String> rankings = new LinkedHashMap<>();
        for (BenchmarkResult r : results) {
            double refVal = bestRefScores.getOrDefault(r.getType().name(), 0.0);
            if (refVal > 0) {
                double pct = (r.getThroughputMBps() / refVal - 1) * 100;
                if (pct > 10) rankings.put(r.getType().name(), String.format("%.0f%% above average", pct));
                else if (pct < -10) rankings.put(r.getType().name(), String.format("%.0f%% below average", Math.abs(pct)));
                else rankings.put(r.getType().name(), "Average");
            }
        }

        // Calculate overall score (0-100 scale, SATA SSD = 50)
        double overallScore = calculateOverallScore(results);

        int percentile = calculatePercentile(results);

        return new ComparisonResult(bestMatch, overallScore, percentile,
                testScores, bestRefScores, rankings, REFERENCE_DATA);
    }

    private static double getRefValue(ReferenceEntry ref, BenchmarkResult.Type type) {
        switch (type) {
            case SEQ_READ: return ref.seqReadMBps;
            case SEQ_WRITE: return ref.seqWriteMBps;
            case RAND_READ: return ref.randReadMBps;
            case RAND_WRITE: return ref.randWriteMBps;
            default: return 0;
        }
    }

    private static double calculateOverallScore(List<BenchmarkResult> results) {
        // Weighted score: Seq Read 30%, Seq Write 25%, Rand Read 25%, Rand Write 20%
        double score = 0;
        double maxScore = 0;
        for (BenchmarkResult r : results) {
            double weight;
            double refMax; // Gen5 NVMe as 100%
            switch (r.getType()) {
                case SEQ_READ:  weight = 0.30; refMax = 12000; break;
                case SEQ_WRITE: weight = 0.25; refMax = 10000; break;
                case RAND_READ: weight = 0.25; refMax = 1200;  break;
                case RAND_WRITE:weight = 0.20; refMax = 1000;  break;
                default: weight = 0; refMax = 1; break;
            }
            score += weight * Math.min(r.getThroughputMBps() / refMax * 100, 100);
            maxScore += weight * 100;
        }
        return maxScore > 0 ? score / maxScore * 100 : 0;
    }

    private static int calculatePercentile(List<BenchmarkResult> results) {
        double avgThroughput = results.stream()
            .mapToDouble(BenchmarkResult::getThroughputMBps)
            .average()
            .orElse(0);

        int belowCount = 0;
        for (ReferenceEntry ref : REFERENCE_DATA) {
            double refAvg = (ref.seqReadMBps + ref.seqWriteMBps + ref.randReadMBps + ref.randWriteMBps) / 4;
            if (avgThroughput > refAvg) belowCount++;
        }

        return (int) ((double) belowCount / REFERENCE_DATA.size() * 100);
    }

    /**
     * Save benchmark results to local database for historical comparison.
     */
    public static void saveToDatabase(String driveName, List<BenchmarkResult> results) {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(DB_DIR));
            boolean exists = new File(DB_FILE).exists();

            try (PrintWriter pw = new PrintWriter(new FileWriter(DB_FILE, true))) {
                if (!exists) {
                    pw.println("timestamp,drive,type,throughputMBps,iops,avgLatencyUs");
                }
                long ts = System.currentTimeMillis();
                for (BenchmarkResult r : results) {
                    pw.printf("%d,%s,%s,%.2f,%.2f,%.2f%n",
                        ts, driveName, r.getType(), r.getThroughputMBps(),
                        r.getIops(), r.getAvgLatencyUs());
                }
            }
        } catch (IOException ignored) {}
    }

    /**
     * Load historical benchmark data from local database.
     */
    public static List<Map<String, String>> loadDatabase() {
        List<Map<String, String>> records = new ArrayList<>();
        File f = new File(DB_FILE);
        if (!f.exists()) return records;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine();
            if (header == null) return records;
            String[] cols = header.split(",");

            String line;
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(",");
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < cols.length && i < vals.length; i++) {
                    row.put(cols[i], vals[i]);
                }
                records.add(row);
            }
        } catch (IOException ignored) {}
        return records;
    }

    /**
     * Get list of all reference drive types and their specifications.
     */
    public static List<ReferenceEntry> getReferenceData() {
        return Collections.unmodifiableList(REFERENCE_DATA);
    }
}
