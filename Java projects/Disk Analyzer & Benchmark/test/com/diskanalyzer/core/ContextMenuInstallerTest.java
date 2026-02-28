package com.diskanalyzer.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

/**
 * Unit tests for ContextMenuInstaller class.
 */
class ContextMenuInstallerTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("generateRegFile creates valid .reg file with context menu entries")
    void testGenerateRegFile() throws IOException {
        File regFile = tempDir.resolve("install.reg").toFile();
        ContextMenuInstaller.generateRegFile(regFile,
                "C:\\Program Files\\Java\\jdk-17\\bin\\java.exe",
                "C:\\DiskAnalyzer\\app.jar");

        assertTrue(regFile.exists());
        String content = Files.readString(regFile.toPath());
        assertTrue(content.contains("Windows Registry Editor"));
        assertTrue(content.contains("HKEY_CLASSES_ROOT"));
        assertTrue(content.contains("DiskAnalyzer"));
        assertTrue(content.contains("Analyze with DiskAnalyzer"));
        // Verify all 3 context menu locations
        assertTrue(content.contains("Directory\\shell"));
        assertTrue(content.contains("Directory\\Background\\shell"));
        assertTrue(content.contains("Drive\\shell"));
    }

    @Test
    @DisplayName("generateUninstallRegFile creates removal .reg file with [-KEY] syntax")
    void testGenerateUninstallRegFile() throws IOException {
        File regFile = tempDir.resolve("uninstall.reg").toFile();
        ContextMenuInstaller.generateUninstallRegFile(regFile);

        assertTrue(regFile.exists());
        String content = Files.readString(regFile.toPath());
        assertTrue(content.contains("Windows Registry Editor"));
        // Uninstall reg files use [-KEY] syntax to delete keys
        assertTrue(content.contains("[-HKEY_CLASSES_ROOT"));
    }
}
