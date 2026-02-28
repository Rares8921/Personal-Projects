# Number Base Converter

<div align="center">

![Java](https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/Swing-GUI-red?style=for-the-badge&logo=java)

**Universal number base converter. Decimal ↔ Binary ↔ Hexadecimal ↔ Octal conversion calculator.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A specialized calculator for converting numbers between different numeral systems. Essential for computer science students, programmers, and digital electronics enthusiasts.

**Conversion Features:**
- **4 Number Bases Supported:**
  - Decimal (base 10) - Standard 0-9 digits
  - Binary (base 2) - 0 and 1 only
  - Hexadecimal (base 16) - 0-9, A-F
  - Octal (base 8) - 0-7 digits
- **Instant Conversion** - Enter number in one base, convert to all others simultaneously
- **Large Number Support** - Handles up to 18 digits (limited by long integer range)
- **Input Validation** - Prevents invalid digits for selected base (e.g., no '2' in binary mode)
- **Visual Feedback** - Beep on invalid input attempt

**Arithmetic Features:**
- Basic arithmetic operations in each base
- Results automatically shown in all bases
- Useful for verifying manual calculations

**UI Features:**
- Dark theme (black background, white text)
- Numeric keypad-style input buttons
- Clear button to reset calculator
- Base selection radio buttons
- Keyboard input with validation

---

## Tech Stack

**Frontend:** Java Swing (JFrame, JTextField, JButton, JRadioButton)  
**Backend:** Java 8 + `Integer`/`Long` parsing methods  
**Build:** Java Compiler (javac)  
**Packaging:** JAR executable

### Architecture

Simple Swing application with base conversion logic:

```
Main.java (JFrame)
      ↓
Input Field (JTextField)
      ↓
Number Buttons (0-F)
      ↓
Base Selection (Radio Buttons)
      ↓
Conversion Logic (Integer.parseInt, Long.toString)
      ↓
Output Display (JLabels for each base)
```

**Conversion Implementation:**
- **Decimal → Other:** `Long.toString(number, radix)`
- **Other → Decimal:** `Long.parseLong(input, radix)`
- **Cross-Conversion:** Convert to decimal as intermediate step
- **Validation:** KeyAdapter checks each keypress against valid digits for current base

**Example Conversion Flow:**
```
Binary "1010" (user input)
  ↓ Long.parseLong("1010", 2)
Decimal 10
  ↓ Long.toString(10, 8)
Octal "12"
  ↓ Long.toString(10, 16)
Hexadecimal "A"
```

---

## Project Structure

```
src/com/company/
└── Main.java          # Main calculator frame + conversion logic

resources/
├── binary.jpg         # Binary number system illustration
├── hexadecimal.jpg    # Hex number system illustration
├── octal.jpg          # Octal number system illustration
├── main.jpg           # App screenshot
└── calculator.png     # Icon

Dec. Conv. Calc..jar   # Packaged executable
README.md
```

---

## Getting Started

**Requirements:**
- Java 8+ (JRE or JDK)

**Run the JAR:**

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Java projects\Dec. conv. calc"

# Run directly
java -jar "Dec. Conv. Calc..jar"
```

**Or compile from source:**

```bash
# Compile
javac -d bin src/com/company/Main.java

# Run
java -cp bin com.company.Main
```

**How to Use:**
1. Launch application
2. Select base mode (Decimal, Binary, Hex, Octal) via radio buttons
3. Enter number using on-screen buttons or keyboard
4. Click **Convert** button
5. Results display in all four bases simultaneously
6. Click **C** (Clear) to reset

**Examples:**
- Binary `1111` = Decimal `15` = Hex `F` = Octal `17`
- Decimal `255` = Binary `11111111` = Hex `FF` = Octal `377`
- Hex `A5` = Decimal `165` = Binary `10100101` = Octal `245`

---

## What's Next

Potential feature additions:

- **More Bases** - Add base 32, base 64, or custom bases (2-36)
- **Arithmetic Mode** - Perform addition/subtraction/multiplication in selected base
- **Bitwise Operations** - AND, OR, XOR, NOT, shift operations
- **Floating Point** - Support decimal fractions in different bases
- **Two's Complement** - Signed integer representation for negative numbers
- **History Panel** - Keep list of recent conversions
- **Copy to Clipboard** - One-click copy of any result
- **Programmer Mode** - Show byte/word/dword representations with bit visualization
- **Scientific Notation** - Support for very large numbers
- **Batch Conversion** - Convert multiple numbers from text file

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
