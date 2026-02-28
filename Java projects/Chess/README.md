# Chess Game

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![AI](https://img.shields.io/badge/AI-Minimax-green?style=for-the-badge)

**Full-featured chess game with AI opponent. Complete with move validation, checkmate detection, and FEN notation support.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A complete chess implementation with both player-vs-player and player-vs-AI modes. Every rule is properly implemented, from basic moves to special actions like castling, en passant, and pawn promotion.

**Core Features:**
- **2-Player Mode** - Play against a friend on the same computer
- **AI Opponent** - Challenge a minimax-based chess engine
- **Full Rule Implementation** - All standard chess rules enforced
- **Move Validation** - Only legal moves allowed
- **Check Detection** - Automatic check/checkmate recognition
- **Special Moves** - Castling (king-side/queen-side), en passant, pawn promotion
- **Piece Highlighting** - Visual indicators for legal moves when piece is selected
- **Move History** - Track all moves with algebraic notation
- **Game State Tracking** - Undo/redo, save/load game positions

**Chess Rules Implemented:**
- Basic piece movement (pawn, knight, bishop, rook, queen, king)
- Capture mechanics
- Check, checkmate, and stalemate detection
- Castling (only when neither king nor rook has moved, path is clear, king not in check)
- En passant (pawn capture)
- Pawn promotion (auto-promote to queen or choose piece)
- Fifty-move rule
- Threefold repetition rule
- Insufficient material draw detection

---

## Tech Stack

**Frontend:** JavaFX 11 + Custom Board Rendering  
**Backend:** Java 11 + Object-Oriented Chess Logic  
**AI Engine:** Minimax Algorithm with Alpha-Beta Pruning  
**Build:** Maven with modular Java (module-info)  
**Architecture:** Layered domain model with separation of concerns

### Architecture

Clean domain-driven design with validation and evaluation layers:

```
JavaFX UI (Board Rendering)
      ↓
Game Controller
      ↓
Game.java (Game State Management)
      ↓
Board.java (8x8 Grid)
      ↓
Field.java (Individual Square)
      ↓
Piece (Abstract) → King, Queen, Rook, Bishop, Knight, Pawn
      ↓
Move.java (Move Object)
      ↓
MoveValidator (Legal Move Checking)
      ↓
GameEvaluator (Check/Checkmate/Stalemate/Draw)
```

**AI Implementation:**
- **Minimax Algorithm:** Recursive game tree search with configurable depth
- **Alpha-Beta Pruning:** Optimization to skip irrelevant branches
- **Position Evaluation:** Scoring based on material, position, king safety
- **Move Ordering:** Queen captures first for better pruning efficiency

**Key Classes:**
- **Game.java:** Central game loop, move execution, state management
- **Board.java:** 8x8 field array, piece positioning
- **Piece Hierarchy:** Abstract `Piece` class with concrete implementations
- **MoveValidator:** Static validation methods (legal move checking, path obstruction, king safety)
- **GameEvaluator:** Check detection, checkmate, stalemate, draw conditions
- **FENBuilder:** Export game state to FEN notation

---

## Project Structure

```
src/sample/
├── chess/
│   ├── Game.java           # Core game logic + move execution
│   ├── Board.java          # 8x8 board representation
│   ├── Field.java          # Individual square
│   ├── Move.java           # Move object (from-to fields)
│   ├── Player.java         # White/Black player state
│   ├── GameState.java      # Save/restore game states
│   ├── pieces/
│   │   ├── Piece.java      # Abstract piece
│   │   ├── King.java
│   │   ├── Queen.java
│   │   ├── Rook.java
│   │   ├── Bishop.java
│   │   ├── Knight.java
│   │   └── Pawn.java
│   └── util/
│       ├── MoveValidator.java     # Move legality checks
│       ├── GameEvaluator.java     # Check/mate/draw detection
│       ├── FENBuilder.java        # FEN notation export
│       ├── Converter.java         # Coordinate conversions
│       └── Movement.java          # Movement helper functions
├── application/
│   └── Main.java           # JavaFX entry point
└── resources/
    └── piece-images/       # PNG sprites for pieces

module-info.java            # Java module descriptor
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
cd "d:\Personal-Projects\Java projects\Chess"

# Run with JavaFX modules
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -m sample/sample.application.Main
```

**Or run from IDE (IntelliJ IDEA recommended):**
1. Open project as Maven/Gradle project
2. Configure JavaFX SDK in project settings
3. Run `Main.java`

**How to Play:**
1. Launch application
2. Choose mode: **2 Players** or **vs AI**
3. Click piece to select (legal moves highlight)
4. Click destination square to move
5. Game enforces all rules automatically
6. Checkmate/stalemate detected automatically

---

## What's Next

Future improvements and features:

- **Difficulty Levels** - Adjustable AI depth (easy/medium/hard)
- **Opening Book** - Pre-programmed opening moves for stronger play
- **Endgame Tables** - Syzygy tablebase integration for perfect endgame play
- **Multiplayer Online** - Network play via sockets or web services
- **Puzzle Mode** - Load chess puzzles (mate in 2, tactical patterns)
- **Game Analysis** - Post-game move analysis with engine evaluation
- **Time Controls** - Blitz, rapid, and classical time formats with clock
- **Tournament Mode** - Bracket system for multiple games
- **PGN Support** - Import/export games in Portable Game Notation
- **3D Board** - OpenGL/JavaFX 3D rendering option

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
