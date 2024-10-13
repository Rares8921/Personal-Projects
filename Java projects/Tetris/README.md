# Tetris Game Application

## Description

The Tetris Game Application is a Java-based implementation of the classic Tetris game, developed using JavaFX. It offers a smooth and responsive gameplay experience, with all the core features of traditional Tetris. The application uses JavaFX for rendering and user interaction, while the game logic is meticulously implemented to provide an engaging and challenging experience.

## Features

- **Classic Tetris Gameplay**: Provides the traditional Tetris experience, with familiar mechanics for rotating and stacking blocks.
- **JavaFX User Interface**: The entire user interface, including the game board and score display, is managed within the `Tetris.java` class using JavaFX, ensuring a modern and fluid visual experience.
- **Score Tracking**: Tracks and displays the player’s score as they clear lines.
- **Responsive Controls**: Players can control the falling blocks using keyboard inputs, with smooth and responsive gameplay.
- **Game Over Detection**: Detects when the game is over and displays the final score, allowing the player to restart or exit.

## Structure

### Java Codebase

- **Main Class (`Tetris.java`)**: Serves as the main entry point for the application. It sets up the JavaFX stage and scene, handles the game loop, and manages user interactions such as block movement, rotation, and line clearing. This class also contains the logic for detecting game over conditions and updating the player's score.
- **Controller Class (`Controller.java`)**: Manages the core game logic, including block movement, collision detection, line clearing, and score calculations. It works closely with `Tetris.java` to update the game state based on user actions and time progression.
- **Form Class (`Form.java`)**: Represents the Tetris pieces (Tetrominoes). This class defines the shapes, rotations, and positioning of each piece, and handles the logic for how these pieces interact with the game board.

### Assets

- **JavaFX Integration**: The entire game interface, including the game board, score display, and control handling, is implemented using JavaFX, providing a sleek and modern user experience.
- **Tetris Pieces**: The `Form.java` class defines the various Tetrominoes used in the game, handling their shapes, rotations, and positioning on the game board.

## Usage

1. **Set Up Development Environment**: Ensure you have a Java development environment with JavaFX properly configured.
2. **Compile the Project**: Use a Java compiler to build the project.
3. **Run the Application**: Execute the compiled application to start playing Tetris.

## Example

Here is an example of how to play the Tetris Game:

- Start the game by running the application.
- Use the arrow keys to move and rotate the Tetrominoes as they fall.
- Clear lines by filling them completely with blocks.
- Continue playing until the blocks stack to the top of the screen, at which point your final score will be displayed.

## Time and Complexity Analysis

### Time Complexity

- **Game Loop**: The time complexity of the game loop is O(1) per frame, involving constant-time operations such as checking for collisions, updating block positions, and rendering the game board.

### Space Complexity

- **Memory Usage**: The space complexity is O(1) for the game state (current block, next block, score) and O(n) for the game board, where n is the number of cells on the board.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
