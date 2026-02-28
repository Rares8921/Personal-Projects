# Weather App

<div align="center">

![Java](https://img.shields.io/badge/Java-JavaFX-orange?style=for-the-badge&logo=java)
![API](https://img.shields.io/badge/OpenWeatherMap-API-blue?style=for-the-badge&logo=weather)
![Forecast](https://img.shields.io/badge/Forecast-5%20Day-green?style=for-the-badge&logo=cloud)

**A sleek weather forecast application that pulls real-time data from OpenWeatherMap API. Get current conditions and 5-day forecasts for any city, with weather icons and temperature trends.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a modern weather application built with JavaFX that integrates with the OpenWeatherMap API. Search for any city worldwide to get current weather conditions and a detailed 5-day forecast with temperature, humidity, wind speed, and weather icons.

**Core features:**
- **City search**: Type any city name to get instant weather data
- **Current weather**: Real-time temperature, conditions, humidity, wind speed
- **5-day forecast**: Extended forecast with daily high/low temperatures
- **Weather icons**: Visual representations of conditions (sunny, cloudy, rainy, etc.)
- **Temperature units**: Switch between Celsius and Fahrenheit
- **Auto-save preferences**: Remembers last searched city and unit preference
- **Clean UI**: Dark-themed interface (#555555) with transparent window
- **Location detection**: Default to saved location on startup
- **API integration**: Pulls data from OpenWeatherMap's free tier

**Displayed information:**
- Temperature (current, feels like, high/low)
- Weather condition (clear, clouds, rain, snow, etc.)
- Humidity percentage
- Wind speed and direction
- Pressure
- Sunrise/sunset times
- 5-day forecast with daily predictions

**Why it's useful:**
- Fast, lightweight weather lookup
- No ads or distractions
- Persistent settings (remembers your city)
- Professional-grade API data
- Cross-platform (runs on Windows, Mac, Linux)

The app makes HTTP GET requests to OpenWeatherMap API, parses JSON responses, and displays formatted data in JavaFX Labels and ImageViews.

---

## Tech Stack

**Language:** Java (JavaFX 17+)  
**API:** OpenWeatherMap REST API  
**UI Framework:** JavaFX (Scene, FXML, Controller)  
**Data Parsing:** JSON parsing (likely org.json or Gson)  
**Networking:** `HttpURLConnection`, `BufferedReader`, `InputStreamReader`  
**Persistence:** File I/O (`Formatter`, `Scanner`) for saving preferences  
**Build:** Maven with JavaFX dependencies

### Architecture

REST API integration with UI updates and preference persistence:

```
JavaFX Application
      ↓
FXML UI (Search bar, Weather display, Forecast cards)
      ↓
Event Handlers (Search button, Unit toggle)
      ↓
┌──────────────┬──────────────┬──────────────┐
│  API Client  │  JSON Parser │  Storage     │
│  (HTTP)      │  (Weather)   │  Manager     │
└──────────────┴──────────────┴──────────────┘
      ↓              ↓               ↓
HTTP GET       Extract Data    Write to File
OpenWeatherMap  Temperature    (Last City,
JSON Response   Conditions     Unit Pref)
                Icons
```

**How it works:**
- **Search Flow**: User types city → clicks search → HTTP GET to OpenWeatherMap → JSON response → parse data → update UI
- **API Request**: `https://api.openweathermap.org/data/2.5/weather?q={city}&appid={key}&units={metric/imperial}`
- **5-Day Forecast**: Separate API call to `/forecast` endpoint with same city
- **JSON Parsing**: Extract temperature, humidity, conditions, icon code from response
- **Icon Loading**: Weather icon codes (e.g., "01d", "10n") mapped to icon images
- **Unit Conversion**: Toggle between Celsius (metric) and Fahrenheit (imperial) units
- **Persistence**: On window close, saves city and unit preference to `src/data.txt`

**Key Implementation Details:**
- **API Key**: Embedded in code or config file (free OpenWeatherMap key)
- **City Storage**: `Formatter` writes last city and unit to text file in `src/` directory
- **Auto-load**: On startup, reads saved city from `data.txt` and fetches weather
- **Error Handling**: Catches invalid city names, network errors, API failures
- **Transparent Window**: `StageStyle.TRANSPARENT` with custom close handler

---

## Project Structure

```
WeatherApp/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/example/weatherapp/
│   │           ├── Main.java       # Entry point + JavaFX setup
│   │           ├── Controller.java # UI logic + API calls
│   │           └── Open.java       # Launch helper (optional)
│   └── data.txt                    # Saved preferences (city, units)
├── pom.xml                         # Maven dependencies
└── WeatherApp.iml                  # IntelliJ module file
```

**Main components:**
- `Main.java`: JavaFX application, scene setup, preference saving
- `Controller.java`: API integration, JSON parsing, UI updates, city search
- `weather.fxml`: FXML layout for weather display
- `data.txt`: Persistent storage (city name, temperature unit)

---

## Getting Started

### Prerequisites

- **Java 17+** with JavaFX SDK
- **Internet connection** (for API calls)
- **OpenWeatherMap API key** (free tier available)
- Maven (for building from source)

### Configuration

1. **Get API key**: Sign up at [OpenWeatherMap.org](https://openweathermap.org/api) for free API key
2. **Add API key**: Open `Controller.java` and add your key to the API URL string

### Running the Application

**Option 1: Maven**
```bash
# Compile and run:
mvn clean javafx:run
```

**Option 2: JAR**
```bash
# Run with JavaFX modules:
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar WeatherApp.jar
```

**Option 3: From Source**
```bash
# Compile:
javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -d bin src/main/java/com/example/weatherapp/*.java

# Run:
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp bin com.example.weatherapp.Main
```

### How to Use

1. **Search for city**:
   - Type city name in search box (e.g., "London", "New York", "Tokyo")
   - Press Enter or click search button
   - Weather data loads automatically

2. **View current weather**:
   - Temperature displayed prominently
   - Weather condition (sunny, cloudy, rainy, etc.)
   - Additional details: humidity, wind speed, pressure

3. **Check 5-day forecast**:
   - Scroll through forecast cards
   - See daily high/low temperatures
   - Weather icons show predicted conditions

4. **Change units**:
   - Click unit toggle (°C / °F)
   - Temperature updates immediately

5. **Auto-loading**:
   - Next time you open app, it remembers last city
   - No need to search again

**Supported cities:**
- All major cities worldwide
- Use format: "City, Country" for disambiguation (e.g., "Paris, FR")

---

## What's Next

**Potential improvements:**
- Add hourly forecast (24-hour breakdown)
- Implement GPS location detection
- Add weather alerts and warnings
- Show air quality index (AQI)
- UV index and pollen count
- Radar/satellite imagery
- Weather maps with precipitation overlay
- Multiple city bookmarks
- Widget mode (minimal, always-on-top)

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
