# Height Converter

<div align="center">

![Java](https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)
![Utility](https://img.shields.io/badge/Type-Converter-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**A bidirectional height conversion calculator supporting feet/inches to centimeters and vice versa with real-time validation.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A practical utility for converting height measurements between imperial (feet/inches) and metric (centimeters) systems. The converter provides instant calculations as you type and includes comprehensive input validation to prevent errors.

**Core functionality:**
- **Feet + inches to centimeters** - combined imperial input for accurate conversion
- **Centimeters to feet + inches** - splits metric input into feet and remaining inches
- **Real-time updates** - calculations happen instantly as you type
- **Input validation** - only accepts numeric values and decimal points
- **Bidirectional conversion** - convert in either direction without switching modes

**What makes it useful:**
- Handles decimal values for precise conversions (e.g., 5.5 feet)
- Clear, intuitive interface with labeled fields
- Error prevention through key filtering (blocks invalid characters)
- Rounded border styling for modern appearance
- Tooltips for guidance on each input field
- No "Convert" button needed - everything updates automatically

The conversion uses standard formulas: 1 foot = 30.48 cm, 1 inch = 2.54 cm. All calculations are performed with floating-point precision and displayed with appropriate decimal places.

---

## Tech Stack

**Language:** Java 8  
**GUI Framework:** Swing (JFrame, JTextFields, JLabels)  
**Layout:** FlowLayout for responsive component positioning  
**Validation:** KeyListener with character filtering  
**Build:** Standard javac compilation

### Architecture

Simple event-driven GUI application:

```
Main (extends JFrame)
      ↓
┌─────────────────────────────────┐
│   GUI Components                │
│   - Input TextFields            │
│   - Label Instructions          │
│   - Border Styling              │
└─────────────────────────────────┘
      ↓
Event Handlers:
  ├─ KeyListener (input validation)
  │    └─ Filters: letters, symbols (allows 0-9, .)
  │
  └─ DocumentListener (real-time conversion)
       ├─ feetToMetric(): (feet * 30.48) + (inches * 2.54)
       └─ metricToImperial(): cm / 30.48 = feet, remainder / 2.54 = inches
```

**Conversion Formulas:**
- **Feet to cm:** `centimeters = (feet × 30.48) + (inches × 2.54)`
- **Inches to cm:** `centimeters = inches × 2.54`
- **Cm to feet:** `feet = floor(cm ÷ 30.48)`, `inches = (cm % 30.48) ÷ 2.54`

**Key Implementation Details:**
- **Input Filtering:** KeyListener intercepts keystrokes, validates against allowed characters (0-9, period, control keys)
- **Real-Time Calculation:** DocumentListener triggers on text change, parses values, performs conversion, updates output fields
- **Error Handling:** Try-catch blocks prevent crashes from invalid number formats, empty fields default to 0
- **Precision:** Uses `Double.parseDouble()` for decimal support, formats output to 2 decimal places
- **UI Styling:** Custom LineBorder with rounded corners, consistent font sizing (16pt), tooltip text for user guidance

---

## Project Structure

```
Height Converter/
├── Main.java                    # GUI + conversion logic (all-in-one)
├── Height Convertor.jar         # Compiled executable
├── errorMessage.png             # Error dialog icon
├── toCm.png                     # UI graphics (imperial → metric)
├── toInches.png                 # UI graphics (metric → imperial)
└── README.md
```

---

## Getting Started

**Requirements:**
- Java 8 or higher (JRE sufficient for running JAR)

**Option 1: Run the JAR file**
```bash
java -jar "Height Convertor.jar"
```

**Option 2: Compile from source**
```bash
# Compile
javac -d bin Main.java

# Run
java -cp bin com.jetbrains.Main
```

**Using the converter:**
1. **To convert feet + inches to cm:**
   - Enter feet value in "Feet" field
   - Enter inches value in "In" field
   - Result appears instantly in "Centimeters" field

2. **To convert cm to feet + inches:**
   - Enter value in "Centimeters" field
   - Result splits into "Feet convert" and "In convert" fields automatically

**Input rules:**
- Only numeric values accepted (0-9 and decimal point)
- Negative values not supported (physical constraint)
- Leaving a field empty defaults to 0

---

## What's Next

Potential improvements for future versions:
- Meters input/output option
- Millimeters support for precision work
- Copy to clipboard functionality
- Unit presets (common heights: 5'8", 6'0", etc.)
- Conversion history log
- Imperial-only mode (feet ↔ inches conversion)
- Dark theme with theme switcher
- Menu bar with About, Help, Settings

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
