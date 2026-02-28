# Dino Game

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![Game](https://img.shields.io/badge/Type-Arcade-brightgreen?style=for-the-badge)

**Chrome's offline dinosaur game recreated in JavaFX. Jump over cacti, rack up points, beat your high score.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A faithful recreation of the iconic Chrome offline dinosaur game, built entirely in JavaFX. The classic endless runner that everyone plays when the internet dies.

**Gameplay Features:**
- **Endless Running** - Infinite side-scrolling desert landscape
- **Jump Mechanic** - Press spacebar to jump over obstacles
- **Obstacle Variety** - Cacti of different heights (1-cactus, 3-cacti, 4-cacti)
- **Score Tracking** - Points increase over time, displays current score
- **High Score** - Persistent high score saved between sessions
- **Increasing Difficulty** - Obstacles spawn faster and move quicker as score increases
- **Sound Effects:**
  - Jump sound (when dinosaur jumps)
  - Score milestone sound (every 100 points)
  - Death sound (collision with obstacle)
- **Game Over Screen** - Shows final score with replay button

**Visual Features:**
- Animated dinosaur sprite (running animation)
- Parallax cloud movement
- Scrolling ground tiles
- Day/night mode visuals
- Game-over screen with replay button

**Controls:**
- **Spacebar** - Jump
- **P** - Pause/Resume
- **R** - Restart after game over
- **Esc** - Close game

---

## Tech Stack

**Frontend:** JavaFX 11 + FXML + CSS  
**Graphics:** JavaFX ImageView + Animation API  
**Backend:** Java 11 + Timeline-based game loop  
**Audio:** JavaFX Media (WAV files)  
**Build:** Maven/Gradle compatible  
**Pattern:** MVC with game state management

### Architecture

Game loop architecture with collision detection:

```
Main.java (Application)
      ↓
sample.fxml + Controller.java
      ↓
Game Loop (Timeline, 60 FPS)
      ↓
Game Entities:
  ├── Dino.java (Player character)
  ├── Enemy.java (Cacti obstacles)
  ├── Floor.java (Ground tiles)
  └── Cloud.java (Background clouds)
      ↓
Collision Detection (Bounds intersection)
      ↓
Game State:
  ├── Running (active gameplay)
  ├── Paused (frozen state)
  └── Game Over (death screen)
      ↓
Sound.java (Audio playback)
```

**Key Implementation Details:**
- **Timeline Animation:** JavaFX Timeline running at ~60 FPS for smooth gameplay
- **Collision Detection:** Bounding box intersection between Dino and Enemy objects
- **Sprite Animation:** KeyFrame-based sprite cycling for dino running frames
- **Procedural Generation:** Enemies spawn at random intervals with increasing frequency
- **State Persistence:** High score saved to AppData folder (`dinogamefile.dat`)
- **Difficulty Scaling:** Enemy speed increases by 5% every 100 points

**Physics:**
- Gravity simulation for jump arc (parabolic motion)
- Ground collision detection keeps dino grounded
- Jump velocity: Initial upward force + gravity pull

---

## Project Structure

```
src/sample/
├── Main.java           # JavaFX Application entry
├── Controller.java     # Game controller + main game loop
├── Dino.java           # Player character logic
├── Enemy.java          # Obstacle (cactus) logic
├── Floor.java          # Scrolling ground tiles
├── Cloud.java          # Background cloud movement
├── Sound.java          # Audio playback wrapper
├── sample.fxml         # UI layout
└── styles.css          # Game styling

resources/images/
├── dino1.png → dino6.png     # Dino sprite frames
├── Cactus1.png, Cactus3.png, Cactus4.png
├── Cloud1.png, Cloud2.png
├── Land.png                   # Ground tile
├── Sun.png                    # Sky decoration
├── gameover_text.png
└── replay_button.png

resources/audio/
├── jump.wav            # Jump sound effect
├── dead.wav            # Collision sound
└── scoreup.wav         # Score milestone sound

META-INF/
test.txt
README.md
```

---

## Getting Started

**Requirements:**
- Java 11+ with JavaFX
- Maven (optional)

**Build & Run:**

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Java projects\Dino Game"

# Run with JavaFX
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     sample.Main
```

**Or run from IDE:**
1. Open project in IntelliJ IDEA
2. Configure JavaFX SDK
3. Run `Main.java`

**How to Play:**
1. Launch game
2. Press **Start** button
3. Press **Spacebar** to jump over cacti
4. Avoid hitting obstacles
5. Survive as long as possible
6. Beat your high score!

**Tips:**
- Different cacti require different jump timing
- Score increases every ~130ms (about 10 points per second initially)
- Speed increases make later stages harder
- Sound cues help with timing

---

## What's Next

Future enhancements to expand the game:

- **Mobile Controls** - Touch/click to jump (for mobile deployment)
- **Duck Mechanic** - Down arrow to duck under flying pterodactyls
- **Power-Ups** - Shields, slow-motion, or score multipliers
- **Level System** - Progress through themed stages (desert, mountains, night)
- **Leaderboard** - Online high score tracking with player names
- **Achievements** - Unlock badges for milestones (100 pts, 1000 pts, etc.)
- **Skins** - Unlock different dinosaur skins and obstacle themes
- **Multiplayer** - Race against friends in real-time
- **Procedural Music** - Dynamic soundtrack that evolves with difficulty
- **Particle Effects** - Dust clouds, cactus debris on collision

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

- **Endless Running** - Show infinite side-scrolling gameplay
- **Jump Mechanic** - Demonstrate spacebar jump over cacti
- **Obstacle Variety** - Display different cactus heights (1, 3, 4-cactus)
- **Score System** - Show score increment and milestone sounds
- **Increasing Difficulty** - Demonstrate speed increase over time
- **Sound Effects** - Jump, score milestone, death sounds

### Recording Setup

**Prerequisites:**
```bash
# Ensure JavaFX 11+ is installed
# Run from IDE or compile to JAR
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include system audio to capture sound effects

**Steps:**
1. Start the application: `java -jar DinoGame.jar` or run Main class
2. Open OBS Studio and set up screen capture with audio
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Java projects\Dino Game"
java -jar DinoGame.jar
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
