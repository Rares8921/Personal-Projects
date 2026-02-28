# Hangman

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![Game](https://img.shields.io/badge/Type-Word%20Game-purple?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**Classic hangman word guessing game with a comprehensive dictionary, difficulty levels, and visual hangman progression.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A polished implementation of the timeless hangman game where players guess letters to reveal a hidden word before running out of lives. The game features a clean JavaFX interface with visual feedback for every guess.

**Core features:**
- **Word dictionary** - extensive word list loaded from file (`words.txt`)
- **Letter guessing** - on-screen keyboard or physical keyboard input
- **Lives system** - 6 incorrect guesses allowed, visualized via hangman drawing
- **Difficulty levels** - easy (short words), medium, hard (long/complex words)
- **Progress tracking** - see guessed letters and remaining blanks

**What makes it engaging:**
- Visual hangman progressively drawn with each wrong guess
- Categorized word lists (common vs. challenging words)
- Real-time feedback for correct/incorrect guesses
- Win/loss screens with option to play again
- Keyboard navigation fully supported
- Clean, minimalist UI that doesn't distract from gameplay

The game randomly selects words from the dictionary based on chosen difficulty, tracks all guessed letters to prevent duplicates, and updates the display in real-time as players make progress.

---

## Tech Stack

**Language:** Java 11  
**Framework:** JavaFX (FXML + Scene Builder)  
**UI:** FXML-based layout with CSS styling  
**Data:** File I/O for word dictionary  
**Build:** Standard Java compilation

### Architecture

MVC pattern with JavaFX FXML:

```
Main (Application Launcher)
      ↓
FXML Layout (sample.fxml)
      ↓
Controller (Game Logic)
      ↓
┌─────────────────────────────────┐
│   Game State Manager            │
│   - Word Selection              │
│   - Letter Validation           │
│   - Lives Tracking              │
│   - Win/Loss Detection          │
└─────────────────────────────────┘
      ↓
Components:
  ├─ Word Dictionary (words.txt)
  ├─ Keyboard Handler (physical + virtual)
  ├─ Hangman Drawer (SVG/Canvas)
  └─ Open Dialog (game start options)
```

**Game Logic Flow:**
1. Load words from `words.txt` file
2. Filter by difficulty (word length/complexity)
3. Select random word and create blank template (`_ _ _ _`)
4. Listen for keyboard/button input
5. Validate letter against word
6. Update display (reveal letters or draw hangman part)
7. Check win condition (all letters found) or loss (6 wrong guesses)

**Key Implementation Details:**
- **Word Storage:** Text file with one word per line, loaded at startup
- **Letter Tracking:** HashSet for guessed letters to prevent duplicates
- **Display Logic:** String with underscores replaced by correct guesses
- **Hangman Drawing:** Staged rendering - base → head → body → left arm → right arm → left leg → right leg
- **Difficulty Algorithm:** Easy (4-6 letters), Medium (7-9 letters), Hard (10+ letters)
- **Input Handling:** Both JavaFX button clicks and physical keyboard events trigger same validator

---

## Project Structure

```
Hangman/
└── src/
    ├── sample/
    │   ├── Main.java              # Application entry point
    │   ├── Controller.java        # Game logic controller
    │   ├── Open.java             # Difficulty selection dialog
    │   ├── sample.fxml           # Main UI layout
    │   ├── words.txt             # Word dictionary (100+ words)
    │   ├── hangman.png           # App icon
    │   └── calendar.png          # UI assets
    └── META-INF/
        └── MANIFEST.MF           # JAR manifest
```

---

## Getting Started

**Requirements:**
- Java 11 or higher
- JavaFX SDK 11+ (if not bundled)

**Setup and run:**

```bash
# Compile
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/sample/*.java

# Run
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin sample.Main
```

**Or use an IDE:**
1. Import project into IntelliJ IDEA / Eclipse / NetBeans
2. Configure JavaFX library in project settings
3. Run `Main.java`

**How to play:**
1. Launch the application
2. Select difficulty level (Easy/Medium/Hard)
3. Guess letters by clicking on-screen keyboard or using physical keyboard
4. Correctly guessed letters appear in the word
5. Incorrect guesses add parts to the hangman
6. Win by completing the word, lose after 6 wrong guesses

**Customization:**
- Edit `words.txt` to add your own words (one per line)
- Longer words = harder difficulty

---

## What's Next

Future enhancements under consideration:
- Category-based word selection (animals, countries, movies, etc.)
- Hint system (reveal one letter for a penalty)
- Timer mode for added challenge
- Multiplayer mode (turn-based guessing)
- Statistics tracking (win rate, average guesses)
- Sound effects and animations
- Dark theme option

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
