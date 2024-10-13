# Pacman Game Project Description

## Code Summary

This project is a recreation of the classic Pacman game implemented using **JavaFX**. The game consists of a player-controlled Pacman that navigates a maze, collecting points and avoiding ghosts. The player wins by collecting all points and loses when they run out of lives. The game includes several features such as ghost AI, score tracking, high score persistence, and a fully responsive user interface. The project uses JavaFX to handle animations, user input, and graphical rendering.

## Program Description and Usage

The Pacman game is built as a desktop application using JavaFX. The application window starts without a title bar for an immersive, arcade-like experience. 

### Features:
- **Movement**: The player controls Pacman using the arrow keys or WASD. Pacman moves continuously in the selected direction.
- **Ghosts**: Six ghosts move within the maze, attempting to capture Pacman. Their movement is based on simple AI that adapts to Pacman’s position.
- **Points**: The player collects points by moving over specific cells. The total number of points required to win is pre-calculated.
- **Game States**: The game handles various states such as game start, win, loss, and pause.
- **Lives**: The player has three lives, which are displayed visually using heart icons.
- **High Score**: The game keeps track of the highest score achieved, saved in a file.

## Custom Components

The Pacman game includes various custom components to enhance the gameplay experience:
1. **Graphics and Animation**: JavaFX’s `Canvas` and `GraphicsContext` are used to render the game graphics, including Pacman, ghosts, walls, and points. The game runs in a loop, with a `Timeline` controlling the frame updates.
2. **Custom Ghost AI**: Ghosts use simple AI to move around the maze. They change direction randomly unless blocked by walls or required to follow Pacman.
3. **Lives System**: The game visually represents lives using heart icons (`ImageView`), which are updated as the player loses lives.
4. **Persistent High Score**: The high score is stored in a text file, which is loaded when the game starts and updated when the player achieves a new high score.
5. **Game State Management**: Different states such as game start, game over, and pause are handled using boolean flags and user inputs.

## Method of Points Collection

The maze consists of cells that either contain a wall or a point. When Pacman moves over a cell containing a point, the point is "collected," and the score is incremented. This is managed by checking whether a cell has the point flag set in the `screenData` array. If a point exists in the current cell, the game removes the point and increases the score.

### Instructions:
1. Run the program, and the main window will appear with a prompt to press "Space" to start.
2. Control Pacman using the arrow keys or WASD.
3. Avoid ghosts while collecting all the points in the maze.
4. If you lose all lives, the game will display a "Game Over" screen.
5. If all points are collected, the game will restart the level.
6. Press "Space" to pause the game.

## Time and Space Complexity Analysis

### Time Complexity:
- **Ghost Movement**: The ghosts are moved in each frame, which has a time complexity of O(1) per frame. There are `n` ghosts, so the total time complexity for ghost movement per frame is O(n).
- **Player Movement**: Similar to ghosts, Pacman’s movement occurs at O(1) per frame.
- **Collision Detection**: Checking for collisions (Pacman vs. ghosts, Pacman vs. walls) also occurs at O(1) per frame.
- **Total**: Since all game actions occur in real-time with constant updates per frame, the overall time complexity per frame is O(n), where `n` is the number of ghosts. However, the complexity of handling user input, updating positions, and rendering graphics remains constant for most actions.

### Space Complexity:
- **Maze Data**: The maze is stored as a 2D array (`screenData`) of size `N_BLOCKS × N_BLOCKS`, which is fixed at 15 × 15. This takes up O(N^2) space.
- **Ghosts' Data**: Each ghost has several properties (`x`, `y`, `dx`, `dy`, `speed`), so the total space complexity for the ghosts is O(n).
- **Total**: The space complexity is dominated by the maze size and the number of ghosts, so it is O(N^2 + n), where `N` is the number of blocks per side of the maze (15 in this case), and `n` is the number of ghosts.


## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.