# Keyboard Tester

## Description

The Keyboard Application is a Java-based graphical user interface designed to visualize and interact with a keyboard's layout and functionalities. The application allows users to observe the response of various keyboard keys, including function keys, alphanumeric keys, and special keys. It is built using Java Swing, providing a visual representation of the keyboard and enabling dynamic interactions through key press events.

## Features

- **Key Highlighting**: Visually highlights keys on the UI when they are pressed, with distinct styling for different keys.
- **Customizable Key Styles**: Provides customization options for key appearance, including font, background color, and text color.
- **Key Event Handling**: Implements event listeners for various keyboard actions, allowing for real-time feedback.
- **Rounded Border Design**: Features a custom border for UI components with adjustable corner radius.

## Structure

### Java Codebase

- **Main Class**: Initializes the main application window and sets up the UI components, including labels for each key.
- **Key Event Listeners**: Manages the behavior of the application in response to keyboard events, such as key presses and releases. Key states are visually represented by changing the appearance of corresponding UI elements.
- **UI Customization**: Defines methods for setting fonts, colors, and borders of keys, offering a responsive and visually appealing user experience.

### Assets

- **Fonts and Colors**: Utilizes built-in fonts and color settings for UI customization. 

### Design Patterns and Programming Techniques

- **Event-Driven Programming**: Utilizes event listeners to respond to user interactions, ensuring that the UI reacts dynamically to keyboard inputs.
- **Encapsulation**: Encapsulates the properties and behaviors of UI components within classes, promoting modularity and ease of maintenance.

## Usage

1. **Set Up Development Environment**: Ensure you have a compatible Java development environment set up, such as an IDE with JDK installed.
2. **Compile the Project**: Use your preferred Java compiler to build the project.
3. **Run the Application**: Execute the compiled Java application to launch the GUI.

## Example

Here is an example of the application's interface:

![Keyboard GUI](example_keyboard_gui.png)

## Time and Complexity Analysis

### Time Complexity

- **Key Event Handling**: The time complexity for processing key events is O(1) per event, ensuring immediate response to user actions.
- **UI Rendering**: The rendering of the UI components, including key highlighting, operates at O(n), where n is the number of keys displayed.

### Space Complexity

- **UI Components**: The space complexity for UI components is O(n), where n represents the number of keys and other interactive elements on the screen.
- **Event Listeners**: Memory usage for event listeners is minimal, with a complexity of O(1) per listener.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.