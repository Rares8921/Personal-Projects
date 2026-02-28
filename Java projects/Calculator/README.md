# Scientific Calculator

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![FXML](https://img.shields.io/badge/FXML-UI-green?style=for-the-badge)

**Full-featured scientific calculator with modern UI. Because the default calculator isn't cutting it.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A complete scientific calculator built with JavaFX, offering both basic arithmetic and advanced mathematical functions in a clean, intuitive interface.

**Calculation Features:**
- **Basic Operations** - Addition, subtraction, multiplication, division
- **Scientific Functions** - sin, cos, tan, arcsin, arccos, arctan
- **Exponential & Logarithmic** - exp, ln, log, power (^), square root
- **Memory Functions** - M+, M-, MR, MC for storing intermediate results
- **Advanced Operations** - Factorial, absolute value, reciprocal (1/x)
- **Constants** - π (pi), e (Euler's number)

**UI Features:**
- Draggable undecorated window
- Keyboard input support
- Expression display with result preview
- Error handling for invalid operations
- Clean, modern dark theme

---

## Tech Stack

**Frontend:** JavaFX 11 + FXML + CSS  
**Backend:** Java 11 + Math library  
**Build:** Maven  
**UI Framework:** JavaFX Controls (TextField, Button, Label)

### Architecture

Classic MVC pattern with FXML-based view:

```
Main.java (Application)
      ↓
Main.fxml (View Layout)
      ↓
Controller.java (Logic)
      ↓
InputBox.fxml (Pop-up dialogs)
      ↓
Math Operations (Java Math library)
```

**Implementation Details:**
- **Expression Parser:** Custom parsing logic for operator precedence
- **State Management:** Tracks current operation, operands, and memory state
- **Event Handling:** Button actions bound via FXML + @FXML annotations
- **Input Validation:** Prevents invalid characters and operations
- **Responsive UI:** JavaFX scene graph with CSS styling

---

## Project Structure

```
src/sample/
├── Main.java           # JavaFX Application entry point
├── Controller.java     # Main calculator controller logic
├── Main.fxml           # Calculator UI layout
├── InputBox.java       # Dialog controller for special inputs
├── InputBox.fxml       # Dialog layout
├── Open.java           # Utility class
└── styles.css          # Application styling

resources/
├── calc.png            # Icon
└── calc.ico            # Windows icon
```

---

## Getting Started

**Requirements:**
- Java 11+ (with JavaFX bundled or as separate modules)
- Maven (optional, can run directly from IDE)

**Run from IDE:**

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Java projects\Calculator"

# Run Main.java directly
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml sample.Main
```

**Or compile with Maven:**

```bash
mvn clean install
mvn javafx:run
```

**Keyboard Shortcuts:**
- Numbers 0-9, operators (+, -, *, /)
- Enter/= for equals
- C for clear
- Backspace to delete last character
- Esc to close

---

## What's Next

Future enhancements to consider:

- **History Panel** - View and recall previous calculations
- **Graphing Mode** - Plot functions like f(x) = sin(x)
- **Programmer Mode** - Binary, hex, octal calculations with bitwise operations
- **Unit Converter** - Integrated conversion for length, weight, temperature
- **Custom Functions** - User-defined functions with variables
- **Export Results** - Copy calculation history to clipboard or save as TXT/CSV
- **Themes** - Light/dark mode toggle with custom color schemes
- **RPN Mode** - Reverse Polish Notation for advanced users

---

## 📹 Demo Video

> **Recording Instructions:** A 3-5 minute walkthrough showcasing the key features of this project.

### What to Demonstrate

**Suggested Timeline:**
- **0:00-0:30** - Project overview and startup
- **0:30-2:00** - Core features demonstration
- **2:00-3:30** - Advanced features and interactions
- **3:30-5:00** - Edge cases and wrap-up

### Features to Showcase

- **Basic Operations** - Addition, subtraction, multiplication, division
- **Scientific Functions** - sin, cos, tan, log, exp, square root
- **Memory Functions** - M+, M-, MR, MC operations
- **Constants** - π (pi) and e (Euler's number)
- **Advanced Operations** - Factorial, absolute value, power (^)
- **Keyboard Input** - Show keyboard support for numbers and operators

### Recording Setup

**Prerequisites:**
```bash
# Ensure JavaFX 11+ is installed
# Run from IDE or compile to JAR with Maven
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `java -jar Calculator.jar` or run Main class
2. Open OBS Studio and set up screen capture
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Java projects\Calculator"
java -jar Calculator.jar

# Or with Maven
mvn javafx:run
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
