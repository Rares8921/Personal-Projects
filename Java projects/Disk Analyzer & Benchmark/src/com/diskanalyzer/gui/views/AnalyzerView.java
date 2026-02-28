package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.util.*;
import java.util.stream.*;
import java.io.File;

public class AnalyzerView {

    private final VBox container;
    private final Label statusLabel;
    private final TableView<FileRow> fileTable;
    private final TableView<FileRow> largestTable;
    private final VBox extStatsBox;
    private TextField pathField;
    private Spinner<Integer> maxEntriesSpinner;

    private ToggleGroup vizToggle;
    private StackPane vizContainer;
    private List<AnalysisEngine.ExtensionStat> cachedExtStats = Collections.emptyList();
    private SunburstChart sunburstChart;

    private TreemapChart treemapChart;

    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ObservableList<FileRow> allFileRows = FXCollections.observableArrayList();
    private FilteredList<FileRow> filteredFileRows;

    private AnalysisEngine.FullScanResult cachedFullScanResult;
    private String cachedScanPath = "";

    private Label summaryLabel;
    private Label dirSizeLabel;
    private Label fileCountLabel;
    private ProgressIndicator scanProgress;
    private Label storageInfoLabel;

    private ProgressBar scanProgressBar;
    private Label progressStageLabel;
    private Button cancelButton;
    private volatile boolean scanCancelled = false;
    private Thread scanThread = null;

    public AnalyzerView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(0);
        container.getStyleClass().add("view-container");

        HBox headerBar = buildModernHeader();
        
        VBox contentBox = new VBox(12);
        contentBox.setPadding(new Insets(24));

        HBox controls = buildControls();
        HBox summaryRow = buildSummaryRow();

        SplitPane splitPane = new SplitPane();
        splitPane.getStyleClass().add("dark-split");
        splitPane.setDividerPositions(0.55);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getStyleClass().add("dark-tab-pane");

        fileTable = buildFileTable(true);
        filteredFileRows = new FilteredList<>(allFileRows, p -> true);
        fileTable.setItems(filteredFileRows);

        VBox folderTab = new VBox(8);
        folderTab.setPadding(new Insets(12));

        HBox searchRow = new HBox(8);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Search files...");
        searchField.getStyleClass().add("dark-field");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        HBox.setHgrow(searchField, Priority.ALWAYS);

        categoryFilter = new ComboBox<>();
        categoryFilter.getStyleClass().add("dark-choice");
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(FileCategories.getAllCategories());
        categoryFilter.setValue("All Categories");
        categoryFilter.setOnAction(e -> applyFilters());

        searchRow.getChildren().addAll(new Label("🔍"), searchField, categoryFilter);
        
        HBox folderActions = new HBox(8);
        folderActions.setAlignment(Pos.CENTER_LEFT);
        Button selectAllBtn = new Button("Select All");
        selectAllBtn.getStyleClass().add("action-button-secondary");
        selectAllBtn.setOnAction(e -> selectAllFiles(true));
        Button deselectAllBtn = new Button("Deselect All");
        deselectAllBtn.getStyleClass().add("action-button-secondary");
        deselectAllBtn.setOnAction(e -> selectAllFiles(false));
        Button deleteBtn = new Button("X  Delete Selected");
        deleteBtn.getStyleClass().add("action-button");
        deleteBtn.setOnAction(e -> deleteSelectedFiles());
        folderActions.getChildren().addAll(selectAllBtn, deselectAllBtn, deleteBtn);
        
        folderTab.getChildren().addAll(searchRow, folderActions, fileTable);
        VBox.setVgrow(fileTable, Priority.ALWAYS);
        Tab dirTab = new Tab("Folders Outline", folderTab);

        largestTable = buildFileTable(false);
        VBox largestBox = new VBox(8);
        largestBox.setPadding(new Insets(12));
        largestBox.getChildren().add(largestTable);
        VBox.setVgrow(largestTable, Priority.ALWAYS);
        Tab largestTab = new Tab("Biggest Files", largestBox);

        extStatsBox = new VBox(8);
        extStatsBox.setPadding(new Insets(12));
        ScrollPane extScroll = new ScrollPane(extStatsBox);
        extScroll.setFitToWidth(true);
        extScroll.getStyleClass().add("edge-to-edge-scroll");
        Tab extTab = new Tab("Extension Stats", extScroll);

        tabs.getTabs().addAll(dirTab, largestTab, extTab);

        VBox vizBox = new VBox(8);
        vizBox.setPadding(new Insets(12));
        vizBox.setAlignment(Pos.CENTER);
        
        HBox vizHeader = new HBox(10);
        vizHeader.setAlignment(Pos.CENTER_LEFT);
        Label vizTitle = new Label("Storage Distribution");
        vizTitle.getStyleClass().add("section-header");
        vizTitle.setStyle("-fx-font-size: 16px;");

        ToggleGroup vizModeToggle = new ToggleGroup();
        ToggleButton sunburstBtn = new ToggleButton("Donut");
        sunburstBtn.setToggleGroup(vizModeToggle);
        sunburstBtn.setSelected(true);
        sunburstBtn.getStyleClass().add("chart-toggle");
        ToggleButton treemapBtn = new ToggleButton("Treemap");
        treemapBtn.setToggleGroup(vizModeToggle);
        treemapBtn.getStyleClass().add("chart-toggle");

        vizHeader.getChildren().addAll(vizTitle, sunburstBtn, treemapBtn);

        StackPane vizStack = new StackPane();
        sunburstChart = new SunburstChart(450, 450);
        treemapChart = new TreemapChart(450, 350);
        treemapChart.setVisible(false);
        vizStack.getChildren().addAll(sunburstChart, treemapChart);
        VBox.setVgrow(vizStack, Priority.ALWAYS);

        vizModeToggle.selectedToggleProperty().addListener((obs, old, nw) -> {
            boolean showTreemap = nw == treemapBtn;
            sunburstChart.setVisible(!showTreemap);
            treemapChart.setVisible(showTreemap);
        });
        
        vizBox.getChildren().addAll(vizHeader, vizStack);

        splitPane.getItems().addAll(tabs, vizBox);
        
        contentBox.getChildren().addAll(controls, summaryRow, splitPane);

        container.getChildren().addAll(headerBar, contentBox);
    }

    public Node getView() { return container; }
    
    private HBox buildModernHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("analyzer-header");
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);
        
        Label diskIcon = new Label("\u25C6");
        diskIcon.setStyle("-fx-font-size: 24px; -fx-text-fill: #3b82f6;");
        
        VBox infoBox = new VBox(2);
        Label diskName = new Label("Disk Analyzer");
        diskName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");
        
        storageInfoLabel = new Label("Select drive to view storage");
        storageInfoLabel.getStyleClass().add("storage-info-label");
        
        infoBox.getChildren().addAll(diskName, storageInfoLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshBtn = new Button("R");
        refreshBtn.getStyleClass().add("icon-button");
        refreshBtn.setTooltip(new Tooltip("Refresh"));
        refreshBtn.setOnAction(e -> runFullAnalysis());
        
        header.getChildren().addAll(diskIcon, infoBox, spacer, refreshBtn);
        return header;
    }
    
    private void selectAllFiles(boolean select) {
        for (FileRow row : fileTable.getItems()) {
            row.setSelected(select);
        }
        fileTable.refresh();
    }
    
    private void deleteSelectedFiles() {
        List<FileRow> selected = fileTable.getItems().stream()
            .filter(FileRow::isSelected)
            .collect(Collectors.toList());
        
        if (selected.isEmpty()) {
            statusLabel.setText("No files selected for deletion");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(String.format("Delete %d selected items?", selected.size()));
        alert.setContentText("This action cannot be undone!");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            statusLabel.setText(String.format("Would delete %d items (not implemented)", selected.size()));
        }
    }

    private HBox buildSummaryRow() {
        HBox row = new HBox(24);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 0, 4, 0));

        summaryLabel = new Label("No scan results yet");
        summaryLabel.setStyle("-fx-text-fill: #94a3b8;");

        dirSizeLabel = new Label("");
        dirSizeLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");

        fileCountLabel = new Label("");
        fileCountLabel.setStyle("-fx-text-fill: #eab308; -fx-font-weight: bold;");

        scanProgress = new ProgressIndicator();
        scanProgress.setPrefSize(20, 20);
        scanProgress.setVisible(false);

        scanProgressBar = new ProgressBar(0);
        scanProgressBar.setPrefWidth(200);
        scanProgressBar.setPrefHeight(16);
        scanProgressBar.setVisible(false);
        scanProgressBar.getStyleClass().add("scan-progress-bar");

        progressStageLabel = new Label("");
        progressStageLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 11px;");
        progressStageLabel.setVisible(false);

        cancelButton = new Button("✖ Cancel");
        cancelButton.getStyleClass().add("action-button-secondary");
        cancelButton.setVisible(false);
        cancelButton.setOnAction(e -> cancelScan());

        row.getChildren().addAll(scanProgress, summaryLabel, dirSizeLabel, fileCountLabel,
                                 scanProgressBar, progressStageLabel, cancelButton);
        return row;
    }

    private HBox buildControls() {
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(8, 0, 8, 0));

        pathField = new TextField();
        pathField.setPromptText("Enter path to analyze (e.g., C:\\Users)");
        pathField.setPrefWidth(350);
        pathField.getStyleClass().add("dark-field");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        pathField.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        pathField.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                File f = db.getFiles().get(0);
                pathField.setText(f.isDirectory() ? f.getAbsolutePath() : f.getParent());
                event.setDropCompleted(true);
            }
            event.consume();
        });

        maxEntriesSpinner = new Spinner<>(10, 10000, 500, 50);
        maxEntriesSpinner.setPrefWidth(100);
        maxEntriesSpinner.setEditable(true);
        maxEntriesSpinner.getStyleClass().add("dark-spinner");

        Label spinLabel = new Label("Max:");
        spinLabel.getStyleClass().add("control-label");

        Button scanBtn = new Button("Scan");
        scanBtn.getStyleClass().add("action-button");
        scanBtn.setOnAction(e -> runFullAnalysis());

        Button largestBtn = new Button("Top 50 Largest");
        largestBtn.getStyleClass().add("action-button");
        largestBtn.setOnAction(e -> runFindLargest());

        MenuButton exportMenu = new MenuButton("Export");
        exportMenu.getStyleClass().add("action-button-secondary");
        MenuItem csvItem = new MenuItem("Export CSV");
        csvItem.setOnAction(e -> exportCSV());
        MenuItem jsonItem = new MenuItem("Export JSON");
        jsonItem.setOnAction(e -> exportJSON());
        MenuItem fullReport = new MenuItem("Full Report (JSON)");
        fullReport.setOnAction(e -> exportFullReport());
        exportMenu.getItems().addAll(csvItem, jsonItem, fullReport);

        ChoiceBox<String> driveChoice = new ChoiceBox<>();
        driveChoice.getStyleClass().add("dark-choice");
        try {
            String[] drives = NativeBridge.getAvailableDrives();
            for (String d : drives) driveChoice.getItems().add(d + ":\\");
            if (!driveChoice.getItems().isEmpty()) {
                driveChoice.setValue(driveChoice.getItems().get(0));
            }
        } catch (Exception ignored) {}

        Button loadDrive = new Button("Load Drive");
        loadDrive.getStyleClass().add("action-button-secondary");
        loadDrive.setOnAction(e -> {
            String sel = driveChoice.getValue();
            if (sel != null) {
                pathField.setText(sel);
                runFullAnalysis();
            }
        });

        controls.getChildren().addAll(pathField, spinLabel, maxEntriesSpinner, scanBtn, largestBtn,
                                       exportMenu,
                                       new Separator(Orientation.VERTICAL), driveChoice, loadDrive);
        return controls;
    }

    private void cancelScan() {
        scanCancelled = true;
        if (scanThread != null) {
            scanThread.interrupt();
        }
        Platform.runLater(() -> setScanning(false, "Scan cancelled"));
    }

    /** Called by DiskAnalyzerApp.stop() to cancel any running scan on shutdown. */
    public void cancelScanIfRunning() {
        scanCancelled = true;
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
    }

    private void runFullAnalysis() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) return;
        int max = maxEntriesSpinner.getValue();

        scanCancelled = false;
        setScanning(true, "Scanning " + path + "...");
        fileTable.getItems().clear();
        largestTable.getItems().clear();
        extStatsBox.getChildren().clear();

        scanThread = new Thread(() -> {
            try {
                // Stage 1/2: Get drive info (fast)
                updateProgress(0.0, "Stage 1/2: Getting drive info...");
                long totalSpace = 0;
                long freeSpace = 0;
                try {
                    char driveLetter = path.charAt(0);
                    DiskInfo driveInfo = AnalysisEngine.getDriveInfo(driveLetter);
                    totalSpace = driveInfo.getTotalBytes();
                    freeSpace = driveInfo.getFreeBytes();
                } catch (Exception e) {}
                if (scanCancelled) return;

                final long finalTotal = totalSpace;
                final long finalFree = freeSpace;
                Platform.runLater(() -> {
                    if (finalTotal > 0) {
                        double totalGB = finalTotal / (1024.0 * 1024.0 * 1024.0);
                        storageInfoLabel.setText(String.format("%.1f GB available of %.1f GB",
                            finalFree / (1024.0 * 1024.0 * 1024.0), totalGB));
                    }
                });

                // Stage 2/2: Single-pass full scan (entries + largest + ext stats + totals)
                updateProgress(-1, "Stage 2/2: Scanning directory tree (this may take a while on large drives)...");
                AnalysisEngine.FullScanResult result = AnalysisEngine.fullScan(path, max, 50);
                if (scanCancelled) return;

                final List<FileEntry> entries = result.getEntries();
                final List<FileEntry> largest = result.getLargestFiles();
                final List<AnalysisEngine.ExtensionStat> extStats = result.getExtStats();
                final long dirSize = result.getTotalSize();
                final int fileCount = result.getFileCount();

                Platform.runLater(() -> {
                    allFileRows.clear();
                    for (FileEntry e : entries) {
                        allFileRows.add(new FileRow(e.getName(), e.getFormattedSize(), e.getSize(),
                                             e.isDirectory() ? "Directory" : e.getExtension(),
                                             e.getPath()));
                    }
                    summaryLabel.setText(String.format("Scanned: %,d entries", entries.size()));

                    ObservableList<FileRow> largestRows = FXCollections.observableArrayList();
                    for (FileEntry e : largest) {
                        largestRows.add(new FileRow(e.getName(), e.getFormattedSize(), e.getSize(),
                                                    e.getExtension(), e.getPath()));
                    }
                    largestTable.setItems(largestRows);

                    cachedExtStats = extStats;
                    populateExtStats(extStats);
                    updateSunburstChart(extStats);
                    updateTreemapChart(extStats);

                    cachedFullScanResult = result;
                    cachedScanPath = path;

                    ScanHistory.saveScanRecord(path, dirSize, fileCount);

                    dirSizeLabel.setText("Size: " + DiskInfo.formatBytes(dirSize));
                    fileCountLabel.setText(String.format("Files: %,d", fileCount));

                    updateProgress(1.0, "Complete");
                    setScanning(false, "Scan complete: " + entries.size() + " entries in " + path);
                });
            } catch (Exception ex) {
                if (!scanCancelled) {
                    Platform.runLater(() -> setScanning(false, "Error: " + ex.getMessage()));
                }
            }
        });
        scanThread.setDaemon(true);
        scanThread.start();
    }
    
    private void updateSunburstChart(List<AnalysisEngine.ExtensionStat> stats) {
        List<SunburstChart.SegmentData> segments = new ArrayList<>();
        
        Color[] colors = {
            Color.rgb(59, 130, 246),   // Blue
            Color.rgb(96, 165, 250),   // Light Blue
            Color.rgb(56, 189, 248),   // Cyan
            Color.rgb(129, 140, 248),  // Indigo
            Color.rgb(167, 139, 250),  // Purple
            Color.rgb(34, 197, 94),    // Green
            Color.rgb(234, 179, 8),    // Yellow
            Color.rgb(239, 68, 68),    // Red
            Color.rgb(251, 146, 60),   // Orange
            Color.rgb(148, 163, 184),  // Gray
        };
        
        long totalSize = stats.stream().mapToLong(AnalysisEngine.ExtensionStat::getTotalSize).sum();
        sunburstChart.setCenterLabel(DiskInfo.formatBytes(totalSize));
        
        int limit = Math.min(stats.size(), 10);
        long shownSize = 0;
        
        for (int i = 0; i < limit; i++) {
            AnalysisEngine.ExtensionStat stat = stats.get(i);
            Color color = colors[i % colors.length];
            segments.add(new SunburstChart.SegmentData(stat.getExt(), stat.getTotalSize(), color));
            shownSize += stat.getTotalSize();
        }
        
        if (totalSize - shownSize > 0) {
            segments.add(new SunburstChart.SegmentData("Other", totalSize - shownSize, Color.rgb(150, 150, 150)));
        }
        
        sunburstChart.setData(segments);
    }

    private void runFindLargest() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) return;

        scanCancelled = false;
        setScanning(true, "Finding largest files in " + path + "...");
        scanProgressBar.setProgress(-1);

        scanThread = new Thread(() -> {
            try {
                List<FileEntry> largest = AnalysisEngine.findLargestFiles(path, 50);
                if (scanCancelled) return;
                Platform.runLater(() -> {
                    ObservableList<FileRow> rows = FXCollections.observableArrayList();
                    for (FileEntry e : largest) {
                        rows.add(new FileRow(e.getName(), e.getFormattedSize(), e.getSize(),
                                             e.getExtension(), e.getPath()));
                    }
                    largestTable.setItems(rows);
                    setScanning(false, "Found " + largest.size() + " largest files");
                });
            } catch (Exception ex) {
                if (!scanCancelled) {
                    Platform.runLater(() -> setScanning(false, "Error: " + ex.getMessage()));
                }
            }
        });
        scanThread.setDaemon(true);
        scanThread.start();
    }
    
    private void updateProgress(double progress, String stage) {
        Platform.runLater(() -> {
            scanProgressBar.setProgress(progress);
            progressStageLabel.setText(stage);
        });
    }

    private void setScanning(boolean scanning, String msg) {
        scanProgress.setVisible(scanning);
        scanProgressBar.setVisible(scanning);
        progressStageLabel.setVisible(scanning);
        cancelButton.setVisible(scanning);
        if (!scanning) {
            scanProgressBar.setProgress(0);
            progressStageLabel.setText("");
            scanThread = null;
        }
        statusLabel.setText(msg);
    }
    
    private void populateExtStats(List<AnalysisEngine.ExtensionStat> stats) {
        extStatsBox.getChildren().clear();

        if (stats.isEmpty()) {
            extStatsBox.getChildren().add(new Label("No extension data available"));
            return;
        }

        Label title = new Label(String.format("File Types (%d types found)", stats.size()));
        title.getStyleClass().add("section-header");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(4);
        grid.getStyleClass().add("ext-grid");

        grid.add(styledLabel("#", "grid-header"), 0, 0);
        grid.add(styledLabel("Extension", "grid-header"), 1, 0);
        grid.add(styledLabel("Total Size", "grid-header"), 2, 0);
        grid.add(styledLabel("Files", "grid-header"), 3, 0);
        grid.add(styledLabel("Avg. Size", "grid-header"), 4, 0);

        long totalSize = stats.stream().mapToLong(AnalysisEngine.ExtensionStat::getTotalSize).sum();

        int row = 1;
        for (AnalysisEngine.ExtensionStat s : stats) {
            double pct = totalSize > 0 ? s.getTotalSize() * 100.0 / totalSize : 0;
            long avgSize = s.getFileCount() > 0 ? s.getTotalSize() / s.getFileCount() : 0;

            grid.add(styledLabel(String.valueOf(row), "grid-cell"), 0, row);
            grid.add(styledLabel(s.getExt(), "grid-cell-ext"), 1, row);
            grid.add(styledLabel(String.format("%s (%.1f%%)", DiskInfo.formatBytes(s.getTotalSize()), pct), "grid-cell"), 2, row);
            grid.add(styledLabel(String.format("%,d", s.getFileCount()), "grid-cell"), 3, row);
            grid.add(styledLabel(DiskInfo.formatBytes(avgSize), "grid-cell"), 4, row);
            row++;
        }

        int totalFiles = stats.stream().mapToInt(AnalysisEngine.ExtensionStat::getFileCount).sum();
        grid.add(styledLabel("", "grid-header"), 0, row);
        grid.add(styledLabel("TOTAL", "grid-header"), 1, row);
        grid.add(styledLabel(DiskInfo.formatBytes(totalSize), "grid-header"), 2, row);
        grid.add(styledLabel(String.format("%,d", totalFiles), "grid-header"), 3, row);
        grid.add(styledLabel("", "grid-header"), 4, row);

        extStatsBox.getChildren().addAll(title, grid);
    }

    @SuppressWarnings("unchecked")
    private TableView<FileRow> buildFileTable(boolean withCheckbox) {
        TableView<FileRow> table = new TableView<>();
        table.getStyleClass().add("dark-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No data - run a scan to populate"));

        if (withCheckbox) {
            TableColumn<FileRow, Boolean> selectCol = new TableColumn<>("");
            selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
            selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
            selectCol.setEditable(true);
            selectCol.setPrefWidth(40);
            selectCol.setMaxWidth(40);
            selectCol.setResizable(false);
            table.getColumns().add(selectCol);
            table.setEditable(true);
        }

        TableColumn<FileRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<FileRow, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("formattedSize"));
        sizeCol.setPrefWidth(100);
        sizeCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        sizeCol.setComparator((a, b) -> Long.compare(parseSizeForSort(a), parseSizeForSort(b)));
        
        sizeCol.setCellFactory(col -> new TableCell<FileRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    long size = getTableView().getItems().get(getIndex()).rawSize;
                    if (size > 1024L * 1024 * 1024) { // > 1GB
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else if (size > 100 * 1024 * 1024) { // > 100MB
                        setStyle("-fx-text-fill: #eab308; -fx-font-weight: bold;");
                    } else if (size > 10 * 1024 * 1024) { // > 10MB
                        setStyle("-fx-text-fill: #60a5fa;");
                    } else {
                        setStyle("-fx-text-fill: #94a3b8;");
                    }
                }
            }
        });

        TableColumn<FileRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(80);

        TableColumn<FileRow, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(new PropertyValueFactory<>("fullPath"));
        pathCol.setPrefWidth(300);

        table.getColumns().addAll(nameCol, sizeCol, typeCol, pathCol);
        return table;
    }

    private long parseSizeForSort(String formatted) {
        try {
            String[] parts = formatted.trim().split("\\s+");
            double val = Double.parseDouble(parts[0]);
            String unit = parts.length > 1 ? parts[1] : "B";
            switch (unit) {
                case "KB": val *= 1024; break;
                case "MB": val *= 1024 * 1024; break;
                case "GB": val *= 1024L * 1024 * 1024; break;
                case "TB": val *= 1024L * 1024 * 1024 * 1024; break;
            }
            return (long) val;
        } catch (Exception e) { return 0; }
    }

    private Label styledLabel(String text, String cls) {
        Label l = new Label(text);
        l.getStyleClass().add(cls);
        return l;
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase().trim();
        String category = categoryFilter.getValue();
        filteredFileRows.setPredicate(row -> {
            boolean matchesSearch = search.isEmpty() ||
                row.getName().toLowerCase().contains(search) ||
                row.getFullPath().toLowerCase().contains(search) ||
                row.getType().toLowerCase().contains(search);
            boolean matchesCategory = "All Categories".equals(category) ||
                FileCategories.categorize(row.getType()).equals(category);
            return matchesSearch && matchesCategory;
        });
    }

    private void updateTreemapChart(List<AnalysisEngine.ExtensionStat> stats) {
        Color[] colors = {
            Color.rgb(59, 130, 246), Color.rgb(96, 165, 250), Color.rgb(56, 189, 248),
            Color.rgb(129, 140, 248), Color.rgb(167, 139, 250), Color.rgb(34, 197, 94),
            Color.rgb(234, 179, 8), Color.rgb(239, 68, 68), Color.rgb(251, 146, 60),
            Color.rgb(148, 163, 184),
        };
        List<TreemapChart.TreemapItem> items = new ArrayList<>();
        int limit = Math.min(stats.size(), 20);
        for (int i = 0; i < limit; i++) {
            AnalysisEngine.ExtensionStat s = stats.get(i);
            items.add(new TreemapChart.TreemapItem(
                s.getExt(), DiskInfo.formatBytes(s.getTotalSize()),
                s.getTotalSize(), colors[i % colors.length]));
        }
        treemapChart.setData(items);
    }

    private void exportCSV() {
        if (allFileRows.isEmpty()) { statusLabel.setText("No data to export"); return; }
        FileChooser fc = new FileChooser();
        fc.setTitle("Export to CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("scan_results.csv");
        File file = fc.showSaveDialog(container.getScene().getWindow());
        if (file != null) {
            try {
                List<FileEntry> entries = allFileRows.stream()
                    .map(r -> new FileEntry(r.getFullPath(), r.rawSize, "Directory".equals(r.getType()), 0))
                    .collect(Collectors.toList());
                ExportUtil.exportFileEntriesToCsv(entries, file);
                statusLabel.setText("Exported CSV: " + file.getName());
            } catch (Exception e) {
                statusLabel.setText("Export failed: " + e.getMessage());
            }
        }
    }

    private void exportJSON() {
        if (allFileRows.isEmpty()) { statusLabel.setText("No data to export"); return; }
        FileChooser fc = new FileChooser();
        fc.setTitle("Export to JSON");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        fc.setInitialFileName("scan_results.json");
        File file = fc.showSaveDialog(container.getScene().getWindow());
        if (file != null) {
            try {
                List<FileEntry> entries = allFileRows.stream()
                    .map(r -> new FileEntry(r.getFullPath(), r.rawSize, "Directory".equals(r.getType()), 0))
                    .collect(Collectors.toList());
                ExportUtil.exportFileEntriesToJson(entries, file);
                statusLabel.setText("Exported JSON: " + file.getName());
            } catch (Exception e) {
                statusLabel.setText("Export failed: " + e.getMessage());
            }
        }
    }

    private void exportFullReport() {
        if (cachedFullScanResult == null) { statusLabel.setText("Run a scan first"); return; }
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Full Report");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        fc.setInitialFileName("full_report.json");
        File file = fc.showSaveDialog(container.getScene().getWindow());
        if (file != null) {
            try {
                ExportUtil.exportFullReport(cachedFullScanResult, cachedScanPath, file);
                statusLabel.setText("Full report exported: " + file.getName());
            } catch (Exception e) {
                statusLabel.setText("Export failed: " + e.getMessage());
            }
        }
    }

    public static class FileRow {
        private final SimpleStringProperty name;
        private final SimpleStringProperty formattedSize;
        private final long rawSize;
        private final SimpleStringProperty type;
        private final SimpleStringProperty fullPath;
        private final SimpleBooleanProperty selected;

        public FileRow(String name, String formattedSize, long rawSize, String type, String fullPath) {
            this.name          = new SimpleStringProperty(name);
            this.formattedSize = new SimpleStringProperty(formattedSize);
            this.rawSize       = rawSize;
            this.type          = new SimpleStringProperty(type);
            this.fullPath      = new SimpleStringProperty(fullPath);
            this.selected      = new SimpleBooleanProperty(false);
        }

        public String getName()          { return name.get(); }
        public String getFormattedSize() { return formattedSize.get(); }
        public String getType()          { return type.get(); }
        public String getFullPath()      { return fullPath.get(); }
        public boolean isSelected()      { return selected.get(); }
        public void setSelected(boolean value) { selected.set(value); }
        public SimpleBooleanProperty selectedProperty() { return selected; }
    }
}
