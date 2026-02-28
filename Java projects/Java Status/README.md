# Java Status

<div align="center">

![Java](https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)
![Utility](https://img.shields.io/badge/Type-System%20Monitor-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**A lightweight system information dashboard displaying Java runtime details with clickable hyperlinks for deeper exploration.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

Java Status is a minimal system information viewer that displays key details about your Java installation. It's designed for developers who need quick access to runtime configuration without opening terminals or control panels.

**Information displayed:**
- **Java Runtime Version** - exact JRE/JDK version (e.g., 1.8.0_281-b09)
- **Java Home** - installation directory path
- **Java Vendor** - provider name (Oracle, AdoptOpenJDK, Amazon Corretto, etc.)
- **Java Vendor URL** - official vendor website

**Interactive features:**
- **Clickable elements** - each value is a hyperlink
- **Runtime Version link** - opens java.com download page
- **Home path link** - opens Java installation folder in file explorer
- **Vendor link** - navigates to vendor's website (Oracle, etc.)
- **Vendor URL link** - opens the official vendor site

**What makes it handy:**
- Starts instantly - no loading time
- Dark theme for reduced eye strain (#111111 background)
- Compact 422x200 window fits anywhere on screen
- No configuration needed - auto-detects installed Java
- Perfect for quick version checks before compiling projects
- Useful when troubleshooting classpath or JDK issues

This tool is ideal for developers switching between multiple Java versions, DevOps engineers verifying environments, or anyone curious about their Java setup.

---

## Tech Stack

**Language:** Java 8  
**GUI Framework:** Swing (JFrame, JLabel)  
**Layout:** FlowLayout for auto-positioning  
**System API:** System.getProperty() for runtime queries  
**Integration:** Desktop API for file/browser launching  
**Build:** Standard Java compilation

### Architecture

Single-window information display with hyperlink handlers:

```
Main (extends JFrame)
      ↓
┌─────────────────────────────────┐
│   System Property Fetcher       │
│   - Query Java properties       │
│   - Format for display          │
└─────────────────────────────────┘
      ↓
Interactive Labels:
  ├─ Runtime Version (cyan text)
  │    └─ MouseListener → Desktop.browse(java.com)
  │
  ├─ Home Path (cyan text)
  │    └─ MouseListener → Desktop.open(File)
  │
  ├─ Vendor Name (cyan text)
  │    └─ MouseListener → Desktop.browse(oracle.com)
  │
  └─ Vendor URL (cyan text)
       └─ MouseListener → Desktop.browse(vendor_url)
```

**System Properties Queried:**
- `java.runtime.version` - full version string with build info
- `java.home` - absolute path to Java installation directory
- `java.vendor` - company/organization providing the JVM
- `java.vendor.url` - official website of the vendor

**Key Implementation Details:**
- **Property Fetching:** `System.getProperty(key)` retrieves runtime configuration
- **Dynamic Linking:** Each value has a MouseListener that triggers appropriate action on click
- **Color Coding:** Labels in white (#ffffff), values in cyan (#13ccd6) for visual distinction
- **Hand Cursor:** Hovering over clickable values shows hand cursor (like web browsers)
- **Error Handling:** Try-catch blocks prevent crashes if URLs/paths are invalid
- **Dark Theme:** Background set to near-black (#111111) for modern appearance

**Desktop API Usage:**
```java
// Open file location
Desktop.getDesktop().open(new File(javaHomePath));

// Open URL in browser
Desktop.getDesktop().browse(new URI("https://..."));
```

---

## Project Structure

```
Java Status/
├── Main.java                    # Application (GUI + system queries)
├── Java Status.jar              # Compiled executable
├── status.jpg                   # Screenshot of interface
└── README.md
```

---

## Getting Started

**Requirements:**
- Java 8 or higher
- Any OS with GUI support (Windows, macOS, Linux)
- Default file manager and web browser

**Run the application:**

```bash
# Option 1: Execute JAR
java -jar "Java Status.jar"

# Option 2: Compile and run
javac Main.java
java com.company.Main
```

**Usage:**

1. Launch the application - information appears instantly
2. **Click on any cyan-colored value** to interact:
   - **Runtime Version** → Opens java.com download page
   - **Home path** → Opens Java folder in file explorer
   - **Vendor** → Opens Wikipedia page about Oracle (or relevant vendor)
   - **Vendor URL** → Opens the official vendor website

**Example output:**
```
(!) Java Runtime Version → 1.8.0_281-b09
(!) Java Home → C:\Program Files\Java\jdk1.8.0_281
(!) Java Vendor → Oracle Corporation
(!) Java Vendor URL → https://www.oracle.com/
```

---

## What's Next

Future improvements being considered:
- Extended system info (OS, CPU, RAM, disk space)
- Real-time memory usage graphs (heap, non-heap)
- Refresh button to update values without restarting
- Export to text/JSON for documentation
- Compare multiple Java installations side-by-side
- Classpath viewer and JAVA_HOME verification
- JVM arguments display (current VM flags)
- List all installed JDKs on system

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
