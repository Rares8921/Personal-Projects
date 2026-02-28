package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.io.File;
import java.util.*;

public class BenchmarkView {

    private final VBox container;
    private final Label statusLabel;
    private final VBox resultsBox;
    private ChoiceBox<String> driveChoice;
    private Spinner<Integer> blockSizeSpinner;
    private Spinner<Integer> testSizeSpinner;
    private Spinner<Integer> randItersSpinner;
    private Button runButton;
    private ProgressIndicator progressIndicator;

    private Label seqReadLabel, seqWriteLabel, randReadLabel, randWriteLabel;
    private ProgressBar seqReadBar, seqWriteBar, randReadBar, randWriteBar;
    private Label seqReadIops, seqWriteIops, randReadIops, randWriteIops;
    private Label seqReadLat, seqWriteLat, randReadLat, randWriteLat;

    private ToggleGroup chartToggle;
    private StackPane chartContainer;
    private List<BenchmarkResult> cachedResults = Collections.emptyList();
    private final List<RunRecord> history = new ArrayList<>();

    public BenchmarkView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        Label header = new Label("Disk Benchmark");
        header.getStyleClass().add("view-header");

        HBox controls = buildControls();

        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.VERTICAL);
        split.getStyleClass().add("dark-split");
        VBox.setVgrow(split, Priority.ALWAYS);

        resultsBox = new VBox(8);
        resultsBox.setPadding(new Insets(12));
        ScrollPane resultsScroll = new ScrollPane(resultsBox);
        resultsScroll.setFitToWidth(true);
        resultsScroll.getStyleClass().add("edge-to-edge-scroll");

        VBox chartBox = buildChartArea();

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getStyleClass().add("dark-tab-pane");
        tabs.getTabs().addAll(
            new Tab("Charts", chartBox),
            new Tab("Detailed Results", resultsScroll)
        );

        split.getItems().addAll(buildGaugeRow(), tabs);
        split.setDividerPositions(0.32);

        container.getChildren().addAll(header, controls, split);
    }

    public Node getView() { return container; }

    private HBox buildControls() {
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(8, 0, 8, 0));

        driveChoice = new ChoiceBox<>();
        driveChoice.getStyleClass().add("dark-choice");
        try {
            String[] drives = NativeBridge.getAvailableDrives();
            for (String d : drives) driveChoice.getItems().add(d + ":\\");
            if (!driveChoice.getItems().isEmpty()) driveChoice.setValue(driveChoice.getItems().get(0));
        } catch (Exception ignored) {}

        Label driveLabel = new Label("Drive:");
        driveLabel.getStyleClass().add("control-label");

        blockSizeSpinner = labeledSpinner(4, 4096, 1024, 128);
        testSizeSpinner  = labeledSpinner(64, 2048, 256, 64);
        randItersSpinner = labeledSpinner(100, 50000, 1000, 500);

        Label bsLbl = new Label("Block (KB):");
        bsLbl.getStyleClass().add("control-label");
        Label tsLbl = new Label("Size (MB):");
        tsLbl.getStyleClass().add("control-label");
        Label riLbl = new Label("Rand Ops:");
        riLbl.getStyleClass().add("control-label");

        runButton = new Button("Run Benchmark");
        runButton.getStyleClass().add("run-button");
        runButton.setOnAction(e -> runBenchmark());

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(24, 24);
        progressIndicator.setVisible(false);

        MenuButton exportMenu = new MenuButton("Export");
        exportMenu.getStyleClass().add("action-button-secondary");
        javafx.scene.control.MenuItem csvItem = new javafx.scene.control.MenuItem("Export CSV");
        csvItem.setOnAction(e -> exportBenchmarkCSV());
        javafx.scene.control.MenuItem jsonItem = new javafx.scene.control.MenuItem("Export JSON");
        jsonItem.setOnAction(e -> exportBenchmarkJSON());
        exportMenu.getItems().addAll(csvItem, jsonItem);

        Button cloneEstBtn = new Button("Clone Estimate");
        cloneEstBtn.getStyleClass().add("action-button-secondary");
        cloneEstBtn.setOnAction(e -> showCloneEstimate());

        controls.getChildren().addAll(
            driveLabel, driveChoice,
            new Separator(Orientation.VERTICAL),
            bsLbl, blockSizeSpinner, tsLbl, testSizeSpinner, riLbl, randItersSpinner,
            new Separator(Orientation.VERTICAL),
            runButton, progressIndicator, exportMenu, cloneEstBtn
        );
        return controls;
    }

    private HBox buildGaugeRow() {
        HBox gaugeRow = new HBox(12);
        gaugeRow.setPadding(new Insets(12));
        gaugeRow.setAlignment(Pos.CENTER);

        seqReadLabel  = new Label("--"); seqReadIops  = new Label(""); seqReadLat  = new Label("");
        seqWriteLabel = new Label("--"); seqWriteIops = new Label(""); seqWriteLat = new Label("");
        randReadLabel = new Label("--"); randReadIops = new Label(""); randReadLat = new Label("");
        randWriteLabel= new Label("--"); randWriteIops= new Label(""); randWriteLat= new Label("");
        seqReadBar  = new ProgressBar(0);
        seqWriteBar = new ProgressBar(0);
        randReadBar = new ProgressBar(0);
        randWriteBar= new ProgressBar(0);

        gaugeRow.getChildren().addAll(
            buildGauge("Seq Read",  seqReadLabel,  seqReadBar,  seqReadIops,  seqReadLat,  "#22c55e"),
            buildGauge("Seq Write", seqWriteLabel, seqWriteBar, seqWriteIops, seqWriteLat, "#ef4444"),
            buildGauge("Rand Read", randReadLabel, randReadBar, randReadIops, randReadLat, "#eab308"),
            buildGauge("Rand Write",randWriteLabel,randWriteBar,randWriteIops,randWriteLat,"#818cf8")
        );
        return gaugeRow;
    }

    private VBox buildGauge(String title, Label valueLabel, ProgressBar bar,
                            Label iopsLabel, Label latLabel, String color) {
        VBox gauge = new VBox(4);
        gauge.setAlignment(Pos.CENTER);
        gauge.getStyleClass().add("gauge-card");
        gauge.setPadding(new Insets(14));
        gauge.setPrefWidth(200);
        HBox.setHgrow(gauge, Priority.ALWAYS);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("gauge-title");

        valueLabel.getStyleClass().add("gauge-value");
        valueLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        bar.setPrefWidth(Double.MAX_VALUE);
        bar.getStyleClass().add("gauge-bar");

        iopsLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        latLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px;");

        gauge.getChildren().addAll(titleLbl, valueLabel, bar, iopsLabel, latLabel);
        return gauge;
    }

    private VBox buildChartArea() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(8));

        HBox toggleRow = new HBox(8);
        toggleRow.setAlignment(Pos.CENTER_LEFT);
        toggleRow.setPadding(new Insets(0, 0, 4, 0));

        Label vizLabel = new Label("Chart Type:");
        vizLabel.getStyleClass().add("control-label");

        chartToggle = new ToggleGroup();
        ToggleButton barBtn = makeToggle("Throughput", "throughput", chartToggle);
        ToggleButton iopsBtn = makeToggle("IOPS", "iops", chartToggle);
        ToggleButton latBtn = makeToggle("Latency", "latency", chartToggle);
        ToggleButton lineBtn = makeToggle("History", "history", chartToggle);

        barBtn.setSelected(true);
        chartToggle.selectedToggleProperty().addListener((obs, old, nw) -> {
            if (nw == null) { barBtn.setSelected(true); return; }
            rebuildChart();
        });

        toggleRow.getChildren().addAll(vizLabel, barBtn, iopsBtn, latBtn, lineBtn);

        chartContainer = new StackPane();
        chartContainer.setMinHeight(280);
        VBox.setVgrow(chartContainer, Priority.ALWAYS);

        box.getChildren().addAll(toggleRow, chartContainer);
        return box;
    }

    private ToggleButton makeToggle(String text, String id, ToggleGroup grp) {
        ToggleButton tb = new ToggleButton(text);
        tb.setUserData(id);
        tb.setToggleGroup(grp);
        tb.getStyleClass().add("chart-toggle");
        return tb;
    }

    private void runBenchmark() {
        String drive = driveChoice.getValue();
        if (drive == null) return;

        int blockKB = blockSizeSpinner.getValue();
        int sizeMB  = testSizeSpinner.getValue();
        int iters   = randItersSpinner.getValue();

        // Create a temp directory on the selected drive for benchmark files
        // Writing directly to drive root (e.g. C:\) requires admin privileges
        String benchDir = drive + "diskanalyzer_bench";
        File benchDirFile = new File(benchDir);
        if (!benchDirFile.exists()) {
            if (!benchDirFile.mkdirs()) {
                // Fallback: try system temp dir
                benchDir = System.getProperty("java.io.tmpdir");
                if (benchDir == null || benchDir.isEmpty()) {
                    statusLabel.setText("Benchmark error: Cannot create temp directory on " + drive);
                    return;
                }
            }
        }
        final String targetDir = benchDir;

        runButton.setDisable(true);
        progressIndicator.setVisible(true);
        statusLabel.setText("Running benchmark on " + drive + "...");

        resetGauge(seqReadLabel, seqReadBar, seqReadIops, seqReadLat);
        resetGauge(seqWriteLabel, seqWriteBar, seqWriteIops, seqWriteLat);
        resetGauge(randReadLabel, randReadBar, randReadIops, randReadLat);
        resetGauge(randWriteLabel, randWriteBar, randWriteIops, randWriteLat);

        Thread t = new Thread(() -> {
            try {
                List<BenchmarkResult> results = new ArrayList<>();

                updateStatus("Sequential Read (" + blockKB + " KB blocks)...");
                results.add(AnalysisEngine.benchmarkSeqRead(targetDir, blockKB, sizeMB));
                updateGauge(seqReadLabel, seqReadBar, seqReadIops, seqReadLat, results.get(0));

                updateStatus("Sequential Write (" + blockKB + " KB blocks)...");
                results.add(AnalysisEngine.benchmarkSeqWrite(targetDir, blockKB, sizeMB));
                updateGauge(seqWriteLabel, seqWriteBar, seqWriteIops, seqWriteLat, results.get(1));

                updateStatus("Random Read (4K, " + iters + " ops)...");
                results.add(AnalysisEngine.benchmarkRandRead(targetDir, 4, iters));
                updateGauge(randReadLabel, randReadBar, randReadIops, randReadLat, results.get(2));

                updateStatus("Random Write (4K, " + iters + " ops)...");
                results.add(AnalysisEngine.benchmarkRandWrite(targetDir, 4, iters));
                updateGauge(randWriteLabel, randWriteBar, randWriteIops, randWriteLat, results.get(3));

                cachedResults = results;
                history.add(new RunRecord(drive, results));

                Platform.runLater(() -> {
                    rebuildChart();
                    populateResults(results, drive);
                    runButton.setDisable(false);
                    progressIndicator.setVisible(false);
                    statusLabel.setText("Benchmark complete: " + drive);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    statusLabel.setText("Benchmark error: " + ex.getMessage());
                    runButton.setDisable(false);
                    progressIndicator.setVisible(false);
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void resetGauge(Label val, ProgressBar bar, Label iops, Label lat) {
        Platform.runLater(() -> {
            val.setText("--");
            bar.setProgress(0);
            iops.setText("");
            lat.setText("");
        });
    }

    private void updateStatus(String msg) {
        Platform.runLater(() -> statusLabel.setText(msg));
    }

    private void updateGauge(Label label, ProgressBar bar, Label iopsLabel, Label latLabel, BenchmarkResult r) {
        Platform.runLater(() -> {
            label.setText(String.format("%.1f MB/s", r.getThroughputMBps()));
            double norm = Math.min(r.getThroughputMBps() / 5000.0, 1.0);
            bar.setProgress(norm);
            iopsLabel.setText(String.format("%.0f IOPS", r.getIops()));
            latLabel.setText(String.format("%.2f ms avg", r.getAvgLatencyMs()));
        });
    }

    @SuppressWarnings("unchecked")
    private void rebuildChart() {
        chartContainer.getChildren().clear();
        if (cachedResults.isEmpty() && history.isEmpty()) {
            chartContainer.getChildren().add(new Label("Run a benchmark to see charts"));
            return;
        }

        String mode = chartToggle.getSelectedToggle() != null
                ? chartToggle.getSelectedToggle().getUserData().toString() : "throughput";

        switch (mode) {
            case "iops":       chartContainer.getChildren().add(buildIopsChart()); break;
            case "latency":    chartContainer.getChildren().add(buildLatencyChart()); break;
            case "history":    chartContainer.getChildren().add(buildHistoryChart()); break;
            default:           chartContainer.getChildren().add(buildThroughputChart()); break;
        }
    }

    private BarChart<String, Number> buildThroughputChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("MB/s");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Throughput Comparison");
        chart.getStyleClass().add("dark-chart");
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Throughput");
        for (BenchmarkResult r : cachedResults) {
            series.getData().add(new XYChart.Data<>(
                r.getType().toString().replace("_", " "), r.getThroughputMBps()));
        }
        chart.getData().add(series);
        return chart;
    }

    private BarChart<String, Number> buildIopsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("IOPS");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("I/O Operations Per Second");
        chart.getStyleClass().add("dark-chart");
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("IOPS");
        for (BenchmarkResult r : cachedResults) {
            series.getData().add(new XYChart.Data<>(
                r.getType().toString().replace("_", " "), r.getIops()));
        }
        chart.getData().add(series);
        return chart;
    }

    private BarChart<String, Number> buildLatencyChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Latency (ms)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Average Latency");
        chart.getStyleClass().add("dark-chart");
        chart.setAnimated(false);
        chart.setLegendVisible(true);

        XYChart.Series<String, Number> avgSeries = new XYChart.Series<>();
        avgSeries.setName("Avg");
        XYChart.Series<String, Number> minSeries = new XYChart.Series<>();
        minSeries.setName("Min");
        XYChart.Series<String, Number> maxSeries = new XYChart.Series<>();
        maxSeries.setName("Max");

        for (BenchmarkResult r : cachedResults) {
            String label = r.getType().toString().replace("_", " ");
            avgSeries.getData().add(new XYChart.Data<>(label, r.getAvgLatencyMs()));
            minSeries.getData().add(new XYChart.Data<>(label, r.getMinLatencyUs() / 1000.0));
            maxSeries.getData().add(new XYChart.Data<>(label, r.getMaxLatencyUs() / 1000.0));
        }
        chart.getData().addAll(avgSeries, minSeries, maxSeries);
        return chart;
    }

    @SuppressWarnings("unchecked")
    private LineChart<String, Number> buildHistoryChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("MB/s");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Benchmark History - Throughput Over Runs");
        chart.getStyleClass().add("dark-chart");
        chart.setAnimated(false);

        String[] types = {"SEQ READ", "SEQ WRITE", "RAND READ", "RAND WRITE"};
        for (int t = 0; t < 4; t++) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(types[t]);
            for (int h = 0; h < history.size(); h++) {
                RunRecord rec = history.get(h);
                if (t < rec.results.size()) {
                    series.getData().add(new XYChart.Data<>(
                        "Run " + (h + 1) + " (" + rec.drive + ")", rec.results.get(t).getThroughputMBps()));
                }
            }
            chart.getData().add(series);
        }
        return chart;
    }

    private void populateResults(List<BenchmarkResult> results, String drive) {
        resultsBox.getChildren().clear();

        Label header = new Label("Benchmark Results - " + drive);
        header.getStyleClass().add("section-header");
        resultsBox.getChildren().add(header);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(6);
        grid.getStyleClass().add("results-grid");

        String[] headers = {"Test", "Throughput", "IOPS", "Avg Latency", "Min Latency", "Max Latency", "Block Size", "Duration"};
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i]);
            h.getStyleClass().add("grid-header");
            h.setFont(Font.font("System", FontWeight.BOLD, 12));
            grid.add(h, i, 0);
        }

        int row = 1;
        for (BenchmarkResult r : results) {
            grid.add(new Label(r.getType().toString()), 0, row);
            grid.add(styledValue(String.format("%.2f MB/s", r.getThroughputMBps()), "#22c55e"), 1, row);
            grid.add(styledValue(String.format("%.0f", r.getIops()), "#eab308"), 2, row);
            grid.add(new Label(String.format("%.2f ms", r.getAvgLatencyMs())), 3, row);
            grid.add(new Label(String.format("%.2f \u00B5s", r.getMinLatencyUs())), 4, row);
            grid.add(new Label(String.format("%.2f \u00B5s", r.getMaxLatencyUs())), 5, row);
            grid.add(new Label(DiskInfo.formatBytes(r.getBlockSize())), 6, row);
            grid.add(new Label(String.format("%.2f s", r.getElapsedSeconds())), 7, row);
            row++;
        }
        resultsBox.getChildren().add(grid);

        if (history.size() > 1) {
            Label histTitle = new Label(String.format("History (%d runs)", history.size()));
            histTitle.getStyleClass().add("section-header");
            histTitle.setPadding(new Insets(16, 0, 0, 0));
            resultsBox.getChildren().add(histTitle);

            for (int i = 0; i < history.size(); i++) {
                RunRecord rec = history.get(i);
                Label runLabel = new Label(String.format("Run %d [%s]:", i + 1, rec.drive));
                runLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                runLabel.setStyle("-fx-text-fill: #ef4444;");
                resultsBox.getChildren().add(runLabel);
                for (BenchmarkResult r : rec.results) {
                    Label l = new Label("  " + r.toTableRow());
                    l.setStyle("-fx-text-fill: #e2e8f0;");
                    resultsBox.getChildren().add(l);
                }
            }
        }
    }

    private Label styledValue(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        return l;
    }

    private Spinner<Integer> labeledSpinner(int min, int max, int init, int step) {
        Spinner<Integer> sp = new Spinner<>(min, max, init, step);
        sp.setPrefWidth(90);
        sp.setEditable(true);
        sp.getStyleClass().add("dark-spinner");
        return sp;
    }

    private void exportBenchmarkCSV() {
        if (cachedResults.isEmpty()) { statusLabel.setText("Run a benchmark first"); return; }
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Export Benchmark CSV");
        fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("benchmark.csv");
        java.io.File file = fc.showSaveDialog(container.getScene().getWindow());
        if (file != null) {
            try {
                ExportUtil.exportBenchmarkToCsv(cachedResults, file);
                statusLabel.setText("Exported: " + file.getName());
            } catch (Exception e) {
                statusLabel.setText("Export failed: " + e.getMessage());
            }
        }
    }

    private void exportBenchmarkJSON() {
        if (cachedResults.isEmpty()) { statusLabel.setText("Run a benchmark first"); return; }
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Export Benchmark JSON");
        fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("JSON", "*.json"));
        fc.setInitialFileName("benchmark.json");
        java.io.File file = fc.showSaveDialog(container.getScene().getWindow());
        if (file != null) {
            try {
                ExportUtil.exportBenchmarkToJson(cachedResults, file);
                statusLabel.setText("Exported: " + file.getName());
            } catch (Exception e) {
                statusLabel.setText("Export failed: " + e.getMessage());
            }
        }
    }

    private void showCloneEstimate() {
        if (cachedResults.isEmpty()) {
            statusLabel.setText("Run a benchmark first to estimate cloning speed");
            return;
        }
        String drive = driveChoice.getValue();
        if (drive == null) return;

        double seqReadMBps = 0, seqWriteMBps = 0;
        for (BenchmarkResult r : cachedResults) {
            if (r.getType() == BenchmarkResult.Type.SEQ_READ) seqReadMBps = r.getThroughputMBps();
            if (r.getType() == BenchmarkResult.Type.SEQ_WRITE) seqWriteMBps = r.getThroughputMBps();
        }

        long driveSize = 0;
        try {
            char driveLetter = drive.charAt(0);
            DiskInfo info = AnalysisEngine.getDriveInfo(driveLetter);
            driveSize = info.getTotalBytes();
        } catch (Exception ignored) {}

        if (driveSize == 0 || seqReadMBps == 0) {
            statusLabel.setText("Cannot estimate: missing data");
            return;
        }

        double effectiveSpeed = Math.min(seqReadMBps, seqWriteMBps > 0 ? seqWriteMBps : seqReadMBps);
        double driveSizeMB = driveSize / (1024.0 * 1024.0);
        double estimatedSeconds = driveSizeMB / effectiveSpeed;

        int hours = (int)(estimatedSeconds / 3600);
        int minutes = (int)((estimatedSeconds % 3600) / 60);
        int seconds = (int)(estimatedSeconds % 60);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Disk Clone Estimate");
        alert.setHeaderText("Disk Cloning Speed Estimate for " + drive);
        alert.setContentText(String.format(
            "Drive Size: %s\n" +
            "Effective Sequential Speed: %.1f MB/s\n" +
            "Estimated Clone Time: %d hours, %d minutes, %d seconds\n\n" +
            "(Based on sequential read/write benchmark results)",
            DiskInfo.formatBytes(driveSize), effectiveSpeed, hours, minutes, seconds));
        alert.showAndWait();
    }

    private static final class RunRecord {
        final String drive;
        final List<BenchmarkResult> results;
        RunRecord(String drive, List<BenchmarkResult> results) {
            this.drive = drive;
            this.results = results;
        }
    }
}
