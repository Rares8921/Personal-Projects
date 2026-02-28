# Heart Drawing

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Turtle](https://img.shields.io/badge/Turtle-Graphics-green?style=for-the-badge)

**Mathematical love. Create a perfect heart shape using geometric curves and precise angular calculations.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This program draws a heart shape using turtle graphics by combining straight lines with curved arcs. The heart is constructed from two symmetrical halves, each formed by a linear segment and a smooth 200-degree curve.

**How it works:**
1. Draw straight line at 140° angle (upper left edge)
2. Create smooth curve by moving forward 1px + rotating 1° × 200 iterations
3. Rotate 120° to position for right half
4. Draw second curve (mirror of first)
5. Complete with straight line back to origin

**Mathematical geometry:**
- Heart symmetry achieved through 120° rotation
- Curves approximated by small linear segments (200 iterations)
- 1° micro-rotations create smooth arcs (200° total rotation)
- Filled with pink color, red outline

**Visual output:**
- Red outline (pen color)
- Pink fill (fill color)
- Black background
- Perfectly symmetrical heart shape

---

## Tech Stack

**Language:** Python 3.7+  
**Graphics:** turtle module (standard library)  
**Technique:** Curve approximation via micro-rotations

### Architecture

```
Turtle Setup
(black background, red/pink colors)
         ↓
Heart Construction
├── Start fill
├── Rotate 140° (initial angle)
├── Draw straight line (111.65px)
├── curve() → 200 micro-rotations (left arc)
├── Rotate 120° (switch to right side)
├── curve() → 200 micro-rotations (right arc)
└── Draw straight line (111.65px)
         ↓
End fill → Complete heart
```

**Curve algorithm:**
```python
def curve():
    for i in range(200):
        gui.right(1)   # Rotate 1°
        gui.forward(1)  # Move 1px
# Result: 200px arc with 200° rotation
```

---

## Project Structure

```
Heart Drawing/
├── heart.py             # Heart drawing algorithm
├── Heart.gif            # Output preview
└── README.md
```

**Code structure:**
- Lines 1-8: Turtle setup (colors, pen size)
- Lines 11-14: `curve()` function (arc drawing)
- Lines 17-29: Main drawing sequence
- Line 30: Hide turtle cursor

**Key measurements:**
- Straight line length: 111.65 pixels
- Curve iterations: 200 (1px each = 200px arc length)
- Curve rotation: 200° total (1° per iteration)
- Initial angle: 140° left turn
- Mid-point rotation: 120° left turn

---

## Getting Started

**Requirements:**
Python 3.7+ (turtle module is standard library)

**Run it:**

```bash
python heart.py
```

The program opens a graphics window and draws a filled heart shape instantly.

**Customization:**

```python
# Change colors
gui.color("purple", "lavender")  # Purple outline, lavender fill

# Change size
gui.forward(200)  # Larger straight lines
for i in range(300):  # Larger curves
    gui.right(1)
    gui.forward(1)

# Change background
gui.screen.bgcolor("white")  # White background

# Adjust curve smoothness
for i in range(400):  # Smoother curve (more iterations)
    gui.right(0.5)   # Smaller angle increments
    gui.forward(0.5) # Smaller forward steps
```

**Understanding the angles:**
- 140° initial turn: positions turtle for left edge
- 200° curve: creates rounded top-left lobe
- 120° mid-rotation: mirrors geometry for right side
- Everything balances to close the shape perfectly

---

## What's Next

**Possible enhancements:**
- Add pulsing animation (scale heart in loop)
- Draw multiple hearts in pattern
- Add text inside heart ("I ❤️ Python")
- Create broken heart animation (split and separate)
- Rainbow gradient fill
- 3D heart using perspective
- Interactive heart that follows mouse cursor

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
