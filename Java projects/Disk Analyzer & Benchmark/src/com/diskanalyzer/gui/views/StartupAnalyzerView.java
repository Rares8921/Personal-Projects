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
 * Startup impact analyzer - shows programs that run at startup and their disk impact.
 */
public class StartupAnalyzerView {

    private final VBox container;
    private final Label statusLabel;
    private VBox resultsBox;

    public StartupAnalyzerView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        Label header = new Label("Startup Impact Analyzer");
        header.getStyleClass().add("view-header");

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(8, 0, 8, 0));

        Button scanBtn = new Button("Analyze Startup Items");
        scanBtn.getStyleClass().add("action-button");
        scanBtn.setOnAction(e -> analyzeStartup());

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

    private void analyzeStartup() {
        resultsBox.getChildren().clear();
        statusLabel.setText("Analyzing startup items...");

        Thread t = new Thread(() -> {
            List<StartupItem> items = new ArrayList<>();

            items.addAll(readRegistryRun("HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"));
            items.addAll(readRegistryRun("HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"));

            String[] startupPaths = {
                System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup",
                "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\Startup"
            };
            for (String path : startupPaths) {
                File dir = new File(path);
                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            items.add(new StartupItem(f.getName(), f.getAbsolutePath(),
                                "Startup Folder", f.length()));
                        }
                    }
                }
            }

            items.addAll(getScheduledTasks());

            Platform.runLater(() -> {
                if (items.isEmpty()) {
                    resultsBox.getChildren().add(new Label("No startup items detected"));
                } else {
                    Label summary = new Label(String.format("Found %d startup items", items.size()));
                    summary.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold;");
                    resultsBox.getChildren().add(summary);

                    Map<String, List<StartupItem>> grouped = new LinkedHashMap<>();
                    for (StartupItem item : items) {
                        grouped.computeIfAbsent(item.source, k -> new ArrayList<>()).add(item);
                    }

                    for (Map.Entry<String, List<StartupItem>> entry : grouped.entrySet()) {
                        VBox section = new VBox(4);
                        section.getStyleClass().add("card");
                        section.setPadding(new Insets(12));

                        Label sectionTitle = new Label(entry.getKey() + " (" + entry.getValue().size() + ")");
                        sectionTitle.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 13px;");
                        section.getChildren().add(sectionTitle);

                        for (StartupItem item : entry.getValue()) {
                            HBox row = new HBox(8);
                            row.setAlignment(Pos.CENTER_LEFT);
                            Label nameL = new Label(item.name);
                            nameL.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold;");
                            nameL.setPrefWidth(200);
                            Label pathL = new Label(item.path);
                            pathL.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
                            pathL.setWrapText(true);
                            HBox.setHgrow(pathL, Priority.ALWAYS);
                            if (item.size > 0) {
                                Label sizeL = new Label(DiskInfo.formatBytes(item.size));
                                sizeL.setStyle("-fx-text-fill: #eab308;");
                                row.getChildren().addAll(nameL, sizeL, pathL);
                            } else {
                                row.getChildren().addAll(nameL, pathL);
                            }
                            section.getChildren().add(row);
                        }
                        resultsBox.getChildren().add(section);
                    }
                }
                statusLabel.setText("Startup analysis complete");
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private List<StartupItem> readRegistryRun(String key) {
        List<StartupItem> items = new ArrayList<>();
        try {
            Process p = new ProcessBuilder("reg", "query", key)
                .redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.contains("REG_SZ") || line.contains("REG_EXPAND_SZ")) {
                    String[] parts = line.split("\\s+REG_(SZ|EXPAND_SZ)\\s+", 2);
                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        String value = parts[1].trim();
                        long size = 0;
                        String exePath = value.replaceAll("\"", "").split("\\s+")[0];
                        File exeFile = new File(exePath);
                        if (exeFile.exists()) size = exeFile.length();
                        items.add(new StartupItem(name, value, key.startsWith("HKLM") ? "Registry (Machine)" : "Registry (User)", size));
                    }
                }
            }
            p.waitFor();
        } catch (Exception ignored) {}
        return items;
    }

    private List<StartupItem> getScheduledTasks() {
        List<StartupItem> items = new ArrayList<>();
        try {
            Process p = new ProcessBuilder("schtasks", "/query", "/fo", "csv", "/nh")
                .redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            int count = 0;
            while ((line = br.readLine()) != null && count < 20) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0].replace("\"", "");
                    String status = parts.length > 3 ? parts[3].replace("\"", "") : "";
                    if (name.contains("\\") && !name.isEmpty()) {
                        items.add(new StartupItem(name.substring(name.lastIndexOf('\\') + 1),
                            name, "Scheduled Task (" + status + ")", 0));
                        count++;
                    }
                }
            }
            p.waitFor();
        } catch (Exception ignored) {}
        return items;
    }

    private static class StartupItem {
        final String name, path, source;
        final long size;
        StartupItem(String name, String path, String source, long size) {
            this.name = name; this.path = path; this.source = source; this.size = size;
        }
    }
}
