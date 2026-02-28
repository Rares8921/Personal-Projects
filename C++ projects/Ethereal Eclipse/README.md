# Ethereal Eclipse

<div align="center">

![C++](https://img.shields.io/badge/C++-17-00599C?style=for-the-badge&logo=cplusplus)
![SFML](https://img.shields.io/badge/SFML-2.5-8CC445?style=for-the-badge&logo=sfml)
![OpenGL](https://img.shields.io/badge/OpenGL-3.3-5586A4?style=for-the-badge&logo=opengl)
![Visual Studio](https://img.shields.io/badge/Visual_Studio-2019-5C2D91?style=for-the-badge&logo=visualstudio)

**A 2D action-adventure game showcasing advanced C++ design patterns, smart pointer memory management, and custom shader programming.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

Ethereal Eclipse is a fully-featured 2D action game built from scratch in C++. It combines classic dungeon-crawler gameplay with modern software engineering practices - think *Enter the Gungeon* meets enterprise design patterns.

**Core Features:**
- **Dynamic Combat System** - Real-time melee and ranged combat with enemy AI that actually reacts to your moves
- **Intelligent Enemy AI** - Enemies use pathfinding (AIFollow) to hunt you down, adapting to your position
- **Tile-Based World** - 8 explorable zones (zone1-zone8) with custom `.slmp` level format
- **Player Progression** - Full inventory system, weapon management, XP/leveling, and character stats (AttributeComponent)
- **Entity-Component Architecture** - Modular entity system with reusable components for movement, combat, and attributes
- **Custom Shaders** - Vertex and fragment shaders for lighting effects and dynamic rendering
- **Audio System** - Background music (`mainMusic.mp3`) and sound effects via OpenAL

**What Makes It Stand Out:**
- **5 Core Design Patterns:** Factory (entity creation), Prototype (object cloning), Observer (event system), State (game states), Component-based design (entities)
- **RAII Memory Management** - Extensive use of smart pointers (`std::unique_ptr`, `std::shared_ptr`) - zero raw pointers in gameplay code
- **Professional Architecture** - Clean separation: Entities → Components → Systems → Rendering
- **Custom GUI System** - Handcrafted UI with pause menus, player stats, inventory screens
- **Sound-Driven Engine** - Integrated OpenAL for 3D positional audio

---

## Tech Stack

**Language:** C++17  
**Graphics:** SFML 2.5 (rendering) + OpenGL 3.3 (custom shaders)  
**Audio:** OpenAL (`openal32.dll`)  
**Build:** Visual Studio 2019 (`.vcxproj`)  
**Patterns:** Factory, Prototype, Observer, State, Component-based, RAII

### Architecture

The game follows a layered Entity-Component-System pattern with state management:

```
Game Loop (Main.cpp)
      ↓
GameRun (state machine)
      ↓
┌─────────────────────────────────────┐
│  Entity Layer                       │
│  - Player (hero w/ inventory)       │
│  - Enemy (AI-driven mobs)           │
│  - Rat (basic enemy type)           │
└─────────────────────────────────────┘
      ↓
┌─────────────────────────────────────┐
│  Component Layer                    │
│  - AttributeComponent (HP/XP/stats) │
│  - MovementComponent                │
│  - AnimationComponent               │
│  - HitboxComponent                  │
└─────────────────────────────────────┘
      ↓
┌─────────────────────────────────────┐
│  System Layer                       │
│  - EnemySystem (AI pathfinding)     │
│  - Controls (input handling)        │
│  - Inventory (item management)      │
│  - GUI (player HUD, menus)          │
└─────────────────────────────────────┘
      ↓
Rendering Pipeline
  - SFML (sprites, textures)
  - Custom shaders (vertex_shader.vert, fragment_shader.frag)
  - OpenGL post-processing
```

**Design Pattern Implementation:**
- **Factory Pattern:** `Entity` creation through factory methods (Player, Enemy, Rat spawning)
- **Prototype Pattern:** Enemy cloning for wave spawning
- **Observer Pattern:** Event system for player damage, level changes, game state transitions
- **State Pattern:** Game states (MainMenu, Playing, Paused, GameOver) via `States/` folder
- **Component Pattern:** Entities composed of reusable components (Movement, Attributes, Hitbox)
- **RAII:** Smart pointers manage all dynamic resources - textures, entities, weapons auto-cleanup

**Key Modules:**
- **Level System:** `Level.cpp` loads zones from `.slmp` files, manages tile collision
- **AI System:** `AIFollow.cpp` implements A* pathfinding for enemy tracking
- **Combat:** `Player.cpp` handles attacks, `Weapon.h` abstraction for melee/ranged weapons
- **GUI:** `PlayerGUI.cpp` + `PauseMenu.cpp` for HUD overlays and menus
- **Shader Pipeline:** `vertex_shader.vert` + `fragment_shader.frag` for dynamic lighting

---

## Project Structure

```
Ethereal Eclipse/
├── Core/
│   ├── Main.cpp                     # Entry point
│   ├── Game.cpp / Game.h            # Main menu and initialization
│   ├── GameRun.cpp / GameRun.h      # Game loop and state machine
│   └── GeneralFunctions.cpp / .h    # Utility functions
│
├── Entities/
│   ├── Entity.cpp / Entity.h        # Base entity class
│   ├── Player.cpp / Player.h        # Player character with inventory
│   ├── Enemy.cpp / Enemy.h          # Enemy base class
│   ├── Rat.cpp / Rat.h              # Basic enemy type
│   └── EnemySystem.cpp / .h         # Enemy spawning and AI coordination
│
├── Components/
│   └── (Various component headers)  # Attribute, Movement, Animation, Hitbox
│
├── Systems/
│   ├── Controls.cpp / Controls.h    # Input handling
│   ├── Inventory.cpp / Inventory.h  # Item and weapon management
│   ├── Level.cpp / Level.h          # Tile-based world loading
│   ├── AIFollow.cpp / AIFollow.h    # Enemy pathfinding AI
│   └── GUI.cpp / GUI.h              # Base GUI system
│
├── GUI/
│   ├── PlayerGUI.cpp / PlayerGUI.h  # HUD (HP, XP, inventory)
│   ├── PauseMenu.cpp / PauseMenu.h  # In-game pause screen
│   ├── TextTags.cpp / TextTags.h    # Floating combat text
│   └── Settings.cpp / Settings.h    # Game settings menu
│
├── Items/
│   └── Weapons/
│       ├── Weapon.h                 # Weapon interface
│       └── Melee/
│           └── Sword.h              # Melee weapon implementation
│
├── States/
│   └── (Game state implementations)
│
├── Shaders/
│   ├── vertex_shader.vert           # Vertex shader (lighting)
│   └── fragment_shader.frag         # Fragment shader (post-processing)
│
├── Assets/
│   ├── Fonts/                       # Game fonts
│   ├── Sounds/                      # Sound effects
│   ├── Tiles/                       # Tile textures
│   ├── mainMusic.mp3                # Background music
│   ├── moveLeft.png                 # Player sprites
│   └── zone1-zone8.slmp             # Level data files
│
├── Config/                          # Game configuration
├── Tabs/                            # UI tab implementations
└── openal32.dll                     # Audio library
```

---

## Getting Started

**Prerequisites:**
- Visual Studio 2019 (or later)
- SFML 2.5.1+ (automatically linked via `.vcxproj`)
- OpenGL 3.3+ compatible GPU
- Windows 10/11

**Setup (2 minutes):**

1. **Clone or download the project**
```bash
# If you have Git
git clone <repo-url>
cd "Ethereal Eclipse"
```

2. **Open in Visual Studio**
```bash
# Double-click to open
start "Ethereal Eclipse.vcxproj"
```

3. **Build and run**
- Press `F5` or click "Local Windows Debugger"
- The game will build automatically (SFML is pre-configured)

**Controls:**
- **WASD** - Movement
- **Mouse** - Aim
- **Left Click** - Attack
- **E** - Interact/Pick up items
- **I** - Inventory
- **ESC** - Pause menu

**First Time Playing:**
1. Navigate the main menu and select "Play"
2. Explore zone1 to get familiar with controls
3. Fight enemies (Rats) to gain XP and level up
4. Collect weapons from defeated enemies
5. Progress through zones 1-8

---

## What's Next

Potential enhancements I'm considering:

**Gameplay:**
- Boss fights with unique attack patterns
- Procedurally generated dungeons (move from `.slmp` to runtime generation)
- Magic system (spells, mana, cooldowns)
- Multiplayer co-op via TCP sockets

**Technical:**
- Serialize save games (JSON or binary format)
- Port to SDL2 for cross-platform support (Linux/Mac)
- Optimize enemy AI with behavior trees instead of raw A*
- Add particle effects for explosions and magic

**Polish:**
- Controller support (Xbox/PS4 gamepads)
- Achievement system
- Dynamic music that changes with combat state
- Mod support (load custom zones, enemies, weapons)

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

- **Dynamic Combat** - Real-time melee and ranged combat with enemies
- **Enemy AI** - Demonstrate AIFollow pathfinding and intelligent pursuit
- **World Exploration** - Navigate through multiple zones (zone1-zone8)
- **Inventory System** - Show weapon management and item usage
- **Custom Shaders** - Display lighting effects and dynamic rendering
- **Player Progression** - XP/leveling and character stats (AttributeComponent)

### Recording Setup

**Prerequisites:**
```bash
# Build the project in Visual Studio 2019
# Ensure SFML 2.5 and OpenAL libraries are linked
# Run in Release mode for best performance
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include system audio to capture game music and sound effects

**Steps:**
1. Start the application: Run `Ethereal Eclipse.exe` from build directory
2. Open OBS Studio and set up screen capture with audio
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Navigate to build directory
cd "d:\Personal-Projects\C++ projects\Ethereal Eclipse\x64\Release"

# Start the game
start "Ethereal Eclipse.exe"
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
