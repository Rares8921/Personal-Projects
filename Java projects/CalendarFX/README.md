# CalendarFX

## Description

CalendarFX is a JavaFX-based application designed to provide a versatile and interactive calendar experience. The application features customizable views, a user-friendly interface, and the ability to display and manage events efficiently. It leverages the JavaFX framework for a modern UI and includes various UI components and styles to enhance user interaction.

## Features

- **Customizable Calendar Views**: Offers multiple views of the calendar, such as daily, weekly, and monthly layouts.
- **Event Management**: Allows users to add, edit, and remove events seamlessly.
- **Interactive UI Elements**: Features drag-and-drop functionality for event scheduling and rescheduling.
- **Stylized Interface**: Utilizes CSS for a visually appealing and consistent user interface.

## Structure

### Java Codebase

- **Main Class**: Initializes the application and sets up the primary stage and scene.
- **Controller Class**: Manages the application's logic, handling user interactions and updating the UI accordingly.
- **CalendarView Class**: Provides the core functionality for displaying and interacting with the calendar.
- **AnchorPaneNode Class**: Extends the JavaFX AnchorPane to support custom node behavior, particularly for calendar events.
- **Open Class**: Handles file operations, such as loading and saving event data.

### Assets

- **FXML Layout**: `sample.fxml` defines the layout of the application's UI, specifying the arrangement of UI components.
- **CSS Stylesheets**: `calendar.css` and `styles.css` are used to style the application's UI, ensuring a consistent look and feel.

### Design Patterns and Programming Techniques

- **Model-View-Controller (MVC)**: Separates the application's data, UI, and logic, promoting a clean and maintainable codebase.
- **Observer Pattern**: Utilized for updating the UI in response to changes in the application's state, such as event modifications.
- **Custom JavaFX Components**: Extends standard JavaFX components to provide enhanced functionality and custom behavior.

## Usage

1. **Set Up Development Environment**: Ensure you have a compatible JavaFX development environment set up, including the necessary JDK and JavaFX SDK.
2. **Compile the Project**: Use a Java compiler with JavaFX support to build the project.
3. **Run the Application**: Execute the compiled application to launch the calendar interface.

## Example

Here is an example of the application's calendar view:

![CalendarFX Interface](example_calendarfx.png)

## Time and Complexity Analysis

### Time Complexity

- **Event Rendering**: The time complexity for rendering events is O(n), where n is the number of events displayed.
- **UI Updates**: UI updates and interactions, such as drag-and-drop, operate at O(1) for responsiveness.

### Space Complexity

- **Event Data Storage**: The space complexity for storing event data is O(m), where m is the number of events.
- **UI Component Storage**: Space complexity for UI components depends on the number of visible elements, typically O(k).

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.