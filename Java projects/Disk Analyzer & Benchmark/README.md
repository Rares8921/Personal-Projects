<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=java&logoColor=white)
![JNI](https://img.shields.io/badge/JNI-Native-006600?style=for-the-badge)
![C++](https://img.shields.io/badge/C%2B%2B-00599C?style=for-the-badge&logo=c%2B%2B&logoColor=white)

</div>

---

**Professional disk performance benchmarking and storage analysis tool with native optimization and comprehensive visualization.**

---

**Navigation:** [What It Does](#what-it-does) · [Tech Stack](#tech-stack) · [Getting Started](#getting-started) · [Demo](#-demo-video) · [What's Next](#whats-next) · [License](#license)

---

## What It Does

**Disk Analyzer & Benchmark** is a high-performance storage analysis and benchmarking application that combines native C++ performance with a modern JavaFX interface. It performs comprehensive disk space analysis, file scanning, duplicate detection, fragmentation analysis, and industry-standard I/O performance benchmarking with detailed metrics including throughput (MB/s), IOPS, and latency measurements.

### Core Features
- **Dual-Mode Interface**: Command-line for automation and JavaFX GUI for interactive analysis
- **Native Performance**: JNI bridge to C++ DLL for maximum speed in disk operations
- **Comprehensive Benchmarking**: Sequential/random read/write tests with configurable block sizes
- **Advanced Analysis**: Duplicate file detection, fragmentation analysis, file age heatmaps
- **Intelligent Scanning**: Parallel directory scanning with exclusion rules and custom grouping
- **Export Capabilities**: PDF reports, CSV/JSON data export, scan history tracking
- **Visualization**: Treemap and sunburst charts for space distribution analysis
- **System Integration**: Windows Explorer context menu integration, startup analysis
- **Professional Reports**: Detailed benchmark comparisons against database of known results

---

## Tech Stack

The application is built with a hybrid architecture combining Java's portability with native code performance:

```
┌─────────────────────────────────────────────────────────────┐
│                     Entry Point                              │
│                  Main.java (CLI/GUI)                         │
│                    ├── --gui flag                            │
│                    └── default CLI                           │
└─────────────────────┬───────────────────────┬───────────────┘
                      │                       │
         ┌────────────▼────────┐   ┌──────────▼──────────────┐
         │   CLI Application   │   │   JavaFX GUI (DiskAnalyzerApp)
         │  CLIApplication.java│   │  ┌──────────────────────┐
         └─────────────────────┘   │  │  Dashboard View      │
                                   │  │  Analyzer View       │
                                   │  │  Benchmark View      │
                                   │  │  Duplicate Finder    │
                                   │  │  Fragmentation View  │
                                   │  │  Folder Compare      │
                                   │  │  Temp Cleaner        │
                                   │  │  Startup Analyzer    │
                                   │  │  File Age Heatmap    │
                                   │  └──────────────────────┘
                                   └─────────────┬─────────────┘
                                                 │
         ┌───────────────────────────────────────▼─────────────────────────┐
         │                      Core Analysis Engine                        │
         │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
         │  │AnalysisEngine│  │ParallelScanner│ │DuplicateFileFinder   │  │
         │  └───────┬──────┘  └──────┬───────┘  └──────────┬───────────┘  │
         │          │                │                      │              │
         │  ┌───────▼────────────────▼──────────────────────▼───────────┐  │
         │  │            Native Bridge (JNI Interface)                   │  │
         │  │                NativeBridge.java                           │  │
         │  └────────────────────────────┬───────────────────────────────┘  │
         └───────────────────────────────┼────────────────────────────────┘
                                         │
                    ┌────────────────────▼────────────────────┐
                    │      diskanalyzer.dll (C++ Native)      │
                    │  ┌────────────────────────────────────┐ │
                    │  │  • Win32 API disk enumeration      │ │
                    │  │  • High-performance file scanning  │ │
                    │  │  • Benchmark engine (SEQ/RAND I/O) │ │
                    │  │  • IOPS, throughput, latency calc  │ │
                    │  │  • JSON serialization              │ │
                    │  └────────────────────────────────────┘ │
                    └─────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   Supporting Components                      │
├─────────────────────────────────────────────────────────────┤
│  BenchmarkDatabase → Historical performance comparison       │
│  ExportUtil → PDF/CSV/JSON report generation                │
│  ScanHistory → Persistent scan tracking                      │
│  FileCategories → Extension-based classification             │
│  CustomGroupingRules → User-defined file organization        │
│  SystemCleanUtil → Temp file cleanup utilities               │
│  ContextMenuInstaller → Windows Explorer integration         │
└─────────────────────────────────────────────────────────────┘

Benchmark Result Format:
┌──────────────────────────────────────────┐
│ Type: SEQ_READ | SEQ_WRITE |             │
│       RAND_READ | RAND_WRITE             │
│ • Throughput (MB/s)                      │
│ • IOPS (operations/second)               │
│ • Latency: avg/min/max (microseconds)    │
│ • Block size, operation count            │
│ • Total bytes transferred                │
└──────────────────────────────────────────┘
```

---

## Getting Started

### Prerequisites
- **Java Development Kit (JDK)** 11 or higher
- **JavaFX SDK** (included in lib/ folder)
- **Windows OS** (native DLL is Windows-specific)
- **diskanalyzer.dll** must be in the same directory or on PATH

### Installation

1. **Clone or download** the project to your local machine

2. **Verify directory structure:**
```
Disk Analyzer & Benchmark/
├── diskanalyzer.dll          # Native library (required)
├── run-cli.bat               # CLI launcher
├── run-gui.bat               # GUI launcher
├── build.bat                 # Compilation script
├── lib/                      # JavaFX libraries
├── src/                      # Java source code
├── config/                   # Configuration files
├── benchmark_db/             # Historical benchmark data
└── scan_history/             # Past scan results
```

### Running the Application

**Option 1: GUI Mode (Recommended)**
```bash
run-gui.bat
# or
java -jar diskanalyzer.jar --gui
```

**Option 2: Command-Line Mode**
```bash
run-cli.bat
# or
java -jar diskanalyzer.jar
```

**Option 3: Build from Source**
```bash
build.bat
java -cp "build;lib/*" com.diskanalyzer.Main --gui
```

### Quick Usage Examples

**CLI Basic Commands:**
```bash
# Analyze specific drive
java -jar diskanalyzer.jar --analyze C:\

# Run full benchmark suite
java -jar diskanalyzer.jar --benchmark D:\ --block 64 --size 1024

# Find duplicate files
java -jar diskanalyzer.jar --duplicates C:\Users

# Export scan results to CSV
java -jar diskanalyzer.jar --scan E:\ --export results.csv
```

**GUI Workflow:**
1. Launch application with `run-gui.bat`
2. Select drive from **Dashboard View**
3. Choose analysis type: Space Analysis, Benchmark, Duplicates, etc.
4. Customize settings (block size, exclusions, depth limit)
5. Run scan/benchmark and view real-time progress
6. Export results as PDF report or CSV/JSON data

---

## 📹 Demo Video

Want to see **Disk Analyzer & Benchmark** in action? Record a demonstration of the disk analysis, benchmarking, and visualization features by following these steps:

### Recording Instructions
1. **Launch the GUI** using `run-gui.bat`
2. **Start your screen recording software** (OBS Studio recommended)
3. **Follow the demonstration timeline** below
4. **Save the recording** as `disk-analyzer-demo.mp4`

### What to Demonstrate (5-8 minutes)
Show these features in sequence:

**0:00 - 1:00** - **Dashboard Overview**
- Open application and display the dashboard with all available drives
- Show disk usage pie charts and drive information panels

**1:00 - 2:30** - **Disk Space Analysis**
- Navigate to Analyzer View
- Select a drive and start a full scan
- Display real-time scanning progress with file counts
- Show results in treemap visualization
- Switch to extension statistics view

**2:30 - 4:00** - **Performance Benchmarking**
- Switch to Benchmark View
- Configure benchmark parameters (block size: 64KB, size: 512MB)
- Run sequential read/write tests
- Display throughput (MB/s) and latency results
- Compare with database of historical results

**4:00 - 5:30** - **Duplicate File Detection**
- Open Duplicate Finder View
- Select scan directory and start analysis
- Show duplicate groups with file sizes and hashes
- Demonstrate batch deletion of duplicates

**5:30 - 6:30** - **Advanced Visualization**
- Navigate to File Age Heatmap View
- Display color-coded heat map by file modification dates
- Switch to Sunburst Chart for hierarchical space visualization

**6:30 - 7:30** - **System Utilities**
- Open Temp Cleaner View and scan for temporary files
- Navigate to Startup Analyzer View
- Show startup programs with disk usage impact
- Demonstrate fragmentation analysis

**7:30 - 8:00** - **Export & Reports**
- Generate PDF report from analysis results
- Export benchmark comparison to CSV
- Show scan history with previous results

### Features to Showcase
- ✅ Dual-mode interface (CLI and GUI)
- ✅ Real-time disk scanning with parallel processing
- ✅ Industry-standard I/O benchmarking (throughput, IOPS, latency)
- ✅ Duplicate file detection with hash comparison
- ✅ Interactive treemap and sunburst visualizations
- ✅ Fragmentation analysis and file age heatmaps
- ✅ PDF report generation with professional formatting
- ✅ Benchmark database comparison
- ✅ System cleaning utilities (temp files, startup programs)
- ✅ CSV/JSON data export capabilities

### Recording Setup (OBS Studio)
- **Resolution:** 1920x1080 (1080p)
- **Frame Rate:** 30 FPS
- **Encoder:** H.264
- **Bitrate:** 5000 Kbps (high quality)
- **Audio:** Optional (add voice-over explaining features)

### Quick Demo Commands
```bash
# Launch GUI with sample drive
run-gui.bat

# For CLI recording, use these commands:
java -jar diskanalyzer.jar --analyze C:\
java -jar diskanalyzer.jar --benchmark D:\ --block 64 --size 512
java -jar diskanalyzer.jar --duplicates C:\Users --export duplicates.csv
java -jar diskanalyzer.jar --fragmentation E:\
```

**Tip:** Start benchmarks with smaller file sizes (256-512 MB) for faster demonstration. Use test directories with diverse file types for better visualization impact.

---

## What's Next

Future enhancements planned for **Disk Analyzer & Benchmark**:
- **Cross-platform support** with native libraries for Linux and macOS
- **Cloud storage integration** for benchmarking network drives and remote storage
- **Machine learning** predictions for disk failure based on performance trends
- **Scheduled scanning** with automatic report generation and email alerts
- **Advanced filtering** with regex patterns for file selection
- **SMART monitoring** integration for hardware health metrics
- **Compression analysis** to identify compressible files for space savings
- **Multi-language support** for internationalization
- **Plugin architecture** for custom analysis modules
- **REST API** for integration with monitoring systems

---

## License

© 2025 Personal Projects Portfolio. All rights reserved.

This is proprietary software developed for portfolio demonstration purposes. Unauthorized copying, modification, distribution, or commercial use of this software, via any medium, is strictly prohibited without explicit written permission from the author.

---

<div align="center">

**Built with native performance optimization and modern Java architecture.**

</div>
