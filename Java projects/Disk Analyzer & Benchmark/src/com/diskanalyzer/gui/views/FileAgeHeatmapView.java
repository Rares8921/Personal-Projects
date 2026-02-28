package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;

/**
 * File age heatmap - shows file distribution by age and size.
 */
public class FileAgeHeatmapView {

    private final VBox container;
    private final Label statusLabel;
    private TextField pathField;
    private Canvas heatmapCanvas;
    private VBox legendBox;
    private Label summaryLabel;

    private static final String[] AGE_LABELS = {
        "< 1 week", "1-4 weeks", "1-3 months", "3-6 months",
        "6-12 months", "1-2 years", "2-5 years", "5+ years"
    };
    private long[] ageCounts;
    private long[] ageSizes;

    public FileAgeHeatmapView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        Label header = new Label("File Age Heatmap");
        header.getStyleClass().add("view-header");

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(8, 0, 8, 0));

        pathField = new TextField();
        pathField.setPromptText("Directory to analyze (e.g., C:\\Users)");
        pathField.setPrefWidth(400);
        pathField.getStyleClass().add("dark-field");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        ChoiceBox<String> driveChoice = new ChoiceBox<>();
        driveChoice.getStyleClass().add("dark-choice");
        try {
            String[] drives = NativeBridge.getAvailableDrives();
            for (String d : drives) driveChoice.getItems().add(d + ":\\");
            if (!driveChoice.getItems().isEmpty()) driveChoice.setValue(driveChoice.getItems().get(0));
        } catch (Exception ignored) {}

        Button loadDrive = new Button("Use Drive");
        loadDrive.getStyleClass().add("action-button-secondary");
        loadDrive.setOnAction(e -> { if (driveChoice.getValue() != null) pathField.setText(driveChoice.getValue()); });

        Button scanBtn = new Button("Analyze Ages");
        scanBtn.getStyleClass().add("action-button");
        scanBtn.setOnAction(e -> analyzeAges());

        controls.getChildren().addAll(pathField, driveChoice, loadDrive, scanBtn);

        summaryLabel = new Label("Select a directory and click Analyze");
        summaryLabel.setStyle("-fx-text-fill: #94a3b8;");

        heatmapCanvas = new Canvas(800, 400);
        legendBox = new VBox(4);
        legendBox.setPadding(new Insets(8, 0, 0, 0));

        HBox chartArea = new HBox(24);
        chartArea.setAlignment(Pos.TOP_LEFT);
        chartArea.getChildren().addAll(heatmapCanvas, legendBox);

        container.getChildren().addAll(header, controls, summaryLabel, chartArea);
    }

    public Node getView() { return container; }

    private void analyzeAges() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) return;

        statusLabel.setText("Analyzing file ages in " + path + "...");
        ageCounts = new long[AGE_LABELS.length];
        ageSizes = new long[AGE_LABELS.length];

        Thread t = new Thread(() -> {
            try {
                Instant now = Instant.now();
                long[] thresholds = {
                    now.minus(7, ChronoUnit.DAYS).toEpochMilli(),
                    now.minus(28, ChronoUnit.DAYS).toEpochMilli(),
                    now.minus(90, ChronoUnit.DAYS).toEpochMilli(),
                    now.minus(180, ChronoUnit.DAYS).toEpochMilli(),
                    now.minus(365, ChronoUnit.DAYS).toEpochMilli(),
                    now.minus(730, ChronoUnit.DAYS).toEpochMilli(),
                    now.minus(1825, ChronoUnit.DAYS).toEpochMilli(),
                    0
                };

                long totalFiles = 0;
                long totalSize = 0;

                Path root = Paths.get(path);
                // Use walkFileTree to gracefully skip access-denied directories
                Files.walkFileTree(root, new java.nio.file.SimpleFileVisitor<Path>() {
                    @Override
                    public java.nio.file.FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        try {
                            long modified = attrs.lastModifiedTime().toMillis();
                            long size = attrs.size();

                            int bucket = AGE_LABELS.length - 1;
                            for (int i = 0; i < thresholds.length; i++) {
                                if (modified >= thresholds[i]) {
                                    bucket = i;
                                    break;
                                }
                            }
                            ageCounts[bucket]++;
                            ageSizes[bucket] += size;
                        } catch (Exception ignored) {}
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }

                    @Override
                    public java.nio.file.FileVisitResult visitFileFailed(Path file, IOException exc) {
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }

                    @Override
                    public java.nio.file.FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        return java.nio.file.FileVisitResult.CONTINUE;
                    }
                });

                for (int i = 0; i < AGE_LABELS.length; i++) {
                    totalFiles += ageCounts[i];
                    totalSize += ageSizes[i];
                }

                final long ft = totalFiles, st = totalSize;

                Platform.runLater(() -> {
                    summaryLabel.setText(String.format(
                        "Analyzed %,d files (%s total)", ft, DiskInfo.formatBytes(st)));
                    renderHeatmap();
                    renderLegend();
                    statusLabel.setText("File age analysis complete");
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void renderHeatmap() {
        GraphicsContext gc = heatmapCanvas.getGraphicsContext2D();
        double w = heatmapCanvas.getWidth();
        double h = heatmapCanvas.getHeight();
        gc.clearRect(0, 0, w, h);

        Color[] colors = {
            Color.web("#22c55e"), Color.web("#84cc16"), Color.web("#eab308"),
            Color.web("#f97316"), Color.web("#ef4444"), Color.web("#dc2626"),
            Color.web("#991b1b"), Color.web("#7f1d1d")
        };

        long maxCount = 0;
        long maxSize = 0;
        for (int i = 0; i < AGE_LABELS.length; i++) {
            maxCount = Math.max(maxCount, ageCounts[i]);
            maxSize = Math.max(maxSize, ageSizes[i]);
        }

        double barWidth = (w - 100) / AGE_LABELS.length;
        double chartBottom = h - 40;
        double chartHeight = chartBottom - 40;

        gc.setFill(Color.web("#e2e8f0"));
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("File Age Distribution", w / 2, 20);

        // Draw bars - dual mode: size (filled) and count (outline)
        for (int i = 0; i < AGE_LABELS.length; i++) {
            double x = 60 + i * barWidth;

            double sizeH = maxSize > 0 ? (double) ageSizes[i] / maxSize * chartHeight : 0;
            gc.setFill(colors[i].deriveColor(0, 1, 1, 0.8));
            gc.fillRoundRect(x + 4, chartBottom - sizeH, barWidth - 12, sizeH, 4, 4);

            double countH = maxCount > 0 ? (double) ageCounts[i] / maxCount * chartHeight : 0;
            gc.setStroke(Color.WHITE.deriveColor(0, 1, 1, 0.6));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(x + 4, chartBottom - countH, barWidth - 12, countH, 4, 4);

            if (sizeH > 20) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
                gc.fillText(DiskInfo.formatBytes(ageSizes[i]), x + barWidth / 2 - 2, chartBottom - sizeH + 14);
            }

            gc.setFill(Color.web("#94a3b8"));
            gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 9));
            gc.fillText(AGE_LABELS[i], x + barWidth / 2 - 2, chartBottom + 14);
        }

        gc.save();
        gc.setFill(Color.web("#94a3b8"));
        gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.translate(14, h / 2);
        gc.rotate(-90);
        gc.fillText("Size / Count", 0, 0);
        gc.restore();
    }

    private void renderLegend() {
        legendBox.getChildren().clear();
        Color[] colors = {
            Color.web("#22c55e"), Color.web("#84cc16"), Color.web("#eab308"),
            Color.web("#f97316"), Color.web("#ef4444"), Color.web("#dc2626"),
            Color.web("#991b1b"), Color.web("#7f1d1d")
        };

        Label legendTitle = new Label("Legend");
        legendTitle.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold;");
        legendBox.getChildren().add(legendTitle);

        for (int i = 0; i < AGE_LABELS.length; i++) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER_LEFT);

            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(12, 12);
            rect.setFill(colors[i]);
            rect.setArcWidth(3); rect.setArcHeight(3);

            Label lbl = new Label(String.format("%s: %,d files (%s)",
                AGE_LABELS[i], ageCounts[i], DiskInfo.formatBytes(ageSizes[i])));
            lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

            row.getChildren().addAll(rect, lbl);
            legendBox.getChildren().add(row);
        }
    }
}
