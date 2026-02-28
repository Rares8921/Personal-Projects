# Pong

<div align="center">

![Java](https://img.shields.io/badge/Java-JavaFX-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue?style=for-the-badge&logo=java)
![Game](https://img.shields.io/badge/Game-Classic-green?style=for-the-badge&logo=gamepad)

**The timeless Pong game, rebuilt with JavaFX. Play against an adaptive AI that tracks the ball, with smooth physics and a clean dark interface.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a faithful recreation of the classic Pong game, built with JavaFX. One player (you) controls the left paddle with the mouse, while an AI opponent controls the right paddle. The AI tracks the ball's position and adjusts its paddle intelligently.

**Core features:**
- **Mouse-controlled gameplay**: Move your paddle by moving the mouse vertically
- **AI opponent** that tracks ball position and adjusts paddle dynamically
- **Ball physics**: Speed increases with each paddle hit, making the game progressively harder
- **Score tracking**: Points awarded when opponent misses the ball
- **Smooth animations** using JavaFX Timeline (60 FPS)
- **Minimalist dark UI** with custom window controls

**Gameplay mechanics:**
- Ball bounces off top and bottom walls
- Ball speed increases after each successful paddle hit
- AI difficulty adapts based on ball position
- Game resets after each point
- Random ball direction on restart

**Why it's fun:**
- Classic arcade feel with modern rendering
- Progressive difficulty (ball gets faster)
- Responsive mouse controls
- Clean, distraction-free visuals

The game uses JavaFX Canvas for rendering and Timeline for the game loop, ensuring smooth 60 FPS animation with accurate collision detection.

---

## Tech Stack

**Language:** Java (JavaFX 17+)  
**UI Framework:** JavaFX (Canvas, GraphicsContext)  
**Game Loop:** JavaFX Timeline with KeyFrame  
**Physics:** Custom ball physics, collision detection  
**Build:** Maven/Gradle (module-based project)

### Architecture

Classic game loop architecture with rendering, physics, and input handling:

```
JavaFX Application
      ↓
Stage + Canvas Setup
      ↓
Game Loop (Timeline @ 60 FPS)
      ↓
┌─────────────┬─────────────┬──────────────┐
│   Input     │   Physics   │   Rendering  │
│  Handler    │   Engine    │   Engine     │
└─────────────┴─────────────┴──────────────┘
      ↓             ↓              ↓
Mouse Events   Ball Movement   GraphicsContext
               Collision Det.   (Canvas Drawing)
               AI Logic
```

**How it works:**
- **Game Loop**: Timeline fires every 10ms, calling `run(gc)` method
- **Input**: Mouse movement updates player paddle Y position
- **AI Logic**: AI paddle follows ball Y position with slight delay
- **Physics**: Ball position updated each frame; collision detection with paddles and walls
- **Rendering**: GraphicsContext draws paddles, ball, scores, and UI elements
- **Scoring**: When ball crosses left/right boundary, update score and reset

**Key Implementation Details:**
- **Ball Speed**: Stored in `ballXSpeed` and `ballYSpeed`; increases by 1 on each paddle collision
- **Collision Detection**: Checks if ball intersects paddle rectangles using boundary conditions
- **AI Behavior**: If ball is in left 75% of screen, AI tracks ball Y position; otherwise, moves toward center
- **Physics**: Ball direction reverses on wall/paddle collision; speed multiplied by -1
- **Custom Window**: Undecorated stage with custom minimize/close buttons

---

## Project Structure

```
Pong/
├── main/
│   └── java/
│       └── com/example/pong/
│           └── Pong.java       # Main game logic + JavaFX application
├── module-info.java            # Java module descriptor
└── README.md
```

**Main components:**
- `Pong.java`: Game loop, rendering, physics, AI, input handling
- Ball physics: Position, speed, collision detection
- Paddle control: Player (mouse) + AI (ball tracking)
- Score system: Player 1 vs Player 2 (AI)

---

## Getting Started

### Prerequisites

- **Java 17+** with JavaFX SDK
- Maven or Gradle (if building from source)

### Running the Game

**Option 1: Maven**
```bash
# Compile and run:
mvn clean javafx:run
```

**Option 2: Gradle**
```bash
# Run with Gradle:
gradle run
```

**Option 3: From JAR**
```bash
# If a JAR is provided:
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar Pong.jar
```

### How to Play

1. **Start the game**: Click anywhere on the canvas to begin
2. **Control your paddle**: Move the mouse up and down
3. **Score points**: Make the AI miss the ball
4. **Watch out**: Ball speeds up with each hit!

**Scoring:**
- Left paddle (you) scores when ball passes right boundary
- Right paddle (AI) scores when ball passes left boundary
- First to... well, there's no limit. Just keep playing!

---

## What's Next

**Potential improvements:**
- Add two-player mode (keyboard controls for second player)
- Difficulty selector (easy, medium, hard AI)
- Sound effects (ball hit, score, wall bounce)
- Power-ups (faster ball, larger paddle, slow-motion)
- High score tracking with persistent storage
- Pause menu
- Visual effects (particle trails, glow effects)

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

- **Mouse-Controlled Gameplay** - Show smooth paddle control with mouse
- **AI Opponent** - Demonstrate AI tracking ball and responding
- **Ball Physics** - Show bouncing, speed increase with hits
- **Score System** - Display point accumulation for both players
- **Progressive Difficulty** - Ball gets faster after each paddle hit
- **Game Reset** - Show automatic reset after each point

### Recording Setup

**Prerequisites:**
```bash
# Ensure JavaFX 17+ is installed
# Build with Maven or run from IDE
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `java -jar Pong.jar` or run Main class
2. Open OBS Studio and set up screen capture
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Java projects\Pong"
java -jar Pong.jar

# Or with Maven
mvn javafx:run
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
