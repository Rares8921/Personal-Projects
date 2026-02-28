# CalendarFX

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![CSS](https://img.shields.io/badge/CSS3-Styled-1572B6?style=for-the-badge&logo=css3)

**Modern calendar with JavaFX. Event scheduling, drag-and-drop, and recurring events in a beautiful UI.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

CalendarFX is a feature-rich desktop calendar built with JavaFX, offering event management with modern UI features like drag-and-drop rescheduling and visual customization.

**Event Management:**
- **Create Events** - Click any date cell to add new events
- **Edit Events** - Double-click existing events to modify details
- **Drag-and-Drop** - Move events between dates by dragging
- **Recurring Events** - Set daily, weekly, or monthly repeats
- **Event Reminders** - Pop-up notifications for upcoming events
- **Multi-Day Events** - Span events across multiple days

**UI Features:**
- **Month Grid View** - Clean calendar layout with custom-styled date cells
- **Today Highlighting** - Current date visually distinguished
- **Custom Styling** - CSS-based themes with hover effects
- **Smooth Animations** - JavaFX transitions for UI updates
- **Undecorated Window** - Custom title bar with minimize/close controls
- **Responsive Layout** - GridPane-based layout adapts to window size

---

## Tech Stack

**Frontend:** JavaFX 11 + FXML + CSS3  
**Backend:** Java 11 + `java.time` API (YearMonth, LocalDate)  
**Build:** Maven/Gradle compatible  
**UI Components:** GridPane, AnchorPane, Tooltip, Custom CalendarView

### Architecture

Component-based JavaFX application with custom view logic:

```
Main.java (Application)
      ↓
sample.fxml (Layout)
      ↓
Controller.java (Event Handlers)
      ↓
CalendarView.java (Custom Component)
      ↓
AnchorPaneNode.java (Date Cell Renderer)
      ↓
YearMonth (java.time API)
```

**Key Implementation:**
- **CalendarView Component:** Custom JavaFX component that generates month grid
- **AnchorPaneNode:** Individual date cells with click/drag event handling
- **YearMonth API:** Java 8+ time library for accurate date calculations
- **FXML Binding:** Controller methods bound to UI elements via @FXML
- **CSS Styling:** External stylesheet (`calendar.css`) for theming
- **Observable Properties:** JavaFX properties for reactive UI updates

---

## Project Structure

```
src/sample/
├── Main.java               # JavaFX Application entry
├── Controller.java         # Main controller logic
├── CalendarView.java       # Custom calendar grid component
├── AnchorPaneNode.java     # Date cell UI + event handling
├── Open.java               # Utility class
├── sample.fxml             # Main window layout
├── styles.css              # Application-wide styles
└── calendar.css            # Calendar-specific styles

resources/
├── calendar.png            # App icon (PNG)
├── calendar.ico            # Windows icon
└── Calendar.exe            # Packaged executable

README.md
```

---

## Getting Started

**Requirements:**
- Java 11+ with JavaFX
- Maven (optional)

**Run from source:**

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Java projects\CalendarFX"

# Run with JavaFX modules
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     sample.Main
```

**Or run the EXE (Windows):**

```bash
.\Calendar.exe
```

**Usage:**
1. Launch CalendarFX
2. Calendar displays current month
3. **Click date cell** to create new event
4. **Double-click event** to edit details
5. **Drag event** to different date to reschedule
6. **Navigation arrows** to change months
7. **Today button** to return to current month

---

## What's Next

Future enhancements to expand functionality:

- **Event Categories** - Color-coded tags (work, personal, birthdays)
- **Week/Day View** - Alternative views beyond monthly grid
- **Cloud Sync** - Google Calendar / Outlook integration via API
- **Search & Filter** - Find events by keyword, date range, or tag
- **Import/Export** - ICS file support for cross-platform compatibility
- **Shared Calendars** - Multiple calendar layers (personal, family, work)
- **Notifications System** - Desktop notifications 15 min before events
- **Event Attachments** - Link files, URLs, or notes to events
- **Time Zone Support** - Handle events across different time zones

---

## 📹 Demo Video

> **Recording Instructions:** A 3-5 minute walkthrough showcasing the key features of this project.

### What to Demonstrate

**Suggested Timeline:**
- **0:00-0:30** - Project overview and startup
- **0:30-2:00** - Core features demonstration
- **2:00-3:30** - Advanced features and interactions
- **3:30-5:00** - Edge cases and wrap-up

### Features to Showcase

- **Event Creation** - Click date cells to add new events
- **Drag-and-Drop Rescheduling** - Move events between dates
- **Event Editing** - Double-click to modify event details
- **Recurring Events** - Set up daily/weekly/monthly repeats
- **Calendar Navigation** - Switch between months, today highlighting
- **Custom Styling** - Show the CSS theming and hover effects

### Recording Setup

**Prerequisites:**
```bash
# Ensure JavaFX 11+ is installed
# Build the project or run from IDE
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `java -jar CalendarFX.jar` or run from IDE
2. Open OBS Studio and set up screen capture
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Java projects\CalendarFX"
java -jar CalendarFX.jar

# Or run Calendar.exe (if packaged)
start Calendar.exe
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
