# Pacman

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![Game](https://img.shields.io/badge/Type-Arcade%20Game-yellow?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-3.6+-red?style=for-the-badge&logo=apachemaven)

**A faithful Pacman clone with intelligent ghost AI, power-ups, maze navigation, scoring system, and classic arcade gameplay.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

Pacman is a complete recreation of the iconic arcade game with authentic mechanics, intelligent ghost behaviors, and smooth JavaFX graphics. Navigate the maze, eat pellets, avoid ghosts, and rack up high scores in this nostalgic experience.

**Core gameplay:**
- **Maze navigation** - classic Pacman maze layout with walls and corridors
- **Pellet collection** - eat all pellets to complete the level
- **Ghost AI** - four ghosts with distinct personalities (chase, scatter, frightened modes)
- **Power-ups** - eat power pellets to temporarily turn ghosts blue and vulnerable
- **Score tracking** - points for pellets (10), power pellets (50), and ghosts (200/400/800/1600)
- **Lives system** - three lives, lose one per ghost collision

**Ghost AI behaviors:**
- **Chase mode** - ghosts actively pursue Pacman using pathfinding
- **Scatter mode** - ghosts retreat to corners (periodic behavior switch)
- **Frightened mode** - triggered by power pellets, ghosts flee randomly
- **Each ghost has unique targeting:** Blinky (direct chase), Pinky (ambush), Inky (complex), Clyde (proximity-based)

**What makes it authentic:**
- Faithful to original Pacman mechanics and timing
- Smooth sprite-based animations
- Ghost respawn system after being eaten
- Classic sound effects (wakka-wakka, ghost eaten, etc.)
- Level completion detection
- Game over and restart functionality

The game loop runs at constant frame rate, collision detection uses grid-based positioning, and ghost movement employs pathfinding algorithms to create challenging yet fair gameplay.

---

## Tech Stack

**Language:** Java 11  
**Framework:** JavaFX (Scene, Canvas, AnimationTimer)  
**Build Tool:** Maven (dependency management, packaging)  
**UI:** FXML for menus, Canvas for game rendering  
**Algorithms:** BFS/A* for ghost pathfinding, state machines for AI  
**Audio:** JavaFX MediaPlayer for sound effects  
**Project Structure:** Maven standard layout

### Architecture

Classic game loop with entity-component pattern:

```
Main (Application Launcher)
      ↓
Controller (Menu Logic)
      ↓
Game Loop (AnimationTimer - 60 FPS)
      ↓
┌─────────────────────────────────┐
│   Game State Manager            │
│   - Pacman entity               │
│   - 4 Ghost entities            │
│   - Maze grid (2D array)        │
│   - Score, lives, level         │
└─────────────────────────────────┘
      ↓
Systems:
  ├─ Input Handler (arrow keys)
  ├─ Movement System (grid-based)
  ├─ Collision Detection (pellets, ghosts, walls)
  ├─ Ghost AI State Machine
  │    ├─ Chase (pathfinding toward Pacman)
  │    ├─ Scatter (retreat to corners)
  │    └─ Frightened (random movement)
  ├─ Rendering (Canvas draw calls)
  └─ Audio System (sound effects)
```

**Ghost AI State Machine:**
```
Initial: SCATTER (7 seconds)
  ↓
CHASE (20 seconds)
  ↓
SCATTER (7 seconds)
  ↓
CHASE (20 seconds)
  ↓
... repeats

FRIGHTENED mode (triggered by power pellet):
  - Overrides current mode for 10 seconds
  - Ghosts turn blue, movement reverses
  - Collision = ghost eaten (returns to spawn)
  - After timeout, resume previous mode
```

**Pathfinding Algorithm (Chase Mode):**
```
For each ghost:
  target = calculateTarget(ghost_type, pacman_position)
  path = A_star(ghost_position, target)
  next_move = path[0]
  move_ghost(next_move)

Ghost-specific targets:
  - Blinky (Red): Pacman's current position
  - Pinky (Pink): 4 tiles ahead of Pacman's direction
  - Inky (Cyan): Vector projection based on Blinky
  - Clyde (Orange): Chase if far, scatter if near
```

**Key Implementation Details:**
- **Maze Representation:** 2D char array ('W'=wall, '.'=pellet, 'O'=power pellet, ' '=empty)
- **Grid-Based Movement:** Entities move tile-by-tile (not pixel-perfect), speeds controlled by frame skip
- **Collision Detection:** Grid position comparison (`pacman.x == ghost.x && pacman.y == ghost.y`)
- **Score Multiplier:** Ghost value doubles for each ghost eaten during single power-up (200→400→800→1600)
- **Canvas Rendering:** Double-buffered drawing with sprite sheets for animations
- **Sound Management:** Pre-loaded audio clips triggered by events (eat pellet, ghost collision, etc.)

---

## Project Structure

```
Pacman/
├── pom.xml                      # Maven project configuration
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/pacman/
│       │       ├── Main.java              # Application entry point
│       │       ├── Controller.java        # Menu controller
│       │       ├── Open.java             # Welcome screen
│       │       └── module-info.java      # Java module descriptor
│       └── resources/
│           ├── fxml files               # UI layouts
│           ├── sprites/                # Pacman, ghosts, maze tiles
│           ├── sounds/                 # Audio effects
│           └── fonts/                  # Custom fonts
└── target/
    └── Pacman.jar                      # Compiled executable
```

---

## Getting Started

**Requirements:**
- Java 11 or higher
- Maven 3.6+
- JavaFX SDK 11+

**Build and run:**

```bash
# Option 1: Maven build and run
mvn clean install
mvn javafx:run

# Option 2: Run compiled JAR
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     -jar target/Pacman.jar

# Option 3: IDE (IntelliJ IDEA recommended)
# 1. Import as Maven project
# 2. Configure JavaFX in project settings
# 3. Run Main.java
```

**How to play:**

1. **Start game** - launch application, click "Play"
2. **Controls:**
   - **Arrow Keys** - move Pacman (up, down, left, right)
   - Movement continues in current direction until hitting wall or changing direction
3. **Objective:**
   - Eat all pellets to complete level
   - Avoid ghosts (red, pink, cyan, orange)
   - Eat power pellets to turn ghosts blue temporarily
   - Chase and eat blue ghosts for bonus points
4. **Scoring:**
   - Small pellet: 10 points
   - Power pellet: 50 points
   - Ghost: 200/400/800/1600 (multiplier per power-up session)
5. **Lives:**
   - Start with 3 lives
   - Lose life on ghost collision (unless ghost is blue/frightened)
   - Game over when all lives lost

**Tips:**
- Corner ghosts in dead-ends when they're frightened
- Plan routes to eat power pellets when ghosts are nearby
- Learn ghost AI patterns for strategic positioning

---

## What's Next

Future enhancements being considered:
- Multiple maze layouts (classic, mini, mega)
- Difficulty levels (faster ghosts, shorter power-up duration)
- High score leaderboard with name entry
- Fruit bonuses (cherry, strawberry, etc.)
- Tunnel teleportation (left edge → right edge)
- Animations for level transitions
- Two-player mode (competitive or co-op)
- Custom maze editor
- Mobile touch controls for Android/iOS port

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

- **Classic Gameplay** - Navigate maze, collect pellets
- **Ghost AI** - Demonstrate distinct ghost behaviors (chase, scatter, frightened)
- **Power Pellets** - Eat power pellets, chase and eat vulnerable ghosts
- **Score System** - Show scoring for pellets (10), power pellets (50), ghosts (200+)
- **Lives System** - Display three lives, ghost collisions
- **Level Completion** - Complete a level by eating all pellets

### Recording Setup

**Prerequisites:**
```bash
# Build with Maven
mvn clean package

# Or run directly
mvn javafx:run
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include system audio to capture sound effects

**Steps:**
1. Start the application: `mvn javafx:run` or `java -jar Pacman.jar`
2. Open OBS Studio and set up screen capture with audio
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Java projects\Pacman"
mvn javafx:run

# Or with JAR
java -jar target/Pacman.jar
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
