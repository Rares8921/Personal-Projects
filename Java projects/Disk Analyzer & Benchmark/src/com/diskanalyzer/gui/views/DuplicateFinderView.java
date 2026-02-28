package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.util.*;
import java.util.stream.*;

/**
 * Duplicate file finder view.
 */
public class DuplicateFinderView {

    private final VBox container;
    private final Label statusLabel;
    private TextField pathField;
    private Spinner<Integer> minSizeSpinner;
    private Button scanBtn;
    private Button cancelBtn;
    private ProgressBar progressBar;
    private Label progressLabel;
    private TableView<DupGroupRow> resultsTable;
    private Label summaryLabel;
    private volatile boolean[] cancelFlag = {false};
    private Thread scanThread;

    public DuplicateFinderView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        Label header = new Label("Duplicate File Finder");
        header.getStyleClass().add("view-header");

        HBox controls = buildControls();
        HBox progressRow = buildProgressRow();

        summaryLabel = new Label("Scan a directory to find duplicate files");
        summaryLabel.setStyle("-fx-text-fill: #94a3b8;");

        resultsTable = buildTable();
        VBox.setVgrow(resultsTable, Priority.ALWAYS);

        HBox actionsRow = new HBox(8);
        actionsRow.setAlignment(Pos.CENTER_LEFT);
        actionsRow.setPadding(new Insets(8, 0, 0, 0));
        Button deleteBtn = new Button("Delete Selected Duplicates");
        deleteBtn.getStyleClass().add("action-button");
        deleteBtn.setOnAction(e -> deleteSelected());
        Button exportBtn = new Button("Export to CSV");
        exportBtn.getStyleClass().add("action-button-secondary");
        exportBtn.setOnAction(e -> exportResults());
        actionsRow.getChildren().addAll(deleteBtn, exportBtn);

        container.getChildren().addAll(header, controls, progressRow, summaryLabel, resultsTable, actionsRow);
    }

    public Node getView() { return container; }

    /** Called by DiskAnalyzerApp.stop() to cancel any running scan on shutdown. */
    public void cancelScanIfRunning() {
        cancelFlag[0] = true;
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
    }

    private HBox buildControls() {
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(8, 0, 8, 0));

        pathField = new TextField();
        pathField.setPromptText("Directory to scan for duplicates (e.g., C:\\Users)");
        pathField.setPrefWidth(400);
        pathField.getStyleClass().add("dark-field");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        Label minLabel = new Label("Min size (KB):");
        minLabel.getStyleClass().add("control-label");

        minSizeSpinner = new Spinner<>(0, 100000, 1, 10);
        minSizeSpinner.setPrefWidth(100);
        minSizeSpinner.setEditable(true);
        minSizeSpinner.getStyleClass().add("dark-spinner");

        scanBtn = new Button("Find Duplicates");
        scanBtn.getStyleClass().add("action-button");
        scanBtn.setOnAction(e -> startScan());

        cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("action-button-secondary");
        cancelBtn.setVisible(false);
        cancelBtn.setOnAction(e -> {
            cancelFlag[0] = true;
            cancelBtn.setDisable(true);
            progressLabel.setText("Cancelling...");
            statusLabel.setText("Cancelling scan...");
        });

        ChoiceBox<String> driveChoice = new ChoiceBox<>();
        driveChoice.getStyleClass().add("dark-choice");
        try {
            String[] drives = NativeBridge.getAvailableDrives();
            for (String d : drives) driveChoice.getItems().add(d + ":\\");
            if (!driveChoice.getItems().isEmpty()) driveChoice.setValue(driveChoice.getItems().get(0));
        } catch (Exception ignored) {}

        Button loadDrive = new Button("Use Drive");
        loadDrive.getStyleClass().add("action-button-secondary");
        loadDrive.setOnAction(e -> {
            if (driveChoice.getValue() != null) pathField.setText(driveChoice.getValue());
        });

        controls.getChildren().addAll(pathField, minLabel, minSizeSpinner, scanBtn, cancelBtn,
                new Separator(Orientation.VERTICAL), driveChoice, loadDrive);
        return controls;
    }

    private HBox buildProgressRow() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(16);
        progressBar.setVisible(false);
        progressBar.getStyleClass().add("scan-progress-bar");

        progressLabel = new Label("");
        progressLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 11px;");

        row.getChildren().addAll(progressBar, progressLabel);
        return row;
    }

    @SuppressWarnings("unchecked")
    private TableView<DupGroupRow> buildTable() {
        TableView<DupGroupRow> table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No duplicates found yet"));

        TableColumn<DupGroupRow, Boolean> selectCol = new TableColumn<>("");
        selectCol.setCellValueFactory(cd -> cd.getValue().selectedProperty());
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setEditable(true);
        selectCol.setPrefWidth(40);
        selectCol.setMaxWidth(40);
        table.setEditable(true);

        TableColumn<DupGroupRow, String> nameCol = new TableColumn<>("File Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<DupGroupRow, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("formattedSize"));
        sizeCol.setPrefWidth(100);

        TableColumn<DupGroupRow, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(new PropertyValueFactory<>("fullPath"));
        pathCol.setPrefWidth(350);

        TableColumn<DupGroupRow, String> hashCol = new TableColumn<>("Hash Group");
        hashCol.setCellValueFactory(new PropertyValueFactory<>("hashGroup"));
        hashCol.setPrefWidth(100);

        TableColumn<DupGroupRow, String> countCol = new TableColumn<>("Copies");
        countCol.setCellValueFactory(new PropertyValueFactory<>("copyCount"));
        countCol.setPrefWidth(60);

        table.getColumns().addAll(selectCol, nameCol, sizeCol, pathCol, hashCol, countCol);
        return table;
    }

    private void startScan() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) return;
        long minSize = minSizeSpinner.getValue() * 1024L;

        cancelFlag[0] = false;
        scanBtn.setDisable(true);
        cancelBtn.setVisible(true);
        progressBar.setVisible(true);
        progressBar.setProgress(-1);
        resultsTable.getItems().clear();
        statusLabel.setText("Scanning for duplicates in " + path + "...");

        scanThread = new Thread(() -> {
            List<DuplicateFileFinder.DuplicateGroup> groups = DuplicateFileFinder.findDuplicates(
                path, minSize,
                (processed, total, currentFile) -> {
                    Platform.runLater(() -> {
                        double pct = total > 0 ? (double) processed / total : 0;
                        progressBar.setProgress(pct);
                        progressLabel.setText(String.format("Hashing %d/%d: %s", processed, total, currentFile));
                    });
                },
                cancelFlag
            );

            Platform.runLater(() -> {
                if (cancelFlag[0]) {
                    summaryLabel.setText("Scan cancelled");
                    scanBtn.setDisable(false);
                    cancelBtn.setVisible(false);
                    cancelBtn.setDisable(false);
                    progressBar.setVisible(false);
                    progressBar.setProgress(0);
                    progressLabel.setText("");
                    statusLabel.setText("Duplicate scan cancelled");
                    return;
                }
                ObservableList<DupGroupRow> rows = FXCollections.observableArrayList();
                int groupNum = 0;
                long totalWasted = 0;
                for (DuplicateFileFinder.DuplicateGroup g : groups) {
                    groupNum++;
                    totalWasted += g.getWastedSpace();
                    for (String p : g.getPaths()) {
                        String name = p.substring(Math.max(p.lastIndexOf('\\'), p.lastIndexOf('/')) + 1);
                        rows.add(new DupGroupRow(name, DiskInfo.formatBytes(g.getSize()),
                                g.getSize(), p, "Group " + groupNum, String.valueOf(g.getCount())));
                    }
                }
                resultsTable.setItems(rows);
                summaryLabel.setText(String.format("Found %d duplicate groups (%d files) - %s wasted space",
                    groups.size(),
                    groups.stream().mapToInt(DuplicateFileFinder.DuplicateGroup::getCount).sum(),
                    DiskInfo.formatBytes(totalWasted)));
                scanBtn.setDisable(false);
                cancelBtn.setVisible(false);
                progressBar.setVisible(false);
                progressLabel.setText("");
                statusLabel.setText("Duplicate scan complete");
            });
        });
        scanThread.setDaemon(true);
        scanThread.start();
    }

    private void deleteSelected() {
        List<DupGroupRow> selected = resultsTable.getItems().stream()
            .filter(DupGroupRow::isSelected)
            .collect(Collectors.toList());
        if (selected.isEmpty()) {
            statusLabel.setText("No files selected");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Duplicates");
        alert.setHeaderText(String.format("Delete %d selected files?", selected.size()));
        alert.setContentText("This cannot be undone!");

        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                List<String> paths = selected.stream().map(DupGroupRow::getFullPath).collect(Collectors.toList());
                int deleted = SystemCleanUtil.deleteFiles(paths);
                resultsTable.getItems().removeAll(selected);
                statusLabel.setText(String.format("Deleted %d files", deleted));
            }
        });
    }

    private void exportResults() {
        if (resultsTable.getItems().isEmpty()) return;
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Export Duplicates");
        fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("duplicates.csv");
        java.io.File file = fc.showSaveDialog(container.getScene().getWindow());
        if (file != null) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
                pw.println("Name,Size,Path,Hash Group,Copies");
                for (DupGroupRow r : resultsTable.getItems()) {
                    pw.printf("\"%s\",%d,\"%s\",\"%s\",%s%n",
                        r.getName(), r.rawSize, r.getFullPath(), r.getHashGroup(), r.getCopyCount());
                }
                statusLabel.setText("Exported to " + file.getAbsolutePath());
            } catch (Exception e) {
                statusLabel.setText("Export failed: " + e.getMessage());
            }
        }
    }

    public static class DupGroupRow {
        private final SimpleStringProperty name, formattedSize, fullPath, hashGroup, copyCount;
        private final SimpleBooleanProperty selected;
        final long rawSize;

        public DupGroupRow(String name, String formattedSize, long rawSize,
                          String fullPath, String hashGroup, String copyCount) {
            this.name = new SimpleStringProperty(name);
            this.formattedSize = new SimpleStringProperty(formattedSize);
            this.rawSize = rawSize;
            this.fullPath = new SimpleStringProperty(fullPath);
            this.hashGroup = new SimpleStringProperty(hashGroup);
            this.copyCount = new SimpleStringProperty(copyCount);
            this.selected = new SimpleBooleanProperty(false);
        }

        public String getName() { return name.get(); }
        public String getFormattedSize() { return formattedSize.get(); }
        public String getFullPath() { return fullPath.get(); }
        public String getHashGroup() { return hashGroup.get(); }
        public String getCopyCount() { return copyCount.get(); }
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean v) { selected.set(v); }
        public SimpleBooleanProperty selectedProperty() { return selected; }
    }
}
