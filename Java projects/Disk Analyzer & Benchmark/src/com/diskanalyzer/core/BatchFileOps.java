package com.diskanalyzer.core;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.*;

/**
 * Batch file operations: move, copy, archive (ZIP) groups of files.
 */
public final class BatchFileOps {

    private BatchFileOps() {}

    public enum Operation { COPY, MOVE, DELETE, ARCHIVE_ZIP }

    public interface ProgressCallback {
        void onProgress(int completed, int total, String currentFile);
    }

    public static class BatchResult {
        private final int totalFiles;
        private final int succeeded;
        private final int failed;
        private final long totalBytes;
        private final List<String> errors;

        public BatchResult(int totalFiles, int succeeded, int failed, long totalBytes, List<String> errors) {
            this.totalFiles = totalFiles;
            this.succeeded = succeeded;
            this.failed = failed;
            this.totalBytes = totalBytes;
            this.errors = Collections.unmodifiableList(errors);
        }

        public int getTotalFiles()  { return totalFiles; }
        public int getSucceeded()   { return succeeded; }
        public int getFailed()      { return failed; }
        public long getTotalBytes() { return totalBytes; }
        public List<String> getErrors() { return errors; }
    }

    /**
     * Copy a list of files to a destination directory.
     */
    public static BatchResult copyFiles(List<String> sourcePaths, String destDir,
                                         ProgressCallback callback, boolean[] cancelFlag) {
        return performFileOp(sourcePaths, destDir, Operation.COPY, callback, cancelFlag);
    }

    /**
     * Move a list of files to a destination directory.
     */
    public static BatchResult moveFiles(List<String> sourcePaths, String destDir,
                                         ProgressCallback callback, boolean[] cancelFlag) {
        return performFileOp(sourcePaths, destDir, Operation.MOVE, callback, cancelFlag);
    }

    /**
     * Delete a list of files.
     */
    public static BatchResult deleteFiles(List<String> sourcePaths,
                                           ProgressCallback callback, boolean[] cancelFlag) {
        return performFileOp(sourcePaths, null, Operation.DELETE, callback, cancelFlag);
    }

    /**
     * Archive a list of files into a ZIP file.
     */
    public static BatchResult archiveFiles(List<String> sourcePaths, String zipPath,
                                            ProgressCallback callback, boolean[] cancelFlag) {
        int total = sourcePaths.size();
        int succeeded = 0;
        long totalBytes = 0;
        List<String> errors = new ArrayList<>();

        try {
            Files.createDirectories(Paths.get(zipPath).getParent());
        } catch (IOException e) {
            errors.add("Cannot create output directory: " + e.getMessage());
            return new BatchResult(total, 0, total, 0, errors);
        }

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            zos.setLevel(6); // balanced compression
            byte[] buffer = new byte[8192];

            for (int i = 0; i < total; i++) {
                if (cancelFlag != null && cancelFlag[0]) break;
                String src = sourcePaths.get(i);
                Path srcPath = Paths.get(src);

                if (callback != null) callback.onProgress(i + 1, total, srcPath.getFileName().toString());

                try {
                    if (Files.isDirectory(srcPath)) {
                        // Add directory recursively
                        Path base = srcPath.getParent();
                        Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                String entryName = base.relativize(file).toString().replace('\\', '/');
                                zos.putNextEntry(new ZipEntry(entryName));
                                Files.copy(file, zos);
                                zos.closeEntry();
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                String entryName = base.relativize(dir).toString().replace('\\', '/') + "/";
                                if (!entryName.equals("/")) {
                                    zos.putNextEntry(new ZipEntry(entryName));
                                    zos.closeEntry();
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } else if (Files.isRegularFile(srcPath)) {
                        ZipEntry entry = new ZipEntry(srcPath.getFileName().toString());
                        zos.putNextEntry(entry);
                        try (InputStream is = Files.newInputStream(srcPath)) {
                            int n;
                            while ((n = is.read(buffer)) > 0) {
                                zos.write(buffer, 0, n);
                            }
                        }
                        zos.closeEntry();
                        totalBytes += Files.size(srcPath);
                    }
                    succeeded++;
                } catch (IOException e) {
                    errors.add(src + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errors.add("ZIP creation failed: " + e.getMessage());
        }

        return new BatchResult(total, succeeded, total - succeeded, totalBytes, errors);
    }

    private static BatchResult performFileOp(List<String> sourcePaths, String destDir,
                                              Operation op, ProgressCallback callback, boolean[] cancelFlag) {
        int total = sourcePaths.size();
        int succeeded = 0;
        long totalBytes = 0;
        List<String> errors = new ArrayList<>();

        if (destDir != null) {
            try {
                Files.createDirectories(Paths.get(destDir));
            } catch (IOException e) {
                errors.add("Cannot create destination: " + e.getMessage());
                return new BatchResult(total, 0, total, 0, errors);
            }
        }

        for (int i = 0; i < total; i++) {
            if (cancelFlag != null && cancelFlag[0]) break;
            String src = sourcePaths.get(i);
            Path srcPath = Paths.get(src);

            if (callback != null) callback.onProgress(i + 1, total, srcPath.getFileName().toString());

            try {
                long fileSize = Files.isRegularFile(srcPath) ? Files.size(srcPath) : 0;

                switch (op) {
                    case COPY: {
                        Path dest = Paths.get(destDir, srcPath.getFileName().toString());
                        if (Files.isDirectory(srcPath)) {
                            copyDirectory(srcPath, dest);
                        } else {
                            Files.copy(srcPath, dest, StandardCopyOption.REPLACE_EXISTING);
                        }
                        totalBytes += fileSize;
                        succeeded++;
                        break;
                    }
                    case MOVE: {
                        Path dest = Paths.get(destDir, srcPath.getFileName().toString());
                        Files.move(srcPath, dest, StandardCopyOption.REPLACE_EXISTING);
                        totalBytes += fileSize;
                        succeeded++;
                        break;
                    }
                    case DELETE: {
                        if (Files.isDirectory(srcPath)) {
                            deleteDirectory(srcPath);
                        } else {
                            Files.deleteIfExists(srcPath);
                        }
                        totalBytes += fileSize;
                        succeeded++;
                        break;
                    }
                    default:
                        break;
                }
            } catch (IOException e) {
                errors.add(src + ": " + e.getMessage());
            }
        }

        int attempted = succeeded + errors.size();
        return new BatchResult(attempted, succeeded, errors.size(), totalBytes, errors);
    }

    private static boolean cancelled(boolean[] flag) {
        return flag != null && flag[0];
    }

    private static void copyDirectory(Path source, Path dest) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(dest.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, dest.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteDirectory(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
                Files.delete(d);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
