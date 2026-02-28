package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;
import java.util.*;
import java.util.Map;

public class DashboardView {

    private final ScrollPane scrollPane;
    private final VBox container;
    private Timeline refreshTimeline;

    private ProgressBar memBar;
    private Label memDetailLabel;
    private Label memLoadLabel;

    private StackPane chartContainer;
    private ToggleGroup chartToggle;
    private List<DiskInfo> cachedDrives = Collections.emptyList();

    private FlowPane driveCardsPane;

    public DashboardView() {
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("edge-to-edge-scroll");
    }

    public Node getView() { return scrollPane; }

    public void refresh() {
        container.getChildren().clear();

        Label header = new Label("\u25A0  System Overview");
        header.getStyleClass().add("view-header");

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label liveIcon = new Label("\u25CF");
        liveIcon.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 10px;");
        Label liveLabel = new Label("LIVE");
        liveLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 11px; -fx-font-weight: bold;");
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Button refreshBtn = new Button("Refresh Now");
        refreshBtn.getStyleClass().add("action-button-secondary");
        refreshBtn.setOnAction(e -> loadData());
        headerRow.getChildren().addAll(header, liveIcon, liveLabel, headerSpacer, refreshBtn);

        VBox memCard = buildMemoryCard();

        Label drivesHeader = new Label("Storage Devices");
        drivesHeader.getStyleClass().add("section-header");

        driveCardsPane = new FlowPane(16, 16);
        driveCardsPane.setPadding(new Insets(8, 0, 0, 0));

        HBox chartHeader = buildChartToggleHeader();
        chartContainer = new StackPane();
        chartContainer.setMinHeight(320);

        container.getChildren().addAll(headerRow, memCard, drivesHeader, driveCardsPane,
                                       chartHeader, chartContainer);

        Label healthHeader = new Label("Disk Health & System Info");
        healthHeader.getStyleClass().add("section-header");
        healthHeader.setPadding(new Insets(16, 0, 0, 0));

        FlowPane healthCardsPane = new FlowPane(16, 16);
        healthCardsPane.setPadding(new Insets(8, 0, 0, 0));

        container.getChildren().addAll(healthHeader, healthCardsPane);

        loadData();
        loadHealthData(healthCardsPane);
        startRealTimeRefresh();
    }

    private void loadData() {
        if (!NativeBridge.isLoaded()) {
            Platform.runLater(() -> {
                showNoNativeLibraryMessage();
            });
            return;
        }
        Thread t = new Thread(() -> {
            List<DiskInfo> drives = AnalysisEngine.getAllDrives();
            long[] mem = AnalysisEngine.getMemoryInfo();

            Platform.runLater(() -> {
                updateMemory(mem);
                cachedDrives = drives;
                rebuildDriveCards(drives);
                rebuildChart();
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void startRealTimeRefresh() {
        if (!NativeBridge.isLoaded()) return;
        if (refreshTimeline != null) refreshTimeline.stop();
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> refreshMemory()));
        refreshTimeline.setCycleCount(Animation.INDEFINITE);
        refreshTimeline.play();
    }

    public void stopRealTime() {
        if (refreshTimeline != null) refreshTimeline.stop();
    }

    private void refreshMemory() {
        if (!NativeBridge.isLoaded()) return;
        Thread t = new Thread(() -> {
            long[] mem = AnalysisEngine.getMemoryInfo();
            Platform.runLater(() -> updateMemory(mem));
        });
        t.setDaemon(true);
        t.start();
    }

    private VBox buildMemoryCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("System Memory");
        title.getStyleClass().add("card-title");
        memLoadLabel = new Label("");
        memLoadLabel.setStyle("-fx-text-fill: #eab308; -fx-font-weight: bold; -fx-font-size: 20px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        titleRow.getChildren().addAll(title, sp, memLoadLabel);

        memBar = new ProgressBar(0);
        memBar.setMaxWidth(Double.MAX_VALUE);
        memBar.setPrefHeight(22);
        memBar.getStyleClass().add("mem-bar");

        memDetailLabel = new Label("Loading...");
        memDetailLabel.getStyleClass().add("card-detail");

        card.getChildren().addAll(titleRow, memBar, memDetailLabel);
        return card;
    }

    private void updateMemory(long[] mem) {
        if (mem == null || mem.length < 5) return;
        double usedPct = mem[0] > 0 ? (double) mem[2] * 100.0 / mem[0] : 0;
        memBar.setProgress(usedPct / 100.0);
        memLoadLabel.setText(String.format("%.0f%%", usedPct));
        memDetailLabel.setText(String.format(
            "Used: %s  |  Total: %s  |  Available: %s  |  Page File: %s  |  Load: %d%%",
            DiskInfo.formatBytes(mem[2]), DiskInfo.formatBytes(mem[0]),
            DiskInfo.formatBytes(mem[1]), DiskInfo.formatBytes(mem[4]), mem[3]));

        if (usedPct > 90) {
            memBar.getStyleClass().removeAll("mem-bar", "mem-bar-warn", "mem-bar-crit");
            memBar.getStyleClass().add("mem-bar-crit");
        } else if (usedPct > 75) {
            memBar.getStyleClass().removeAll("mem-bar", "mem-bar-warn", "mem-bar-crit");
            memBar.getStyleClass().add("mem-bar-warn");
        } else {
            memBar.getStyleClass().removeAll("mem-bar-warn", "mem-bar-crit");
            if (!memBar.getStyleClass().contains("mem-bar")) memBar.getStyleClass().add("mem-bar");
        }
    }

    private void rebuildDriveCards(List<DiskInfo> drives) {
        driveCardsPane.getChildren().clear();
        for (DiskInfo d : drives) {
            driveCardsPane.getChildren().add(buildDriveCard(d));
        }
    }

    private VBox buildDriveCard(DiskInfo d) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));
        card.setPrefWidth(300);
        card.setMinWidth(280);

        Label nameLabel = new Label(String.format("%c:  %s",
            d.getDrive(), d.getVolumeName().isEmpty() ? "Local Disk" : d.getVolumeName()));
        nameLabel.getStyleClass().add("card-title");

        String ssdText = d.isSSD() == null ? "Unknown" : (d.isSSD() ? "SSD" : "HDD");
        Label typeLabel = new Label(String.format("%s  \u2022  %s  \u2022  %s",
            d.getFileSystem(), d.getDriveType(), ssdText));
        typeLabel.getStyleClass().add("card-subtitle");

        ProgressBar usageBar = new ProgressBar(d.getUsagePercent() / 100.0);
        usageBar.setMaxWidth(Double.MAX_VALUE);
        usageBar.setPrefHeight(18);
        usageBar.getStyleClass().add(d.getUsagePercent() > 90 ? "usage-bar-critical" :
                                     d.getUsagePercent() > 75 ? "usage-bar-warning" : "usage-bar-normal");

        Label spaceLabel = new Label(String.format("%s free of %s  (%.1f%% used)",
            DiskInfo.formatBytes(d.getFreeBytes()), DiskInfo.formatBytes(d.getTotalBytes()),
            d.getUsagePercent()));
        spaceLabel.getStyleClass().add("card-detail");

        HBox statsRow = new HBox(8);
        String serial = d.getSerialNumber().isEmpty() ? "N/A" :
            (d.getSerialNumber().length() > 10 ? d.getSerialNumber().substring(0, 10) + "..." : d.getSerialNumber());
        statsRow.getChildren().addAll(
            buildStatChip("Serial", serial),
            buildStatChip("Cluster", DiskInfo.formatBytes(d.getClusterSize())),
            buildStatChip("Sector", DiskInfo.formatBytes(d.getSectorSize()))
        );

        GridPane detailGrid = new GridPane();
        detailGrid.setHgap(16);
        detailGrid.setVgap(2);
        detailGrid.setPadding(new Insets(6, 0, 0, 0));
        addDetailRow(detailGrid, 0, "Total Capacity", DiskInfo.formatBytes(d.getTotalBytes()));
        addDetailRow(detailGrid, 1, "Used Space", DiskInfo.formatBytes(d.getUsedBytes()));
        addDetailRow(detailGrid, 2, "Free Space", DiskInfo.formatBytes(d.getFreeBytes()));

        card.getChildren().addAll(nameLabel, typeLabel, usageBar, spaceLabel, statsRow, detailGrid);
        return card;
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label l = new Label(label + ":");
        l.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 11px; -fx-font-weight: bold;");
        grid.add(l, 0, row);
        grid.add(v, 1, row);
    }

    private VBox buildStatChip(String label, String value) {
        VBox chip = new VBox(2);
        chip.getStyleClass().add("stat-chip");
        chip.setPadding(new Insets(4, 8, 4, 8));
        Label l = new Label(label);
        l.getStyleClass().add("chip-label");
        Label v = new Label(value);
        v.getStyleClass().add("chip-value");
        chip.getChildren().addAll(l, v);
        return chip;
    }

    private HBox buildChartToggleHeader() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(16, 0, 4, 0));

        Label title = new Label("Storage Distribution");
        title.getStyleClass().add("section-header");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        chartToggle = new ToggleGroup();

        ToggleButton barBtn = makeToggle("Bar Chart", "bar", chartToggle);
        ToggleButton pieBtn = makeToggle("Pie Chart", "pie", chartToggle);
        ToggleButton stackBtn = makeToggle("Stacked", "stacked", chartToggle);

        pieBtn.setSelected(true);
        chartToggle.selectedToggleProperty().addListener((obs, old, nw) -> {
            if (nw == null) { pieBtn.setSelected(true); return; }
            rebuildChart();
        });

        row.getChildren().addAll(title, spacer, barBtn, pieBtn, stackBtn);
        return row;
    }

    private ToggleButton makeToggle(String text, String id, ToggleGroup grp) {
        ToggleButton tb = new ToggleButton(text);
        tb.setUserData(id);
        tb.setToggleGroup(grp);
        tb.getStyleClass().add("chart-toggle");
        return tb;
    }

    private void rebuildChart() {
        chartContainer.getChildren().clear();
        if (cachedDrives.isEmpty()) return;

        String mode = chartToggle.getSelectedToggle() != null
                ? chartToggle.getSelectedToggle().getUserData().toString() : "bar";

        switch (mode) {
            case "pie":     chartContainer.getChildren().add(buildPieChart()); break;
            case "stacked": chartContainer.getChildren().add(buildStackedChart()); break;
            default:        chartContainer.getChildren().add(buildBarChart()); break;
        }
    }

    @SuppressWarnings("unchecked")
    private BarChart<String, Number> buildBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("GB");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Drive Capacity Overview");
        chart.setPrefHeight(300);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("dark-chart");
        chart.setAnimated(false);

        XYChart.Series<String, Number> usedSeries = new XYChart.Series<>();
        usedSeries.setName("Used");
        XYChart.Series<String, Number> freeSeries = new XYChart.Series<>();
        freeSeries.setName("Free");

        for (DiskInfo d : cachedDrives) {
            String label = d.getDrive() + ": " +
                (d.getVolumeName().isEmpty() ? "Local" : d.getVolumeName());
            usedSeries.getData().add(new XYChart.Data<>(label, d.getUsedBytes() / (1024.0 * 1024 * 1024)));
            freeSeries.getData().add(new XYChart.Data<>(label, d.getFreeBytes() / (1024.0 * 1024 * 1024)));
        }

        chart.getData().addAll(usedSeries, freeSeries);
        return chart;
    }

    private PieChart buildPieChart() {
        PieChart pie = new PieChart();
        pie.setTitle("Storage Allocation");
        pie.getStyleClass().add("dark-chart");
        pie.setPrefHeight(300);
        pie.setLabelsVisible(true);

        for (DiskInfo d : cachedDrives) {
            String name = d.getDrive() + ": " +
                (d.getVolumeName().isEmpty() ? "Local" : d.getVolumeName());
            pie.getData().add(new PieChart.Data(
                String.format("%s Used (%.1f GB)", name, d.getUsedBytes() / (1024.0*1024*1024)),
                d.getUsedBytes()));
            pie.getData().add(new PieChart.Data(
                String.format("%s Free (%.1f GB)", name, d.getFreeBytes() / (1024.0*1024*1024)),
                d.getFreeBytes()));
        }
        return pie;
    }

    @SuppressWarnings("unchecked")
    private StackedBarChart<String, Number> buildStackedChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("GB");

        StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);
        chart.setTitle("Storage Distribution (Stacked)");
        chart.setPrefHeight(300);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("dark-chart");
        chart.setAnimated(false);

        XYChart.Series<String, Number> usedSeries = new XYChart.Series<>();
        usedSeries.setName("Used");
        XYChart.Series<String, Number> freeSeries = new XYChart.Series<>();
        freeSeries.setName("Free");

        for (DiskInfo d : cachedDrives) {
            String label = d.getDrive() + ":";
            usedSeries.getData().add(new XYChart.Data<>(label, d.getUsedBytes() / (1024.0*1024*1024)));
            freeSeries.getData().add(new XYChart.Data<>(label, d.getFreeBytes() / (1024.0*1024*1024)));
        }

        chart.getData().addAll(usedSeries, freeSeries);
        return chart;
    }

    private void loadHealthData(FlowPane pane) {
        Thread t = new Thread(() -> {
            List<DiskSpaceMonitor.SmartDrive> smartDrives = DiskSpaceMonitor.getSmartData();
            long recycleBinSize = SystemCleanUtil.getRecycleBinSize();
            Map<String, String[]> cloudPaths = SystemCleanUtil.detectCloudStorage();
            Map<String, Integer> temps = DiskSpaceMonitor.getDiskTemperatures();
            List<ScanHistory.ScanRecord> history = ScanHistory.loadHistory();

            Platform.runLater(() -> {
                for (DiskSpaceMonitor.SmartDrive sd : smartDrives) {
                    VBox card = new VBox(6);
                    card.getStyleClass().add("card");
                    card.setPadding(new Insets(14));
                    card.setPrefWidth(280);

                    Label nameL = new Label(sd.model);
                    nameL.getStyleClass().add("card-title");
                    nameL.setWrapText(true);

                    String statusColor = sd.isHealthy() ? "#22c55e" : "#ef4444";
                    Label statusL = new Label("Status: " + sd.status);
                    statusL.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");

                    Label infoL = new Label(String.format("%s  |  %s  |  %s",
                        sd.getFormattedSize(), sd.mediaType, sd.interfaceType));
                    infoL.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

                    card.getChildren().addAll(nameL, statusL, infoL);
                    pane.getChildren().add(card);
                }

                VBox rbCard = new VBox(6);
                rbCard.getStyleClass().add("card");
                rbCard.setPadding(new Insets(14));
                rbCard.setPrefWidth(200);
                Label rbTitle = new Label("Recycle Bin");
                rbTitle.getStyleClass().add("card-title");
                Label rbSize = new Label(DiskInfo.formatBytes(recycleBinSize));
                rbSize.setStyle("-fx-text-fill: #eab308; -fx-font-weight: bold; -fx-font-size: 18px;");
                rbCard.getChildren().addAll(rbTitle, rbSize);
                pane.getChildren().add(rbCard);

                if (!cloudPaths.isEmpty()) {
                    VBox cloudCard = new VBox(6);
                    cloudCard.getStyleClass().add("card");
                    cloudCard.setPadding(new Insets(14));
                    cloudCard.setPrefWidth(280);
                    Label cloudTitle = new Label("Cloud Storage Detected");
                    cloudTitle.getStyleClass().add("card-title");
                    cloudCard.getChildren().add(cloudTitle);
                    for (Map.Entry<String, String[]> entry : cloudPaths.entrySet()) {
                        Label cl = new Label(entry.getKey() + ": " + String.join(", ", entry.getValue()));
                        cl.setStyle("-fx-text-fill: #60a5fa; -fx-font-size: 11px;");
                        cl.setWrapText(true);
                        cloudCard.getChildren().add(cl);
                    }
                    pane.getChildren().add(cloudCard);
                }

                if (!temps.isEmpty()) {
                    VBox tempCard = new VBox(6);
                    tempCard.getStyleClass().add("card");
                    tempCard.setPadding(new Insets(14));
                    tempCard.setPrefWidth(200);
                    Label tempTitle = new Label("Temperatures");
                    tempTitle.getStyleClass().add("card-title");
                    tempCard.getChildren().add(tempTitle);
                    for (Map.Entry<String, Integer> entry : temps.entrySet()) {
                        String color = entry.getValue() > 50 ? "#ef4444" : entry.getValue() > 40 ? "#eab308" : "#22c55e";
                        Label tl = new Label(entry.getKey() + ": " + entry.getValue() + " \u00B0C");
                        tl.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                        tempCard.getChildren().add(tl);
                    }
                    pane.getChildren().add(tempCard);
                }

                if (!history.isEmpty()) {
                    VBox histCard = new VBox(6);
                    histCard.getStyleClass().add("card");
                    histCard.setPadding(new Insets(14));
                    histCard.setPrefWidth(320);
                    Label histTitle = new Label("Recent Scans (" + history.size() + ")");
                    histTitle.getStyleClass().add("card-title");
                    histCard.getChildren().add(histTitle);
                    int shown = Math.min(history.size(), 5);
                    for (int i = history.size() - shown; i < history.size(); i++) {
                        ScanHistory.ScanRecord sr = history.get(i);
                        Label sl = new Label(String.format("%s - %s (%,d files)",
                            sr.getFormattedDate(),
                            DiskInfo.formatBytes(sr.getTotalSize()), sr.getFileCount()));
                        sl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
                        histCard.getChildren().add(sl);
                    }
                    pane.getChildren().add(histCard);
                }

                DiskSpaceMonitor.checkDrives((drive, freeSpace, totalSpace, usedPct) -> {
                    Platform.runLater(() -> {
                        VBox alertCard = new VBox(6);
                        alertCard.setStyle("-fx-background-color: #3b1818; -fx-background-radius: 8;");
                        alertCard.setPadding(new Insets(14));
                        alertCard.setPrefWidth(250);
                        Label alertTitle = new Label("\u26A0 Low Disk Space");
                        alertTitle.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                        Label alertDetail = new Label(String.format("%s  %.1f%% used\n%s free",
                            drive, usedPct, DiskInfo.formatBytes(freeSpace)));
                        alertDetail.setStyle("-fx-text-fill: #fca5a5;");
                        alertCard.getChildren().addAll(alertTitle, alertDetail);
                        pane.getChildren().add(0, alertCard);
                    });
                });
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void showNoNativeLibraryMessage() {
        driveCardsPane.getChildren().clear();
        chartContainer.getChildren().clear();
        
        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(40));
        messageBox.setMaxWidth(600);
        messageBox.setStyle("-fx-background-color: #162640; -fx-background-radius: 8px;");
        
        Label icon = new Label("\u26A0");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #eab308;");
        
        Label title = new Label("Native Library Not Available");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");
        
        Label message = new Label(
            "The native disk analysis library could not be loaded.\n\n" +
            "This Docker container runs on Linux but the native library is Windows-specific.\n" +
            "To use full functionality, please run the application directly on Windows."
        );
        message.setWrapText(true);
        message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8;");
        
        messageBox.getChildren().addAll(icon, title, message);
        
        StackPane centerPane = new StackPane(messageBox);
        centerPane.setMinHeight(300);
        driveCardsPane.getChildren().add(centerPane);
    }
}
