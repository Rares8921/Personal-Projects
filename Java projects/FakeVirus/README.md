# FakeVirus

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![Media](https://img.shields.io/badge/JavaFX-Media-red?style=for-the-badge)

**Harmless prank program that simulates virus behavior. For educational purposes and pranks only.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A prank application that simulates alarming virus-like behavior without actually causing any harm. Perfect for harmless pranks on friends or demonstrating what "suspicious" software might look like.

**Prank Features:**
- **Fullscreen Takeover** - App opens in fullscreen mode, takes over display
- **Undecorated Window** - No title bar or close button visible
- **Always On Top** - Window stays above all other applications
- **Looping Video** - Plays continuous video content (customizable)
- **Continuous Beeping** - System beep plays in infinite loop
- **Close Prevention** - Standard close attempts are blocked
- **Minimize Prevention** - Attempts to minimize are caught and reversed
- **Fake System Icon** - Uses custom icon to appear system-related

**Safety Note:**
This is a harmless prank program. It does NOT:
- Modify system files
- Delete data
- Install actual malware
- Connect to networks
- Access personal information

The app can be closed via Task Manager (Ctrl+Alt+Del → End Task).

---

## Tech Stack

**Frontend:** JavaFX 11 + Custom Window Styling  
**Media:** JavaFX Media API (MediaPlayer, MediaView)  
**Backend:** Java 11 + Threading (Runnable)  
**Audio:** java.awt.Toolkit (system beep)  
**Build:** Maven/Gradle compatible

### Architecture

Simple full-screen media player with event blocking:

```
Main.java (Application + Runnable)
      ↓
MediaPlayer (looping video)
      ↓
MediaView (fullscreen display)
      ↓
Pane (black background)
      ↓
Event Handlers:
  ├── Close Request (blocked)
  ├── Iconified Property (reversed)
  └── Always On Top (enforced)
      ↓
Thread (continuous beeping)
```

**Implementation Details:**
- **MediaPlayer:** JavaFX MediaPlayer set to infinite loop (`setCycleCount(INDEFINITE)`)
- **Window Control:** `StageStyle.UNDECORATED` removes all window controls
- **Event Blocking:** `setOnCloseRequest(Event::consume)` prevents normal closure
- **Minimize Detection:** PropertyListener on `iconifiedProperty()` restores window
- **Background Thread:** Separate thread runs infinite beep loop via Toolkit
- **Auto-Position:** `setMaximized(true)` fills entire screen

---

## Project Structure

```
src/sample/
├── Main.java           # Application + prank logic
└── Open.java           # Utility class

resources/
├── video.mp4           # Looping video content
├── rick.png            # Window icon
└── zelda.ico           # Executable icon

README.md
```

---

## Getting Started

**Requirements:**
- Java 11+ with JavaFX
- JavaFX Media modules

**Build & Run:**

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Java projects\FakeVirus"

# Run with JavaFX + Media modules
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.media \
     sample.Main
```

**Or compile from IDE:**
1. Open `Main.java` in IntelliJ IDEA
2. Ensure JavaFX is configured
3. Run application

**To Close the Prank:**
- **Windows:** Ctrl+Alt+Del → Task Manager → End Task
- **macOS:** Cmd+Option+Esc → Force Quit
- **Linux:** System Monitor → Kill Process

**Customization:**
Replace `video.mp4` in resources folder with your own video for custom prank content.

---

## What's Next

Potential prank enhancements (use responsibly):

- **Timed Auto-Close** - Automatically close after X seconds
- **Password Exit** - Add secret key combination to close (e.g., Ctrl+Shift+Q)
- **Multiple Screens** - Spawn windows on all connected monitors
- **Fake Progress Bars** - Display fake "deleting system32" progress
- **Custom Messages** - Show fake error dialogs or warnings
- **Screen Recording** - Capture victim's reaction (with consent)
- **Sound Variations** - Different sounds (sirens, alarms, explosions)
- **Animation Effects** - Screen shake, color inversion, glitch effects
- **Fake BSOD** - Display blue screen of death simulation (harmless)

**Educational Use Cases:**
- Demonstrate malicious software behavior in cybersecurity courses
- Teach users to recognize suspicious program behavior
- Practice incident response procedures
- Illustrate importance of Task Manager/Force Quit

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.

**Disclaimer:** This software is intended for educational and entertainment purposes only. The author is not responsible for misuse, harassment, or any damages resulting from use of this software. Always obtain consent before running prank software on someone else's device. Use responsibly and ethically.
