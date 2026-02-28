# Guess the Number

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Game](https://img.shields.io/badge/Type-CLI%20Game-green?style=for-the-badge)

**Classic number guessing game with strategic hints. Three chances to outsmart the random number generator.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a command-line number guessing game where the player has 3 attempts to guess a randomly generated number between 1 and 100. After each incorrect guess, the game provides strategic hints (higher/lower) to narrow down the possibilities.

**Game mechanics:**
- Random number generated between 1-100
- Player has exactly 3 chances to guess correctly
- After each guess, receive feedback:
  - "Number is bigger than X" if guess too low
  - "Number is less than X" if guess too high
  - "Congratulations!" if guess correct
- Game ends when player guesses correctly OR runs out of chances

**Strategic gameplay:**
Using binary search strategy, 3 guesses can narrow down 100 possibilities:
- Guess 1: 50 (eliminates 50 numbers)
- Guess 2: 25 or 75 (eliminates another 25)
- Guess 3: Based on previous feedback

**Edge cases handled:**
- Equal values between guesses and target
- Win on any of the 3 attempts
- Reveal answer if all attempts exhausted

---

## Tech Stack

**Language:** Python 3.7+  
**Modules:** random (standard library)  
**I/O:** Command-line interface with `input()`

### Architecture

```
Game Initialization
         ↓
Random Number Generation
(random.randint(1, 100))
         ↓
Attempt Loop (3 iterations)
├── Get user input
├── Compare with target
├── Provide hint (higher/lower)
└── Check win condition
         ↓
Game End (win/lose)
```

**Implementation Details:**
- Random number stored in variable `a`
- Nested conditionals for 3 attempt levels
- Each attempt has 3 outcomes: higher, lower, equal
- No loops used - explicit nested if-else chains
- All 27 possible paths coded explicitly (3³ branches)

---

## Project Structure

```
Guess the number/
├── guessgame.py         # Game logic (56 lines)
├── gamewon.jpg          # Victory screenshot
├── gamelost.jpg         # Defeat screenshot
└── README.md
```

**Code structure:**
- Line 3: Import random module
- Line 5: Generate target number (1-100)
- Lines 7-57: Nested conditional chains for 3 attempts
- Each attempt: input → compare → hint → next attempt

---

## Getting Started

**Requirements:**
Python 3.7+ (no external dependencies)

**Run it:**

```bash
python guessgame.py
```

**Example gameplay:**

```
Guess the number (from 1 to 100). You have 3 chances left: 50
Nope, but the number is bigger than 50

Guess the number. You have 2 chances left: 75
Nope, but the number is less than 75

Guess the number. You have 1 chance left: 62

Congratulations! You guessed the number ( 62 )
```

**Optimal strategy:**
1. **First guess:** 50 (middle of range)
2. **Second guess:** 25 or 75 (depending on hint)
3. **Third guess:** 12, 37, 62, or 87 (quarter points)

This strategy guarantees finding any number within 7 guesses (log₂(100) ≈ 6.64), so 3 guesses offers ~43% win rate.

---

## What's Next

**Possible enhancements:**
- Use loops instead of nested conditionals (cleaner code)
- Add difficulty levels (more/fewer attempts)
- Track guess history and show at end
- Keep score across multiple rounds
- Add time limit per guess
- GUI version with visual feedback
- Multiplayer mode (player vs player)
- Statistics tracking (win rate, average guesses)

**Code improvements:**
- Refactor nested ifs to while loop
- Add input validation (handle non-numeric input)
- Create function-based structure
- Add color output (ANSI codes)

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
