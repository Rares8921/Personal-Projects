# Dino Game

This JavaFX-based project recreates a simplified version of the popular "Dino" game, featuring a dinosaur running through a desert while dodging enemies and obstacles. The game mechanics such as movement, jumping, gravity, and background animation are handled using JavaFX animation timelines. The game also incorporates sound effects for interaction feedback.

## Core Classes and their Functionality

### Dino Class (Player Character)

The `Dino` class is the main player character class responsible for the dinosaur's actions, particularly running and jumping. The player's movement and jumping mechanics are handled through a velocity and gravity system.

#### Jumping Mechanism

The `Dino` class uses a simple physics-based system to control jumps. The jump starts by setting an upward velocity. As the game progresses, gravity continuously affects the dinosaur, decreasing the velocity and bringing the character back to the ground. The following parameters control the Dino's movement:

- `velocityY`: Determines the current vertical velocity of the Dino. A negative value sends the Dino upwards, while gravity reduces the velocity to bring it back down.
- `gravity`: Constantly decreases the Dino's `velocityY` to simulate gravity. Once the velocity reaches 0, the Dino starts to descend.

The Dino's Y position is updated in each frame of the game loop based on the velocity.

**Key Aspects:**

- `velocityY`: The current vertical speed of the Dino.
- `gravity`: Simulates the downward force bringing the Dino back to the ground.
  
#### Jumping Logic

When the jump key is pressed, `velocityY` is set to a negative value (e.g., `-10`), which moves the Dino upwards. Gravity, a constant value, is then added each frame to gradually reduce `velocityY` until it reaches 0 and the Dino falls back down. Once `velocityY` becomes positive, the Dino descends, creating the arc of the jump.

### Enemy Class (Obstacles)

The `Enemy` class represents obstacles that the Dino must avoid. These enemies move from right to left, giving the illusion that the Dino is running. The movement of enemies is done using JavaFX's `Timeline` class.

#### Animation

The movement of the enemies across the screen is handled by a JavaFX `Timeline`, which updates the enemy's position at regular intervals (similar to asynchronous threading). As the timeline progresses, the enemy moves toward the Dino, creating a dynamic game environment.

### Cloud Class (Background Objects)

The `Cloud` class simulates the movement of clouds in the background to enhance the visual depth and create a dynamic environment. The clouds move slowly from right to left, providing the illusion of a moving sky.

#### Animation

Similar to the `Enemy` class, the cloud's position is updated periodically using a JavaFX `Timeline`. This timeline moves the clouds from the right side of the screen to the left, resetting their position once they move off-screen. This creates a continuous, looping background animation.

### Floor Class (Ground Animation)

The `Floor` class is responsible for the movement of the ground. Like the clouds and enemies, the floor moves from right to left, giving the illusion that the Dino is running through a desert landscape.

#### Animation

The floor uses a `Timeline` to update its position at regular intervals. As the game runs, the floor tiles shift to the left. When one tile moves off-screen, it is repositioned to the right to simulate an infinite scrolling ground. The timeline ensures smooth transitions and continuous ground movement, much like background scrolling in 2D games.

### Sound Class (Audio Effects)

The `Sound` class is used to play sound effects during gameplay. It wraps around JavaFX's `AudioClip` class to load and play sounds for specific game events such as jumps or collisions.

**Key Aspects:**

- `playClip()`: This method is responsible for playing the sound effect when triggered by certain game events.
- `AudioClip`: Loads sound files (e.g., `.wav` or `.mp3`) and plays them asynchronously to provide instant feedback to the player.

The sound effects are played asynchronously using the `AudioClip` class, allowing sounds to play independently of the game logic and animation, ensuring a smooth audio experience.

## Time and Space Complexity Analysis

### Dino (Jump and Gravity System)
- **Time Complexity**: The jumping and gravity mechanism is updated in each frame of the game loop, so its time complexity is O(1) per frame, as it involves constant-time operations (updating velocity and position).
- **Space Complexity**: The space complexity is O(1) since the system only stores a few variables (`velocityY`, `gravity`) for controlling the jump.

### Enemy, Cloud, and Floor (Animations)
- **Time Complexity**: The animations for enemies, clouds, and the floor are updated in constant time, O(1) per frame. The JavaFX `Timeline` class ensures that each frame is processed at regular intervals, and each object (enemy, cloud, floor tile) is moved based on a fixed time step.
- **Space Complexity**: The space complexity is O(n), where `n` is the number of enemies, clouds, or floor tiles that need to be stored and updated in memory.

### Sound Class
- **Time Complexity**: The sound effects play asynchronously when triggered, so their time complexity is O(1) for playing each clip.
- **Space Complexity**: The space complexity is O(1) since the class only stores a reference to the audio clip being played.

### Summary

This project combines JavaFX timelines and physics-based movement to simulate the Dino's jump and run dynamics, along with smooth background animations and sound effects.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
