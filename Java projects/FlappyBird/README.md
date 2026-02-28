# FlappyBird

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![Game](https://img.shields.io/badge/Type-Game-red?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**A fully-featured Flappy Bird clone with realistic physics, multiple bird skins, in-game shop, and high score tracking.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a complete recreation of the classic Flappy Bird game with added features that make it more engaging. It's not just a basic clone - it includes a shop system, multiple bird skins, smooth animations, and persistent score tracking.

**Core gameplay:**
- **Gravity physics** - realistic falling motion with smooth acceleration
- **Pipe obstacles** - randomly generated pipes with varying gaps
- **Collision detection** - precise hitbox calculations for fair gameplay
- **Score tracking** - earn points for passing pipes, track high scores
- **Lives system** - collect coins to unlock new bird skins

**What makes it special:**
- Three bird skins (red, blue, gold) unlockable via in-game currency
- Smooth sprite animations for flapping and movement
- Multiple background themes that change based on selected bird
- Persistent data storage for high scores, coins, and inventory
- Sound effects for jumping, scoring, collisions, and game over
- Clean JavaFX UI with drag-to-move window support
- Game over screen with restart functionality

The game loop runs at 60 FPS with AnimationTimer, physics calculations use delta time for consistent behavior across different frame rates, and all game state is automatically saved between sessions.

---

## Tech Stack

**Language:** Java 11  
**Framework:** JavaFX (Scene, Canvas, AnimationTimer)  
**Audio:** Custom Sound class with clip playback  
**Storage:** File I/O for persistence (high scores, coins, inventory)  
**Build:** No build system required (standard Java compile)

### Architecture

Classic game architecture with MVC influences:

```
Main (Application Launcher)
      ↓
FlappyBird (Game Core)
      ↓
┌─────────────────────────────────┐
│   Game Loop (AnimationTimer)    │
│   - Physics Updates (60 FPS)    │
│   - Collision Detection          │
│   - Rendering                    │
└─────────────────────────────────┘
      ↓
Components:
  ├─ Bird (Sprite + Physics)
  ├─ Pipe (ArrayList<Pipe>)
  ├─ Sprite (Position + Velocity)
  ├─ Sound (Audio Clips)
  └─ Controller (Shop System)
```

**Physics Implementation:**
- Gravity: 400 units/s² constant acceleration
- Jump velocity: -250 units/s instant upward impulse  
- Bird rotation: dynamic angle based on vertical velocity
- Collision: rectangular hitbox comparison between bird and pipes

**Key Technical Details:**
- **Game Loop:** Uses JavaFX's `AnimationTimer` with `System.nanoTime()` for delta time calculations
- **Sprite System:** Custom `Sprite` class with position, velocity, and rendering methods
- **Pipe Generation:** Pipes spawn at fixed intervals with randomized gap positions
- **Score Calculation:** Incremented when bird's X position exceeds pipe's right edge (once per pipe)
- **Persistence Layer:** Plain text files for `highscore.txt`, `coins.txt`, `inventory.txt`
- **Sound Management:** Pre-loaded audio clips triggered by game events (no streaming)

**Animation Details:**
- Bird sprite cycles through 3 frames for flapping animation
- Floor scrolls horizontally using dual-sprite technique for seamless looping
- Background parallax with 4 layered images moving at different speeds
- Fade transitions for game over and start screens

---

## Project Structure

```
FlappyBird/
├── Main.java                  # JavaFX application launcher
├── FlappyBird.java            # Core game logic + physics
├── Bird.java                  # Bird entity with animation
├── Pipe.java                  # Obstacle generation
├── Controller.java            # Shop system + UI
├── BuyBox.java               # Purchase confirmation dialog
├── InventoryBox.java         # Skin selection UI
├── Open.java                 # Landing page controller
└── resources/
    ├── game.fxml                # Main menu layout
    ├── back{0-3}.png           # Background layers
    ├── {red,blue,gold}Bird{1-3}.png  # Sprite sheets
    ├── {up,down}_pipe{0-3}.png       # Pipe sprites
    ├── floor.png                     # Ground texture
    ├── game_over.png                 # Game over screen
    ├── ready.png                     # Start indicator
    ├── highscore.txt                 # Persistent high score
    ├── coins.txt                     # Player currency
    └── inventory.txt                 # Unlocked skins
```

---

## Getting Started

**Requirements:**
- Java 11 or higher
- JavaFX SDK (if not bundled with JDK)

**Running the game:**

```bash
# Compile (if needed)
javac -d bin --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml sample/*.java

# Run
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin sample.Main
```

**Or use your IDE:**
1. Open project in IntelliJ IDEA / Eclipse / NetBeans
2. Ensure JavaFX is configured in project settings
3. Run `Main.java`

**Controls:**
- **SPACE** or **MOUSE CLICK** - Flap/Jump
- **RIGHT CLICK** - Start game from menu
- **Drag title bar** - Move window

**First time playing:**
- You start with the red bird unlocked
- Earn coins by achieving high scores (score = coins earned)
- Use coins to unlock blue bird (50 coins) and gold bird (100 coins)
- High scores persist across game sessions

---

## What's Next

Future improvements being considered:
- Difficulty levels (adjust pipe gaps, bird speed)
- Leaderboard with player names
- Daily challenges with special rewards
- Power-ups (shield, slow-motion, double points)
- More bird skins with unique abilities
- Online multiplayer race mode
- Achievement system with unlock conditions

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
