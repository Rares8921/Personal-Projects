# AutoClicker

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-Latest-blue?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven)

**Precision auto-clicking tool with configurable intervals and hotkey control. No more manual spam-clicking.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

AutoClicker automates mouse clicking at configurable speeds with global hotkey support. Perfect for repetitive tasks, gaming macros, or testing applications that require rapid clicking.

**Core Features:**
- **Configurable CPS** - Adjust clicks-per-second from 1 to 100+
- **Global Hotkeys** - Start/stop clicking without focusing the app (F6 toggle, F8 exit)
- **Position Locking** - Lock click position or follow mouse cursor
- **Multi-Button Support** - Left click, right click, or both simultaneously
- **Background Operation** - Runs minimized with system tray support
- **Native Hook Integration** - JNativeHook for global keyboard/mouse event capture

**How it works:**
- Set your desired CPS (clicks per second)
- Choose click type (left, right, or both)
- Position mode: fixed point or follow cursor
- Press hotkey to start - clicks happen in the background
- Press again to stop instantly

Built with JavaFX for a clean GUI and Robot class for precise mouse control.

---

## Tech Stack

**Frontend:** JavaFX + FXML + CSS  
**Backend:** Java 17 + Robot class (AWT) + JNativeHook  
**Build:** Maven  
**Libraries:** JNativeHook (global hotkeys), JavaFX Controls

### Architecture

Lightweight event-driven architecture with native system hooks:

```
JavaFX GUI (Controller)
      ↓
AutoClicker Core
      ↓
Timeline (JavaFX Animation) → Clicker Loop
      ↓
Robot API → Mouse Events
      ↑
JNativeHook (Global Listeners)
      ↓
Native OS Events (Keyboard/Mouse)
```

**Key Implementation:**
- **Robot Class:** Java AWT Robot for simulating mouse button press/release events
- **Timeline Animation:** JavaFX Timeline running at 1ms intervals for precise timing
- **Global Hooks:** JNativeHook captures hotkeys even when app is unfocused
- **Event Listeners:** AutoKeyListener (F6/F8 hotkeys) and AutoMouseListener (position tracking)
- **State Machine:** Toggle system prevents accidental multiple activations

---

## Project Structure

```
src/main/java/sample/autoclicker/
├── Main.java                # JavaFX application entry
├── AutoClicker.java         # Core clicking logic + Timeline
├── HelloController.java     # GUI controller (FXML bindings)
├── AutoKeyListener.java     # Global keyboard hook handler
└── AutoMouseListener.java   # Global mouse hook handler

src/main/resources/sample/autoclicker/
├── hello-view.fxml          # JavaFX UI layout
├── styles.css               # Styling
└── appicon.jpg              # Application icon

pom.xml                      # Maven dependencies (JNativeHook, JavaFX)
```

---

## Getting Started

**Requirements:**
- Java 17+
- Maven 3.6+
- Administrator privileges (for global hooks on some systems)

**Build & Run:**

```bash
# Clone or navigate to project directory
cd "d:\Personal-Projects\Java projects\AutoClicker"

# Build with Maven
mvn clean install

# Run the application
mvn javafx:run
```

**Or run directly from IDE:**
1. Open `Main.java` in IntelliJ IDEA or Eclipse
2. Run as Java Application
3. Grant permissions if system prompts for input monitoring access

**Usage:**
1. Launch AutoClicker
2. Set CPS (clicks per second) using the slider
3. Choose click type: Left (1), Right (2), or Both
4. Select position mode: Follow cursor or fixed position
5. Press **F6** to toggle clicking on/off
6. Press **F8** to exit application

**Note:** On macOS/Linux, you may need to grant Accessibility permissions for global hotkeys to work.

---

## What's Next

Potential enhancements for future releases:

- **Click Patterns** - Record and replay complex clicking sequences
- **Conditional Clicking** - Stop after X clicks or Y seconds
- **Color Detection** - Click only when specific pixel colors appear
- **Multiple Profiles** - Save/load different CPS and position configurations
- **Randomization** - Add slight CPS variance to avoid detection in anti-cheat systems
- **Scripting Support** - Lua/JavaScript integration for advanced automation
- **Click Heatmap** - Visualize click distribution across screen

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
