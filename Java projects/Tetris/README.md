# Tetris

<div align="center">

![Java](https://img.shields.io/badge/Java-JavaFX-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue?style=for-the-badge&logo=java)
![Game](https://img.shields.io/badge/Game-Puzzle-green?style=for-the-badge&logo=gamepad)

**The classic Tetris game, rebuilt from scratch with JavaFX. Seven tetromino shapes, line clearing, increasing speed, and a polished game loop. Drop, rotate, and clear lines.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a full implementation of Tetris, built with JavaFX. Control falling tetrominoes (the iconic 7 shapes), rotate them, and clear lines to score points. The game speeds up as you progress, testing your reflexes and spatial reasoning.

**Core features:**
- **7 classic tetromino shapes** (I, O, T, S, Z, J, L) with accurate rotation mechanics
- **Line clearing**: Complete horizontal lines to score points and clear space
- **Increasing difficulty**: Game speed increases as you clear more lines
- **Next piece preview**: See what's coming next to plan ahead
- **Score and line tracking**: Real-time display of score and lines cleared
- **2D grid collision detection**: Accurate placement and rotation validation
- **Game over detection**: Ends when pieces stack to the top
- **Smooth animations** using JavaFX Timeline

**Gameplay mechanics:**
- Pieces fall automatically at timed intervals
- Arrow keys: Left/Right (move), Up (rotate), Down (drop faster)
- Pieces lock in place when they hit the bottom or another piece
- Completed lines are cleared and score increases
- Speed increases every N lines cleared
- Game over when new piece spawns at occupied position

**Why it's addictive:**
- Classic Tetris mechanics faithfully recreated
- Smooth controls and collision detection
- Progressive difficulty keeps it challenging
- Clean, minimalist UI with score/line display

The game uses a 2D integer array for the grid (12x24 cells), JavaFX Rectangles for rendering pieces, and a Timeline-based game loop for consistent timing.

---

## Tech Stack

**Language:** Java (JavaFX 17+)  
**UI Framework:** JavaFX (Pane, Scene, Rectangle, Text)  
**Data Structures:** 2D int array (grid state), Form class (tetromino shapes)  
**Game Loop:** JavaFX Timer + TimerTask  
**Collision Detection:** Grid boundary and occupied cell checking  
**Build:** Maven (with JavaFX dependencies)

### Architecture

Game loop with piece spawning, movement, rotation, line clearing, and rendering:

```
JavaFX Application
      ↓
Stage + Pane Setup (12x24 grid)
      ↓
Game Loop (Timer @ variable speed)
      ↓
┌──────────────┬──────────────┬───────────────┐
│   Input      │   Game       │   Rendering   │
│  Handler     │   Logic      │   Engine      │
└──────────────┴──────────────┴───────────────┘
      ↓              ↓               ↓
Key Events    Piece Movement   Rectangle Nodes
Rotation      Collision Det.   (JavaFX Shapes)
              Line Clearing
              Score Update
```

**How it works:**
- **Grid State**: 2D array `tetrisMatr[12][24]` stores occupied cells (0 = empty, N = color ID)
- **Form Class**: Represents tetromino shapes with 4 Rectangle nodes (a, b, c, d)
- **Movement**: Arrow keys trigger horizontal movement or rotation; validated against grid
- **Gravity**: Timer moves piece down every N milliseconds
- **Collision**: Checks if target cells are occupied or out of bounds
- **Line Clearing**: Scans for complete rows, removes them, shifts rows down
- **Next Piece**: Preview displayed on right side of grid

**Key Implementation Details:**
- **Tetromino Shapes**: Each shape defined by 4 Rectangle positions (e.g., I-piece, L-piece, T-piece)
- **Rotation Logic**: Calculates new positions for 4 blocks based on shape type and current rotation state
- **Line Detection**: Iterates through rows, checks if all columns are filled
- **Speed Increase**: Timer interval decreases as `numberOfLines` increases
- **Game Over**: Detected when new piece spawns at Y=0 and position is occupied twice consecutively

---

## Project Structure

```
Tetris/
├── src/
│   └── main/
│       └── java/
│           └── com/example/tetris/
│               ├── Tetris.java     # Main game loop + rendering
│               ├── Controller.java # Piece factory + rotation logic
│               └── Form.java       # Tetromino shape class
├── pom.xml                         # Maven dependencies
└── README.md
```

**Main components:**
- `Tetris.java`: Game loop, grid management, line clearing, scoring
- `Controller.java`: Tetromino factory (`makeObject()`), rotation algorithms
- `Form.java`: Tetromino representation (4 Rectangle nodes + color)
- Grid: 12 columns × 24 rows (each cell = 25px)

---

## Getting Started

### Prerequisites

- **Java 17+** with JavaFX SDK
- Maven (for building from source)

### Running the Game

**Option 1: Maven**
```bash
# Compile and run:
mvn clean javafx:run
```

**Option 2: From JAR**
```bash
# If JAR is provided:
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -jar Tetris.jar
```

**Option 3: From Source**
```bash
# Compile manually:
javac --module-path /path/to/javafx/lib --add-modules javafx.controls -d bin src/main/java/com/example/tetris/*.java

# Run:
java --module-path /path/to/javafx/lib --add-modules javafx.controls -cp bin com.example.tetris.Tetris
```

### How to Play

1. **Start**: Run the application—the first piece falls automatically
2. **Move**: Use Left/Right arrow keys to move piece horizontally
3. **Rotate**: Press Up arrow to rotate piece clockwise
4. **Drop faster**: Hold Down arrow to speed up descent
5. **Clear lines**: Fill horizontal rows completely to clear them
6. **Goal**: Score as many points as possible before game over

**Controls:**
- **Left/Right Arrow**: Move piece horizontally
- **Up Arrow**: Rotate piece
- **Down Arrow**: Drop piece faster (soft drop)

**Scoring:**
- Each line cleared: +100 points
- Faster drop (manual): +10 points per cell
- Game over when pieces reach the top

---

## What's Next

**Potential improvements:**
- Add hard drop (instant drop to bottom)
- Implement hold piece feature
- Add T-spin detection for bonus points
- Ghost piece (shows where piece will land)
- Level progression with defined level thresholds
- High score persistence
- Sound effects (line clear, game over, rotation)
- Multiplayer mode (side-by-side or competitive)

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
