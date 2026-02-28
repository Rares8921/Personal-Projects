# Circle Drawing

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Turtle](https://img.shields.io/badge/Turtle-Graphics-green?style=for-the-badge)

**Geometric art from first principles. Create perfect circles by rotating squares at precise angles.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This program demonstrates how to approximate a circle using straight-line segments. By drawing squares at incrementally smaller rotation angles, the algorithm creates a smooth circular pattern.

**How it works:**
- Draws 80x80 pixel squares
- Rotates 5° after each square (72 total squares for 360°)
- The 5° rotation creates a tight circular arrangement
- Rendered with blue pen on black background
- Counter-based termination (`c >= 360/5`)

**Mathematical concept:**
As rotation angle approaches 0°, the polygon approaches a perfect circle. This uses 5° increments as a balance between visual smoothness and computation time.

**Key features:**
- Pure algorithmic circle generation (no `circle()` function)
- Demonstrates geometric transformation principles
- Deterministic output
- Real-time rendering

---

## Tech Stack

**Language:** Python 3.7+  
**Graphics:** turtle module (standard library)

### Architecture

```
Turtle Initialization
         ↓
Square Drawing Loop
(4 sides, 90° turns)
         ↓
5° Rotation After Each Square
         ↓
Circle Approximation
(72 iterations)
         ↓
Visual Output
(blue on black canvas)
```

**Implementation Details:**
- Outer loop: 72 iterations (360° ÷ 5° per iteration)
- Inner loop: 4 sides per square (90° turns)
- Pen movement: 80px per side
- Rotation: `t.right(5)` after each square
- Termination: counter reaches 72

---

## Project Structure

```
Circle Drawing/
├── circle.py            # Circle approximation algorithm
├── circle.gif           # Output preview
└── README.md
```

**Single-file implementation:**
- Lines 1-7: Turtle setup (screen, pen color, speed)
- Lines 9-10: Counter initialization
- Lines 11-17: Drawing loop with rotation
- Lines 19-20: Hide turtle and display window

---

## Getting Started

**Requirements:**
Python 3.7+ (turtle module is standard library)

**Run it:**

```bash
python circle.py
```

The program opens a graphics window and draws the circular pattern automatically. Close the window when finished.

**Customization:**
- **Smoother circle:** Change `t.right(5)` to `t.right(1)` (requires 360 iterations)
- **Rougher circle:** Increase rotation angle to 10° or 15°
- **Different colors:** Modify `t.pencolor("blue")`
- **Larger circle:** Increase `t.forward(80)` value
- **Background:** Change `s.bgcolor("black")`

**Try this experiment:**
Compare rotation angles: 1°, 3°, 5°, 10°, 15°, 20°. Observe how larger angles create more visible "corners."

---

## What's Next

**Possible enhancements:**
- Add multiple concentric circles
- Create spiral patterns by increasing radius per iteration
- Animate the drawing process (slower speed)
- Add color gradients (cycle through HSV spectrum)
- Generate ellipses by varying x/y movement

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
