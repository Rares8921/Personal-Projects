# Java Prompt

<div align="center">

![Java](https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)
![Utility](https://img.shields.io/badge/Type-Terminal-yellow?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**A custom command-line interface built in Java with multiple utility commands, terminal aesthetics, and system integration.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

Java Prompt is a terminal emulator with built-in commands for common tasks - think of it as a mini command prompt with Java-specific utilities plus general-purpose functions. It provides a nostalgic terminal experience while offering practical tools.

**Available commands:**
- **!print** - display custom messages (echo functionality)
- **!java** - view Java runtime information and system properties
- **!url** - open specified URLs in your default browser
- **!prime** - calculate and display prime numbers from 1 to 100
- **!date** - show current date in day.month.year format
- **!exit** - quit the application

**What makes it useful:**
- Terminal-style black background with cyan text for authentic command-line feel
- Dialog-based output (JOptionPane) for clear results display
- System integration via Desktop API for browser launching
- Real-time command processing with Enter key execution
- Copyright notice and command list displayed on startup
- Lightweight and fast - no external dependencies

The application combines the simplicity of a terminal with Java's GUI capabilities, making it educational for learning about command processors while being genuinely useful for quick calculations and system checks.

---

## Tech Stack

**Language:** Java 8  
**GUI Framework:** Swing (JFrame, JTextField, JLabel, JOptionPane)  
**Layout:** FlowLayout for component stacking  
**Integration:** Desktop API (browser launching), LocalDate (date handling)  
**Algorithms:** Prime number sieve, URL validation  
**Build:** Standard javac compilation

### Architecture

Command-pattern implementation with event-driven execution:

```
Main (extends JFrame)
      ↓
┌─────────────────────────────────┐
│   Command Processor             │
│   - KeyListener (Enter key)     │
│   - Command Parser              │
│   - Executor                    │
└─────────────────────────────────┘
      ↓
Command Handlers:
  ├─ !print → JOptionPane message dialog
  ├─ !java → System.getProperty() calls
  ├─ !url → Desktop.browse(URI)
  ├─ !prime → prime calculation algorithm
  ├─ !date → LocalDate.now() formatter
  └─ !exit → System.exit(0)
```

**Prime Number Algorithm:**
```
For each number i from 1 to 100:
    counter = 0
    For each divisor num from i down to 1:
        If i % num == 0:
            counter++
    If counter == 2:  // only divisible by 1 and itself
        Add i to primes list
```

**Key Implementation Details:**
- **Command Parsing:** Uses `String.equals()` for exact command matching
- **Output Display:** JOptionPane dialogs for non-terminal-like pop-up results
- **URL Handling:** Validates URL format, opens in default browser via `Desktop.getDesktop().browse()`
- **Java Info:** Queries system properties: runtime version, home directory, vendor, vendor URL
- **Date Formatting:** Uses Java 8's `LocalDate` API for current date (day, month, year)
- **Terminal Styling:** Black background, cyan text (RGB: #00FFFF), white labels for instructions

**System Properties Queried:**
- `java.runtime.version` - JRE version
- `java.home` - JDK/JRE installation path
- `java.vendor` - Java provider (Oracle, OpenJDK, etc.)
- `java.vendor.url` - vendor's website

---

## Project Structure

```
Java Prompt/
├── Main.java                    # Core application (GUI + command logic)
├── Java Prompt.jar              # Compiled executable
├── mainFrame.png                # Screenshot: main interface
├── javaInfoCommand.png          # Screenshot: !java output
├── primeNumbersCommand.png      # Screenshot: !prime output
├── dateCommand.png              # Screenshot: !date output
├── urlCommand.png               # Screenshot: !url usage
├── errorMessage.png             # Error dialog visual
└── README.md
```

---

## Getting Started

**Requirements:**
- Java 8 or higher
- Default web browser (for !url command)

**Run the application:**

```bash
# Option 1: Use the JAR
java -jar "Java Prompt.jar"

# Option 2: Compile from source
javac Main.java
java com.jetbrains.Main
```

**Usage guide:**

```bash
# Print a custom message
!print
# (follow the prompt to enter your message)

# View Java system information
!java

# Open a URL in browser
!url
# (enter URL when prompted, e.g., https://github.com)

# Display prime numbers 1-100
!prime

# Show current date
!date

# Exit the application
!exit
```

**Tips:**
- Commands must be typed exactly as shown (case-sensitive)
- Results appear in pop-up dialogs for easy reading
- Invalid commands are ignored (no error message)
- Command field clears after execution for next command

---

## What's Next

Planned enhancements:
- Command history with up/down arrow keys
- Autocomplete for commands (tab completion)
- More commands: `!calc`, `!sysinfo`, `!clear`, `!help`
- File operations: `!ls`, `!cd`, `!mkdir`, `!rm`
- Output logging to file
- Piping/chaining commands (e.g., `!prime | !count`)
- Script execution from `.jpt` files
- Customizable themes (green, amber, matrix)
- Command aliases for shortcuts

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
