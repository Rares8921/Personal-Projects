# Snake Game Code Description

## Code Summary
The provided Java code implements the classic Snake game using JavaFX for the graphical interface and event handling. The game consists of a snake that grows in size as it "eats" food, with the goal of avoiding collisions with the edges of the screen or with the snake's own body. The snake is controlled via keyboard input, and the gameplay speeds up as the player progresses. The score is tracked, and the player can restart the game after losing by pressing the spacebar.

## Program Description and Usage
This Snake game uses the JavaFX framework to create a window with a graphical canvas on which the game is displayed. It features a responsive interface where the snake moves in four directions (up, down, left, and right) and grows longer as it consumes food items that randomly spawn on the canvas. The game ends when the snake either collides with the screen boundaries or runs into itself. 

### Features:
- **Keyboard Controls:** 
  - Arrow keys or W, A, S, D control the snake's movement.
  - Pressing the spacebar starts the game and pauses/resumes it. 
- **Score Tracking:** The score increases each time the snake eats food. The high score is saved between game sessions.
- **Randomized Food:** Each time food is consumed, new food is generated at a random location that is not occupied by the snake's body.
- **Collision Detection:** The game handles collision with walls and the snake’s own body, ending the game upon such collisions.
  
### Running the Game:
1. The game starts with a blank grid and a prompt to press the spacebar to start.
2. Use the keyboard to navigate the snake to the food.
3. The game automatically tracks the player's score and compares it to the saved high score.
4. The game ends when the snake collides with itself or the border, displaying a "Game Over" message.
5. Press the spacebar to restart the game after a loss.

## Components:
- **Main Class:** This class handles the main game logic, including rendering the graphics, controlling the game state, and processing user input.
- **Controller Class:** This class manages UI controls for minimizing and closing the game window, as well as enabling draggable functionality for the game window.

## Custom Components:
- **Random Food Generation:** The `generateFood()` method ensures that food is not placed on the snake's body. A random image from a list of food images is assigned to each new food item.
- **Graphical Rendering:** The game uses JavaFX’s `Canvas` to draw all elements (snake, food, grid, and text) on the screen. The snake and food are dynamically rendered as colored rectangles and images, respectively.
- **Game State and Score Tracking:** The game tracks the score in real time and updates the high score if the player beats their previous record.

## Key Methods:
- **`run(GraphicsContext gct)`**: The core game loop that handles snake movement, collision detection, and rendering.
- **`gameOver()`**: Checks if the snake has collided with the walls or itself and sets the game state to over.
- **`generateFood()`**: Randomly generates the food's position on the grid.
- **`eatFood()`**: Checks if the snake's head has reached the food's position and adds a new segment to the snake’s body.
- **`drawScore()`**: Renders the current score and high score on the canvas.

### Main Game Elements:
- **Canvas and Graphics Context:** The game elements (snake, food, grid, and text) are drawn using the `GraphicsContext` object on a `Canvas` element. 
- **Snake:** The snake is represented by a list of points (`ArrayList<Point>`), with each point corresponding to a square on the grid.
- **Food:** Food is randomly generated at a location that does not intersect with the snake's body, and a random image is assigned to each food item from a set of images.
- **Direction Control:** The snake's movement is determined by the current direction (right, left, up, down), and its speed increases after every collision with food.

## Time Complexity Analysis
- **Snake Movement:** The snake moves in constant time `O(1)` for each game tick, as only the head position is updated, and the rest of the body follows the previous position.
- **Collision Detection:** Checking for collisions with walls or itself involves scanning the body of the snake, which has a time complexity of `O(n)`, where `n` is the length of the snake.
- **Food Generation:** Generating food involves finding a random position on the grid that is not occupied by the snake. In the worst case, this can be `O(n)`, where `n` is the number of points on the grid.
- **Overall Time Complexity:** The overall time complexity per game tick is `O(n)`, where `n` is the length of the snake, as updating the snake and checking for collisions depends on its length.

## Space Complexity Analysis
- **Snake Representation:** The snake is stored as an `ArrayList` of `Point` objects, so the space complexity is `O(n)`, where `n` is the number of segments in the snake's body.
- **Grid Representation:** The grid does not need to be stored explicitly since it is dynamically rendered each frame, but rendering the grid itself takes constant space, `O(1)`, relative to the grid's dimensions.
- **Food Storage:** The food's position is stored as two integers (x and y coordinates), which takes constant space, `O(1)`.

Overall, the space complexity of the game is `O(n)`, where `n` is the length of the snake.


## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.