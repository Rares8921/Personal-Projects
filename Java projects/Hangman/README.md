# Java Hangman Game

## Code Summary

This Java program is a simple Hangman game implemented using the JavaFX framework. It features a graphical user interface (GUI) that allows users to interact with the game easily. The application includes different screens for starting the game, playing the game, and displaying game results.

## Program Description and Usage

### Main Components

1. **Main Class (`Main`)**:
   - This is the entry point of the application. It extends `Application` and is responsible for setting up the primary stage (window) and loading the initial scene. The `Main` class initializes the game by displaying the start screen (`Open` scene).

2. **Open Class (`Open`)**:
   - The `Open` class is responsible for the initial screen that the user sees when starting the game. It sets up the GUI components for the start screen, allowing the player to begin a new game or exit the application.
   - This class includes buttons and event handlers to navigate to the actual game scene when the game starts.

3. **Controller Class (`Controller`)**:
   - The `Controller` class handles the core game logic for the Hangman game. It manages the gameplay, including word selection, tracking guessed letters, and updating the display based on user input.
   - The class interacts with the GUI to update the displayed word, incorrect guesses, and the hangman figure as the game progresses.

### Gameplay Mechanics

1. **Word Selection**:
   - A random word is chosen from a predefined list at the start of each game. The word is hidden, and only the correct guesses are revealed as the player guesses letters.

2. **User Input**:
   - The player guesses letters through the GUI. If the guessed letter is in the word, it is revealed in its correct positions. If not, the hangman figure progresses towards completion, indicating a wrong guess.

3. **Game Progression**:
   - The game continues until the player correctly guesses the word or the hangman figure is fully drawn (indicating the player has run out of guesses).

4. **Game End**:
   - When the game ends, the player is either presented with a "You Win" or "Game Over" screen depending on the outcome. The player can then choose to start a new game or exit.

### Custom Components

- **Start Screen**:
  - The start screen is implemented in the `Open` class, providing a user-friendly interface to begin the game.
  
- **Game Logic Handling**:
  - The `Controller` class manages the game logic, ensuring smooth gameplay and interaction between the user interface and the game's core functions.

## Method of Generating the Game Elements

1. **Scene Initialization**:
   - The game window and scenes are set up in the `Main` and `Open` classes. `Main` initializes the primary stage and loads the initial scene. `Open` creates the start screen, while the `Controller` manages the gameplay screen.

2. **Game Loop**:
   - The Hangman game does not rely on a traditional game loop but instead uses event-driven programming. The game state updates in response to user actions (e.g., guessing a letter).

3. **Event Handling**:
   - The game utilizes event handlers to manage user input and navigation between different scenes. For instance, the `Open` class has buttons to start the game, while the `Controller` manages letter guesses.

## Time and Space Complexity Analysis

### Time Complexity

- **Initialization**: Setting up the GUI components and initializing the game state occurs in constant time, O(1).
- **Gameplay**: Each letter guess involves checking the word, which has a time complexity of O(n), where n is the length of the word. Updating the displayed word and checking for a win/loss condition also runs in O(n).

### Space Complexity

- **Memory Usage**: The primary space usage is for storing the game state, including the word to guess, the letters guessed, and the GUI components:
  - Word and guessed letters: O(n) space.
  - GUI components and state variables: O(1) space.

Overall, the space complexity is O(n), where n is the length of the word.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.