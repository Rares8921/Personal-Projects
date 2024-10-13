# Weather Application Description

## Overview

This application is a real-time weather forecasting tool built using Java and JavaFX for the graphical user interface. It allows users to input a city name and retrieve both current weather data and a 5-day weather forecast from the OpenWeatherMap API. The app is designed to be simple and user-friendly, featuring dynamic updates based on user input, automatic temperature unit conversions, and seamless interaction with the OpenWeatherMap API.

### Usability
- **City Search**: Users can input the name of any city in the provided text field to receive real-time weather data for that location. Upon pressing Enter, the app fetches the current conditions and the forecast.
- **Dynamic Unit Conversion**: The user can toggle between metric and imperial units (Celsius to Fahrenheit, km/h to mph) by pressing a button on the interface.
- **Forecast Visualization**: The app displays a 5-day weather forecast with high/low temperatures and icons representing the expected conditions for each day.
- **Real-time Clock**: The current time and day of the week are continuously updated and displayed at the top of the application.
- **Window Controls**: The window is draggable, and there are buttons to minimize or close the app.

### Key Features
1. **Current Weather Data**: Displays real-time temperature, "feels like" temperature, wind speed, humidity, pressure, visibility, sunrise/sunset times, and an icon representing the weather condition.
2. **5-Day Forecast**: Includes future high and low temperatures, along with weather condition icons for the next five days.
3. **Automatic Unit Switching**: Users can switch between metric and imperial units, and the app will automatically update all measurements (temperature, wind speed, etc.) accordingly.
4. **Real-time Clock**: Continuously updates the current time and day of the week on the screen.
5. **Responsive and Interactive UI**: The app responds to user inputs instantly, with city names being entered and updated dynamically.
6. **Smooth Window Dragging**: Users can click and drag the application window to reposition it on the screen easily.

---

## External API Usage
The application integrates with the OpenWeatherMap API to fetch real-time weather information. It sends two separate HTTP requests:
1. **Current Weather Data**: Retrieves data for the current weather conditions in a specified city.
2. **Forecast Data**: Fetches a 5-day forecast with temperature highs/lows and weather icons for each day.

### API Endpoints
- **Current Weather**: `https://api.openweathermap.org/data/2.5/weather`
- **Forecast**: `https://api.openweathermap.org/data/2.5/forecast`

### HTTP Connections
The `connect()` and `connectNextDays()` methods handle HTTP requests. They use `URLConnection` to establish a connection to the API endpoints and parse the JSON responses into Java objects using Jackson’s `ObjectMapper`.

---

## Core Methods

### `initialize(URL url, ResourceBundle resourceBundle)`
This method is called when the Controller is initialized. It performs the following actions:
- Calls `setDraggable()` to allow dragging the window around the screen.
- Loads the default city name and units from a data file using `loadUrl()`.
- Connects to the OpenWeatherMap API to fetch weather data and calls `loadContent()` to update the UI with this information.
- Sets up the user input listener through `onUserInput()` and initializes the real-time clock with `setTime()`.

### `loadUrl(String u)`
This method constructs the API request URL. It reads the city name and preferred unit of measurement from a file (`data.txt`). If the user has provided a different unit (metric/imperial), the URL is updated to reflect that.

### `connect()`
This method handles the HTTP connection for current weather data. It fetches data from the OpenWeatherMap API, and the response is parsed using the `ObjectMapper` class into a `Map` for easy access.

### `connectNextDays()`
Similar to `connect()`, this method fetches the weather forecast data for the next 5 days. It processes and stores the forecasted temperatures and icons for display in the UI.

### `onUserInput()`
This method listens for user input in the city `TextField`. When the user presses the **Enter** key, it triggers a new API call to update the weather information based on the newly entered city.

### `loadContent()`
After the API response is fetched, this method updates the UI components with the data:
- Sets the temperature, feels like, and max/min temperatures.
- Updates the wind speed, pressure, humidity, and visibility.
- Displays the country, weather description, and weather icon.
- Updates the sunrise and sunset times.
- Sets the days and weather icons for the forecast section.

### `setTime()`
This method uses a `Timeline` to update the current day and time every second. It displays the day of the week and the current time in hours, minutes, and seconds.

### `changeUnit()`
This method toggles between metric and imperial units for temperature, wind speed, and other measurements. It reloads the data and updates the UI based on the chosen unit.

### `setDraggable()`
Allows the window to be dragged around the screen by updating its `x` and `y` position when the mouse is pressed and dragged.

### `close()` and `minimize()`
Handles window actions to close or minimize the application.

## UI Elements
- **Labels**: Used to display weather-related information, such as country, temperature, wind speed, humidity, visibility, and pressure.
- **ImageViews**: Display weather icons for the current and forecasted weather conditions.
- **TextField**: Allows the user to input the city name.
- **Button**: Toggles between metric and imperial units of measurement.

## Data Handling and Parsing
The weather data is fetched in JSON format and parsed into Java `Map` objects. Specific fields such as temperature, weather conditions, and icons are extracted from these objects and displayed in the UI.

### JSON Parsing
- **Main Object**: Contains key weather data (temperature, pressure, humidity).
- **Sys Object**: Contains information about the country, sunrise, and sunset times.
- **Weather Object**: Contains a description of the current weather and the icon ID.

### Example of API Response Parsing:
```java
Object sys = content.get("sys");
String[] strSys = sys.toString().replace("{", "").replace("}", "").split(", ");
long sunriseTimeStamp = Long.parseLong(strSys[sunriseIndex].substring(strSys[sunriseIndex].indexOf("=") + 1));
```

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.