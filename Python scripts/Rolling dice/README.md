# Rolling Dice

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Game](https://img.shields.io/badge/Type-CLI%20Game-green?style=for-the-badge)

**Compete against the computer in a dice-rolling showdown. First to 2 points wins in this luck-based battle.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a command-line dice game where you compete against the computer to be the first to score 2 points. Each round, both players roll two dice, and whoever has the higher sum scores a point. Ties give both players a point.

**Game rules:**
- Each player rolls two 6-sided dice per round
- Player with higher dice sum wins the round (scores 1 point)
- If sums are equal, both score 1 point (tie round)
- First to reach 2 points wins the game
- Maximum 3 rounds possible (2-0, 1-1-win, 0-1-win)

**Scoring logic:**
- **Player > Computer:** Player gets 1 point
- **Player < Computer:** Computer gets 1 point
- **Player = Computer:** Both get 1 point

**Possible outcomes:**
- Player wins 2-0 (dominates first 2 rounds)
- Computer wins 2-0 (dominates first 2 rounds)
- Player wins 2-1 (after 3 rounds)
- Computer wins 2-1 (after 3 rounds)
- Tie 2-2 (all equal rolls, no winner)

---

## Tech Stack

**Language:** Python 3.7+  
**Modules:** random (dice simulation)  
**I/O:** Command-line with colored ANSI output

### Architecture

```
Game Menu
(roll or exit)
         ↓
Round Loop (up to 3 rounds)
├── Roll dice for player (2 dice)
├── Roll dice for computer (2 dice)
├── Compare sums
├── Update scores
├── Check win condition (2 points)
└── Next round or game end
         ↓
Display Winner/Tie Message
```

**Dice Rolling:**

```python
# Each roll: random integer 1-6
a = random.randint(1, 6)  # Player die 1
b = random.randint(1, 6)  # Player die 2
c = random.randint(1, 6)  # Computer die 1
d = random.randint(1, 6)  # Computer die 2

# Calculate sums
player_sum = a + b      # Range: 2-12
computer_sum = c + d    # Range: 2-12
```

**Probability analysis:**
- Minimum sum: 2 (1+1, probability 1/36)
- Maximum sum: 12 (6+6, probability 1/36)
- Most likely sum: 7 (probability 6/36 = 16.67%)
- Distribution: bell curve centered at 7

---

## Project Structure

```
Rolling dice/
├── roll.py              # Game logic (135 lines)
├── gamewon.jpg          # Victory screenshot
├── gamelost.jpg         # Defeat screenshot
├── gamedrawn.jpg        # Tie screenshot
└── README.md
```

**Code structure:**
- Lines 1-14: Imports and ANSI color definitions
- Lines 17-18: Initial dice rolls
- Lines 21-22: Game menu (roll vs exit)
- Lines 24-135: Nested conditionals for 3-round gameplay
- Each level: roll → compare → update score → check win

**ANSI color codes:**
- GREEN: Scores
- BLUE: Dice values
- WHITE: General text
- MAGENTA: "computer" (winner announcement)

---

## Getting Started

**Requirements:**
Python 3.7+ (random module is standard library)

**Run it:**

```bash
python roll.py
```

**Example gameplay:**

```
[Roll dice] - First who get 2 points is the winner.
If you want to play, type 'roll'; if u want to quit, type 'exit': roll

Rolling..
Your values: 4 5
Computer's values: 3 2
You got 1 point.
You: 1
Computer: 0

Rolling..
Your values: 6 3
Computer's values: 5 4
You got 1 point.
You: 2
Computer: 0

You are the winner. Congratulations!
```

**Game statistics:**
- Pure chance (no skill involved)
- Each player has equal ~50% win probability
- Tie rounds: ~9.2% probability (33/36² when sums equal)
- Expected game length: ~2.1 rounds on average

---

## What's Next

**Possible enhancements:**
- Refactor nested conditionals to loop structure
- Add input validation (handle typos)
- Track game statistics (win/loss record)
- Multiple rounds (best of 3, 5, etc.)
- Dice animation (show rolling effect)
- GUI version with dice images
- Multiplayer mode (player vs player)
- Betting system (virtual currency)

**Code improvements:**

```python
# Replace nested ifs with loop
score_player = 0
score_computer = 0

while score_player < 2 and score_computer < 2:
    # Roll dice
    # Compare
    # Update scores
    # Display status
```

**Additional features:**
- Save game history to file
- Leaderboard system
- Sound effects (dice rolling)
- Different dice types (d4, d8, d10, d20)

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
