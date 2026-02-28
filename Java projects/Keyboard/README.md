# Keyboard

<div align="center">

![Java](https://img.shields.io/badge/Java-8-orange?style=for-the-badge&logo=java)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)
![Utility](https://img.shields.io/badge/Type-Virtual%20Keyboard-purple?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**A fully-functional virtual on-screen keyboard with real-time key press simulation and visual feedback for accessibility and testing.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

Keyboard is a virtual on-screen keyboard application that simulates physical keyboard input. It's useful for accessibility purposes, testing keyboard interactions, or when physical keyboard access is limited.

**Core features:**
- **Full QWERTY layout** - complete keyboard including function keys, numpad, and special keys
- **Real-time visual feedback** - keys light up when pressed on physical keyboard
- **Key press simulation** - clicking buttons simulates actual key presses (can type into other applications)
- **Comprehensive key support** - Esc, F1-F12, Print Screen, Scroll Lock, Pause, arrows, modifiers
- **Robot class integration** - generates native system key events
- **Customizable layout** - rounded buttons with modern styling

**What makes it special:**
- Mirrors physical keyboard state in real-time (press Space, see it highlighted)
- Can be used as input method for applications that accept keyboard input
- Dark theme (#1b1b1c background) with white text for modern appearance
- Hand cursor on hover for intuitive interaction
- Supports Tab key traversal (though focus management overridden for full control)
- ~100+ buttons covering entire keyboard surface

The application uses Java's `Robot` class to inject key events into the system, making clicks on the virtual keyboard indistinguishable from physical key presses. This is particularly useful for automated testing, accessibility features, or gaming overlays.

---

## Tech Stack

**Language:** Java 8  
**GUI Framework:** Swing (JFrame, JButton, JPanel)  
**Layout:** FlowLayout with manual positioning  
**Input Simulation:** java.awt.Robot (key press/release)  
**Event Handling:** KeyListener for physical keyboard, ActionListener for buttons  
**Styling:** Custom RoundedBorder class for button aesthetics  
**Build:** Standard javac compilation

### Architecture

Event-driven GUI with bidirectional keyboard synchronization:

```
Main (extends JFrame)
      ↓
┌─────────────────────────────────┐
│   Virtual Keyboard Grid         │
│   - 100+ JButton instances      │
│   - RoundedBorder styling       │
│   - Static references           │
└─────────────────────────────────┘
      ↓
Event Handlers:
  ├─ KeyListener (MyKeyListener)
  │    ├─ keyPressed() → highlight button
  │    └─ keyReleased() → unhighlight button
  │
  └─ ActionListener (per button)
       └─ Robot.keyPress(keyCode)
           Robot.keyRelease(keyCode)
```

**Key Simulation Mechanism:**
```java
Robot robot = new Robot();
robot.keyPress(KeyEvent.VK_A);    // Press A key down
robot.keyRelease(KeyEvent.VK_A);  // Release A key
// Other applications receive the 'A' keystroke
```

**Visual Feedback System:**
- Physical key press → KeyListener detects → Button enabled state toggles → Color/style changes
- Virtual key click → Robot sends event → System processes → Other apps receive input

**Key Implementation Details:**
- **Button Mapping:** Static JButton references for each key (escape, F1-F12, letters, numbers, etc.)
- **KeyListener:** Attached to JPanel, listens for all physical keyboard events
- **Highlighting:** Pressed keys become enabled (white text), released keys disabled (gray text)
- **Robot Usage:** Creates system-level keyboard events that work across all applications
- **Focus Management:** Panel requests focus, disables tab traversal to capture all keys
- **Rounded Borders:** Custom Border implementation with configurable radius (10px)

**Full Key Coverage:**
- **Function Row:** Esc, F1-F12, PrtSc, ScrLk, Pause
- **Number Row:** ` 1 2 3 4 5 6 7 8 9 0 - = Backspace
- **Top Row:** Tab Q W E R T Y U I O P [ ] \
- **Middle Row:** Caps A S D F G H J K L ; ' Enter
- **Bottom Row:** Shift Z X C V B N M , . / Shift
- **Modifiers:** Ctrl, Windows, Alt, Space, Fn, Context Menu, Arrows
- **Navigation:** Ins, Home, PgUp, Del, End, PgDn

---

## Project Structure

```
Keyboard/
├── Main.java                    # Application + keyboard layout (1400+ lines)
├── RoundedBorder.java          # Custom border styling (embedded in Main)
├── MyKeyListener.java          # Physical keyboard event handler (embedded)
├── Keyboard.exe                # Windows executable wrapper
└── README.md
```

---

## Getting Started

**Requirements:**
- Java 8 or higher
- Operating system with AWT/Robot support (Windows, macOS, Linux)

**Run the application:**

```bash
# Option 1: Windows executable
Keyboard.exe

# Option 2: Compile and run Java
javac Main.java
java com.company.Main
```

**Using the virtual keyboard:**

1. **Visual Feedback:**
   - Press keys on your physical keyboard
   - Watch corresponding buttons light up on-screen
   - Helps verify which keys are being pressed

2. **Virtual Input:**
   - Click any button on the virtual keyboard
   - That key press is sent to the system
   - Switches to another app and type using virtual keyboard

3. **Testing:**
   - Open Notepad or any text editor
   - Click keys on virtual keyboard
   - Text appears in the target application

**Important notes:**
- Requires Robot class permissions (may need to run as administrator on some systems)
- Key simulation works system-wide, not limited to the keyboard app
- Some keys like Windows key behavior may vary by OS

---

## What's Next

Future enhancements under consideration:
- Compact layout mode (without numpad)
- Customizable key bindings
- Macro recording (key sequences)
- Opacity control for overlay usage
- Always-on-top option
- Click sound effects for tactile feedback
- Alternative layouts (DVORAK, AZERTY, etc.)
- Hotkeys to show/hide keyboard
- Multi-language support with character maps

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
