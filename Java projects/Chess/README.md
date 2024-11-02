# ChessFX

## Overview
This Chess Application is built using JavaFX, allowing for a rich user experience with drag-and-drop functionality for moving pieces, real-time game validation, and various game state evaluations. The application supports two-player gameplay, including features like castling, en passant, and promotion.

## Features
- **Graphical Interface**: The game is presented on a chessboard with pieces that can be moved using mouse drag-and-drop actions.
- **Move Validation**: Moves are validated based on chess rules, including checks for legality, path obstruction, and whether the king is in check.
- **Game States**: The application tracks the game state, including moves made, pieces taken, and allows for undoing moves.
- **Promotion**: When a pawn reaches the opposite side of the board, the player can promote it to a different piece (queen, rook, bishop, or knight).
- **Timer Functionality**: Players can set timers, and the application manages turn switching and time limits.
- **Threefold Repetition & Fifty-Move Rule**: The game checks for conditions to declare draws based on repeated moves and insufficient material.
- **User Settings**: Players can select their names and choose the color of their pieces.
- **End-Game Animation**: When the game ends (checkmate, stalemate, etc.), an animation is displayed, providing a visual cue to the players.

## Rules of Chess
1. **Objective**: The goal of chess is to checkmate the opponent's king, meaning the king is under threat of capture and has no legal moves to escape.
2. **Piece Movement**:
   - **Pawns**: Move forward one square (or two from their starting position) and capture diagonally.
   - **Rooks**: Move any number of squares horizontally or vertically.
   - **Knights**: Move in an "L" shape (two squares in one direction and then one square perpendicular).
   - **Bishops**: Move any number of squares diagonally.
   - **Queens**: Move any number of squares in any direction.
   - **Kings**: Move one square in any direction.
3. **Special Moves**:
   - **Castling**: A move involving the king and a rook that allows for simultaneous movement under certain conditions.
   - **En Passant**: A special pawn capture that can occur immediately after a pawn moves two squares forward from its starting position.
   - **Promotion**: A pawn that reaches the opposite end of the board can be promoted to any other piece (except a king).
4. **Winning the Game**: A player wins by checkmating the opponent's king. The game can also end in stalemate, draws, or insufficient material.

## Structure of the Application
The application is structured into various components, primarily represented by Java classes:

1. **`ModelListeners`**: This class listens for changes in the game model, such as when pieces are taken, the results of move validations, and evaluations of the game state. It facilitates communication between the game logic and the user interface.
2. **`PromotionStage`**: Responsible for handling the promotion of pawns. When a pawn reaches the end of the board, this class presents a selection of pieces for the player to choose from.
3. **`StartStage`**: Manages the initial settings and preferences for the game. It allows players to set their names and choose the color of their pieces before starting the game.
4. **`BoardHandler`**: This class is responsible for setting up the chessboard and managing the visual representation of the game. It handles resizing, placing pieces, and the drag-and-drop functionalities.
5. **`DragAndDropHandler`**: Implements the drag-and-drop functionality for moving chess pieces on the board. It manages the graphical cues during the drag operation and ensures that the rules of chess are respected during moves.
6. **`Piece`**: An abstract class representing chess pieces. Each specific piece (like King, Queen, Rook, etc.) extends this class and implements its unique movement logic.
7. **`FenBuilder`**: Responsible for converting the game state into FEN (Forsyth-Edwards Notation) format, which is a standard notation for describing chess positions.
8. **`Converter`**: Provides utility methods for converting between different representations of chess pieces and their positions on the board.
9. **`MoveValidator`**: Contains the logic to validate moves based on chess rules. It checks whether moves are legal, unobstructed, and if they place the king in check.
10. **`Game`**: The main class that manages the overall game logic, including player turns, move execution, and game state evaluations.

## Code details and approaches

### Object-Oriented Programming (OOP)
The application is designed using OOP principles, which enhances modularity and code reusability. Key OOP concepts employed include:

- **Encapsulation**: Each class encapsulates its state and behavior, providing a clean interface for interacting with other components. For example, the `Piece` class defines the common properties and methods for all chess pieces, while each specific piece (e.g., `King`, `Queen`, `Knight`) extends this class to implement its unique movement logic.
- **Inheritance**: The inheritance hierarchy allows subclasses to inherit properties and methods from parent classes, reducing code duplication. For instance, the `Piece` class serves as a base class for all specific pieces, each inheriting common functionality while defining their movement rules.
- **Polymorphism**: The application uses polymorphism to treat different pieces uniformly. Methods that operate on pieces can call the same method on any subclass, allowing for flexible and extensible code.

### Design Patterns
The application employs several design patterns that contribute to its functionality and maintainability. Some of them are:

- **Observer Pattern**: This pattern is used in the `ModelListeners` class, which listens for changes in the game model (e.g., move validity, pieces taken). When the game state changes, the observers (the user interface) are notified to update accordingly. This decouples the model from the view, allowing for easier modifications and testing.
- **Command Pattern**: The application utilizes the command pattern for executing moves and actions. Moves are encapsulated as command objects that can be executed, undone, or redone, allowing for features like move history and redo functionality.
- **Dependency injection**: The application follows a form of dependency injection, especially in the `Controller` and `ModelListeners` classes, where components are passed as parameters rather than instantiated directly within classes. This practice enhances testability and flexibility, allowing for easier substitution of mock objects during unit testing.

### Functional Programming Techniques
The application incorporates functional programming concepts, particularly with Java's functional interfaces:

- **Lambda Expressions**: These are used extensively throughout the application for event handling, move validation, and conditions. For instance, in the `MoveValidator`, lambda expressions simplify the code by reducing boilerplate for functional interfaces, allowing for concise definitions of complex conditions.
- **Stream API**: The application utilizes Java's Stream API for processing collections of data in a functional style. This is evident in methods that filter and transform collections, such as those used to gather available moves or validate game states. The use of streams improves readability and allows for more expressive data manipulation.

### Event-Driven Programming
The GUI leverages event-driven programming principles, particularly in managing user interactions through JavaFX event handlers. User actions, such as dragging pieces or clicking buttons, trigger events that are handled asynchronously, allowing for a responsive and dynamic user experience.

### Exception Handling
The application implements robust exception handling to manage errors gracefully. For instance, file operations for loading piece images are wrapped in try-catch blocks, ensuring that the application does not crash due to missing resources. Instead, it logs errors to the console, which helps with debugging without interrupting the user experience.

## Time and Space Complexity
- **Time Complexity**:
  - **Move Validation**: Each move validation operation can take **O(n)** time, where **n** is the number of pieces on the board, since it may require checking each piece’s movement and the validity of the move.
  - **Game State Evaluations**: Evaluating the game state (e.g., checkmate, stalemate) involves checking possible moves and board conditions, which can also be **O(m)**, where **m** is the number of valid moves being checked. In the worst case, the application may need to evaluate all potential moves and check positions, leading to higher time complexity during complex scenarios.
  - **Path Checking**: For moves like bishops or rooks that traverse multiple squares, path checking could lead to up to **O(k)** time complexity, where **k** is the distance of the move.

- **Space Complexity**:
  - The application maintains various data structures for game states, move history, and player information. The space complexity is **O(n)** for storing the pieces and the state of the board, as well as **O(m)** for the list of past moves and game states, where **n** is the number of pieces and **m** is the number of moves.
  - **Game State Storage**: The application uses lists to maintain game states, which can grow depending on the length of the game, contributing to the overall space complexity.

## Requirements to Run the Application
- **Java Development Kit (JDK)**: Version 11 or higher.
- **JavaFX**: The application uses JavaFX for its GUI components.
- **IDE**: A suitable IDE for Java development (e.g., IntelliJ IDEA, Eclipse).
- **Operating System**: The application is platform-independent, running on any OS that supports Java and JavaFX.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
