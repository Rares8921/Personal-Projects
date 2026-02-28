package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Temporary file cleaner view.
 */
public class TempCleanerView {

    private final VBox container;
    private final Label statusLabel;
    private VBox resultsBox;
    private Label summaryLabel;
    private ProgressIndicator progress;
    private Map<String, List<SystemCleanUtil.CleanItem>> categorized = Collections.emptyMap();
    private Map<String, CheckBox> categoryChecks = new LinkedHashMap<>();

    public TempCleanerView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        Label header = new Label("Temporary File Cleaner");
        header.getStyleClass().add("view-header");

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(8, 0, 8, 0));

        Button scanBtn = new Button("Scan for Temp Files");
        scanBtn.getStyleClass().add("action-button");
        scanBtn.setOnAction(e -> scanTempFiles());

        Button cleanBtn = new Button("Clean Selected");
        cleanBtn.getStyleClass().add("action-button");
        cleanBtn.setStyle("-fx-background-color: #ef4444;");
        cleanBtn.setOnAction(e -> cleanSelected());

        Button selectAllBtn = new Button("Select All");
        selectAllBtn.getStyleClass().add("action-button-secondary");
        selectAllBtn.setOnAction(e -> categoryChecks.values().forEach(cb -> cb.setSelected(true)));

        progress = new ProgressIndicator();
        progress.setPrefSize(20, 20);
        progress.setVisible(false);

        controls.getChildren().addAll(scanBtn, cleanBtn, selectAllBtn, progress);

        summaryLabel = new Label("Click 'Scan' to find temporary files");
        summaryLabel.setStyle("-fx-text-fill: #94a3b8;");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("edge-to-edge-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        resultsBox = new VBox(12);
        resultsBox.setPadding(new Insets(12));
        scroll.setContent(resultsBox);

        container.getChildren().addAll(header, controls, summaryLabel, scroll);
    }

    public Node getView() { return container; }

    private void scanTempFiles() {
        progress.setVisible(true);
        statusLabel.setText("Scanning for temporary files...");
        resultsBox.getChildren().clear();
        categoryChecks.clear();

        Thread t = new Thread(() -> {
            List<SystemCleanUtil.CleanItem> items = SystemCleanUtil.findTempFiles();
            long recycleBinSize = SystemCleanUtil.getRecycleBinSize();

            Map<String, List<SystemCleanUtil.CleanItem>> byCategory = items.stream()
                .collect(Collectors.groupingBy(i -> i.category, LinkedHashMap::new, Collectors.toList()));

            Platform.runLater(() -> {
                categorized = byCategory;
                long totalSize = items.stream().mapToLong(i -> i.size).sum();

                summaryLabel.setText(String.format("Found %,d temp files (%s) | Recycle Bin: %s",
                    items.size(), DiskInfo.formatBytes(totalSize), DiskInfo.formatBytes(recycleBinSize)));

                for (Map.Entry<String, List<SystemCleanUtil.CleanItem>> entry : byCategory.entrySet()) {
                    String cat = entry.getKey();
                    List<SystemCleanUtil.CleanItem> catItems = entry.getValue();
                    long catSize = catItems.stream().mapToLong(i -> i.size).sum();

                    VBox catBox = new VBox(4);
                    catBox.getStyleClass().add("card");
                    catBox.setPadding(new Insets(12));

                    CheckBox catCheck = new CheckBox(String.format("%s  (%d files, %s)",
                        cat, catItems.size(), DiskInfo.formatBytes(catSize)));
                    catCheck.setStyle("-fx-text-fill: #e2e8f0; -fx-font-weight: bold; -fx-font-size: 13px;");
                    categoryChecks.put(cat, catCheck);

                    VBox fileList = new VBox(2);
                    int shown = Math.min(catItems.size(), 10);
                    for (int i = 0; i < shown; i++) {
                        SystemCleanUtil.CleanItem ci = catItems.get(i);
                        Label fileLabel = new Label(String.format("  %s  (%s)",
                            ci.path.substring(Math.max(ci.path.lastIndexOf('\\'), ci.path.lastIndexOf('/')) + 1),
                            DiskInfo.formatBytes(ci.size)));
                        fileLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
                        fileList.getChildren().add(fileLabel);
                    }
                    if (catItems.size() > 10) {
                        Label more = new Label(String.format("  ... and %d more files", catItems.size() - 10));
                        more.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-font-size: 11px;");
                        fileList.getChildren().add(more);
                    }

                    catBox.getChildren().addAll(catCheck, fileList);
                    resultsBox.getChildren().add(catBox);
                }

                progress.setVisible(false);
                statusLabel.setText("Temp file scan complete");
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void cleanSelected() {
        List<String> toDelete = new ArrayList<>();
        for (Map.Entry<String, CheckBox> entry : categoryChecks.entrySet()) {
            if (entry.getValue().isSelected()) {
                List<SystemCleanUtil.CleanItem> items = categorized.get(entry.getKey());
                if (items != null) {
                    for (SystemCleanUtil.CleanItem ci : items) {
                        toDelete.add(ci.path);
                    }
                }
            }
        }

        if (toDelete.isEmpty()) {
            statusLabel.setText("No categories selected for cleaning");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cleanup");
        alert.setHeaderText(String.format("Delete %d files?", toDelete.size()));
        alert.setContentText("This cannot be undone!");

        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                int deleted = SystemCleanUtil.deleteFiles(toDelete);
                statusLabel.setText(String.format("Deleted %d of %d files", deleted, toDelete.size()));
                scanTempFiles(); // refresh
            }
        });
    }
}
