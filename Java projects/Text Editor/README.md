# Text Editor

<div align="center">

![Java](https://img.shields.io/badge/Java-Swing-orange?style=for-the-badge&logo=java)
![Editor](https://img.shields.io/badge/Text-Editor-blue?style=for-the-badge&logo=notepad)
![Features](https://img.shields.io/badge/Rich-Features-green?style=for-the-badge&logo=text)

**A full-featured text editor built with Java Swing. Open, edit, and save files with undo/redo, find/replace, syntax highlighting, and line numbers. Everything you need for basic text editing.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a feature-rich text editor built with Java Swing, offering all the essentials for text manipulation and file management. It's not just a basic notepad—it includes undo/redo, find/replace with highlighting, customizable fonts, and theme support.

**Core features:**
- **File operations**: New, Open, Save, Save As with JFileChooser integration
- **Undo/Redo**: Full undo manager with unlimited history
- **Find/Replace**: Search for text with regex support and highlight matches
- **Line numbers**: Automatic line numbering in left gutter
- **Syntax highlighting**: Custom highlight painter for search results
- **Font customization**: Choose from all system fonts with size adjustment
- **Theme support**: Default and dark themes with color scheme persistence
- **Menu-driven UI**: Organized File, Edit, Settings, and Help menus
- **Keyboard shortcuts**: Ctrl+O (open), Ctrl+S (save), Ctrl+Z (undo), Ctrl+Y (redo), etc.

**Advanced features:**
- **Multiple highlight colors**: Configure highlight color for search results
- **Recent file tracking**: Remember last opened file path
- **Auto-scroll**: Follows cursor position in large files
- **Status indicators**: Visual feedback for save/load operations

**Why it's powerful:**
- Handles files of any size (within memory limits)
- Regex-powered find/replace for complex patterns
- Persistent settings (font, theme, last file path)
- Clean, intuitive menu system
- Professional-grade undo/redo implementation

The editor uses JTextArea for text input, UndoManager for edit history, and Highlighter API for search result visualization.

---

## Tech Stack

**Language:** Java SE (Swing for GUI)  
**UI Framework:** Swing (JTextArea, JMenuBar, JFileChooser, JScrollPane)  
**Text APIs:** `javax.swing.text.*`, `javax.swing.undo.UndoManager`  
**Highlighting:** `Highlighter`, `HighlightPainter`, `DefaultHighlighter`  
**File I/O:** `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`  
**Regex:** `Pattern`, `Matcher` for find/replace operations

### Architecture

Layered architecture with UI, file operations, and text manipulation logic:

```
UI Layer (Swing Components)
      ↓
Menu System (File, Edit, Settings, Help)
      ↓
┌─────────────┬─────────────┬──────────────┬───────────────┐
│   File I/O  │  Undo/Redo  │  Find/Replace│  Highlighting │
│  Manager    │  Manager    │  Engine      │  Engine       │
└─────────────┴─────────────┴──────────────┴───────────────┘
      ↓             ↓              ↓              ↓
BufferedReader  UndoManager   Regex Matcher  Highlighter API
FileWriter      CompoundEdit   (Pattern)      (HighlightPainter)
```

**How it works:**
- **File Operations**: JFileChooser opens dialog; BufferedReader/Writer handles I/O
- **Undo/Redo**: UndoManager listens to document changes; stores edit history
- **Find**: Regex Matcher finds all occurrences; Highlighter marks positions
- **Replace**: Iterates through matches and replaces text; updates highlights
- **Line Numbers**: TextLineNumber component attached to JScrollPane
- **Theme**: Updates background, foreground, and highlight colors programmatically

**Key Implementation Details:**
- **UndoManager Integration**: Attached to JTextArea's Document as DocumentListener
- **Highlight Removal**: `removeAllHighlights()` clears previous search results before new search
- **File Saving**: Detects if file exists; prompts for overwrite or new file
- **Font Selector**: Iterates through `GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()`
- **Theme Persistence**: Saves theme choice to config file; loads on startup

---

## Project Structure

```
Text Editor/
├── Main.java                   # Entry point + Swing UI + all logic
├── CustomJToolTip.java         # Custom tooltip styling (optional)
├── MyHighlightPainter.java     # Custom highlight painter for search
├── TextLineNumber.java         # Line number gutter component
└── Text Editor.exe             # Compiled executable
```

**Main components:**
- `Main.java`: UI setup, menu handlers, file I/O, find/replace, undo/redo
- TextLineNumber: Side panel showing line numbers
- MyHighlightPainter: Custom color for highlighted text
- Menu system: File (New, Open, Save, Exit), Edit (Undo, Redo, Find, Replace), Settings (Font, Theme), Help

---

## Getting Started

### Prerequisites

- **Java SE 8+** (recommended: Java 11 or higher)

### Running the Application

**Option 1: Executable**
```bash
# Windows:
.\Text Editor.exe

# Or run from source:
javac Main.java
java com.kickstart.Main
```

**Option 2: From Source**
```bash
# Compile:
javac -d bin src/com/kickstart/*.java

# Run:
java -cp bin com.kickstart.Main
```

### How to Use

**File operations:**
- **New**: File → New (or Ctrl+N)
- **Open**: File → Open (or Ctrl+O) → Select file
- **Save**: File → Save (or Ctrl+S)
- **Exit**: File → Exit (or Alt+F4)

**Editing:**
- **Undo**: Edit → Undo (or Ctrl+Z)
- **Redo**: Edit → Redo (or Ctrl+Y)
- **Find**: Edit → Find (or Ctrl+F) → Enter search term
- **Replace**: Edit → Replace (or Ctrl+H) → Enter find/replace terms

**Settings:**
- **Font**: Settings → Font → Choose from system fonts
- **Theme**: Settings → Theme → Default or Dark

**Keyboard shortcuts:**
- Ctrl+N: New file
- Ctrl+O: Open file
- Ctrl+S: Save file
- Ctrl+Z: Undo
- Ctrl+Y: Redo
- Ctrl+F: Find
- Ctrl+H: Replace

---

## What's Next

**Potential improvements:**
- Add syntax highlighting for code (Java, Python, etc.)
- Implement tabs for multiple documents
- Add word wrap toggle
- Recent files menu (MRU list)
- Auto-save functionality
- Line/column indicator in status bar
- Print preview and print support
- Find in files (search multiple files)

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
