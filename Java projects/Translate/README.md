# Translate

<div align="center">

![Java](https://img.shields.io/badge/Java-Swing-orange?style=for-the-badge&logo=java)
![API](https://img.shields.io/badge/Google-Translate%20API-blue?style=for-the-badge&logo=google)
![Languages](https://img.shields.io/badge/100+-Languages-green?style=for-the-badge&logo=translate)

**A powerful translation tool that integrates with Google Translate API. Translate text or entire files between 100+ languages, with undo/redo, find/replace, and custom shortcuts.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a feature-rich translation application built with Java Swing that leverages the Google Translate API. Translate text between any of 100+ supported languages, with support for file translation, undo/redo, find/replace, and keyboard shortcuts for power users.

**Core features:**
- **Google Translate API integration**: Real-time translation using Google's translation service
- **100+ languages**: Support for all major world languages
- **Bidirectional translation**: Swap source and target languages instantly (Ctrl+F2)
- **File translation**: Select a text file to translate its entire contents
- **Dual text areas**: Side-by-side input and output panes
- **Undo/Redo**: Full edit history with UndoManager
- **Find/Replace**: Regex-powered search with text highlighting
- **Copy to clipboard**: One-click copy of translated text
- **Keyboard shortcuts**: 13+ shortcuts for fast workflow
- **Language switching**: Quick language picker with favorites
- **Dark theme**: Custom dark UI with #1c1c1c background

**Advanced features:**
- **Highlight search results**: Find text and highlight all matches
- **Replace functionality**: Find and replace text in source area
- **Text-to-speech ready**: Prepared for TTS integration
- **Multi-language support**: UI switches between English and Romanian

**Why it's powerful:**
- No manual API key entry (integrated)
- Handles large text blocks and files
- Professional-grade text editing features
- Fast keyboard shortcuts for productivity
- Clean, distraction-free interface

The app uses HTTP requests to Google Translate API, parses JSON responses, and displays translations in real-time. All text processing uses regex for pattern matching and validation.

---

## Tech Stack

**Language:** Java SE (Swing for GUI)  
**API:** Google Translate REST API  
**JSON Parsing:** org.json library (`JSONObject`)  
**UI Framework:** Swing (JTextArea, JButton, JMenuBar, JFileChooser)  
**Networking:** `HttpURLConnection`, `URLEncoder`, `BufferedReader`  
**Text Processing:** `UndoManager`, `Highlighter`, regex (`Pattern`, `Matcher`)  
**File I/O:** `BufferedReader`, `FileReader` for file translation

### Architecture

REST API integration with text processing and UI event handling:

```
UI Layer (Swing Components)
      ↓
Event Handlers (Button clicks, Key bindings)
      ↓
┌──────────────┬──────────────┬──────────────┬───────────────┐
│  Translation │  File I/O    │  Undo/Redo   │  Find/Replace │
│  Engine      │  Handler     │  Manager     │  Engine       │
└──────────────┴──────────────┴──────────────┴───────────────┘
      ↓              ↓              ↓               ↓
HTTP Request    BufferedReader  UndoManager    Regex Matcher
Google API      FileReader      CompoundEdit   Highlighter
JSON Parse
```

**How it works:**
- **Translation Flow**: User enters text → clicks "Translate" → HTTP POST to Google API → JSON response parsed → result displayed
- **Language Selection**: Dropdown buttons for source/target language; stored as ISO codes (en, es, fr, ro, etc.)
- **API Request**: URL-encode text → construct API URL → send GET request → receive JSON → extract translation
- **File Translation**: FileChooser opens file → read contents → send to API → display translated result
- **Find/Replace**: Regex searches source text → highlights matches → replace function updates text
- **Keyboard Shortcuts**: Key bindings registered for Ctrl+C, Ctrl+V, Ctrl+Z, Ctrl+F, Ctrl+F3, etc.

**Key Implementation Details:**
- **API Integration**: Constructs URL like `https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=es&dt=t&q=text`
- **JSON Parsing**: Uses org.json library to extract translation from nested JSON array
- **Language Pairs**: Source and target languages stored in atomic integers or strings
- **Highlight System**: Custom `MyHighlightPainter` with configurable color (#3b3a3a)
- **Shortcuts**: 13 key bindings (Ctrl+C/V/X/A/Z/R/H/F/F1-F5)

---

## Project Structure

```
Translate/
├── src/
│   ├── Main.java               # UI + translation logic + API calls
│   ├── MyKeyListener.java      # Custom key event handling
│   ├── CustomJToolTip.java     # Styled tooltips
│   └── META-INF/
├── json-20240303.jar           # JSON parsing library
├── Translate.jar               # Compiled executable JAR
└── README.md
```

**Main components:**
- `Main.java`: Swing UI, API integration, translation engine, keyboard shortcuts
- MyKeyListener: Handles custom key events and text highlighting
- JSON library: Parses API responses
- Language selector: Dropdown with 100+ languages

---

## Getting Started

### Prerequisites

- **Java SE 8+** (recommended: Java 11 or higher)
- **Internet connection** (for Google Translate API)
- json-20240303.jar (included)

### Running the Application

**Option 1: JAR**
```bash
# Run the JAR (includes dependencies):
java -jar Translate.jar
```

**Option 2: From Source**
```bash
# Compile (with json library in classpath):
javac -cp json-20240303.jar -d bin src/*.java

# Run:
java -cp "bin;json-20240303.jar" Main
```

### How to Use

**Basic translation:**
1. **Select source language**: Click first language button, pick language
2. **Enter text**: Type or paste text in left text area
3. **Select target language**: Click second language button, pick language
4. **Translate**: Click "Translate" button or press Ctrl+F3
5. **View result**: Translated text appears in right text area
6. **Copy**: Click copy button to copy to clipboard

**File translation:**
1. Press Ctrl+F4 or click "Select File"
2. Choose a text file
3. File contents are translated automatically
4. View result in right pane

**Keyboard shortcuts:**
- **Ctrl+C**: Copy text
- **Ctrl+V**: Paste text
- **Ctrl+X**: Cut text
- **Ctrl+A**: Select all
- **Ctrl+Z**: Undo
- **Ctrl+R**: Redo
- **Ctrl+H**: Replace text
- **Ctrl+F**: Find and highlight text
- **Ctrl+F1**: Remove highlights
- **Ctrl+F2**: Swap languages
- **Ctrl+F3**: Translate
- **Ctrl+F4**: Select file
- **Ctrl+F5**: Change language

**Language switching:**
- Click language button to open picker
- Select from 100+ languages
- Common options: English, Spanish, French, German, Chinese, Japanese, Arabic, Russian, etc.

---

## What's Next

**Potential improvements:**
- Add text-to-speech for translated text
- Implement offline translation mode (local model)
- Add translation history with search
- Support for document formats (PDF, DOCX)
- Real-time translation as you type
- Dictionary/definition lookup
- Pronunciation guide
- Multiple API provider support (DeepL, Microsoft Translator)

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
