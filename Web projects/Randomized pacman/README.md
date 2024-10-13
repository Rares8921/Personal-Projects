# Pacman Game

## Description

Pacman is a web-based game inspired by classic maze and snack collection games. The player navigates a maze, collecting points while avoiding enemies. The game features a start screen, a leaderboard, and control buttons for navigation.

## Features

- **Interactive Gameplay**: Navigate through the maze, collect points, and avoid enemies.
- **LeaderBoard**: Displays top scores.
- **Lives and Score Display**: Shows the remaining lives and the current score.
- **Controls**: Directional buttons for movement and keyboard arrow key support.
- **Start Screen**: Initial screen to start the game.
- **Responsive Design**: Fits various screen sizes with a smartphone-like appearance.

## Structure

### HTML

- The HTML structure includes a header, start button, leaderboard, main game area, lives display, score display, and control buttons.
- Elements like the leaderboard, lives, score, and controls are organized using CSS grid layout.
- The `main` tag contains the game maze, dynamically populated with div elements representing walls, points, the player, and enemies.

### CSS

- **Font**: Uses 'Press Start 2P' for a retro game feel.
- **Grid Layout**: CSS grid for responsive and structured layout.
- **Animations**: Keyframe animations for player mouth movement and death/hit effects.
- **Styling**: Visual styles for walls, points, enemies, and control buttons.

### JavaScript

- **Game Initialization**: Functions to initialize the game, generate enemy positions, and ensure maze accessibility.
- **Movement Handling**: Functions to handle player movement and direction based on keyboard and button inputs.
- **Collision Detection**: Functions to check for collisions with points and enemies, update the score, and handle game-over scenarios.
- **Dynamic Maze Rendering**: Populates the maze grid based on the predefined maze array.
- **Event Listeners**: Listens for keydown and keyup events for player movement.

## Usage

1. **Start the Game**: Click the start button on the initial screen.
2. **Navigate the Maze**: Use the arrow keys or the directional buttons on the screen to move the player.
3. **Collect Points**: Move over points to collect them and increase your score.
4. **Avoid Enemies**: Navigate carefully to avoid colliding with enemies.
5. **Game Over**: The game ends when all points are collected or the player collides with an enemy.

## Example

Here is an example of how the webpage might look when the game is running:

![Pacman Game Example](example.png)

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.