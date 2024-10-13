# Java Pong Game

## Code Summary

This Java program is a simple Pong game implemented using the JavaFX framework. It features a graphical user interface (GUI) with basic gameplay mechanics for a two-player game. The game window includes custom buttons for minimizing and closing the application, and the gameplay is controlled using the mouse.

## Program Description and Usage

### Main Components

1. **Pong Class (`Pong`)**:
   - This is the main class extending `Application`, responsible for setting up the game window, initializing game elements, and handling the game's animation and logic.
   - The window is styled without the default decorations (`StageStyle.UNDECORATED`), providing a clean and minimalistic interface.

2. **Canvas and GraphicsContext**:
   - A `Canvas` object is used to draw all game elements, including the paddles, ball, and score. The `GraphicsContext` is obtained from the canvas to facilitate drawing operations.

3. **Timeline**:
   - The game uses a `Timeline` with a `KeyFrame` to create a loop that updates the game state every 10 milliseconds, ensuring smooth gameplay.

4. **Mouse Controls**:
   - The player's paddle (player one) is controlled by the mouse. The paddle's Y position follows the mouse's vertical movement, and clicking the mouse starts the game.

5. **Custom Buttons**:
   - The game window includes custom buttons for minimizing and closing the application. These buttons are styled with a dark theme and change appearance when hovered over.

### Gameplay Mechanics

1. **Ball Movement**:
   - The ball moves in the window, bouncing off the top and bottom edges. The direction and speed of the ball are randomized at the start of each round.

2. **AI for Player Two**:
   - Player two is controlled by a simple AI that follows the ball's vertical position, with a slight delay to simulate human reaction time.

3. **Scoring System**:
   - Each time a player misses the ball, the opposing player scores a point. The game resets the ball's position and speed when a point is scored.

4. **Paddle and Ball Collision**:
   - The ball's speed increases each time it hits a paddle. The ball also reverses direction when it collides with a paddle, adding to the difficulty as the game progresses.

5. **Game Start and Reset**:
   - The game starts when the user clicks on the canvas. If the ball goes out of bounds (i.e., a player misses), the game pauses, and the user must click again to restart.

### Custom Components

- **Minimize and Close Buttons**:
  - Custom buttons are used for minimizing and closing the game window. These buttons are styled using CSS-like properties to fit the dark theme of the game.

## Method of Generating the Game Elements

1. **Canvas Initialization**:
   - The game window is created using a `Canvas`, and the `GraphicsContext` is used to draw the game's elements. The background, paddles, ball, and score are all drawn and updated in the `run` method.

2. **Game Loop**:
   - A `Timeline` is used to create a game loop, which calls the `run` method every 10 milliseconds. This loop is responsible for updating the game state and redrawing the canvas.

3. **Event Handling**:
   - Mouse events are handled to control the player one paddle and to start the game. The minimize and close buttons also use event handlers to perform their respective actions.

## Time and Space Complexity Analysis

### Time Complexity

- **Initialization**: The initialization of the UI components and game elements occurs in constant time, O(1).
- **Game Loop**: Each iteration of the game loop updates the position of the ball and paddles and redraws the canvas. Since these operations are linear with respect to the number of elements (ball, paddles, score), the time complexity of each loop iteration is O(1).

### Space Complexity

- **Memory Usage**: The primary space usage is for storing the positions of the ball and paddles, the scores, and the UI components:
  - Ball and paddles: O(1) space.
  - UI components and state variables: O(1) space.

Overall, the space complexity is O(1).

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
