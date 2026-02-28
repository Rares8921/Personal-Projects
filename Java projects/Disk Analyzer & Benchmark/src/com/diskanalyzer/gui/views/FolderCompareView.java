package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.stream.*;

/**
 * Folder comparison view - compares two directories side by side.
 */
public class FolderCompareView {

    private final VBox container;
    private final Label statusLabel;
    private TextField leftPathField, rightPathField;
    private VBox resultsBox;
    private Label summaryLabel;

    public FolderCompareView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        Label header = new Label("Folder Comparison");
        header.getStyleClass().add("view-header");

        GridPane pathGrid = new GridPane();
        pathGrid.setHgap(10); pathGrid.setVgap(8);
        pathGrid.setAlignment(Pos.CENTER_LEFT);
        ColumnConstraints cc1 = new ColumnConstraints(80);
        ColumnConstraints cc2 = new ColumnConstraints(); cc2.setHgrow(Priority.ALWAYS);
        pathGrid.getColumnConstraints().addAll(cc1, cc2);

        Label l1 = new Label("Folder A:"); l1.setStyle("-fx-text-fill: #e2e8f0;");
        leftPathField = new TextField(); leftPathField.setPromptText("e.g., C:\\Users\\Docs");
        leftPathField.getStyleClass().add("dark-field");

        Label l2 = new Label("Folder B:"); l2.setStyle("-fx-text-fill: #e2e8f0;");
        rightPathField = new TextField(); rightPathField.setPromptText("e.g., D:\\Backup\\Docs");
        rightPathField.getStyleClass().add("dark-field");

        pathGrid.add(l1, 0, 0); pathGrid.add(leftPathField, 1, 0);
        pathGrid.add(l2, 0, 1); pathGrid.add(rightPathField, 1, 1);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(4, 0, 4, 0));

        Button compareBtn = new Button("Compare Folders");
        compareBtn.getStyleClass().add("action-button");
        compareBtn.setOnAction(e -> compareFolders());

        Button exportBtn = new Button("Export Results");
        exportBtn.getStyleClass().add("action-button-secondary");
        exportBtn.setOnAction(e -> exportResults());

        controls.getChildren().addAll(compareBtn, exportBtn);

        summaryLabel = new Label("Enter two folder paths and click Compare");
        summaryLabel.setStyle("-fx-text-fill: #94a3b8;");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("edge-to-edge-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        resultsBox = new VBox(8);
        resultsBox.setPadding(new Insets(8));
        scroll.setContent(resultsBox);

        container.getChildren().addAll(header, pathGrid, controls, summaryLabel, scroll);
    }

    public Node getView() { return container; }

    private void compareFolders() {
        String leftPath = leftPathField.getText().trim();
        String rightPath = rightPathField.getText().trim();
        if (leftPath.isEmpty() || rightPath.isEmpty()) return;

        resultsBox.getChildren().clear();
        statusLabel.setText("Comparing folders...");

        Thread t = new Thread(() -> {
            try {
                Path leftDir = Paths.get(leftPath);
                Path rightDir = Paths.get(rightPath);

                if (!Files.isDirectory(leftDir) || !Files.isDirectory(rightDir)) {
                    Platform.runLater(() -> statusLabel.setText("Both paths must be valid directories"));
                    return;
                }

                Map<String, FileInfo> leftFiles = scanDir(leftDir, leftDir);
                Map<String, FileInfo> rightFiles = scanDir(rightDir, rightDir);

                Set<String> allKeys = new TreeSet<>();
                allKeys.addAll(leftFiles.keySet());
                allKeys.addAll(rightFiles.keySet());

                List<CompareResult> results = new ArrayList<>();
                for (String key : allKeys) {
                    FileInfo left = leftFiles.get(key);
                    FileInfo right = rightFiles.get(key);

                    if (left != null && right == null) {
                        results.add(new CompareResult(key, "Only in A", left.size, 0, left.modified, 0));
                    } else if (left == null && right != null) {
                        results.add(new CompareResult(key, "Only in B", 0, right.size, 0, right.modified));
                    } else {
                        if (left.size != right.size) {
                            results.add(new CompareResult(key, "Size differs", left.size, right.size, left.modified, right.modified));
                        } else if (Math.abs(left.modified - right.modified) > 2000) {
                            results.add(new CompareResult(key, "Date differs", left.size, right.size, left.modified, right.modified));
                        }
                    }
                }

                int total = allKeys.size();
                long onlyA = results.stream().filter(r -> r.status.equals("Only in A")).count();
                long onlyB = results.stream().filter(r -> r.status.equals("Only in B")).count();
                long sizeDiff = results.stream().filter(r -> r.status.equals("Size differs")).count();
                long dateDiff = results.stream().filter(r -> r.status.equals("Date differs")).count();
                int identical = total - results.size();

                Platform.runLater(() -> {
                    summaryLabel.setText(String.format(
                        "Total: %,d files | Identical: %,d | Only in A: %d | Only in B: %d | Size differs: %d | Date differs: %d",
                        total, identical, onlyA, onlyB, sizeDiff, dateDiff));

                    Map<String, List<CompareResult>> grouped = results.stream()
                        .collect(Collectors.groupingBy(r -> r.status, LinkedHashMap::new, Collectors.toList()));

                    for (Map.Entry<String, List<CompareResult>> entry : grouped.entrySet()) {
                        VBox section = new VBox(4);
                        section.getStyleClass().add("card");
                        section.setPadding(new Insets(12));

                        String color;
                        switch (entry.getKey()) {
                            case "Only in A": color = "#f59e0b"; break;
                            case "Only in B": color = "#3b82f6"; break;
                            case "Size differs": color = "#ef4444"; break;
                            default: color = "#8b5cf6"; break;
                        }

                        Label sectionHeader = new Label(entry.getKey() + " (" + entry.getValue().size() + ")");
                        sectionHeader.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 13px;");
                        section.getChildren().add(sectionHeader);

                        int shown = Math.min(entry.getValue().size(), 50);
                        for (int i = 0; i < shown; i++) {
                            CompareResult cr = entry.getValue().get(i);
                            String detail;
                            if (cr.status.equals("Size differs")) {
                                detail = String.format("  %s  (A: %s, B: %s)",
                                    cr.relativePath, DiskInfo.formatBytes(cr.leftSize), DiskInfo.formatBytes(cr.rightSize));
                            } else {
                                long size = cr.leftSize > 0 ? cr.leftSize : cr.rightSize;
                                detail = String.format("  %s  (%s)", cr.relativePath, DiskInfo.formatBytes(size));
                            }
                            Label l = new Label(detail);
                            l.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
                            section.getChildren().add(l);
                        }
                        if (entry.getValue().size() > 50) {
                            Label more = new Label("  ... and " + (entry.getValue().size() - 50) + " more");
                            more.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-font-size: 11px;");
                            section.getChildren().add(more);
                        }

                        resultsBox.getChildren().add(section);
                    }

                    if (results.isEmpty()) {
                        Label identical2 = new Label("All files are identical!");
                        identical2.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 16px; -fx-font-weight: bold;");
                        resultsBox.getChildren().add(identical2);
                    }

                    statusLabel.setText("Comparison complete");
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private Map<String, FileInfo> scanDir(Path root, Path base) {
        Map<String, FileInfo> map = new TreeMap<>();
        try {
            Files.walk(root).filter(Files::isRegularFile).forEach(p -> {
                try {
                    String rel = base.relativize(p).toString().replace('/', '\\');
                    BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
                    map.put(rel.toLowerCase(), new FileInfo(attrs.size(), attrs.lastModifiedTime().toMillis()));
                } catch (Exception ignored) {}
            });
        } catch (Exception ignored) {}
        return map;
    }

    private void exportResults() {
        if (resultsBox.getChildren().isEmpty()) return;
        statusLabel.setText("Use the Compare button first to generate results");
    }

    private static class FileInfo {
        final long size, modified;
        FileInfo(long size, long modified) { this.size = size; this.modified = modified; }
    }

    private static class CompareResult {
        final String relativePath, status;
        final long leftSize, rightSize, leftMod, rightMod;
        CompareResult(String rel, String status, long ls, long rs, long lm, long rm) {
            this.relativePath = rel; this.status = status;
            this.leftSize = ls; this.rightSize = rs; this.leftMod = lm; this.rightMod = rm;
        }
    }
}
