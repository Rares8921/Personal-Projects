# AutoClicker Application

## Description

The AutoClicker Application is a Java-based tool designed to automate mouse clicks at a specified rate. The application leverages JavaFX for its interface and utilizes the `jnativehook` library to listen for global mouse and keyboard events. It is designed to be a lightweight yet powerful utility for automating repetitive clicking tasks, with customizable settings and robust event handling.

## Features

- **Automated Mouse Clicking**: Automates mouse clicks at a user-defined rate, with support for both left and right mouse buttons.
- **Global Event Listening**: Uses the `jnativehook` library to capture global mouse and keyboard events, allowing for seamless toggling and control of the auto-clicker.
- **Customizable Click Settings**: Allows users to adjust the clicks per second (CPS) and toggle between different mouse buttons.
- **Toggle Functionality**: Provides easy on/off toggling of the auto-clicker through designated keys or mouse buttons.
- **Multi-Button Press Support**: Enables pressing multiple mouse buttons simultaneously for enhanced functionality.

## Structure

### Java Codebase

- **Main Class (`AutoClicker.java`)**: Handles the core functionality of the auto-clicker, including initialization of the robot for simulating mouse clicks, managing the click events, and setting up global listeners for mouse and keyboard actions.
- **Key Listener Class (`AutoKeyListener.java`)**: Implements global key event listening, triggering the auto-clicker’s toggle and other key-based functions.
- **Mouse Listener Class (`AutoMouseListener.java`)**: Manages global mouse events, including detecting specific mouse button clicks to control the auto-clicker's behavior.
- **Controller Class (`HelloController.java`)**: Likely handles the user interface interactions, managing the connection between the user’s inputs in the UI and the core auto-clicker functionality.

### Assets

- **JavaFX Integration**: Uses JavaFX for building the user interface, providing a modern and responsive GUI for the application.
- **Global Event Handling**: Utilizes the `jnativehook` library to manage global events, enabling the application to detect and respond to inputs even when it’s not in focus.

## Usage

1. **Set Up Development Environment**: Ensure you have a compatible Java development environment with JavaFX and the `jnativehook` library configured.
2. **Compile the Project**: Use a Java compiler to build the project, ensuring that all dependencies (like `jnativehook`) are included.
3. **Run the Application**: Execute the compiled application to launch the auto-clicker and interact with its GUI.

## Example

Here is an example of how the AutoClicker can be used:

- Start the application.
- Set your desired clicks per second (CPS).
- Toggle the auto-clicker on or off using the designated hotkeys or mouse buttons.

## Time and Complexity Analysis

### Time Complexity

- **Click Event Handling**: The time complexity for handling click events is O(1) per click, ensuring consistent performance regardless of the click rate.
- **Event Listening**: The event listeners operate with O(1) complexity for each event, providing immediate response to user inputs.

### Space Complexity

- **Event Listener Memory Usage**: The space complexity for maintaining event listeners is minimal, with a constant space requirement O(1) per listener.
- **Robot Instance**: The memory usage for the Robot instance is also minimal, with a constant space complexity O(1).

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
