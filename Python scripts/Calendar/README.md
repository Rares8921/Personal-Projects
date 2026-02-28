# Calendar

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Calendar](https://img.shields.io/badge/Module-calendar-green?style=for-the-badge)

**Terminal-based calendar viewer with ANSI color formatting. Check any month of any year instantly.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a minimalist calendar application that displays monthly calendars directly in your terminal. Input any year and month, and it renders a formatted calendar with proper day alignment.

**Features:**
- Display any month from any year (supports full datetime range)
- Green ANSI color output for better visibility
- Proper week alignment (Monday-Sunday)
- Uses Python's built-in `calendar` module for accuracy
- Interactive CLI interface

**Why it's useful:**
- Quick date lookups without opening external apps
- Works entirely in terminal (perfect for SSH sessions)
- No dependencies beyond standard library
- Lightweight and fast

---

## Tech Stack

**Language:** Python 3.7+  
**Modules:** calendar (standard library), ANSI escape codes

### Architecture

```
User Input (year, month)
         ↓
   Input Validation
         ↓
calendar.month() Function
(calculates day alignment)
         ↓
  ANSI Color Formatting
         ↓
Terminal Output (green text)
```

**Implementation Details:**
- Uses `calendar.month(year, month)` for accurate calendar generation
- ANSI escape code `\033[92m` for green terminal text
- Integer input conversion with `int()`
- Month validation: 1-12 range expected
- Year support: full Python datetime range

---

## Project Structure

```
Calendar/
├── cal.py               # Main calendar display logic
├── calendar.jpg         # Output screenshot
└── README.md
```

**Single-file implementation (10 lines):**
- Line 1: Shebang for Python 3.7
- Line 3: Import calendar module
- Line 5: ANSI green color code definition
- Lines 7-8: User input (year and month)
- Line 10: Calendar rendering with color

---

## Getting Started

**Requirements:**
Python 3.7+ (no external dependencies)

**Run it:**

```bash
python cal.py
```

**Example interaction:**

```
Input the year: 2026
Input the month (from 1 to 12): 2

   February 2026
Mo Tu We Th Fr Sa Su
                   1
 2  3  4  5  6  7  8
 9 10 11 12 13 14 15
16 17 18 19 20 21 22
23 24 25 26 27 28
```

**Notes:**
- Month input must be 1-12 (January-December)
- Year can be any valid year (0-9999)
- Invalid inputs will raise ValueError exceptions

---

## What's Next

**Possible enhancements:**
- Add input validation and error handling
- Support full-year calendar display (12 months)
- Add event/reminder annotations
- Highlight current date
- Export calendar to text file
- Build GUI version with tkinter

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
