package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for BatchFileOps class.
 */
class BatchFileOpsTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("copyFiles copies files to destination")
    void testCopyFiles() throws IOException {
        Path src = tempDir.resolve("source");
        Files.createDirectory(src);
        Files.write(src.resolve("a.txt"), "hello".getBytes());
        Files.write(src.resolve("b.txt"), "world".getBytes());

        Path dest = tempDir.resolve("dest");
        Files.createDirectory(dest);

        BatchFileOps.BatchResult result = BatchFileOps.copyFiles(
            Arrays.asList(src.resolve("a.txt").toString(), src.resolve("b.txt").toString()),
            dest.toString(), null, null);

        assertEquals(2, result.getTotalFiles());
        assertEquals(2, result.getSucceeded());
        assertEquals(0, result.getFailed());
        assertTrue(Files.exists(dest.resolve("a.txt")));
        assertTrue(Files.exists(dest.resolve("b.txt")));
        // Source files should still exist
        assertTrue(Files.exists(src.resolve("a.txt")));
    }

    @Test
    @DisplayName("moveFiles moves files to destination")
    void testMoveFiles() throws IOException {
        Path src = tempDir.resolve("movesrc");
        Files.createDirectory(src);
        Files.write(src.resolve("file.txt"), "data".getBytes());

        Path dest = tempDir.resolve("movedest");
        Files.createDirectory(dest);

        BatchFileOps.BatchResult result = BatchFileOps.moveFiles(
            List.of(src.resolve("file.txt").toString()),
            dest.toString(), null, null);

        assertEquals(1, result.getSucceeded());
        assertFalse(Files.exists(src.resolve("file.txt")));
        assertTrue(Files.exists(dest.resolve("file.txt")));
    }

    @Test
    @DisplayName("deleteFiles deletes files")
    void testDeleteFiles() throws IOException {
        Path file1 = tempDir.resolve("del1.txt");
        Path file2 = tempDir.resolve("del2.txt");
        Files.write(file1, "delete me".getBytes());
        Files.write(file2, "delete me too".getBytes());

        BatchFileOps.BatchResult result = BatchFileOps.deleteFiles(
            Arrays.asList(file1.toString(), file2.toString()), null, null);

        assertEquals(2, result.getSucceeded());
        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(file2));
    }

    @Test
    @DisplayName("archiveFiles creates ZIP file")
    void testArchiveFiles() throws IOException {
        Files.write(tempDir.resolve("zip1.txt"), "content1".getBytes());
        Files.write(tempDir.resolve("zip2.txt"), "content2".getBytes());

        String zipPath = tempDir.resolve("archive.zip").toString();
        BatchFileOps.BatchResult result = BatchFileOps.archiveFiles(
            Arrays.asList(
                tempDir.resolve("zip1.txt").toString(),
                tempDir.resolve("zip2.txt").toString()
            ), zipPath, null, null);

        assertEquals(2, result.getSucceeded());
        assertTrue(Files.exists(Paths.get(zipPath)));
        assertTrue(Files.size(Paths.get(zipPath)) > 0);
    }

    @Test
    @DisplayName("copyFiles can be cancelled")
    void testCancelCopy() throws IOException {
        for (int i = 0; i < 5; i++) {
            Files.write(tempDir.resolve("f" + i + ".txt"), ("data" + i).getBytes());
        }
        Path dest = tempDir.resolve("cancelled");
        Files.createDirectory(dest);

        boolean[] cancel = {true}; // Immediately cancel
        List<String> files = new ArrayList<>();
        for (int i = 0; i < 5; i++) files.add(tempDir.resolve("f" + i + ".txt").toString());

        BatchFileOps.BatchResult result = BatchFileOps.copyFiles(files, dest.toString(), null, cancel);
        assertNotNull(result);
    }

    @Test
    @DisplayName("copyFiles reports progress")
    void testProgressCallback() throws IOException {
        Files.write(tempDir.resolve("prog.txt"), "data".getBytes());
        Path dest = tempDir.resolve("progdest");
        Files.createDirectory(dest);

        List<String> progressFiles = new ArrayList<>();
        BatchFileOps.ProgressCallback cb = (completed, total, file) -> progressFiles.add(file);

        BatchFileOps.copyFiles(
            List.of(tempDir.resolve("prog.txt").toString()),
            dest.toString(), cb, null);

        assertEquals(1, progressFiles.size());
        assertEquals("prog.txt", progressFiles.get(0));
    }

    @Test
    @DisplayName("BatchResult has correct error tracking")
    void testBatchResultErrors() {
        BatchFileOps.BatchResult result = new BatchFileOps.BatchResult(5, 3, 2, 1000,
            Arrays.asList("error1", "error2"));
        assertEquals(5, result.getTotalFiles());
        assertEquals(3, result.getSucceeded());
        assertEquals(2, result.getFailed());
        assertEquals(1000, result.getTotalBytes());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("copyFiles handles nonexistent source gracefully")
    void testCopyNonexistent() {
        Path dest = tempDir.resolve("destx");
        BatchFileOps.BatchResult result = BatchFileOps.copyFiles(
            List.of("C:\\nonexistent\\file.txt"),
            dest.toString(), null, null);

        assertEquals(1, result.getTotalFiles());
        assertFalse(result.getErrors().isEmpty());
    }
}
