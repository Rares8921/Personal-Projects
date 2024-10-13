# Astronomy Picture of the Day (APOD) Viewer

## Description

The Astronomy Picture of the Day (APOD) Viewer is a JavaFX-based application that interacts with NASA's APOD API to display daily images of space phenomena, along with descriptions. The application leverages HTTP requests to fetch data from the API and presents it in a simple and user-friendly interface. This project is designed to provide space enthusiasts with easy access to NASA's rich collection of astronomical images.

## Features

- **NASA APOD API Integration**: Fetches daily images and descriptions directly from NASA’s Astronomy Picture of the Day API.
- **JavaFX User Interface**: A modern and interactive GUI using JavaFX to display the image and its details (title, description, etc.).
- **Error Handling**: Implements error handling to manage issues such as failed API requests or missing image data.
- **JSON Parsing**: Efficiently parses the JSON response from NASA's API to extract and display relevant data.
- **Modular Design**: Separates the concerns of UI management, API interaction, and data handling for improved maintainability.

## Structure

### Java Codebase

- **Main Class (`Main.java`)**: Initializes the JavaFX application, sets up the primary stage, and loads the FXML for the UI layout.
- **Controller Class (`Controller.java`)**: Handles user interactions, such as triggering the API request to fetch a new APOD image. It also manages the display of the image, title, and description in the UI.
- **APOD Class (`APOD.java`)**: Manages the HTTP requests and JSON parsing for retrieving the APOD data from NASA’s API. It structures the data into easily accessible fields such as the image URL, title, and explanation.

### Assets

- **JavaFX Layouts**: Utilizes FXML to define the structure of the UI, separating the design from the business logic.
- **NASA API**: Accesses NASA's APOD API for daily images, making use of standard HTTP libraries in Java to fetch and handle the data.
- **JSON Parsing**: Parses the API response using libraries such as `org.json` to extract relevant data (title, image, description).

### Design Patterns and Programming Techniques

- **Model-View-Controller (MVC)**: The application follows the MVC pattern, where the `Main.java` sets up the view, `Controller.java` handles user input and events, and `APOD.java` acts as the model that interacts with the NASA API and provides data.
- **Separation of Concerns**: Clearly divides API interaction, UI logic, and data presentation to ensure maintainability and scalability.
- **Error Handling**: Implements error handling for HTTP requests and JSON parsing to manage potential issues such as connectivity problems or missing data.

## Usage

1. **Set Up Development Environment**: Ensure that your Java environment is configured with JavaFX and JSON libraries for API interactions.
2. **Compile the Project**: Use a Java compiler or IDE to build the project, ensuring that dependencies for JavaFX and HTTP requests are correctly configured.
3. **Run the Application**: Launch the compiled application to view the Astronomy Picture of the Day and related information.

## Example

Here is an example of how the application functions:

- Open the application.
- The daily Astronomy Picture of the Day from NASA is fetched and displayed.
- The user can view the title and a detailed description of the astronomical image.

## Time and Complexity Analysis

### Time Complexity

- **API Request**: The time complexity of making an API request to NASA's APOD API is O(1), as it is a constant-time operation regardless of the data being retrieved.
- **JSON Parsing**: The time complexity of parsing the JSON response is O(n), where n is the number of fields in the API response.
- **UI Updates**: Updating the JavaFX UI components is an O(1) operation, ensuring smooth user experience.

### Space Complexity

- **API Data Storage**: The space complexity of storing the APOD data (image, title, and description) is O(m), where m is the size of the JSON response and the associated image data.
- **JavaFX UI Components**: The memory usage for the UI components is constant and does not scale with the size of the data, making it O(1).

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
