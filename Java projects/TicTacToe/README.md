# Tic Tac Toe

<div align="center">

![Java](https://img.shields.io/badge/Java-Swing-orange?style=for-the-badge&logo=java)
![AI](https://img.shields.io/badge/AI-Minimax-blue?style=for-the-badge&logo=ai)
![Game](https://img.shields.io/badge/Game-Strategy-green?style=for-the-badge&logo=gamepad)

**The classic Tic Tac Toe game with an unbeatable AI. Play against a minimax algorithm that never loses—with score tracking and a sleek dark interface.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is Tic Tac Toe with a twist—the AI opponent uses the minimax algorithm with alpha-beta pruning, making it impossible to beat (best you can do is draw). It's built with Java Swing and features persistent score tracking across games.

**Core features:**
- **Unbeatable AI** using minimax algorithm with alpha-beta pruning
- **Score tracking**: Keeps track of player wins, AI wins, and draws
- **Persistent scores**: Scores carry over between rounds
- **Instant AI response**: AI calculates optimal move immediately
- **Win detection**: Checks all 8 possible winning combinations (rows, columns, diagonals)
- **Dark-themed UI** with custom button styling
- **Play again**: Quick restart without losing score data

**How the AI works:**
- Uses minimax algorithm to evaluate all possible game states
- Alpha-beta pruning optimizes performance (cuts unnecessary branches)
- Always plays optimal move—never makes mistakes
- Impossible to beat (you can only draw or lose)

**Gameplay mechanics:**
- Player (X) always goes first
- Click any empty cell to place your mark
- AI (O) responds immediately with optimal move
- First to get 3 in a row wins
- Game ends with win message and score update

**Why it's challenging:**
- AI literally cannot lose if it moves correctly
- Forces you to play perfectly just to draw
- Score tracking motivates multiple rounds
- Fast AI response keeps gameplay smooth

The game uses a 3x3 integer matrix to represent the board state, with minimax recursively exploring all possible move sequences to find the best play.

---

## Tech Stack

**Language:** Java SE (Swing for GUI)  
**AI Algorithm:** Minimax with alpha-beta pruning  
**UI Framework:** Swing (JButton, JLabel, JFrame)  
**Game Logic:** 3x3 int array (board state), recursive tree search  
**Custom Components:** Styled JButton with hover effects

### Architecture

Game loop with player input, AI decision-making, and win detection:

```
UI Layer (Swing Components)
      ↓
Event Handlers (JButton ActionListener)
      ↓
┌──────────────┬──────────────┬───────────────┐
│  Player      │  AI Engine   │  Win          │
│  Input       │  (Minimax)   │  Detection    │
└──────────────┴──────────────┴───────────────┘
      ↓              ↓               ↓
Button Click    Minimax Tree    Check Rows/Cols
Board Update    Alpha-Beta      Check Diagonals
                Best Move       Update Score
```

**How it works:**
- **Board State**: 3x3 int array (0=empty, 1=player, 2=AI)
- **Player Move**: Click button → update array → check win → trigger AI
- **AI Move**: Minimax evaluates all possible moves → returns best position → update board
- **Win Detection**: `wonGame()` checks all 8 combinations (3 rows, 3 cols, 2 diagonals)
- **Score**: Persistent JLabel displays player vs AI score
- **Reset**: "Play Again" button clears board, resets matrix, preserves score

**Key Implementation Details:**
- **Minimax Algorithm**: Recursively explores all possible game states down to terminal nodes (win/loss/draw)
- **Alpha-Beta Pruning**: Cuts branches that can't affect final decision (performance optimization)
- **Evaluation Function**: +10 for AI win, -10 for player win, 0 for draw
- **Board Representation**: `table[i][j]` stores cell state (0/1/2)
- **UI Updates**: Buttons disabled after click, text set to "X" or "O", color changes

---

## Project Structure

```
TicTacToe/
├── Main.java                   # Game logic + UI + minimax implementation
├── CustomJToolTip.java         # Custom tooltip styling (optional)
└── TicTacToe.exe               # Compiled executable
```

**Main components:**
- `Main.java`: Swing UI, game loop, minimax algorithm, win detection
- Minimax: Recursive function with alpha-beta pruning
- Win detection: `wonGame()` checks all winning combinations
- Score tracking: `playerPoints`, `opponentPoints` variables

---

## Getting Started

### Prerequisites

- **Java SE 8+** (any modern Java version)

### Running the Game

**Option 1: Executable**
```bash
# Windows:
.\TicTacToe.exe

# Or run from source:
javac Main.java
java com.company.Main
```

**Option 2: From Source**
```bash
# Compile:
javac -d bin src/com/company/*.java

# Run:
java -cp bin com.company.Main
```

### How to Play

1. **Start game**: Application launches with empty 3x3 grid
2. **Make your move**: Click any empty cell (you are X)
3. **AI responds**: AI (O) calculates and plays optimal move instantly
4. **Repeat**: Continue until someone wins or board is full
5. **Play again**: Click "Play Again" button to restart (score persists)

**Game rules:**
- Get 3 in a row (horizontal, vertical, or diagonal) to win
- If board fills with no winner, it's a draw
- Player always goes first

**Strategy tips:**
- Start center or corner for best chances
- Block AI's winning moves
- Perfect play results in draw (AI is unbeatable)

---

## What's Next

**Potential improvements:**
- Add difficulty levels (easy, medium, impossible)
- Implement two-player mode (human vs human)
- Add move history/replay
- Larger board sizes (4x4, 5x5)
- Time limit per move
- Sound effects (move, win, lose)
- Animated transitions for moves
- Multiplayer over network

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
