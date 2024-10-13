# Java Currency Converter

## Code Summary

This Java program is a Currency Converter application implemented using the JavaFX framework. It provides a graphical user interface (GUI) that allows users to convert between 153 different currencies. The application retrieves real-time exchange rates from a reliable currency exchange API, ensuring accurate and up-to-date conversions.

## Program Description and Usage

### Main Components

1. **Main Class (`Main`)**:
   - This is the entry point of the application. It extends `Application` and is responsible for setting up the primary stage (window) and loading the initial scene. The `Main` class initializes the GUI by displaying the main screen (`Open` scene), where users can start the conversion process.

2. **Open Class (`Open`)**:
   - The `Open` class is responsible for the initial screen that the user interacts with when launching the application. It sets up the GUI components for the currency converter, allowing the user to select the source and target currencies, enter the amount, and perform the conversion.
   - This class includes buttons and event handlers to manage the conversion process and update the display based on user input.

3. **Controller Class (`Controller`)**:
   - The `Controller` class handles the core logic of the Currency Converter. It manages the conversion rates, processes user input, and updates the UI with the converted amount. 
   - The class interacts with the currency exchange API to fetch real-time exchange rates, ensuring that the conversions are accurate and reflect the current market conditions.

### API Integration

- The application integrates with a currency exchange API to retrieve real-time exchange rates for 153 different currencies. The API ensures that users get the most accurate and current conversion rates available.
- The API is called whenever the user initiates a conversion, and the retrieved data is used to calculate the equivalent amount in the target currency.

### Conversion Mechanics

1. **Currency Selection**:
   - The user selects the source and target currencies from dropdown menus. The available currencies are dynamically loaded based on the data retrieved from the API, ensuring that the list is always up-to-date.

2. **User Input**:
   - The user inputs the amount they wish to convert. The application ensures that the input is a valid number before proceeding with the conversion.

3. **Conversion Process**:
   - Upon clicking the "Convert" button, the application sends a request to the currency exchange API to retrieve the latest exchange rate for the selected currency pair. The conversion is then performed using this rate.

4. **Display of Results**:
   - Once the conversion is complete, the result is displayed on the screen, showing the equivalent amount in the target currency. The user is also provided with the exchange rate used for the conversion.

### Custom Components

- **Currency Selection Menus**:
  - The application includes dropdown menus for selecting the source and target currencies. These menus are populated dynamically using the data retrieved from the API.

- **Conversion Handling**:
  - The `Controller` class manages the conversion logic, ensuring accurate and efficient processing of user inputs and updating the UI accordingly.

## Method of Generating the UI Elements

1. **Scene Initialization**:
   - The application window and scenes are set up in the `Main` and `Open` classes. `Main` initializes the primary stage and loads the initial scene. `Open` creates the main currency converter screen, while the `Controller` manages the conversion logic.

2. **Event Handling**:
   - The application uses event handlers to manage user interactions, such as selecting currencies and initiating conversions. The `Controller` class handles these events and updates the display based on the conversion results.

## Time and Space Complexity Analysis

### Time Complexity

- **Initialization**: Setting up the GUI components and initializing the conversion rates occurs in constant time, O(1).
- **API Call and Conversion**: The API call to retrieve exchange rates and the subsequent conversion process are performed in O(1) time, as they involve constant time operations like HTTP requests and simple arithmetic.

### Space Complexity

- **Memory Usage**: The primary space usage is for storing the conversion rates, user inputs, and GUI components:
  - Conversion rates and selected currencies: O(1) space.
  - GUI components and state variables: O(1) space.

Overall, the space complexity is O(1).

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.