# Info Java

<div align="center">

![Java](https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)
![Utility](https://img.shields.io/badge/Type-System%20Info-yellow?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**An educational terminal-style app that displays Java history and technical details through an interactive command interface.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

Info Java is a retro-styled command prompt application that teaches users about the Java programming language's history and origins. It provides an engaging, terminal-like experience for learning about Java's creation story.

**Core features:**
- **!info command** - displays comprehensive Java history and facts
- **!exit command** - quits the application
- **Interactive terminal** - command-line aesthetic with cyan text on black background
- **Hyperlink integration** - click to open Wikipedia for more details
- **Back navigation** - return to main page after viewing info

**What you'll learn:**
- Java's creation by James Gosling, Mike Sheridan, and Patrick Naughton (1991)
- Original purpose: interactive television (too advanced for the time)
- Name evolution: Oak → Green → Java (named after Java coffee)
- Design philosophy: C/C++ syntax familiarity for developers
- Historical context and early design decisions

**User experience:**
- Terminal-style interface mimics classic command prompts
- White copyright notice and cyan command text for authenticity
- Clickable Wikipedia link (opens in default browser)
- Simple two-command system keeps focus on content
- Back button returns to command input for repeated use

This educational tool is perfect for beginners learning Java who want to understand the language's history, or for anyone curious about programming language design and evolution.

---

## Tech Stack

**Language:** Java 8  
**GUI Framework:** Swing (JFrame, JLabel, JTextField)  
**Layout:** FlowLayout for vertical stacking  
**Integration:** Desktop API for browser launching  
**Build:** Standard Java compilation

### Architecture

Single-class application with event-driven command processing:

```
Main (extends JFrame)
      ↓
┌─────────────────────────────────┐
│   Command Processor             │
│   - KeyListener on TextField    │
│   - Command Parser              │
│   - UI State Manager            │
└─────────────────────────────────┘
      ↓
Commands:
  ├─ !info
  │    ├─ Hide command input
  │    ├─ Display HTML-formatted history
  │    ├─ Add Wikipedia hyperlink
  │    └─ Add "Back" navigation button
  │
  └─ !exit
       └─ System.exit(0)
```

**Command Flow:**
1. User types command in TextField
2. KeyListener detects Enter key press
3. getText() retrieves command string
4. equals() comparison determines action
5. UI updates dynamically (resize, content change)
6. MouseListener on hyperlinks opens browser

**Key Implementation Details:**
- **Terminal Aesthetic:** Black background (`Color.BLACK`), white/cyan foreground, monospace-style font
- **Dynamic Resizing:** Window expands from compact (650x100) to info view (650x370)
- **HTML Rendering:** JLabel with HTML tags for multi-line formatted text (line breaks, horizontal rules)
- **Hyperlink Handling:** MouseListener on JLabel + Desktop.browse() to open URLs
- **Back Navigation:** Reverts UI to original state (restores input field, shrinks window)
- **Copyright Notice:** Static header text providing context

**Desktop Integration:**
Uses `java.awt.Desktop` class to launch system browser:
```java
Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/..."));
```

---

## Project Structure

```
Info Java/
├── Main.java                    # Core application (GUI + logic)
├── Info Java.jar                # Executable JAR
├── scene1.jpg                   # Command prompt screen
├── scene2.jpg                   # Info display screen
└── README.md
```

---

## Getting Started

**Requirements:**
- Java 8 or higher
- Operating system with default web browser

**Running the application:**

```bash
# Option 1: Run JAR file
java -jar "Info Java.jar"

# Option 2: Compile and run from source
javac Main.java
java com.jetbrains.Main
```

**How to use:**

1. Launch the application - you'll see a terminal-style window
2. Type `!info` and press Enter to view Java's history
3. Click the blue Wikipedia link to learn more (opens in browser)
4. Click "Back to main page ↺" to return to command prompt
5. Type `!exit` and press Enter to quit

**Interface:**
- Copyright notice at top (white text)
- Command prompt with instructions (white text)
- Input field for commands (bottom)
- Info screen uses HTML formatting for readability

---

## What's Next

Future enhancements under consideration:
- More commands: `!version`, `!vm`, `!memory`, `!environment`
- Display current JVM information (version, vendor, memory stats)
- System properties viewer (`!props`)
- Command history with up/down arrow navigation
- Copy to clipboard for displayed information
- Export info to text file
- Color themes (green terminal, amber terminal, etc.)
- Command suggestions/autocomplete

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
