# Digital Clock

<div align="center">

![Java](https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/Swing-GUI-red?style=for-the-badge&logo=java)
![Graphics2D](https://img.shields.io/badge/Graphics2D-Rendering-blue?style=for-the-badge)

**Analog clock with real-time display and hourly chimes. Classic desktop clock that actually looks good.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A beautifully rendered analog clock with moving hands, complete with hourly sound notifications. Perfect for keeping time on your desktop with style.

**Clock Features:**
- **Real-Time Display** - Second, minute, and hour hands update in real-time
- **Analog Design** - Traditional circular clock face with numbers
- **Smooth Animation** - Hour/minute hands move continuously (not in discrete jumps)
- **Date Display** - Current date shown in title bar (DD.MM.YYYY format)
- **12-Hour Format** - Standard 12-hour clock display

**Sound Features:**
- **Hourly Chime** - System beep plays every hour on the hour
- **Toggle Sound** - Menu option to turn sound on/off
- **Status Display** - Title bar shows "Sound ON" or "Sound OFF"

**UI Features:**
- Clean white clock face with black hands
- Minimal, distraction-free design
- Fixed-size window (510×555 pixels)
- Menu bar for sound control
- System tray compatible

---

## Tech Stack

**Frontend:** Java Swing (JFrame, JMenuBar, Custom JPanel)  
**Graphics:** Java2D (Graphics2D, AffineTransform)  
**Backend:** Java 8 + java.util.Timer + SimpleDateFormat  
**Audio:** java.awt.Toolkit (system beep)  
**Build:** Java Compiler (javac)

### Architecture

Thread-based animation with custom rendering:

```
Clock1.java (JFrame + Main Loop)
      ↓
ClockDial.java (Custom JPanel)
      ↓
paintComponent(Graphics g) → Graphics2D
      ↓
Draw Clock Face (circle, numbers)
      ↓
Calculate Hand Angles (hour, minute, second)
      ↓
Rotate & Draw Hands (AffineTransform)
      ↑
Thread (1-second update loop)
      ↑
Timer (hourly beep check)
```

**Rendering Implementation:**
- **Graphics2D:** Used for anti-aliased drawing, rotation, and smooth lines
- **AffineTransform:** Rotates graphics context to draw clock hands at correct angles
- **Thread-Based Animation:** Separate thread updates seconds/minutes/hours every 1000ms
- **Timer Task:** Java Timer checks every second if it's top of the hour for beep
- **SimpleDateFormat:** Formats date for title bar display

**Hand Angle Calculations:**
```java
// Second hand: 6 degrees per second (360° / 60 seconds)
secondAngle = seconds * 6;

// Minute hand: 6 degrees per minute + small offset for seconds
minuteAngle = minutes * 6 + seconds * 0.1;

// Hour hand: 30 degrees per hour + offset for minutes
hourAngle = hours * 30 + minutes * 0.5;
```

---

## Project Structure

```
src/com/company/
├── Clock1.java        # Main JFrame + timing logic
└── ClockDial.java     # Custom JPanel with clock rendering

resources/
├── alarm.png          # Window icon
└── clockAnimation.gif # Demo animation

Clock.exe              # Windows executable
README.md
```

---

## Getting Started

**Requirements:**
- Java 8+ (JRE or JDK)

**Run the EXE (Windows):**

```bash
cd "d:\Personal-Projects\Java projects\Digital Clock"
.\Clock.exe
```

**Or compile and run from source:**

```bash
# Compile both classes
javac -d bin src/com/company/Clock1.java src/com/company/ClockDial.java

# Run main class
java -cp bin com.company.Clock1
```

**Controls:**
- **Menu → Turn off the sound** - Disables hourly chime
- **Menu → Turn on the sound** - Re-enables hourly chime
- Time updates automatically - no user input needed
- Close window to exit

---

## What's Next

Potential enhancements:

- **Customizable Chimes** - Choose different sounds (bell, beep, custom WAV)
- **Alarm Clock** - Set alarm for specific time
- **12/24 Hour Toggle** - Switch between 12-hour and 24-hour display
- **Themes** - Different clock face designs (modern, classic, minimal)
- **Time Zone Support** - Display clocks for multiple time zones simultaneously
- **Stopwatch Mode** - Toggle to stopwatch with start/stop/lap buttons
- **Timer Mode** - Countdown timer with notification
- **Transparency** - Make clock window semi-transparent
- **Always On Top** - Keep clock above other windows
- **Customizable Colors** - Choose clock face, hand, and number colors
- **Second Hand Smoothing** - Smooth sweep second hand instead of ticking

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
