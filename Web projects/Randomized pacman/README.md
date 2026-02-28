# Randomized Pacman

<div align="center">

![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![Canvas API](https://img.shields.io/badge/Canvas%20API-FF6F00?style=for-the-badge&logo=html5&logoColor=white)

**Classic Pacman game with procedurally generated mazes. Every playthrough is different, every maze is solvable.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is Pacman reimagined with procedural maze generation. Built with pure JavaScript and HTML5 Canvas, it creates random mazes that are guaranteed to be solvable while maintaining classic gameplay.

**The good stuff:**
- **Procedural Generation:** Every game creates a unique, random maze layout
- **Maze Validation:** BFS algorithm ensures all pellets are reachable before game starts
- **Smart Enemy Placement:** Ghosts spawn in valid positions that don't block critical paths
- **Classic Gameplay:** Navigate mazes, collect pellets, avoid ghosts, rack up high scores
- **Lives System:** Three lives per game with visual indicators
- **Leaderboard:** Track high scores across sessions
- **Keyboard Controls:** Arrow keys or WASD for movement
- **Responsive Canvas:** Clean rendering with smooth animations

**What makes it different:**
- No two games are the same - infinite replayability
- Smart maze generation with pathfinding validation
- Pure vanilla JavaScript - no frameworks, no dependencies
- Runs entirely in the browser, no backend needed
- Clean, readable code perfect for learning game development

The game validates maze solvability using breadth-first search to ensure players can always reach all pellets. Ghost positions are randomly generated but constrained to maintain fair gameplay.

---

## Tech Stack

**Frontend:** HTML5 + CSS3 + Vanilla JavaScript  
**Rendering:** HTML5 Canvas API  
**Algorithms:** BFS (pathfinding validation), procedural maze generation  
**State Management:** Pure JavaScript (no frameworks)

### Architecture

Simple game loop architecture with procedural generation:

```
Game State Manager
       ↓
Maze Generation Algorithm (random layout)
       ↓
BFS Validation (ensure solvability)
       ↓
Enemy Placement (random valid positions)
       ↓
Game Loop (60 FPS)
       ↓
Canvas Renderer (DOM updates)
```

**How maze generation works:**

1. **Initial Layout:** Start with a base 10x10 grid (walls on borders)
2. **Random Placement:** Randomly place internal walls avoiding player start position
3. **Enemy Spawning:** Generate random positions from free cells
4. **Validation:** Run BFS from player position to verify all pellets are reachable
5. **Retry Logic:** If maze is unsolvable, regenerate enemy positions and validate again
6. **Game Start:** Once validated, render maze and begin gameplay

**BFS Validation Algorithm:**
```javascript
function isAccessible(maze, startX, startY) {
    // Breadth-first search from player position
    // Visit all reachable cells (0 = pellet, 2 = player)
    // Return false if any pellet (0) is unreachable
    // Return true if all pellets can be collected
}
```

**Game Loop:**
```
User Input (keyboard events)
       ↓
Update Player Position (collision detection)
       ↓
Update Enemy Positions (AI movement)
       ↓
Check Win/Loss Conditions
       ↓
Render Frame (Canvas draw calls)
       ↓
Request Next Frame (60 FPS)
```

---

## Project Structure

```
Randomized pacman/
├── index.html              # Game container and UI elements
├── script.js               # Game logic, maze generation, BFS validation
├── style.css              # Styling for game board and UI
├── images/
│   └── icon.png           # Pacman icon/avatar
└── README.md             # This file
```

All game logic, maze generation, and validation happens in `script.js`. The maze is a 2D array where:
- `1` = Wall (black cells)
- `0` = Pellet (collectible points)
- `2` = Player (Pacman)
- `3` = Enemy (Ghosts)

---

## Getting Started

**What you need:**
A modern web browser with JavaScript enabled. That's it.

**Setup (10 seconds):**

```bash
# Navigate to the project
cd "d:\Personal-Projects\Web projects\Randomized pacman"

# Open in browser
start index.html
```

**Or use a local server:**

```bash
# Python 3
python -m http.server 8000

# Node.js
npx http-server -p 8000
```

Then open `http://localhost:8000` in your browser.

**How to play:**
1. Click the "Start" button to generate a new maze
2. Use **arrow keys** or **WASD** to move Pacman
3. Collect all pellets (white dots) to win
4. Avoid the ghosts - they'll cost you a life
5. Beat your high score and climb the leaderboard

**Controls:**
- **↑ / W** - Move Up
- **↓ / S** - Move Down
- **← / A** - Move Left
- **→ / D** - Move Right

---

## Game Mechanics

**Scoring:**
- Each pellet collected: +10 points
- Complete maze: Bonus points based on time and lives remaining
- High scores saved in leaderboard

**Lives:**
- Start with 3 lives
- Lose a life when touching a ghost
- Game over at 0 lives

**Enemy AI:**
- Ghosts move randomly within the maze
- Cannot pass through walls
- Movement speed increases as game progresses (optional feature)

**Win Condition:**
- Collect all pellets in the maze
- Survive with at least 1 life remaining

---

## What's Next

**Planned improvements:**
- Add power pellets for ghost-eating mode
- Implement smarter ghost AI (chase/scatter modes)
- Add sound effects and background music
- Create multiple difficulty levels (maze size, ghost count, speed)
- Add touch controls for mobile devices
- Implement maze size customization (10x10, 15x15, 20x20)
- Add animations for Pacman mouth and ghost movement
- Persistent leaderboard with localStorage
- Add level progression (maze complexity increases)
- Implement special bonus fruits

**Want to contribute?**
The codebase is vanilla JavaScript - easy to understand and extend. Focus areas: AI improvements, visual effects, mobile support.

---

## Deployment

**Static Hosting (Free):**
- **GitHub Pages:** Push to repo, enable Pages, live instantly
- **Netlify:** Drag and drop the folder, deployed in seconds
- **Vercel:** `vercel deploy` from the command line
- **AWS S3:** Static website hosting with CloudFront CDN

**Docker (optional):**

```dockerfile
FROM nginx:alpine
COPY . /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

```bash
docker build -t randomized-pacman .
docker run -p 8080:80 randomized-pacman
```

**Cloudflare Pages:**
```bash
# Install Wrangler
npm install -g wrangler

# Deploy
wrangler pages publish .
```

---

## Technical Details

**Maze Representation:**
The maze is a 2D JavaScript array:
```javascript
let maze = [
    [1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    [1, 0, 0, 1, 0, 0, 0, 0, 2, 1],
    [1, 0, 0, 0, 0, 0, 0, 1, 1, 1],
    // ... more rows
];
```

**BFS Validation:**
- Time Complexity: O(n × m) where n, m are maze dimensions
- Space Complexity: O(n × m) for visited array
- Ensures every pellet is reachable from player start position

**Rendering:**
- Canvas-based rendering for smooth performance
- Cell-by-cell drawing for walls, pellets, player, and enemies
- 60 FPS game loop using `requestAnimationFrame`

**Collision Detection:**
- Check adjacent cell before moving player
- Prevent movement into walls (cell value === 1)
- Detect player-enemy collision for life deduction

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

- **Procedural Maze Generation** - Show multiple game starts with different random mazes
- **Gameplay Mechanics** - Navigate maze, collect pellets, avoid ghosts
- **BFS Validation** - Explain how maze solvability is guaranteed
- **Ghost AI** - Demonstrate random but constrained ghost movement
- **Lives System** - Show what happens when hit by a ghost
- **Score Tracking** - Display score accumulation and high scores

### Recording Setup

**Prerequisites:**
```bash
# No installation needed - runs in browser
# Simply open index.html in your browser
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Open `index.html` in your browser
2. Open OBS Studio and set up screen capture
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Web projects\Randomized pacman"

# Open in default browser
start index.html

# Or use a local server
python -m http.server 8000
# Then open http://localhost:8000/index.html
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary Software** - All rights reserved.

This software is provided for personal use and evaluation only. No license is granted for commercial use, modification, or distribution without explicit written permission from the author.

For licensing inquiries, please contact the repository owner.