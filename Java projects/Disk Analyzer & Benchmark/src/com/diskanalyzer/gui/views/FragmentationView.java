package com.diskanalyzer.gui.views;

import com.diskanalyzer.core.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import java.util.*;

public class FragmentationView {

    private final VBox container;
    private final Label statusLabel;

    private ChoiceBox<String> driveChoice;
    private TextField pathField;
    private Spinner<Integer> maxFilesSpinner;
    private Button analyzeButton;
    private ProgressIndicator progressIndicator;

    private VBox summaryBox;
    private VBox detailsBox;
    private StackPane vizContainer;
    private ToggleGroup chartToggle;
    private FragmentationResult cachedResult;

    public FragmentationView(Label statusLabel) {
        this.statusLabel = statusLabel;
        container = new VBox(16);
        container.setPadding(new Insets(24));
        container.getStyleClass().add("view-container");

        Label header = new Label("Fragmentation Analysis");
        header.getStyleClass().add("view-header");

        HBox controls = buildControls();

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("edge-to-edge-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox content = new VBox(16);
        content.setPadding(new Insets(8));

        summaryBox = new VBox(8);
        summaryBox.setPadding(new Insets(4));

        HBox chartToggleRow = buildChartToggleRow();
        vizContainer = new StackPane();
        vizContainer.setMinHeight(320);

        detailsBox = new VBox(8);
        detailsBox.setPadding(new Insets(4));

        content.getChildren().addAll(summaryBox, chartToggleRow, vizContainer, detailsBox);
        scroll.setContent(content);

        container.getChildren().addAll(header, controls, scroll);
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

        pathField = new TextField();
        pathField.setPromptText("Or enter specific path...");
        pathField.setPrefWidth(300);
        pathField.getStyleClass().add("dark-field");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        Button loadDriveBtn = new Button("Use Drive");
        loadDriveBtn.getStyleClass().add("action-button-secondary");
        loadDriveBtn.setOnAction(e -> {
            String sel = driveChoice.getValue();
            if (sel != null) pathField.setText(sel);
        });

        Label maxLabel = new Label("Max Files:");
        maxLabel.getStyleClass().add("control-label");
        maxFilesSpinner = new Spinner<>(100, 100000, 5000, 1000);
        maxFilesSpinner.setPrefWidth(100);
        maxFilesSpinner.setEditable(true);
        maxFilesSpinner.getStyleClass().add("dark-spinner");

        analyzeButton = new Button("Analyze");
        analyzeButton.getStyleClass().add("run-button");
        analyzeButton.setOnAction(e -> runAnalysis());

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(24, 24);
        progressIndicator.setVisible(false);

        controls.getChildren().addAll(driveLabel, driveChoice, loadDriveBtn,
            new Separator(Orientation.VERTICAL),
            pathField, maxLabel, maxFilesSpinner,
            new Separator(Orientation.VERTICAL),
            analyzeButton, progressIndicator);
        return controls;
    }

    private HBox buildChartToggleRow() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 4, 0));

        Label vizLabel = new Label("Visualization:");
        vizLabel.getStyleClass().add("control-label");

        chartToggle = new ToggleGroup();
        ToggleButton gridBtn = makeToggle("Block Grid", "grid", chartToggle);
        ToggleButton barBtn = makeToggle("Bar Chart", "bar", chartToggle);
        ToggleButton pieBtn = makeToggle("Pie Chart", "pie", chartToggle);

        pieBtn.setSelected(true);
        chartToggle.selectedToggleProperty().addListener((obs, old, nw) -> {
            if (nw == null) { pieBtn.setSelected(true); return; }
            rebuildViz();
        });

        row.getChildren().addAll(vizLabel, gridBtn, barBtn, pieBtn);
        return row;
    }

    private ToggleButton makeToggle(String text, String id, ToggleGroup grp) {
        ToggleButton tb = new ToggleButton(text);
        tb.setUserData(id);
        tb.setToggleGroup(grp);
        tb.getStyleClass().add("chart-toggle");
        return tb;
    }

    private void runAnalysis() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            String sel = driveChoice.getValue();
            if (sel != null) path = sel;
            else return;
        }

        final String finalPath = path;
        int maxFiles = maxFilesSpinner.getValue();

        analyzeButton.setDisable(true);
        progressIndicator.setVisible(true);
        statusLabel.setText("Analyzing fragmentation on " + finalPath + " (may take a while)...");

        summaryBox.getChildren().clear();
        detailsBox.getChildren().clear();
        vizContainer.getChildren().clear();

        Thread t = new Thread(() -> {
            try {
                FragmentationResult result = AnalysisEngine.analyzeFragmentation(finalPath, maxFiles);
                cachedResult = result;

                Platform.runLater(() -> {
                    buildSummary(result);
                    rebuildViz();
                    buildDetails(result);
                    analyzeButton.setDisable(false);
                    progressIndicator.setVisible(false);
                    statusLabel.setText(String.format("Fragmentation analysis complete: %s - %s (%.1f%%)",
                        finalPath, result.getHealthRating(), result.getFragmentationPct()));
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + ex.getMessage());
                    analyzeButton.setDisable(false);
                    progressIndicator.setVisible(false);
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void buildSummary(FragmentationResult r) {
        summaryBox.getChildren().clear();

        Label title = new Label("Fragmentation Summary");
        title.getStyleClass().add("section-header");

        HBox healthRow = new HBox(24);
        healthRow.setAlignment(Pos.CENTER_LEFT);
        healthRow.setPadding(new Insets(12));
        healthRow.getStyleClass().add("card");

        VBox healthBox = new VBox(4);
        Label healthTitle = new Label("Health Rating");
        healthTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        Label healthValue = new Label(r.getHealthRating());
        healthValue.setFont(Font.font("System", FontWeight.BOLD, 28));
        String healthColor = getHealthColor(r.getHealthRating());
        healthValue.setStyle("-fx-text-fill: " + healthColor + ";");
        healthBox.getChildren().addAll(healthTitle, healthValue);

        VBox pctBox = new VBox(4);
        Label pctTitle = new Label("Fragmentation");
        pctTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        Label pctValue = new Label(String.format("%.1f%%", r.getFragmentationPct()));
        pctValue.setFont(Font.font("Consolas", FontWeight.BOLD, 28));
        pctValue.setStyle("-fx-text-fill: " + healthColor + ";");
        pctBox.getChildren().addAll(pctTitle, pctValue);

        ProgressBar fragBar = new ProgressBar(r.getFragmentationPct() / 100.0);
        fragBar.setPrefWidth(200);
        fragBar.setPrefHeight(20);
        fragBar.getStyleClass().add(r.getFragmentationPct() > 50 ? "usage-bar-critical" :
                                    r.getFragmentationPct() > 30 ? "usage-bar-warning" : "usage-bar-normal");

        VBox statsBox = new VBox(2);
        statsBox.getChildren().addAll(
            statLine("Files Analyzed", String.format("%,d", r.getTotalFiles())),
            statLine("Fragmented Files", String.format("%,d", r.getFragmentedFiles())),
            statLine("Total Fragments", String.format("%,d", r.getTotalFragments())),
            statLine("Avg. Fragments/File", String.format("%.2f", r.getAvgFragmentsPerFile()))
        );

        healthRow.getChildren().addAll(healthBox, pctBox, fragBar, statsBox);
        summaryBox.getChildren().addAll(title, healthRow);
    }

    private HBox statLine(String label, String value) {
        HBox line = new HBox(8);
        Label l = new Label(label + ":");
        l.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        l.setMinWidth(140);
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 12px; -fx-font-weight: bold;");
        line.getChildren().addAll(l, v);
        return line;
    }

    private String getHealthColor(String rating) {
        switch (rating) {
            case "Excellent": return "#22c55e";
            case "Good":      return "#22c55e";
            case "Fair":      return "#eab308";
            case "Poor":      return "#ef4444";
            case "Critical":  return "#ef4444";
            default:          return "#94a3b8";
        }
    }

    private void rebuildViz() {
        vizContainer.getChildren().clear();
        if (cachedResult == null) {
            vizContainer.getChildren().add(new Label("No fragmentation data - run analysis first"));
            return;
        }

        String mode = chartToggle.getSelectedToggle() != null
                ? chartToggle.getSelectedToggle().getUserData().toString() : "grid";

        switch (mode) {
            case "bar": vizContainer.getChildren().add(buildFragBarChart()); break;
            case "pie": vizContainer.getChildren().add(buildFragPieChart()); break;
            default:    vizContainer.getChildren().add(buildBlockGrid()); break;
        }
    }

    private ScrollPane buildBlockGrid() {
        FlowPane grid = new FlowPane(2, 2);
        grid.setPadding(new Insets(12));
        grid.setStyle("-fx-background-color: #0c1525;");

        int total = cachedResult.getTotalFiles();
        int fragmented = cachedResult.getFragmentedFiles();
        int maxBlocks = Math.min(total, 500); /* Cap at 500 blocks for performance */
        double scale = total > 0 ? (double) maxBlocks / total : 1;
        int fragBlocks = (int) Math.round(fragmented * scale);
        int goodBlocks = maxBlocks - fragBlocks;

        for (int i = 0; i < goodBlocks; i++) {
            Rectangle r = new Rectangle(8, 8);
            r.setFill(Color.web("#22c55e"));
            r.setArcWidth(2);
            r.setArcHeight(2);
            grid.getChildren().add(r);
        }
        for (int i = 0; i < fragBlocks; i++) {
            Rectangle r = new Rectangle(8, 8);
            r.setFill(Color.web("#ef4444"));
            r.setArcWidth(2);
            r.setArcHeight(2);
            grid.getChildren().add(r);
        }

        VBox wrapper = new VBox(8);
        wrapper.setPadding(new Insets(4));

        HBox legend = new HBox(16);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(4));
        legend.getChildren().addAll(
            legendItem("#22c55e", String.format("Contiguous (%,d files)", total - fragmented)),
            legendItem("#ef4444", String.format("Fragmented (%,d files)", fragmented)),
            new Label(String.format("  [Showing %d of %,d files]", maxBlocks, total))
        );
        ((Label) legend.getChildren().get(2)).setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setPrefHeight(200);
        sp.getStyleClass().add("edge-to-edge-scroll");

        wrapper.getChildren().addAll(sp, legend);
        return new ScrollPane(wrapper);
    }

    private HBox legendItem(String color, String text) {
        HBox item = new HBox(4);
        item.setAlignment(Pos.CENTER_LEFT);
        Rectangle sq = new Rectangle(10, 10);
        sq.setFill(Color.web(color));
        sq.setArcWidth(2);
        sq.setArcHeight(2);
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 12px;");
        item.getChildren().addAll(sq, l);
        return item;
    }

    private BarChart<String, Number> buildFragBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Fragment Count");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Most Fragmented Files");
        chart.getStyleClass().add("dark-chart");
        chart.setPrefHeight(300);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Fragments");
        int limit = Math.min(cachedResult.getTopFragmented().size(), 15);
        for (int i = 0; i < limit; i++) {
            FragmentationResult.FragmentedFile f = cachedResult.getTopFragmented().get(i);
            String name = f.getName();
            if (name.length() > 20) name = name.substring(0, 17) + "...";
            series.getData().add(new XYChart.Data<>(name, f.getFragments()));
        }
        chart.getData().add(series);
        return chart;
    }

    private PieChart buildFragPieChart() {
        PieChart pie = new PieChart();
        pie.setTitle("Fragmentation Overview");
        pie.getStyleClass().add("dark-chart");
        pie.setPrefHeight(300);

        int contiguous = cachedResult.getTotalFiles() - cachedResult.getFragmentedFiles();
        pie.getData().add(new PieChart.Data(
            String.format("Contiguous (%,d)", contiguous), contiguous));
        pie.getData().add(new PieChart.Data(
            String.format("Fragmented (%,d)", cachedResult.getFragmentedFiles()),
            cachedResult.getFragmentedFiles()));

        return pie;
    }

    private void buildDetails(FragmentationResult r) {
        detailsBox.getChildren().clear();

        if (r.getTopFragmented().isEmpty()) {
            detailsBox.getChildren().add(new Label("No fragmented files found"));
            return;
        }

        Label title = new Label(String.format("Top Fragmented Files (%d files)", r.getTopFragmented().size()));
        title.getStyleClass().add("section-header");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(4);

        grid.add(headerLabel("#"), 0, 0);
        grid.add(headerLabel("File Name"), 1, 0);
        grid.add(headerLabel("Fragments"), 2, 0);
        grid.add(headerLabel("Size"), 3, 0);
        grid.add(headerLabel("Full Path"), 4, 0);

        int row = 1;
        for (FragmentationResult.FragmentedFile f : r.getTopFragmented()) {
            grid.add(cellLabel(String.valueOf(row)), 0, row);

            Label nameLabel = new Label(f.getName());
            nameLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 12px;");
            nameLabel.setMaxWidth(250);
            nameLabel.setEllipsisString("...");
            grid.add(nameLabel, 1, row);

            Label fragLabel = new Label(String.format("%,d", f.getFragments()));
            fragLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 12px;");
            grid.add(fragLabel, 2, row);

            grid.add(cellLabel(f.getFormattedSize()), 3, row);

            Label pathLabel = new Label(f.getPath());
            pathLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
            pathLabel.setMaxWidth(400);
            pathLabel.setEllipsisString("...");
            Tooltip.install(pathLabel, new Tooltip(f.getPath()));
            grid.add(pathLabel, 4, row);

            row++;
        }

        detailsBox.getChildren().addAll(title, grid);
    }

    private Label headerLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("grid-header");
        l.setFont(Font.font("System", FontWeight.BOLD, 12));
        return l;
    }

    private Label cellLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 12px;");
        return l;
    }
}
