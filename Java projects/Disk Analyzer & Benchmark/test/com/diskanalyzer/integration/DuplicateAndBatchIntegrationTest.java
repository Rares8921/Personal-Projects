package com.diskanalyzer.integration;

import com.diskanalyzer.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Integration tests: Duplicate finding → batch operations pipeline.
 * Tests finding duplicates and then batch moving/archiving them.
 */
class DuplicateAndBatchIntegrationTest {

    @TempDir
    Path tempDir;

    private Path testDataDir;

    @BeforeEach
    void setupTestData() throws IOException {
        testDataDir = tempDir.resolve("data");
        Files.createDirectories(testDataDir);
        Files.createDirectories(testDataDir.resolve("folder1"));
        Files.createDirectories(testDataDir.resolve("folder2"));

        byte[] duplicateContent = "This is duplicate file content for integration testing.".getBytes();
        byte[] uniqueContent = "This is unique content.".getBytes();

        Files.write(testDataDir.resolve("folder1/dup_a.txt"), duplicateContent);
        Files.write(testDataDir.resolve("folder2/dup_b.txt"), duplicateContent);
        Files.write(testDataDir.resolve("unique1.txt"), uniqueContent);
        Files.write(testDataDir.resolve("unique2.txt"), "Different content".getBytes());
    }

    @Test
    @DisplayName("Find duplicates → archive duplicates into ZIP")
    void testFindDuplicatesAndArchive() throws IOException {
        // 1. Find duplicates
        List<DuplicateFileFinder.DuplicateGroup> groups =
            DuplicateFileFinder.findDuplicates(testDataDir.toString(), 0, null, null);

        assertFalse(groups.isEmpty(), "Should find at least one duplicate group");
        DuplicateFileFinder.DuplicateGroup group = groups.get(0);
        assertEquals(2, group.getCount());

        // 2. Archive duplicates
        String zipPath = tempDir.resolve("duplicates.zip").toString();
        List<String> dupPaths = new ArrayList<>(group.getPaths());

        BatchFileOps.BatchResult archiveResult = BatchFileOps.archiveFiles(dupPaths, zipPath, null, null);
        assertEquals(2, archiveResult.getSucceeded());
        assertTrue(Files.exists(Paths.get(zipPath)));
    }

    @Test
    @DisplayName("Find duplicates → move extras to 'duplicates' folder")
    void testFindDuplicatesAndMove() throws IOException {
        // 1. Find duplicates
        List<DuplicateFileFinder.DuplicateGroup> groups =
            DuplicateFileFinder.findDuplicates(testDataDir.toString(), 0, null, null);

        assertFalse(groups.isEmpty());
        DuplicateFileFinder.DuplicateGroup group = groups.get(0);

        // 2. Move all but the first (keep one, move extras)
        Path dupDir = tempDir.resolve("moved_duplicates");
        List<String> toMove = group.getPaths().subList(1, group.getCount());

        BatchFileOps.BatchResult moveResult = BatchFileOps.moveFiles(
            new ArrayList<>(toMove), dupDir.toString(), null, null);

        assertEquals(1, moveResult.getSucceeded());
        assertTrue(Files.exists(dupDir));
    }

    @Test
    @DisplayName("Batch copy specific file types")
    void testBatchCopyByType() throws IOException {
        // Scan directory
        ParallelScanner.ParallelScanResult scanResult = ParallelScanner.scan(
            testDataDir.toString(), 0, 2, null, null, null);

        // Filter to only .txt files
        List<String> txtFiles = new ArrayList<>();
        for (FileEntry e : scanResult.getEntries()) {
            if (e.getExtension().equals(".txt")) {
                txtFiles.add(e.getPath());
            }
        }
        assertFalse(txtFiles.isEmpty());

        // Batch copy them
        Path backupDir = tempDir.resolve("txt_backup");
        BatchFileOps.BatchResult copyResult = BatchFileOps.copyFiles(txtFiles, backupDir.toString(), null, null);

        assertTrue(copyResult.getSucceeded() > 0);
        assertEquals(0, copyResult.getFailed());
    }

    @Test
    @DisplayName("Batch delete with progress tracking")
    void testBatchDeleteWithProgress() throws IOException {
        // Create some expendable files
        Path expendable = tempDir.resolve("expendable");
        Files.createDirectories(expendable);
        for (int i = 0; i < 5; i++) {
            Files.write(expendable.resolve("temp" + i + ".tmp"), ("data" + i).getBytes());
        }

        List<String> toDelete = new ArrayList<>();
        try (var stream = Files.list(expendable)) {
            stream.forEach(p -> toDelete.add(p.toString()));
        }

        List<Integer> progressUpdates = new ArrayList<>();
        BatchFileOps.ProgressCallback callback = (completed, total, file) -> {
            progressUpdates.add(completed);
        };

        BatchFileOps.BatchResult delResult = BatchFileOps.deleteFiles(toDelete, callback, null);
        assertEquals(5, delResult.getSucceeded());
        assertEquals(5, progressUpdates.size());

        // Verify files are deleted
        for (String p : toDelete) {
            assertFalse(Files.exists(Paths.get(p)));
        }
    }
}
