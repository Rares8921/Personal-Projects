# Currency Converter

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![API](https://img.shields.io/badge/REST-API-green?style=for-the-badge)

**Real-time currency conversion with live exchange rates. 150+ currencies with historical rate graphs.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

A modern currency converter that pulls live exchange rates from external APIs and visualizes historical trends. Perfect for travelers, forex traders, or anyone dealing with international currencies.

**Conversion Features:**
- **153 Currencies Supported** - Major and minor currencies from around the world
- **Live Exchange Rates** - Real-time data via REST API integration
- **Instant Conversion** - Type amount, conversion happens automatically
- **Bidirectional Exchange** - Easily swap "from" and "to" currencies
- **High Precision** - Up to 10 decimal places for accuracy

**Visualization Features:**
- **Historical Rate Graphs** - AreaChart showing exchange rate trends
- **Multiple Timeframes** - 7 days, 1/3/6 months, 1/5/10/20 years
- **Interactive Charts** - Hover over points to see exact rates
- **Trend Analysis** - Quickly spot currency strengthening/weakening

**UI Features:**
- Clean, modern JavaFX interface
- ComboBox dropdowns with search/filter for currencies
- Draggable undecorated window
- Currency flags displayed for visual recognition
- Responsive layout with FXML

---

## Tech Stack

**Frontend:** JavaFX 11 + FXML + CSS  
**Backend:** Java 11 + HttpURLConnection (REST API calls)  
**Data Format:** JSON (Gson library for parsing)  
**Charts:** JavaFX AreaChart + LineChart  
**API:** Exchange rates API (configurable endpoint)

### Architecture

MVC pattern with external API integration:

```
Main.java (Application)
      ↓
sample.fxml + Controller.java
      ↓
API Request (HttpURLConnection)
      ↓
JSON Response (Gson Parser)
      ↓
Exchange Rate Data → Map<String, Object>
      ↓
ComboBox Population + Conversion Logic
      ↓
AreaChart Rendering (Historical Data)
```

**Implementation Details:**
- **API Integration:** HttpURLConnection for GET requests to exchange rate API
- **JSON Parsing:** Gson library converts JSON responses to Java Map structures
- **Conversion Formula:** `amount * (toRate / fromRate)` for cross-currency conversion
- **Data Caching:** Rates stored in memory to reduce API calls
- **Chart Rendering:** JavaFX XYChart.Series for plotting time-series data
- **Currency List:** Loaded from `currencies.txt` file (153 currency codes)

---

## Project Structure

```
src/sample/
├── Main.java               # JavaFX Application entry
├── Controller.java         # Main conversion logic + API calls
├── CalendarView.java       # Date picker for historical rates
├── AnchorPaneNode.java     # Custom UI components
├── Open.java               # Utility class
├── sample.fxml             # Main window layout
├── styles.css              # Application styling
├── currencies.txt          # List of 153 currency codes
└── flags/                  # Flag images for each currency

resources/
├── background.jpeg         # App background
└── currency-icons/         # Additional graphics

README.md
```

---

## Getting Started

**Requirements:**
- Java 11+ with JavaFX
- Active internet connection (for API calls)
- Gson library (Maven dependency)

**Setup:**

```bash
# Navigate to project directory
cd "d:\Personal-Projects\Java projects\CurrencyConverter"

# Ensure dependencies are available (Gson)
# If using Maven, run:
mvn clean install

# Run with JavaFX
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     sample.Main
```

**Usage:**
1. Launch application
2. Select **"From"** currency (e.g., USD)
3. Select **"To"** currency (e.g., EUR)
4. Enter amount in "From" field
5. Converted amount appears automatically in "To" field
6. Click timeframe buttons (7 days, 1 month, etc.) to view historical graph
7. Hover over graph points to see exact rates on specific dates

**API Configuration:**
Edit `Controller.java` to change API endpoint if needed:
```java
// Example: Update API URL
String apiUrl = "https://api.exchangerate.host/latest?base=" + baseCurrency;
```

---

## What's Next

Enhancements for future versions:

- **Offline Mode** - Cache last-known rates for offline conversion
- **Cryptocurrency Support** - Add Bitcoin, Ethereum, and other cryptos
- **Rate Alerts** - Notify when exchange rate hits target threshold
- **Favorites** - Pin frequently used currency pairs
- **Multi-Currency Calculator** - Convert to multiple targets simultaneously
- **Commission Calculator** - Factor in bank/exchange fees
- **CSV Export** - Download historical rate data as spreadsheet
- **Dark Mode** - Toggle between light/dark themes
- **Widget Mode** - Compact always-on-top overlay window
- **Custom API Sources** - Choose between multiple rate providers

---

## License

**Proprietary Software - All Rights Reserved**

This software is the exclusive property of the author. No part of this software may be copied, modified, distributed, or used without explicit written permission. Unauthorized use, reproduction, or distribution is strictly prohibited and may result in legal action.
