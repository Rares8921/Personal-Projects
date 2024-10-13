# Tic-Tac-Toe Game

## Summary
The provided Java program is a graphical implementation of the classic Tic-Tac-Toe game. It utilizes the Swing framework to create a user interface (UI) where two players can take turns placing their marks (X or O) on a 3x3 grid. The game keeps track of the players' points, determines the winner, and allows players to reset the game. The game's logic includes checking for winning conditions, detecting draw scenarios, and managing player turns.

## Code Structure

### 1. **Class `Main`**
The `Main` class extends `JFrame` and serves as the main window for the application. It contains several components, methods, and inner classes to handle the game logic and UI.

#### Key Components:
- **Game State Variables:** 
  - `playerPoints`, `opponentPoints`: Track the points for each player.
  - `playerTurn`: A boolean flag indicating the current player's turn.
  - `playerWin`, `opponentWin`: Flags indicating if a player has won.
  - `table`: A 3x3 integer array representing the game board (0 for empty, 1 for player 1, 2 for player 2).

- **UI Components:**
  - `JButton`: Represents the grid buttons and the "Play" button.
  - `JLabel`: Displays player scores.

#### Key Methods:
- **`wonGame(int[][] v)`:** Checks the game board to determine if a player has won by forming a straight line (horizontal, vertical, or diagonal).
- **`finishedGame()`:** Determines if the game has ended in a draw by checking if all cells are filled and no player has won.
- **`resetMatrix()`:** Resets the game board and win flags.
- **`reset()`:** Resets the UI components to their initial state.

#### Constructor `Main()`
Initializes the UI components, sets up the board, and defines the appearance and behavior of the game. It includes configuration for colors, borders, fonts, and mouse event listeners.

### 2. **Game Logic Implementation**
The game starts with all buttons disabled except the "Play" button. Once the "Play" button is pressed, the grid buttons are enabled, allowing players to take turns. The game checks for winning conditions after each move, and if a win or draw is detected, the game is reset.

## Time and Space Complexity

### Time Complexity
- **Game Initialization:** O(1) - Constant time to initialize the game components.
- **Game Play (`wonGame` method):** O(1) - The method checks a fixed number of conditions (8 possible winning lines).
- **Checking Draw (`finishedGame` method):** O(n^2) - Iterates over the 3x3 board, resulting in a constant operation count of 9.

### Space Complexity
- **Static Space:** O(1) - The space required for primitive variables and constants.
- **Dynamic Space:** O(n^2) - The space used by the 3x3 `table` array.

## Summary
This Java program implements a simple Tic-Tac-Toe game with a graphical user interface using Swing. It handles player turns, determines the game outcome, and supports resetting the game. The game's logic is straightforward, with time complexity mainly being constant for most operations, except for the draw check, which scales with the board size (fixed at 3x3). The space complexity is also constant due to the fixed size of the game board and the use of a small number of additional variables.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.