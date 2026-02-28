# Snake

<div align="center">

![Java](https://img.shields.io/badge/Java-JavaFX-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue?style=for-the-badge&logo=java)
![Game](https://img.shields.io/badge/Game-Classic-green?style=for-the-badge&logo=gamepad)

**The classic Snake game with a twist—9 different food types, increasing difficulty, and a polished JavaFX interface. Eat, grow, and try not to bite yourself.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a modern take on the classic Snake game, built with JavaFX. Control a snake as it moves around the canvas, eating food to grow longer. But be careful—hitting walls or your own body ends the game. Speed increases as your score climbs, making it progressively harder.

**Core features:**
- **WASD + Arrow key controls** for smooth snake movement
- **9 unique food types** with different graphics (apple, cherry, berry, coconut, peach, watermelon, etc.)
- **Growing snake body** using ArrayList of Points
- **Score tracking** with persistent high score storage
- **Increasing speed**: Game gets faster as score increases
- **Pause functionality**: Spacebar to pause/resume
- **Collision detection**: Walls, self-collision, and food collection
- **Custom FXML UI** with styled buttons and menu

**Gameplay mechanics:**
- Snake starts with length 1 and grows by 1 segment per food
- Food spawns randomly on the 20x20 grid
- Direction changes instantly on key press (no queued inputs)
- Speed increases every X points
- High score persists across sessions

**Why it's engaging:**
- Smooth 60 FPS animation with JavaFX Timeline
- Varied food graphics keep it visually interesting
- Progressive difficulty makes it challenging
- Simple, intuitive controls

The game uses JavaFX Canvas for rendering, ArrayList for the snake body, and a Timeline-based game loop for consistent frame timing.

---

## Tech Stack

**Language:** Java (JavaFX 17+)  
**UI Framework:** JavaFX (Canvas, FXML, CSS)  
**Data Structures:** ArrayList (snake body), Point (coordinates)  
**Game Loop:** JavaFX Timeline with 120ms intervals  
**Persistence:** File I/O for high score storage  
**Build:** Maven/Gradle (modular project)

### Architecture

Game loop with collision detection, rendering, and state management:

```
JavaFX Application
      ↓
FXML UI + Canvas Setup
      ↓
Game Loop (Timeline @ ~8 FPS)
      ↓
┌──────────────┬──────────────┬───────────────┐
│   Input      │   Game       │   Rendering   │
│  Handler     │   Logic      │   Engine      │
└──────────────┴──────────────┴───────────────┘
      ↓              ↓               ↓
Key Events    Snake Movement   GraphicsContext
Direction     Food Collision   (Canvas Drawing)
              Self Collision
              Wall Collision
              Score Update
```

**How it works:**
- **Snake Body**: ArrayList of Points, where head is at index 0
- **Movement**: Each frame, new head position is calculated based on direction; tail is removed (unless food eaten)
- **Food**: Random (x, y) coordinates; if head collides, snake grows and new food spawns
- **Collision Detection**: Check if head hits walls (x < 0 || x >= 20) or any body segment
- **Speed**: Timeline interval decreases as score increases
- **High Score**: Written to file on game over, read on game start

**Key Implementation Details:**
- **Grid System**: 20x20 grid, each cell is 40px (800x800 canvas)
- **Direction Control**: Integer constants (RIGHT=0, LEFT=1, UP=2, DOWN=3)
- **Food Images**: Array of 9 PNG paths loaded dynamically
- **Pause Logic**: Boolean flag stops Timeline when spacebar is pressed
- **Score Calculation**: +10 per food item; displayed in real-time

---

## Project Structure

```
Snake/
├── Controller.java             # FXML controller for UI interactions
├── Main.java                   # Game logic, rendering, collision detection
├── Open.java                   # Initial splash screen or menu
├── sample.fxml                 # FXML layout for UI
├── snake.css                   # Custom CSS styling
├── imgs/                       # Food images (apple, cherry, berry, etc.)
│   ├── orange.png
│   ├── apple.png
│   ├── cherry.png
│   ├── berry.png
│   ├── coconut.png
│   ├── peach.png
│   ├── watermelon.png
│   ├── tomato.png
│   └── pomegranate.png
└── Snake.exe                   # Compiled executable
```

**Main components:**
- `Main.java`: Game loop, snake movement, collision detection, rendering
- `Controller.java`: Handles FXML button clicks (start, pause, restart)
- Snake body: ArrayList of Points (head at index 0)
- Food system: Random generation with 9 different sprites

---

## Getting Started

### Prerequisites

- **Java 17+** with JavaFX SDK
- Maven or Gradle (optional, for building from source)

### Running the Game

**Option 1: Executable**
```bash
# Windows:
.\Snake.exe

# Or run from source:
javac Main.java
java sample.Main
```

**Option 2: Maven**
```bash
mvn clean javafx:run
```

**Option 3: From JAR**
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar Snake.jar
```

### How to Play

1. **Start**: Click the "Start" button or press Enter
2. **Move**: Use Arrow keys or WASD to change direction
3. **Goal**: Eat food to grow your snake and increase score
4. **Avoid**: Don't hit walls or your own body
5. **Pause**: Press Spacebar to pause/resume

**Scoring:**
- Each food item: +10 points
- High score is saved automatically
- Speed increases every 50 points

**Controls:**
- Arrow keys / WASD: Change direction
- Spacebar: Pause/Resume
- Enter: Start/Restart game

---

## What's Next

**Potential improvements:**
- Add difficulty levels (easy, medium, hard)
- Special food items (bonus points, speed boost, slow-down)
- Obstacles on the grid
- Multiplayer mode (two snakes on same grid)
- Level progression (walls, barriers, smaller grids)
- Sound effects (eating, collision, level up)
- Leaderboard with top 10 scores

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
