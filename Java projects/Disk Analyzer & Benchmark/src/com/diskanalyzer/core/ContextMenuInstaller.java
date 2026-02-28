package com.diskanalyzer.core;

import java.io.*;

/**
 * Windows right-click context menu integration.
 * Generates a .reg file that adds "Analyze with DiskAnalyzer" to folder context menu.
 */
public class ContextMenuInstaller {

    /**
     * Generate a .reg file to add DiskAnalyzer to the Windows Explorer folder context menu.
     */
    public static void generateRegFile(File outputFile, String javaPath, String appJarPath) throws IOException {
        try (PrintWriter pw = new PrintWriter(outputFile)) {
            pw.println("Windows Registry Editor Version 5.00");
            pw.println();
            pw.println("; DiskAnalyzer & Benchmark - Folder Context Menu Integration");
            pw.println("; Import this file to add 'Analyze with DiskAnalyzer' to folder right-click menu");
            pw.println();
            
            // Folder context menu
            pw.println("[HKEY_CLASSES_ROOT\\Directory\\shell\\DiskAnalyzer]");
            pw.println("@=\"Analyze with DiskAnalyzer\"");
            pw.println("\"Icon\"=\"" + escapeRegPath(javaPath) + "\"");
            pw.println();
            pw.println("[HKEY_CLASSES_ROOT\\Directory\\shell\\DiskAnalyzer\\command]");
            pw.println("@=\"" + escapeRegPath(javaPath) + " -jar " + escapeRegPath(appJarPath) + " --gui \\\"%1\\\"\"");
            pw.println();

            // Directory background context menu
            pw.println("[HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\DiskAnalyzer]");
            pw.println("@=\"Analyze with DiskAnalyzer\"");
            pw.println("\"Icon\"=\"" + escapeRegPath(javaPath) + "\"");
            pw.println();
            pw.println("[HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\DiskAnalyzer\\command]");
            pw.println("@=\"" + escapeRegPath(javaPath) + " -jar " + escapeRegPath(appJarPath) + " --gui \\\"%V\\\"\"");
            pw.println();

            // Drive context menu
            pw.println("[HKEY_CLASSES_ROOT\\Drive\\shell\\DiskAnalyzer]");
            pw.println("@=\"Analyze with DiskAnalyzer\"");
            pw.println("\"Icon\"=\"" + escapeRegPath(javaPath) + "\"");
            pw.println();
            pw.println("[HKEY_CLASSES_ROOT\\Drive\\shell\\DiskAnalyzer\\command]");
            pw.println("@=\"" + escapeRegPath(javaPath) + " -jar " + escapeRegPath(appJarPath) + " --gui \\\"%1\\\"\"");
        }
    }

    /**
     * Generate uninstall .reg file to remove context menu entries.
     */
    public static void generateUninstallRegFile(File outputFile) throws IOException {
        try (PrintWriter pw = new PrintWriter(outputFile)) {
            pw.println("Windows Registry Editor Version 5.00");
            pw.println();
            pw.println("; Remove DiskAnalyzer context menu entries");
            pw.println();
            pw.println("[-HKEY_CLASSES_ROOT\\Directory\\shell\\DiskAnalyzer]");
            pw.println("[-HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\DiskAnalyzer]");
            pw.println("[-HKEY_CLASSES_ROOT\\Drive\\shell\\DiskAnalyzer]");
        }
    }

    private static String escapeRegPath(String path) {
        return path.replace("\\", "\\\\");
    }
}
