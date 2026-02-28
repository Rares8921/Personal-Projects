# Octagon Drawing

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Turtle](https://img.shields.io/badge/Turtle-Graphics-green?style=for-the-badge)

**Geometric art through nested rotation. Create intricate octagonal patterns with concentric symmetry.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This program creates complex geometric patterns by drawing nested octagons with rotational symmetry. Each outer loop draws a complete octagon, and each inner loop draws 8 sides per octagon, resulting in a mandala-like design.

**How it works:**
- Outer loop: 8 iterations (one per octagon layer)
- Inner loop: 8 sides per octagon (45° turns for regular octagon)
- Each outer iteration rotates the entire octagon by 45°
- Creates 8 overlapping octagons forming a star pattern

**Mathematical properties:**
- Regular octagon: 8 sides, 135° interior angles, 45° exterior angles
- Rotation: 45° per octagon (360° ÷ 8)
- Side length: 100 pixels
- Total sides drawn: 8 × 8 = 64 line segments

**Visual output:**
- Yellow lines on black background
- 2-pixel pen width for visibility
- Creates 8-fold rotational symmetry
- Overlapping octagons form intricate star pattern

---

## Tech Stack

**Language:** Python 3.7+  
**Graphics:** turtle module (standard library)  
**Geometry:** Regular polygon construction with rotation

### Architecture

```
Turtle Initialization
(black background, yellow pen)
         ↓
Outer Loop (8 iterations)
├── Rotate 45° (initial positioning)
│
└── Inner Loop (8 iterations)
    ├── Draw line (100px forward)
    └── Rotate 45° (next side)
         ↓
Complete Pattern
(8 overlapping octagons)
```

**Nested Loop Structure:**

```python
for i in range(8):           # 8 octagons
    t.left(45)                # Rotate entire octagon
    
    for j in range(8):        # 8 sides per octagon
        t.forward(100)        # Draw side
        t.right(45)           # Turn to next side
```

**Why it works:**
- Inner loop draws one complete octagon (8 sides × 45° = 360°)
- Outer loop rotates starting angle by 45° each time
- 8 rotations × 45° = 360° (full circular coverage)
- Result: 8 octagons evenly distributed around center point

---

## Project Structure

```
Octagon Drawing/
├── octagon.py           # Octagon pattern algorithm
├── Octagon.gif          # Output preview
└── README.md
```

**Code structure:**
- Lines 1-9: Turtle setup (screen, colors, speed, pen)
- Lines 11-14: Nested loop structure (outer + inner)
- Line 16: Display window (`turtle.done()`)

**Configuration:**
- `t.speed(0)`: Maximum drawing speed
- `t.pensize(2)`: 2-pixel line width
- `t.color("yellow")`: Pen color
- `s.bgcolor("black")`: Background color

---

## Getting Started

**Requirements:**
Python 3.7+ (turtle module is standard library)

**Run it:**

```bash
python octagon.py
```

The program opens a graphics window and draws the octagonal pattern instantly.

**Customization:**

```python
# Draw more/fewer layers
for i in range(12):  # 12 octagons (more dense pattern)

# Change side length
t.forward(150)  # Larger octagons

# Change colors
t.color("cyan")
s.bgcolor("navy")

# Change polygon type
for j in range(6):   # Hexagons (6 sides)
    t.forward(100)
    t.right(60)      # 360°/6 = 60°

# Add color cycling
colors = ["red", "orange", "yellow", "green", "blue", "purple"]
for i in range(8):
    t.color(colors[i % 6])  # Cycle through colors
    # ... rest of code
```

**Try different polygons:**
- Triangle: 3 sides, 120° turns
- Square: 4 sides, 90° turns
- Pentagon: 5 sides, 72° turns
- Hexagon: 6 sides, 60° turns
- Octagon: 8 sides, 45° turns

---

## What's Next

**Possible enhancements:**
- Add gradual color transitions (rainbow effect)
- Animate drawing with slower speed
- Create 3D effect with nested sizing (smaller inner octagons)
- Generate random patterns (varying sizes/angles)
- Add fill colors (filled octagons instead of outlines)
- Create spirograph-style patterns (non-regular polygons)
- User input for number of sides and layers

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
