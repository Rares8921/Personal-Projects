# JavaFX Calculator

This project is a **JavaFX-based calculator** that allows users to perform calculations and enter values through a custom graphical user interface (GUI). It consists of two primary classes: `Controller.java` and `InputBox.java`, which together provide functionality for basic arithmetic operations, input handling, and a clean user interface.

## Key Components

### 1. Controller.java

The `Controller.java` class serves as the backbone of the calculator. It manages the input, calculation logic, and button interactions. This class connects the UI buttons to the appropriate calculation functions and handles user interaction.

**Main Features**:
- **Button Event Handling**: Listens for button clicks to perform operations like addition, subtraction, multiplication, division, and resetting values.
- **Input Processing**: Collects numeric input from users and passes it to the appropriate functions.
- **Result Display**: After performing the calculation, it updates the calculator’s display with the result.
- **Error Handling**: Displays appropriate messages or indicators when the user attempts to perform an invalid operation (e.g., division by zero).
  
The `Controller.java` class coordinates between user input, mathematical operations, and the UI display to ensure a smooth user experience.

### 2. InputBox.java

The `InputBox.java` class handles custom input dialogs, allowing users to input numbers manually. This is especially useful for advanced calculators that may require precise input beyond button presses.

**Main Features**:
- **Custom Input Dialog**: Provides a modal input box where users can manually enter numbers.
- **Input Validation**: Ensures that only valid numeric values are accepted, including support for commas as decimal points and negative numbers.
- **Keyboard Interaction**: Supports keyboard events for entering and adjusting input (e.g., adding/removing negative signs, adjusting decimal points).
- **Window Dragging**: Allows the user to drag the input box around the screen.
- **Tooltips and UI Enhancements**: Provides tooltips to guide users in interacting with the input box effectively.

### How the Calculator Works

1. **User Interaction**: The user can either enter numbers using the on-screen buttons or open the custom input dialog (managed by `InputBox.java`) to enter more precise values.
2. **Operation Selection**: Once the user has entered their numbers, they select the desired arithmetic operation (addition, subtraction, multiplication, division).
3. **Calculation**: After selecting an operation, the `Controller.java` class processes the input and performs the calculation.
4. **Result Display**: The result of the operation is displayed on the calculator’s screen. Any errors, such as invalid input or division by zero, are also managed by the `Controller.java` class.

### Project Flow

- **Controller.java**: Manages the overall flow of the calculator, handling button interactions, mathematical operations, and UI updates.
- **InputBox.java**: Provides an additional method for entering numbers manually, complete with validation and customization for more complex inputs.

### Technologies Used
- **JavaFX**: For the user interface.
- **FXML**: To define and structure the calculator layout and input box.
- **Event Handling**: JavaFX’s event listeners are used for button clicks, key presses, and window dragging.

### Conclusion

This JavaFX calculator project is designed to provide a clean, user-friendly experience for performing arithmetic operations. The integration of a custom input dialog (via `InputBox.java`) allows users to enter values with precision, making it suitable for both simple and advanced calculations. The project’s clear structure and modular design make it easy to extend and customize.


## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.