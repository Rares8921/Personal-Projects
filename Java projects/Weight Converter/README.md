# Weight Converter

<div align="center">

![Java](https://img.shields.io/badge/Java-Swing-orange?style=for-the-badge&logo=java)
![Conversion](https://img.shields.io/badge/Weight-Converter-blue?style=for-the-badge&logo=scale)
![Units](https://img.shields.io/badge/kg%20%E2%87%84%20lbs-Bidirectional-green?style=for-the-badge&logo=convert)

**A simple, accurate weight unit converter. Convert between kilograms, pounds, ounces, and tons with real-time bidirectional conversion and input validation.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a lightweight weight conversion calculator built with Java Swing. Type a value in any unit (kg, lbs, oz, tons), and it instantly converts to other units. Bidirectional, real-time, and with built-in input validation to prevent errors.

**Core features:**
- **Kilograms ↔ Pounds** conversion (primary)
- **Real-time conversion**: Updates as you type
- **Bidirectional**: Convert kg→lbs or lbs→kg simultaneously
- **Input validation**: Blocks non-numeric characters except decimal point
- **Decimal support**: Handles fractional weights (e.g., 72.5 kg)
- **Clear UI**: Labeled fields with conversion formulas visible
- **Error prevention**: Validates input to prevent crashes
- **Lightweight**: Instant startup, minimal resource usage

**Conversion formulas:**
- **lbs to kg**: `kg = lbs / 2.20462`
- **kg to lbs**: `lbs = kg × 2.20462`

**Additional units (extendable):**
- Ounces (oz): `1 lb = 16 oz`
- Tons (metric): `1 ton = 1000 kg`
- Tons (imperial): `1 ton = 2000 lbs`
- Grams: `1 kg = 1000 g`

**Why it's useful:**
- Fast, accurate conversions without internet
- No ads or distractions
- Simple, intuitive interface
- Handles edge cases (empty input, negative numbers, decimals)

The converter uses ActionListener and KeyAdapter to detect input changes, calculates conversions using standard formulas, and displays results in JFormattedTextField components.

---

## Tech Stack

**Language:** Java SE (Swing for GUI)  
**UI Framework:** Swing (JLabel, JFormattedTextField, JButton)  
**Validation:** KeyAdapter for input filtering  
**Math:** Standard arithmetic operations (`*`, `/`)  
**Formatting:** `JFormattedTextField` for numeric input

### Architecture

Simple event-driven calculator with input validation and conversion logic:

```
UI Layer (Swing Components)
      ↓
Input Fields (JFormattedTextField)
      ↓
Event Handlers (KeyAdapter, ActionListener)
      ↓
┌──────────────┬──────────────┐
│  Validation  │  Conversion  │
│  Engine      │  Engine      │
└──────────────┴──────────────┘
      ↓              ↓
Block Invalid   Calculate
Characters      kg ↔ lbs
(regex check)   (formulas)
      ↓
Display Result
(JLabel or TextField)
```

**How it works:**
- **Input Detection**: KeyAdapter on JFormattedTextField listens for keystrokes
- **Validation**: On each key press, checks if character is numeric or decimal point
- **Blocking**: If invalid character (letter, symbol), sets `editable=false` momentarily to block input
- **Conversion**: When valid number entered, calculates opposite unit using formula
- **Display**: Updates result field in real-time
- **Bidirectional**: Both fields have listeners, so conversion works in both directions

**Key Implementation Details:**
- **Key Event Filtering**: KeyAdapter checks KeyEvent for allowed keys (0-9, decimal, Enter, Delete, Backspace, function keys)
- **Editable Toggle**: Temporarily sets `setEditable(false)` to prevent invalid character insertion
- **Conversion Logic**: `lbs_value / 2.20462` or `kg_value * 2.20462`
- **Decimal Support**: Accepts '.' character for fractional weights
- **Result Display**: Can be JLabel (read-only) or JFormattedTextField (editable)

---

## Project Structure

```
Weight Converter/
├── Main.java                   # Entry point + Swing UI + conversion logic
├── Weight Converter.jar        # Compiled JAR executable
└── README.md
```

**Main components:**
- `Main.java`: Swing UI setup, input validation, conversion calculations
- Input fields: Two JFormattedTextField components (lbs and kg)
- Conversion methods: `convertLbsToKg()`, `convertKgToLbs()`

---

## Getting Started

### Prerequisites

- **Java SE 8+** (any modern Java version)

### Running the Application

**Option 1: JAR**
```bash
# Run the JAR file:
java -jar "Weight Converter.jar"
```

**Option 2: From Source**
```bash
# Compile:
javac -d bin Main.java

# Run:
java -cp bin com.jetbrains.Main
```

### How to Use

1. **Convert lbs to kg**:
   - Enter pounds value in "Lbs" field
   - Result appears automatically in kg

2. **Convert kg to lbs**:
   - Enter kilograms value in "Kg" field
   - Result appears automatically in lbs

3. **Decimal values**:
   - Use decimal point for fractional weights (e.g., 72.5)
   - Both fields support decimals

4. **Clear and retry**:
   - Delete existing value
   - Enter new value
   - Conversion updates instantly

**Example conversions:**
- 100 lbs → 45.36 kg
- 70 kg → 154.32 lbs
- 1 lb → 0.45 kg

**Input validation:**
- Only numbers and decimal point allowed
- Special characters blocked automatically
- Empty input handled gracefully

---

## What's Next

**Potential improvements:**
- Add more units (ounces, grams, tons, stones)
- Implement dropdown for unit selection
- Add conversion history
- Support for batch conversions (multiple values)
- Scientific notation support
- Rounding preferences (decimal places)
- Imperial ton vs metric ton distinction
- Export results to file

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
