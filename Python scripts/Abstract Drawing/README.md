# Abstract Drawing

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Turtle](https://img.shields.io/badge/Turtle-Graphics-green?style=for-the-badge)

**Generative art through algorithmic geometry. Watch squares spiral into mesmerizing patterns.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a generative art program that creates abstract geometric patterns using turtle graphics. The algorithm draws rotating squares that gradually spiral outward, producing a hypnotic visual effect.

**The algorithm:**
- Draws 80x80 pixel squares repeatedly
- Rotates 15° after each square (24 iterations for full rotation)
- Advances forward 50 pixels every 24 squares
- Creates two complete rings before terminating
- All rendered on black background with green lines

**Key features:**
- Deterministic pattern generation (same output every run)
- Real-time rendering with turtle graphics
- Counter-based loop control for precise geometry
- Zero user interaction - pure algorithmic art

Perfect for understanding how simple geometric transformations create complex visual patterns.

---

## Tech Stack

**Language:** Python 3.7+  
**Graphics:** turtle module (standard library)

### Architecture

```
Turtle Graphics Engine
         ↓
  Drawing Algorithm
  (nested loops with counters)
         ↓
  Geometric Transforms
  (forward, rotate operations)
         ↓
    Visual Output
  (green on black canvas)
```

**Implementation Details:**
- Uses two counters (c, d) for nested loop control
- Inner loop: draws 4-sided square (90° turns)
- Outer rotation: 15° increments (360/15 = 24 iterations)
- Position advancement: 50px every 24 squares
- Rendering: maximum speed (`speed(0)`) for instant draw

---

## Project Structure

```
Abstract Drawing/
├── abstract.py          # Main drawing algorithm
├── abstract.gif         # Output preview
└── README.md
```

**Single-file implementation:**
- Lines 1-8: Turtle setup (black background, green pen)
- Lines 10-11: Counter initialization
- Lines 13-24: Nested loop algorithm (draw + rotate)
- Lines 26-27: Cleanup and display window

---

## Getting Started

**Requirements:**
Python 3.7+ (turtle module is standard library, no installation needed)

**Run it:**

```bash
python abstract.py
```

The program opens a graphics window and draws the pattern automatically. Close the window when complete or press Ctrl+C to interrupt.

**Customization ideas:**
- Change `t.pencolor("green")` to other colors
- Adjust rotation angle (line 19: `t.right(15)`)
- Modify square size (line 15: `t.forward(80)`)
- Increase/decrease ring count (line 22: `if d >= 2`)

---

## What's Next

**Possible enhancements:**
- Add randomization (colors, sizes, angles)
- User input for pattern parameters
- Export to image file (PostScript/EPS)
- Animate with gradual color transitions
- Create multiple overlapping patterns

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
