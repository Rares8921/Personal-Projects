# Text Editor Application

## Description

The Text Editor Application is a comprehensive Java-based tool designed to create, edit, and manage text files. It provides a user-friendly interface for text editing, with a rich set of features for customization, file management, and enhanced usability. The application is built using Java Swing for the user interface and standard Java I/O classes for file operations, making it a versatile and robust solution for handling text documents.

## Features

- **Create and Edit Text Files**: Users can create new text files or open and edit existing ones with ease.
- **Save Files**: Provides advanced functionality to save text files, including options to save, save as, and autosave.
- **Tabs for Multiple Documents**: Supports multiple tabs, allowing users to work on several documents simultaneously without opening multiple instances of the application.
- **Theme Change**: Users can switch between different themes to personalize the appearance of the text editor. Available themes include:
  - **Light Theme**
  - **Dark Theme**
  - **Hacker Theme**
  - **Kimbie Monokai Theme**
  - **Night Blue Theme**
- **Font Customization**: Allows users to change the font type, size, and style to suit their preferences.
- **Zoom In/Zoom Out**: Provides functionality to zoom in and out within the text area, making it easier to read or review documents.
- **Help Section**: Includes a help section that guides users on how to use the application and its features.
- **File Management**: Enhanced file management capabilities, including options to open recent files, manage file encoding, and handle unsaved changes with prompts.
- **Basic GUI**: Uses Java Swing to create a simple, yet powerful, user interface for text editing.
- **File Handling**: Efficiently manages file operations, ensuring that the user's text is correctly saved and retrieved with minimal effort.

## Structure

### Java Codebase

- **Main Class (`Main.java`)**: Contains the main method and initializes the application's GUI. This class sets up the JFrame, JTextArea, and other components, manages file I/O operations, and handles user interactions such as opening, saving files, switching themes, managing fonts, and handling tabs.

### Assets

- **Java Swing Integration**: The application uses Java Swing to build the graphical user interface, offering a responsive and customizable text editing environment.
- **Themes and Fonts**: Integrates different themes and font settings to provide a personalized user experience.

## Usage

1. **Set Up Development Environment**: Ensure you have a Java development environment ready.
2. **Compile the Project**: Use a Java compiler to build the project.
3. **Run the Application**: Execute the compiled application to launch the text editor and begin editing, managing, or creating text files.

## Example

Here is an example of how the Text Editor can be used:

- Open the application.
- Create a new document or open an existing one.
- Switch between different themes for a comfortable viewing experience.
- Adjust the font and size to your preference.
- Save your work with options like "Save As" or use the autosave feature.
- Open multiple documents in different tabs and easily switch between them.
- Use the zoom in/zoom out feature to adjust the view of your text.
- Access the help section if you need guidance on using any feature.

## Time and Complexity Analysis

### Time Complexity

- **File Operations**: The time complexity for file operations such as opening and saving files is O(n), where n is the size of the file being processed.
- **Tab Management**: Switching between tabs and managing multiple documents is handled with O(1) complexity.

### Space Complexity

- **Memory Usage**: The space complexity is O(1) for the text area and O(n) for the text content, where n is the length of the text being edited. Additional memory is used for managing multiple tabs and themes, but this remains efficient and within constant space for each individual component.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
