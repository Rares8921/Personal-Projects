package com.diskanalyzer.gui;

import com.diskanalyzer.core.*;
import com.diskanalyzer.gui.views.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;

public class DiskAnalyzerApp extends Application {

    private BorderPane root;
    private StackPane contentArea;
    private DashboardView dashboardView;
    private AnalyzerView analyzerView;
    private BenchmarkView benchmarkView;
    private FragmentationView fragmentationView;
    private DuplicateFinderView duplicateFinderView;
    private TempCleanerView tempCleanerView;
    private FolderCompareView folderCompareView;
    private FileAgeHeatmapView fileAgeHeatmapView;
    private StartupAnalyzerView startupAnalyzerView;
    private RegistryAnalyzerView registryAnalyzerView;
    private Label statusLabel;
    private Scene scene;
    private boolean darkTheme = true;
    
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) {
        // Register shutdown hook for Ctrl+C / terminal close
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdownAll();
        }, "ShutdownHook"));

        VBox mainContainer = new VBox();
        mainContainer.getStyleClass().add("main-container");
        
        HBox titleBar = buildCustomTitleBar(stage);
        mainContainer.getChildren().add(titleBar);
        
        root = new BorderPane();
        root.getStyleClass().add("root-pane");

        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);

        HBox statusBar = buildStatusBar();
        root.setBottom(statusBar);
        
        mainContainer.getChildren().add(root);
        VBox.setVgrow(root, Priority.ALWAYS);

        dashboardView = new DashboardView();
        analyzerView  = new AnalyzerView(statusLabel);
        benchmarkView = new BenchmarkView(statusLabel);
        fragmentationView = new FragmentationView(statusLabel);
        duplicateFinderView = new DuplicateFinderView(statusLabel);
        tempCleanerView = new TempCleanerView(statusLabel);
        folderCompareView = new FolderCompareView(statusLabel);
        fileAgeHeatmapView = new FileAgeHeatmapView(statusLabel);
        startupAnalyzerView = new StartupAnalyzerView(statusLabel);
        registryAnalyzerView = new RegistryAnalyzerView(statusLabel);

        showView(dashboardView.getView());

        scene = new Scene(mainContainer, 1280, 800);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(600);
        stage.show();

        dashboardView.refresh();
    }
    
    private HBox buildCustomTitleBar(Stage stage) {
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("custom-title-bar");
        titleBar.setPrefHeight(40);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(0, 8, 0, 16));
        
        // Make title bar draggable
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        titleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        
        Label appIcon = new Label("\u25C6");
        appIcon.getStyleClass().add("title-bar-icon");
        
        Label appTitle = new Label("Disk Analyzer & Benchmark");
        appTitle.getStyleClass().add("title-bar-title");
        
        Label sysLabel = new Label("[ SYS: READY ]");
        sysLabel.getStyleClass().add("title-bar-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button minimizeBtn = new Button("\u2500");
        minimizeBtn.getStyleClass().add("title-bar-button");
        minimizeBtn.setOnAction(e -> stage.setIconified(true));
        
        Button maximizeBtn = new Button("\u25A1");
        maximizeBtn.getStyleClass().add("title-bar-button");
        maximizeBtn.setOnAction(e -> {
            stage.setMaximized(!stage.isMaximized());
            maximizeBtn.setText(stage.isMaximized() ? "\u25A2" : "\u25A1");
        });
        
        Button closeBtn = new Button("\u2715");
        closeBtn.getStyleClass().add("title-bar-button-close");
        closeBtn.setOnAction(e -> stage.close());
        
        titleBar.getChildren().addAll(
            appIcon, appTitle, sysLabel, spacer,
            minimizeBtn, maximizeBtn, closeBtn
        );
        
        return titleBar;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.setSpacing(4);
        sidebar.setPadding(new Insets(16, 8, 16, 8));

        Label title = new Label("DiskAnalyzer");
        title.getStyleClass().add("sidebar-title");
        title.setPadding(new Insets(0, 0, 12, 8));

        Label analysisSec = new Label("ANALYSIS");
        analysisSec.getStyleClass().add("sidebar-section");
        analysisSec.setPadding(new Insets(0, 0, 4, 8));

        Button btnDashboard = createNavButton("\u25A0  Dashboard", () -> {
            showView(dashboardView.getView());
            dashboardView.refresh();
        });
        Button btnAnalyzer = createNavButton("\u25B8  Analyzer", () -> {
            showView(analyzerView.getView());
        });
        Button btnDuplicates = createNavButton("\u25B8  Duplicate Finder", () -> {
            showView(duplicateFinderView.getView());
        });
        Button btnCompare = createNavButton("\u25B8  Folder Compare", () -> {
            showView(folderCompareView.getView());
        });
        Button btnAgeMap = createNavButton("\u25B8  File Age Heatmap", () -> {
            showView(fileAgeHeatmapView.getView());
        });

        Label perfSec = new Label("PERFORMANCE");
        perfSec.getStyleClass().add("sidebar-section");
        perfSec.setPadding(new Insets(12, 0, 4, 8));

        Button btnBenchmark = createNavButton("\u25BA  Benchmark", () -> {
            showView(benchmarkView.getView());
        });
        Button btnFragment = createNavButton("\u25C6  Fragmentation", () -> {
            showView(fragmentationView.getView());
        });

        Label toolsSec = new Label("TOOLS");
        toolsSec.getStyleClass().add("sidebar-section");
        toolsSec.setPadding(new Insets(12, 0, 4, 8));

        Button btnTempCleaner = createNavButton("\u2702  Temp Cleaner", () -> {
            showView(tempCleanerView.getView());
        });

        Button btnStartup = createNavButton("\u2699  Startup Analyzer", () -> {
            showView(startupAnalyzerView.getView());
        });

        Button btnRegistry = createNavButton("\u2630  Registry Analyzer", () -> {
            showView(registryAnalyzerView.getView());
        });

        Button btnTheme = new Button(darkTheme ? "\u263E  Light Theme" : "\u2600  Dark Theme");
        btnTheme.getStyleClass().add("nav-button");
        btnTheme.setMaxWidth(Double.MAX_VALUE);
        btnTheme.setAlignment(Pos.CENTER_LEFT);
        btnTheme.setOnAction(e -> {
            darkTheme = !darkTheme;
            btnTheme.setText(darkTheme ? "\u263E  Light Theme" : "\u2600  Dark Theme");
            applyTheme();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label verLabel = new Label("v2.0 \u2022 Native Engine");
        verLabel.getStyleClass().add("sidebar-version");

        sidebar.getChildren().addAll(title,
            analysisSec, btnDashboard, btnAnalyzer, btnDuplicates, btnCompare, btnAgeMap,
            perfSec, btnBenchmark, btnFragment,
            toolsSec, btnTempCleaner, btnStartup, btnRegistry,
            btnTheme,
            spacer, verLabel);
        return sidebar;
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private HBox buildStatusBar() {
        HBox bar = new HBox();
        bar.getStyleClass().add("status-bar");
        bar.setPadding(new Insets(4, 12, 4, 12));
        bar.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label nativeLabel = new Label(NativeBridge.isLoaded() ? "\u2713 Native Engine Active" : "\u2717 Native Engine Failed");
        nativeLabel.getStyleClass().add(NativeBridge.isLoaded() ? "status-ok" : "status-err");

        bar.getChildren().addAll(statusLabel, spacer, nativeLabel);
        return bar;
    }

    private void showView(Node view) {
        contentArea.getChildren().setAll(view);
    }

    private void applyTheme() {
        scene.getStylesheets().clear();
        if (darkTheme) {
            scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
        }
    }

    @Override
    public void stop() {
        shutdownAll();
        // Force exit to kill any lingering native/JNI threads
        System.exit(0);
    }

    private void shutdownAll() {
        if (dashboardView != null) {
            dashboardView.stopRealTime();
        }

        if (analyzerView != null) {
            analyzerView.cancelScanIfRunning();
        }

        if (duplicateFinderView != null) {
            duplicateFinderView.cancelScanIfRunning();
        }

        DiskSpaceMonitor.stopMonitoring();
        DiskSpaceMonitor.stopScheduledScans();
    }

    public static void launch(String[] args) {
        Application.launch(DiskAnalyzerApp.class, args);
    }
}
