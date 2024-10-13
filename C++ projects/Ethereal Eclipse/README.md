# Ethereal Eclipse

## Description

Ethereal Eclipse is an immersive 2D action-adventure game developed using C++. The game features dynamic character interactions, challenging enemy encounters, and a rich, detailed environment. The project includes comprehensive game mechanics, a robust tile system, and various visual assets to create an engaging player experience. Debugging and performance analysis were conducted using native code analysis tools to ensure optimal game performance.

## Features

- **Dynamic Character Interactions**: Engage with the game world through various character abilities and actions.
- **Challenging Enemy Encounters**: Battle a variety of enemies with unique behaviors and attack patterns.
- **Rich Environment**: Explore detailed environments with diverse textures and interactive elements.
- **Robust Tile System**: Utilizes a sophisticated tile map system for environment design.
- **Debugging and Performance Analysis**: Ensures a smooth gaming experience with extensive debugging and performance optimization.

## Structure

### C++ Codebase

- **Game Mechanics**: Implements core gameplay features such as character movement, combat, and enemy 'AI'.
- **Tile System**: Manages the game's environment using a tile-based approach for efficient rendering and interaction.
- **Debugging Tools**: Uses native code analysis tools to detect and resolve performance bottlenecks and bugs.
- **Memory Management**: Utilizes smart pointers (e.g., `std::unique_ptr` and `std::shared_ptr`) to ensure efficient and safe memory usage, preventing leaks and dangling pointers.

### Assets

- **Textures**: Includes a wide array of textures for characters, environments, and user interface elements.
- **Shaders**: Custom vertex and fragment shaders for advanced graphical effects.

### Design Patterns and Programming Techniques

- **Factory Pattern**: Used for creating instances of different game objects, such as enemies and tiles, allowing for easy scalability and maintenance.
- **Prototype Pattern**: Utilized in the Item class to create new item instances by copying existing objects, allowing for efficient item creation.
- **Observer Pattern**: Implements an event system where game entities can subscribe to events, allowing for decoupled and maintainable code.
- **State Pattern**: Manages the different states of the game and its entities, such as the main menu, gameplay, and pause states.
- **Component-Based Design**: Uses composition over inheritance to create flexible and reusable game entity behaviors.
- **RAII (Resource Acquisition Is Initialization)**: Ensures that resources such as textures and file handles are properly managed and released.

## Usage

1. **Set Up Development Environment**: Ensure you have a compatible C++ development environment set up.
2. **Compile the Project**: Use your preferred C++ compiler to build the project.
3. **Run the Game**: Execute the compiled binary to start the game.

## Example

Here is an example of the game's environment featuring various interactive elements:

![Ethereal Eclipse Environment](example_environment.png)

## Time and Complexity Analysis

### Time Complexity

- **Game Loop**: The main game loop operates at O(1) per frame, ensuring consistent performance regardless of the game's complexity.
- **Tile Rendering**: The time complexity for rendering tiles is O(n), where n is the number of visible tiles on the screen. This is efficient given the spatial locality of the rendering process.
- **Collision Detection**: Uses spatial partitioning techniques, such as a quadtree or grid, reducing the average time complexity of collision detection to O(log n) for query operations. Broad-phase and narrow-phase collision detection are used to optimize performance.
  - **Broad-phase**: Quickly eliminates pairs of objects that cannot collide using a spatial partitioning method, typically O(n log n) due to sorting and partitioning.
  - **Narrow-phase**: Detailed collision detection on the remaining pairs, usually O(k) where k is the number of pairs from broad-phase.
- **Animation System**: Manages character animations with time complexity O(1) per frame, as animations are updated based on a fixed frame rate and state changes.
- **AI Behavior**: The AI behavior for enemies is based on simple trigger conditions and calculations, with a time complexity of O(1) for each evaluation, ensuring efficient and responsive enemy actions.

### Space Complexity

- **Asset Storage**: The space complexity for storing game assets (textures, tiles) is O(m), where m is the total number of assets. Efficient data structures are used to manage these assets in memory.
- **Game State**: The space complexity for maintaining the game state is O(1), as it primarily depends on the fixed-size data structures for storing player stats, current level information, and other game-specific data.
- **Entity Component System (ECS)**: Manages game entities and their components with space complexity O(e + c), where e is the number of entities and c is the number of components. This allows for efficient querying and updating of entity states.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
