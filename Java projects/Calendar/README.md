# Calendar Application

<div align="center">

![Java](https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/Swing-GUI-red?style=for-the-badge&logo=java)

**Interactive desktop calendar with month/year navigation. Simple, clean, functional.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A lightweight desktop calendar built with Java Swing, providing intuitive month navigation and date viewing in a familiar grid layout.

**Key Features:**
- **Month Navigation** - Arrow buttons to move forward/backward through months
- **Year Tracking** - Display current month and year (e.g., January 2026)
- **Grid Layout** - Traditional 7-column week view (Sun-Sat)
- **Today Highlighting** - Current date marked/highlighted automatically
- **Clean Interface** - No clutter, just the calendar you need
- **Packaged Executable** - Runs as standalone JAR file

**How it works:**
Uses Java's `GregorianCalendar` to calculate days in each month, accounting for leap years and proper week alignment. The JTable component displays dates in a grid, with columns for each day of the week. Navigation updates the table model dynamically without restarting the app.

---

## Tech Stack

**Frontend:** Java Swing (JFrame, JTable, JButton, JLabel)  
**Backend:** Java 8 + `java.util.Calendar` API  
**Build:** Java Compiler (javac)  
**Packaging:** JAR executable

### Architecture

Simple Swing-based MVC-like structure:

```
Main.java (JFrame)
      ↓
JTable (Calendar Grid)
      ↓
DefaultTableModel
      ↓
GregorianCalendar (Date Logic)
      ↑
Navigation Buttons (←/→)
```

**Implementation Details:**
- **GregorianCalendar:** Core API for date arithmetic and week-of-month calculations
- **DefaultTableModel:** Dynamically updates table data when month changes
- **Custom Renderer:** Centers text in table cells for clean alignment
- **Event Listeners:** ActionListener on buttons to increment/decrement month
- **Non-Editable Cells:** Table cells are read-only to prevent accidental date edits

---

## Project Structure

```
src/com/company/
└── Main.java          # Main calendar frame + logic

resources/
├── cal.jpg            # Header image
└── img.png            # Application icon

Calendar.jar           # Compiled executable
README.md
```

---

## Getting Started

**Requirements:**
- Java 8+ (JRE or JDK)

**Run the JAR:**

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Java projects\Calendar"

# Run directly
java -jar Calendar.jar
```

**Or compile and run from source:**

```bash
# Compile
javac -d bin src/com/company/Main.java

# Run
java -cp bin com.company.Main
```

**Usage:**
1. Launch application
2. Current month displays automatically
3. Click **←** (left arrow) to go to previous month
4. Click **→** (right arrow) to go to next month
5. Month/year updates in header label

---

## What's Next

Potential improvements for future versions:

- **Event Creation** - Click dates to add events/reminders
- **Event Storage** - Save events to file or SQLite database
- **Color Coding** - Different colors for holidays, birthdays, work events
- **Multi-Month View** - Show 3 or 6 months at once
- **Date Picker Component** - Reusable widget for other applications
- **Holiday Integration** - Automatically mark national/religious holidays
- **Week Numbers** - Display ISO week numbers in left margin
- **Export to ICS** - Export events to iCalendar format for import into Outlook/Google Calendar

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
