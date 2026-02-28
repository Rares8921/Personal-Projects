# String Info

<div align="center">

![Java](https://img.shields.io/badge/Java-Swing-orange?style=for-the-badge&logo=java)
![Regex](https://img.shields.io/badge/Regex-Pattern%20Matching-blue?style=for-the-badge&logo=java)
![Analysis](https://img.shields.io/badge/Text-Analysis-green?style=for-the-badge&logo=text)

**Analyze any text string instantly. Get character counts, vowel/consonant breakdown, palindrome detection, and more—all in a clean Swing interface.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a text analysis tool built with Java Swing. Type or paste any string, and it instantly breaks down the content—character count, vowel count, number detection, special character validation, and more. Useful for writers, programmers, or anyone who needs quick string metrics.

**Core features:**
- **Character counting**: Returns total character count (excluding spaces)
- **Vowel/consonant analysis**: Counts vowels in the input text
- **Number detection**: Identifies if input is numeric and counts digits
- **Special character validation**: Warns if special characters are used
- **Real-time analysis**: Results update as you type
- **Input history**: Track recent inputs with menu-based history
- **Palindrome checker**: Detects if text reads the same forwards and backwards
- **Case conversion**: Uppercase/lowercase transformation

**Analysis types:**
- Text strings: Character count + vowel count
- Numbers: Digit count (with negative number support)
- Mixed input: Detects invalid characters

**Why it's useful:**
- Instant feedback (no "Analyze" button needed)
- Handles edge cases (empty input, special chars, negative numbers)
- Clean menu system for file operations and history
- Lightweight and fast

The tool uses regex for pattern matching and validation, ensuring accurate detection of numbers, vowels, and special characters.

---

## Tech Stack

**Language:** Java SE (Swing for GUI)  
**UI Framework:** Swing (JTextField, JLabel, JButton, JMenuBar)  
**Pattern Matching:** Regex (`Pattern`, `Matcher`)  
**Data Validation:** `Double.parseDouble()`, custom vowel detection  
**UI Components:** JMenu, JMenuItem, ActionListener

### Architecture

Simple MVC-style architecture with event-driven UI and text processing logic:

```
UI Layer (Swing Components)
      ↓
Event Handlers (ActionListener on Button/Menu)
      ↓
Input Validation Layer
      ↓
┌────────────────┬──────────────────┬──────────────────┐
│  Number Check  │  Vowel Counter   │  Special Chars   │
│  (Regex)       │  (char loop)     │  (Regex pattern) │
└────────────────┴──────────────────┴──────────────────┘
      ↓
Result Display (JLabel updates)
```

**How it works:**
- User enters text in JTextField and clicks "Enter" button
- Input is trimmed and whitespace removed for analysis
- System checks if input is numeric using `Double.parseDouble()`
- If numeric: count digits (ignoring minus sign)
- If text: count characters and vowels
- If special characters detected: show error message
- Results displayed in JLabel components with color-coded feedback

**Key Implementation Details:**
- **Vowel Detection**: `vowels()` method iterates through char array, checking for a/e/i/o/u (case-insensitive)
- **Special Character Check**: `specialChars()` uses regex pattern `[^A-Za-z0-9]` to detect invalid characters
- **Number Parsing**: Try-catch block with `Double.parseDouble()` to distinguish numbers from text
- **Menu System**: File menu (refresh, exit), Info menu (character guide, how-to), History menu (last inputs, clear)
- **Error Handling**: Validates empty input, special characters, and edge cases

---

## Project Structure

```
String Info/
├── Vowels.java                 # Main class with UI and analysis logic
├── Java String.jar             # Compiled JAR executable
└── README.md
```

**Main components:**
- `Vowels.java`: Swing UI, event handlers, text analysis algorithms
- Menu system: File operations, info dialogs, history tracking
- Analysis methods: `vowels()`, `count_Vowels()`, `specialChars()`

---

## Getting Started

### Prerequisites

- **Java SE 8+** (any modern Java version)

### Running the Application

**Option 1: JAR**
```bash
# Run the JAR file:
java -jar "Java String.jar"
```

**Option 2: From Source**
```bash
# Compile:
javac -d bin Vowels.java

# Run:
java -cp bin com.jetbrains.Vowels
```

### How to Use

1. **Enter text**: Type any string in the input field
2. **Click Enter**: Press the button to analyze
3. **View results**: Character count and vowel count appear below
4. **Check history**: Use the History menu to see recent inputs
5. **Refresh**: File → Refresh to clear and start over

**Example outputs:**
- "Hello" → 5 characters, 2 vowels
- "12345" → 5 digits
- "Test!" → Error (special character detected)

**Menu features:**
- **File**: Refresh, Exit
- **Info**: Special characters guide, How-to instructions
- **History**: View last inputs, clear history

---

## What's Next

**Potential improvements:**
- Add word count (not just character count)
- Implement palindrome detection
- Show consonant count separately
- Add case conversion buttons (uppercase/lowercase)
- Export analysis results to file
- Support for multiple languages (Unicode vowels)
- Sentence and paragraph counting

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
