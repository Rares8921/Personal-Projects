# Lee Visualizer

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![Algorithm](https://img.shields.io/badge/Type-Pathfinding-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**An interactive pathfinding visualizer implementing Lee's BFS algorithm with support for obstacles, lasers, teleports, and multiple movement modes.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

Lee Visualizer is an educational tool for understanding the Lee algorithm (breadth-first search for shortest path finding) through interactive visualization. It allows you to build mazes, place obstacles, and watch the algorithm discover the optimal path in real-time.

**Core pathfinding features:**
- **Lee's algorithm** - BFS-based shortest path finder (guarantees optimal path)
- **Grid-based navigation** - 20x20 cell board for path planning
- **Start/end placement** - define source and destination points
- **Step-by-step visualization** - watch the algorithm explore nodes with animations
- **Path reconstruction** - highlights final shortest path after completion

**Advanced mechanics:**
- **Obstacles** - impassable cells (walls) marked with 'X'
- **Lasers** - directional barriers (horizontal `/`, vertical `/`, cross `+`)
- **Teleports** - paired portals for instant travel between distant cells
- **Movement modes:**
  - Horizontal/Vertical (4-directional)
  - Diagonal (8-directional)
  - Knight moves (chess knight pattern)
  - Any combination of the above

**What makes it educational:**
- Real-time wave propagation visualization (see BFS expand)
- Color-coded cells showing distance from start
- Animated path tracing from end back to start
- Help menu explaining all features
- Error handling with clear messages for invalid configurations

This tool is perfect for students learning graph algorithms, computer science teachers demonstrating BFS, or game developers prototyping pathfinding systems.

---

## Tech Stack

**Language:** Java 11  
**Framework:** JavaFX (Canvas for rendering, Timeline for animation)  
**Algorithm:** Lee's Algorithm (BFS variant for grid-based pathfinding)  
**UI:** FXML + Scene Builder for layout  
**Data Structures:** 2D arrays for grid, Queue for BFS  
**Build:** Standard Java compilation

### Architecture

MVC pattern with animation framework:

```
Main (Application Controller)
      ↓
┌─────────────────────────────────┐
│   Grid State Manager            │
│   - 20x20 char matrix           │
│   - Distance matrix (int[][])   │
│   - Object positions            │
└─────────────────────────────────┘
      ↓
Lee Algorithm (BFS):
  1. Initialize queue with start position
  2. While queue not empty:
     ├─ Dequeue cell (i, j)
     ├─ For each valid neighbor:
     │    ├─ Check obstacles/lasers
     │    ├─ Handle teleports
     │    └─ Enqueue if unvisited
     └─ Mark distance from start
  3. Backtrack from end to start using distance matrix
      ↓
Visualization Layer:
  ├─ Canvas rendering (40x40 cells)
  ├─ Timeline animation (keyframes)
  ├─ Color coding by distance
  └─ Path highlighting
```

**Lee Algorithm Details:**
```
Input: grid, start (iStart, jStart), end (iFinish, jFinish)
Output: shortest path or "no path"

matrLee[][] = -1 for obstacles, 0 for empty
Queue Q
Q.enqueue(start)
matrLee[iStart][jStart] = 1

while Q not empty:
    (i, j) = Q.dequeue()
    for each direction (di, dj):
        ni = i + di, nj = j + dj
        if valid(ni, nj) and matrLee[ni][nj] == 0:
            matrLee[ni][nj] = matrLee[i][j] + 1
            Q.enqueue(ni, nj)
            if (ni, nj) == end:
                reconstruct path and return

Path reconstruction:
Start from end, move to neighbor with distance-1, repeat until start
```

**Key Implementation Details:**
- **Grid Representation:** `char[][] matr` for objects, `int[][] matrLee` for distances
- **Direction Arrays:** `di[]`, `dj[]` for 4-directional, 8-directional, knight moves
- **Laser Logic:** Blocks movement if crossing horizontal/vertical/cross laser cells
- **Teleport System:** Paired teleports stored in 3D array `s[i][j][2]` linking positions
- **Animation:** JavaFX Timeline with KeyFrame delays showing wave progression
- **Error Handling:** Validates start/end placement, direction selection, teleport pairing

**Visual Encoding:**
- Empty cell: white
- Obstacle 'X': gray/black
- Laser '/', '|', '+': red
- Teleport 'O': blue
- Start: green
- End: red
- Distance value: color gradient (lighter = farther)
- Final path: yellow highlight

---

## Project Structure

```
Lee Visualizer/
└── src/
    ├── sample/
    │   ├── Main.java              # Core algorithm + visualization (400+ lines)
    │   ├── Controller.java        # UI event handlers
    │   ├── Open.java             # Welcome screen
    │   ├── HelpBox.java          # Help dialog
    │   ├── ErrorBox.java         # Error messages
    │   └── sample.fxml           # UI layout
    ├── imgs/
    │   ├── Knight.gif            # Knight move animation
    │   ├── Lasers.gif            # Laser demo
    │   ├── lasersWithTps.gif     # Combined demo
    │   └── lasersAndTps.gif      # Feature showcase
    ├── errorMessage.png          # Error icon
    ├── helpMenu.png              # Help screen
    └── Lee Visualizer.jar        # Compiled executable
```

---

## Getting Started

**Requirements:**
- Java 11 or higher
- JavaFX SDK 11+

**Run the visualizer:**

```bash
# Option 1: JAR file
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar "Lee Visualizer.jar"

# Option 2: Compile from source
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/sample/*.java
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin sample.Main
```

**How to use:**

1. **Setup:**
   - Left-click to place start position (green)
   - Right-click to place end position (red)
   - Select movement modes (checkboxes: horizontal, vertical, diagonal, knight)

2. **Add obstacles (optional):**
   - Click "Obstacles" mode
   - Click cells to place walls (X)

3. **Add lasers (optional):**
   - Select laser type (horizontal `/`, vertical `|`, cross `+`)
   - Click cells to place laser barriers

4. **Add teleports (optional):**
   - Click "Teleport" mode
   - Place two teleport portals (O) - they pair automatically

5. **Run algorithm:**
   - Click "Start" button
   - Watch BFS wave propagate from start
   - See shortest path highlighted at completion

6. **Reset:**
   - Click "Clear" to reset grid

---

## What's Next

Future improvements being considered:
- More algorithms: Dijkstra, A*, Greedy Best-First
- Variable grid sizes (10x10, 30x30, 50x50)
- Weighted cells (some terrain costs more to traverse)
- Maze generation algorithms (Prim's, Kruskal's)
- Export/import custom maps (JSON format)
- Performance metrics (nodes explored, time taken)
- 3D visualization option
- Algorithm comparison mode (run multiple side-by-side)

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

- **Lee's Algorithm Visualization** - Watch BFS explore nodes step-by-step
- **Obstacle Placement** - Create walls and barriers
- **Laser Mechanics** - Demonstrate horizontal, vertical, and cross lasers
- **Teleports** - Show paired portal instant travel
- **Movement Modes** - Switch between 4-directional, 8-directional, knight moves
- **Path Animation** - Highlight final shortest path reconstruction

### Recording Setup

**Prerequisites:**
```bash
# Ensure JavaFX 11+ is installed
# Run from IDE or compile to JAR
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `java -jar LeeVisualizer.jar` or run Main class
2. Open OBS Studio and set up screen capture
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Java projects\Lee Visualizer"
java -jar LeeVisualizer.jar
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
