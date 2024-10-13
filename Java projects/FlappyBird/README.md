# FlappyBird Game

## Description

FlappyBird is a simple desktop game developed in Java using JavaFX. The game simulates a bird that must avoid pipes by flying through the gaps. The player controls the bird by clicking to make it fly higher, and the goal is to pass through as many pipes as possible without colliding with them. The game also features a high score system, an inventory menu, and a shop menu.

## Features

- **Bird Control**: The player can control the bird's movement using mouse clicks, making it ascend. Gravity will cause the bird to descend when no clicks are made.
- **Pipes Obstacles**: Randomly generated pipes with gaps that the bird must fly through. The game ends if the bird collides with a pipe or the ground.
- **Inventory and Shop Menu**: The game includes an inventory system for managing collected items and a shop where the player can purchase upgrades.
- **Sound Control**: A sound settings menu allows the player to control in-game audio.
- **Draggable Window**: The game window can be dragged around by the user, providing a flexible interface.
- **High Score System**: The game tracks and displays the highest score achieved by the player.

## Code Structure

### FlappyBird.java
`FlappyBird.java` contains the main game loop and initializes all essential components, such as the bird and pipes, while also managing the game's state transitions (e.g., game start, game over). It acts as the controller for the gameplay.

#### Key Responsibilities:
- **Game Initialization**: Initializes the bird, pipes, and the game window.
- **Event Handling**: Captures mouse events, primarily for controlling the bird's movement.
- **Main Game Loop**: Handles the continuous game update cycle (movement, collision detection, and rendering).
- **Game Over Detection**: Checks whether the bird has collided with pipes or the ground and ends the game when a collision occurs.
- **Score Update**: Tracks the player's progress through pipes and updates the score accordingly.

#### Key Methods:
- `start()`: Initializes the game window and elements.
- `updateGameState()`: Runs every frame to update the bird’s position, the pipe’s position, and handles game state changes (e.g., game over detection).
- `handleMouseClick()`: Controls the bird’s ascent when the player clicks the mouse.
- `checkCollision()`: Determines if the bird has collided with a pipe or the ground.

### Bird.java
`Bird.java` manages the properties and behaviors of the bird character. It is responsible for the bird’s movement, including ascending on mouse clicks and descending due to gravity. The class tracks the bird’s position and handles its interactions with the game world.

#### Key Responsibilities:
- **Bird Movement**: Controls vertical movement of the bird based on physics (gravity) and player input (mouse click for upward thrust).
- **Collision Box**: Defines the bird's boundaries for collision detection.
- **Rendering**: Updates the bird's visual representation as it moves.

#### Key Methods:
- `ascend()`: Moves the bird upwards in response to a mouse click.
- `applyGravity()`: Causes the bird to fall gradually when no input is given, simulating gravity.
- `getBounds()`: Returns the bird’s rectangular bounds used for collision detection.
- `reset()`: Resets the bird’s position and velocity when the game restarts.

### Pipe.java
`Pipe.java` manages the generation and movement of pipes on the screen. The pipes are obstacles the bird must navigate through. Pipes are continuously generated at random intervals, and their position is updated every frame to move from right to left across the screen. This class is also responsible for detecting when the bird passes through the gap between pipes to increase the score.

#### Key Responsibilities:
- **Pipe Generation**: Randomly generates pipes with variable gap heights for the bird to pass through.
- **Movement**: Moves the pipes horizontally across the screen from right to left.
- **Collision Detection**: Determines if the bird has collided with a pipe.
- **Score Tracking**: Detects when the bird successfully passes through a pipe's gap and updates the score.

#### Key Methods:
- `generatePipes()`: Creates new pipes with a random gap for the bird to fly through.
- `movePipes()`: Moves the pipes leftward, giving the effect of the bird moving forward.
- `getTopPipeBounds()`: Returns the collision boundaries for the top pipe.
- `getBottomPipeBounds()`: Returns the collision boundaries for the bottom pipe.
- `resetPipes()`: Resets the position of the pipes when the game restarts.

### Controller.java
This class serves as the controller for the game's GUI. It handles interactions with menus (shop, inventory, sound settings) and allows the user to control the game window. Additionally, it reads and updates the high score from a file.

#### Key Methods:
- `close()`: Closes the game window.
- `minimize()`: Minimizes the game window.
- `soundMenu()`: Opens the sound control menu.
- `shopMenu()`: Opens the shop menu.
- `inventoryMenu()`: Opens the inventory menu.
- `setDraggable()`: Enables the user to drag the game window around the screen.
- `setHighscore()`: Reads and displays the high score from a file.

### InventoryBox.java
This class handles the inventory menu for the game. The player can view and manage collected items. It provides a visual interface for selecting and using items in the game.

#### Key Methods:
- `display()`: Shows the inventory window to the user.
- `selectItem()`: Allows the player to choose an item from the inventory.

### BuyBox.java
This class manages the shop menu where players can buy upgrades or new items. It interacts with the game's currency or points system to allow purchases.

#### Key Methods:
- `display()`: Opens the shop window.
- `buyItem()`: Allows the player to purchase items or upgrades using in-game currency.

## Complexity Analysis

- **Main Game Loop**: The game loop updates the bird's position and checks for collisions with pipes at a time complexity of O(n), where n is the number of pipes. The pipes are stored in a list and updated every frame.
- **Collision Detection**: The collision detection algorithm runs in O(1) per frame, checking if the bird collides with a pipe or the ground.
- **Menu Interaction**: The inventory and shop menus have a low time complexity, as they only involve simple button interactions and data retrieval.

## Requirements

- Java JRE 15 or later
- JavaFX libraries for GUI components


## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.