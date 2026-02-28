# Fractal Tree Drawing

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Turtle](https://img.shields.io/badge/Turtle-Graphics-green?style=for-the-badge)
![Algorithm](https://img.shields.io/badge/Algorithm-Recursion-red?style=for-the-badge)

**Nature's branching patterns through recursive algorithms. Watch mathematical beauty unfold as code becomes art.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This program generates fractal trees using recursive algorithms, mimicking the branching patterns found in nature. Each branch splits into two smaller branches at 60° angles, creating self-similar structures at every scale.

**The algorithm:**
- Start with a trunk of length 80 pixels
- Split into two branches at 30° left and 60° right
- Each branch is 75% the length of its parent (3/4 ratio)
- Recursion continues until branches reach 10 pixels
- Backtracking ensures proper branch positioning

**Mathematical principles:**
- **Recursion:** Function calls itself with smaller inputs
- **Geometric reduction:** Each level multiplies length by 0.75
- **Binary tree structure:** Each branch splits into exactly 2 children
- **Depth-first traversal:** Left branch fully explored before right

**Visual characteristics:**
- White branches on black background
- Natural asymmetry from angle variation
- Approximately 6-7 recursion levels (80 → 60 → 45 → 33 → 25 → 18 → 13 → stops)

---

## Tech Stack

**Language:** Python 3.7+  
**Graphics:** turtle module (standard library)  
**Algorithm:** Recursive depth-first traversal

### Architecture

```
Turtle Graphics Setup
         ↓
draw_fractal(80) Initial Call
         ↓
Recursive Tree Generation
├── Draw trunk forward
├── Rotate 30° left
│   └── draw_fractal(60) [left branch]
│       └── (recursion continues...)
├── Rotate 60° right (net 30° right from center)
│   └── draw_fractal(60) [right branch]
│       └── (recursion continues...)
└── Return to original position (backward)
         ↓
Visual Output (white tree on black)
```

**Recursion Details:**
- **Base case:** `if a >= 10` (stops when length < 10)
- **Recursive case:** `draw_fractal(3 * a / 4)` called twice per branch
- **Backtracking:** `gui.backward(a)` ensures turtle returns to branch origin
- **Angle management:** `left(30)`, `right(60)`, `left(30)` for proper positioning

---

## Project Structure

```
Fractal Tree Drawing/
├── fractal.py           # Recursive tree algorithm
├── Fractal Tree.gif     # Output preview
└── README.md
```

**Code structure:**
- Lines 1-10: Turtle configuration (black background, white pen, speed)
- Lines 13-21: `draw_fractal()` recursive function
- Line 24: Initial function call with length=80
- Line 25: Display window

**Recursion complexity:**
Each call spawns 2 more calls → exponential growth.  
Approximate total branches: 2⁰ + 2¹ + 2² + 2³ + 2⁴ + 2⁵ + 2⁶ ≈ 127 branches

---

## Getting Started

**Requirements:**
Python 3.7+ (turtle module is standard library)

**Run it:**

```bash
python fractal.py
```

The program opens a graphics window and draws the fractal tree automatically. Watch the recursion unfold in real-time.

**Customization:**

```python
# Change tree height
draw_fractal(120)  # Taller tree

# Change branching ratio (default 0.75)
draw_fractal(3 * a / 5)  # Steeper reduction (0.6)

# Change angles
gui.left(45)   # Wider left branches
gui.right(90)  # Wider right branches

# Change colors
gui.color('green')  # Green tree
s.bgcolor('white')  # White background

# Change base case (more/fewer branches)
if a >= 5:  # More branches (smaller threshold)
```

**Performance notes:**
- Speed set to 20 (0 = fastest, 10 = fast, 1 = slowest)
- Lower base case threshold significantly increases branch count
- Exponential complexity: reducing base case from 10 to 5 doubles recursion depth

---

## What's Next

**Possible enhancements:**
- Add randomization to angles (more natural look)
- Vary branch thickness (thicker trunk, thinner branches)
- Add seasonal colors (green leaves, autumn colors, bare winter)
- Create animated growth sequence
- Generate multiple trees (forest scene)
- Add wind effect (branch sway animation)
- 3D version using turtle's 3D capabilities

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
