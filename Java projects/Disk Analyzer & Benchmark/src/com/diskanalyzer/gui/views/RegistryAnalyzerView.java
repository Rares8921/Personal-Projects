package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.util.*;

/**
 * Registry size analyzer - shows the size of Windows registry hive files.
 */
public class RegistryAnalyzerView {

    private final VBox container;
    private final Label statusLabel;
    private VBox resultsBox;

    public RegistryAnalyzerView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        Label header = new Label("Registry Size Analyzer");
        header.getStyleClass().add("view-header");

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(8, 0, 8, 0));

        Button scanBtn = new Button("Analyze Registry");
        scanBtn.getStyleClass().add("action-button");
        scanBtn.setOnAction(e -> analyzeRegistry());

        controls.getChildren().add(scanBtn);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("edge-to-edge-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        resultsBox = new VBox(8);
        resultsBox.setPadding(new Insets(12));
        scroll.setContent(resultsBox);

        container.getChildren().addAll(header, controls, scroll);
    }

    public Node getView() { return container; }

    private void analyzeRegistry() {
        resultsBox.getChildren().clear();
        statusLabel.setText("Analyzing Windows Registry...");

        Thread t = new Thread(() -> {
            List<RegistryHive> hives = new ArrayList<>();

            String configDir = System.getenv("SystemRoot") + "\\System32\\config";
            String[] hiveNames = {"SYSTEM", "SOFTWARE", "SAM", "SECURITY", "DEFAULT"};
            for (String h : hiveNames) {
                File f = new File(configDir, h);
                if (f.exists()) {
                    hives.add(new RegistryHive(h, f.getAbsolutePath(), f.length(), "System Hive"));
                }
            }

            String userProfile = System.getenv("USERPROFILE");
            if (userProfile != null) {
                File ntuser = new File(userProfile, "NTUSER.DAT");
                if (ntuser.exists()) {
                    hives.add(new RegistryHive("NTUSER.DAT", ntuser.getAbsolutePath(),
                        ntuser.length(), "Current User"));
                }
                File usrclass = new File(userProfile, "AppData\\Local\\Microsoft\\Windows\\UsrClass.dat");
                if (usrclass.exists()) {
                    hives.add(new RegistryHive("UsrClass.dat", usrclass.getAbsolutePath(),
                        usrclass.length(), "User Classes"));
                }
            }

            File usersDir = new File("C:\\Users");
            if (usersDir.isDirectory()) {
                File[] userDirs = usersDir.listFiles(File::isDirectory);
                if (userDirs != null) {
                    for (File ud : userDirs) {
                        if (ud.getName().equals("Public") || ud.getName().equals("Default") ||
                            ud.getAbsolutePath().equals(userProfile)) continue;
                        File nt = new File(ud, "NTUSER.DAT");
                        if (nt.exists()) {
                            hives.add(new RegistryHive("NTUSER.DAT (" + ud.getName() + ")",
                                nt.getAbsolutePath(), nt.length(), "Other User"));
                        }
                    }
                }
            }

            long totalSize = hives.stream().mapToLong(h -> h.size).sum();

            Platform.runLater(() -> {
                Label summaryLabel = new Label(String.format(
                    "Registry: %d hive files, %s total", hives.size(), DiskInfo.formatBytes(totalSize)));
                summaryLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold; -fx-font-size: 14px;");
                resultsBox.getChildren().add(summaryLabel);

                Map<String, List<RegistryHive>> grouped = new LinkedHashMap<>();
                for (RegistryHive h : hives) {
                    grouped.computeIfAbsent(h.type, k -> new ArrayList<>()).add(h);
                }

                for (Map.Entry<String, List<RegistryHive>> entry : grouped.entrySet()) {
                    VBox section = new VBox(6);
                    section.getStyleClass().add("card");
                    section.setPadding(new Insets(14));

                    Label sectionTitle = new Label(entry.getKey());
                    sectionTitle.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 13px;");
                    section.getChildren().add(sectionTitle);

                    for (RegistryHive h : entry.getValue()) {
                        HBox row = new HBox(12);
                        row.setAlignment(Pos.CENTER_LEFT);

                        Label nameL = new Label(h.name);
                        nameL.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold;");
                        nameL.setPrefWidth(200);

                        Label sizeL = new Label(DiskInfo.formatBytes(h.size));
                        String sizeColor = h.size > 100 * 1024 * 1024 ? "#ef4444" :
                                          h.size > 50 * 1024 * 1024 ? "#eab308" : "#22c55e";
                        sizeL.setStyle("-fx-text-fill: " + sizeColor + "; -fx-font-weight: bold;");
                        sizeL.setPrefWidth(100);

                        ProgressBar bar = new ProgressBar(totalSize > 0 ? (double) h.size / totalSize : 0);
                        bar.setPrefWidth(200);
                        bar.setPrefHeight(14);
                        bar.getStyleClass().add("gauge-bar");

                        Label pathL = new Label(h.path);
                        pathL.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");
                        pathL.setWrapText(true);

                        VBox details = new VBox(2);
                        HBox firstRow = new HBox(8);
                        firstRow.setAlignment(Pos.CENTER_LEFT);
                        firstRow.getChildren().addAll(nameL, sizeL, bar);
                        details.getChildren().addAll(firstRow, pathL);
                        row.getChildren().add(details);
                        section.getChildren().add(row);
                    }

                    resultsBox.getChildren().add(section);
                }

                statusLabel.setText("Registry analysis complete");
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private static class RegistryHive {
        final String name, path, type;
        final long size;
        RegistryHive(String name, String path, long size, String type) {
            this.name = name; this.path = path; this.size = size; this.type = type;
        }
    }
}
