# Password Generator

<div align="center">

![Java](https://img.shields.io/badge/Java-SE%2021-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/Swing-GUI-green?style=for-the-badge&logo=java)
![SecureRandom](https://img.shields.io/badge/SecureRandom-Crypto-blue?style=for-the-badge&logo=java)

**Create cryptographically secure passwords in seconds. Full control over character sets, length, and complexity—with a password strength meter that actually matters.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a secure password generator built with Java Swing. It's not just another random string generator—it uses `SecureRandom` for cryptographic-quality randomness, gives you real-time strength feedback, and lets you copy passwords to the clipboard instantly.

**Core features:**
- Generate **cryptographically secure passwords** using Java's `SecureRandom` API
- Control password length from **8 to 32 characters** with an intuitive slider
- Pick your character sets: **uppercase letters, lowercase letters, numbers, symbols**
- Visual strength meter shows password complexity (weak, moderate, strong, very strong)
- **Copy to clipboard** with one click for fast workflow
- Clean dark-themed UI with custom tooltips

**Why it's better:**
- Uses `SecureRandom` instead of basic `Random` (actually secure)
- Real-time strength calculation based on entropy
- No online API calls—your passwords never leave your machine
- Simple, distraction-free interface
- Configurable character pool with instant preview

The strength meter analyzes entropy based on character set diversity and length, giving you a realistic assessment of password security. And because it's all local, there's zero risk of password exposure.

---

## Tech Stack

**Language:** Java SE (Swing for GUI)  
**Core APIs:** `SecureRandom`, `java.awt.datatransfer.Clipboard`, `javax.swing`  
**UI Components:** JSlider, JRadioButton, JButton, JLabel, custom tooltips  
**Security:** Cryptographic-quality random number generation

### Architecture

Simple layered design—UI layer, generation logic, and clipboard integration:

```
UI Layer (Swing Components)
      ↓
Event Handlers (ActionListener, ChangeListener)
      ↓
Password Generation Engine
      ↓
SecureRandom API + Character Pool Builder
      ↓
Strength Calculator (Entropy Analysis)
      ↓
Clipboard API (Copy to System Clipboard)
```

**How it works:**
- User configures password length (8-32) and character sets via UI
- On "Generate" button click, character pool is built from selected options
- `SecureRandom` generates random indices into the pool
- Password strength is calculated based on character set diversity and length
- Generated password is displayed with strength indication
- One-click copy to clipboard using `java.awt.datatransfer` API

**Key Implementation Details:**
- **SecureRandom:** Uses OS-level entropy sources for cryptographically strong randomness
- **Character Pool:** Dynamically built from selected checkboxes (uppercase, lowercase, digits, symbols)
- **Strength Calculation:** `log2(pool_size^length)` gives entropy bits; categorized into weak/moderate/strong/very strong
- **UI Theme:** Custom dark theme (#1b1c1c background) with white text and hover effects
- **Copy Mechanism:** `StringSelection` + `Clipboard.setContents()` for OS-level clipboard integration

---

## Project Structure

```
PasswordGenerator/
├── Main.java                   # Entry point + Swing UI setup
├── MyButton.java               # Custom button with hover effects
├── MyRadioButton.java          # Styled radio buttons for character sets
├── CustomJToolTip.java         # Dark-themed tooltips
└── PasswordGenerator.exe       # Compiled executable
```

**Main components:**
- `Main.java`: UI layout, event handlers, password generation logic
- `MyButton`: Custom button class with hover/pressed color states
- `MyRadioButton`: Styled checkboxes for character set selection
- Strength meter: Real-time entropy calculation based on selected character sets

---

## Getting Started

### Prerequisites

- **Java SE 8+** (recommended: Java 11 or higher)

### Running the Application

**Option 1: JAR/Executable**
```bash
# If .exe is available (Windows):
.\PasswordGenerator.exe

# Or run the Java class directly:
javac Main.java
java com.company.Main
```

**Option 2: From Source**
```bash
# Compile the source:
javac -d bin src/com/company/*.java

# Run:
java -cp bin com.company.Main
```

### How to Use

1. **Select character sets**: Check one or more options (uppercase, lowercase, numbers, symbols)
2. **Adjust length**: Use the slider to pick password length (8-32 characters)
3. **Generate**: Click "Generate" to create a secure password
4. **Check strength**: View the strength indicator (color-coded from red to green)
5. **Copy**: Click "Copy to Clipboard" to use the password immediately

**Pro tip:** For maximum security, enable all character sets and use at least 16 characters.

---

## What's Next

**Potential improvements:**
- Add password history with secure storage
- Implement passphrase generator (diceware-style)
- Export passwords to encrypted file
- Add pronounceable password mode
- Remember last used settings
- Dark/light theme toggle
- Multi-language support

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
