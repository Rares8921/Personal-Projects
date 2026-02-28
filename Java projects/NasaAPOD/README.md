# NasaAPOD

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-11-blue?style=for-the-badge&logo=java)
![API](https://img.shields.io/badge/NASA%20API-Integrated-red?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=for-the-badge)

**NASA Astronomy Picture of the Day viewer with date selection, descriptions, author credits, and direct HD image downloads.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

NasaAPOD is a desktop application that brings NASA's Astronomy Picture of the Day directly to your screen. It fetches stunning space images and detailed descriptions from NASA's public API, making it easy to explore astronomical wonders.

**Core features:**
- **Daily astronomy images** - automatically loads today's APOD on startup
- **Date selection** - browse any APOD since June 20, 1995 (when the archive began)
- **HD image viewing** - displays high-resolution space photography
- **Descriptions** - hover tooltips with full explanations from NASA astronomers
- **Author credits** - displays photographer/artist name for each image
- **Direct HD links** - click image to open full-resolution version in browser
- **Calendar navigation** - intuitive date picker for exploring past images

**What makes it special:**
- Validates date range automatically (blocks dates before June 20, 1995 or future dates)
- Fallback mechanism handles days with videos instead of images
- Clean JavaFX interface with custom-styled date picker
- Persistent window dragging for flexible positioning
- Tooltip text wrapping for readable long descriptions
- JSON parsing via Jackson library for reliable API consumption

The application uses NASA's official APOD API endpoint, which provides daily selections of breathtaking images including deep space photos from Hubble, planetary surfaces, nebulae, galaxies, and more - all accompanied by professional astronomical explanations.

---

## Tech Stack

**Language:** Java 11  
**Framework:** JavaFX (Scene, FXML, DatePicker)  
**API:** NASA APOD RESTful API (https://api.nasa.gov/planetary/apod)  
**JSON Parsing:** Jackson (codehaus.jackson) for ObjectMapper  
**HTTP Client:** HttpURLConnection for API requests  
**UI:** FXML layout with custom CSS styling  
**Build:** Standard Java compilation

### Architecture

REST API consumer with MVC pattern:

```
Main (Application Launcher)
      ↓
Controller (FXML Controller)
      ↓
┌─────────────────────────────────┐
│   NASA API Client               │
│   - HTTP GET request            │
│   - JSON response parsing       │
│   - APOD model mapping          │
└─────────────────────────────────┘
      ↓
Data Flow:
  1. User selects date (or default = today)
  2. Build API URL with date parameter
  3. HttpURLConnection sends GET request
  4. Receive JSON response
  5. Jackson ObjectMapper → APOD object
  6. Extract: title, author, explanation, hdUrl, url
  7. Display image + metadata in UI
      ↓
UI Components:
  ├─ ImageView (APOD image)
  ├─ Label (title, author)
  ├─ Tooltip (description on hover)
  └─ DatePicker (calendar navigation)
```

**API Integration:**
```java
URL: https://api.nasa.gov/planetary/apod
Parameters:
  - api_key: YOUR_NASA_API_KEY
  - date: YYYY-MM-DD (optional, defaults to today)

Response (JSON):
{
  "copyright": "Photo Credit Name",
  "date": "2020-05-19",
  "explanation": "Long description text...",
  "hdurl": "https://apod.nasa.gov/apod/image/2005/..._4096.jpg",
  "media_type": "image",
  "service_version": "v1",
  "title": "Image Title",
  "url": "https://apod.nasa.gov/apod/image/2005/..._1024.jpg"
}
```

**Key Implementation Details:**
- **API Key:** Hardcoded NASA API key (public demo key, rate limited to 1000 requests/hour)
- **Date Validation:** Blocks futures dates and dates before `1995-06-20` (APOD launch date)
- **JSON Parsing:** Uses Jackson's `ObjectMapper.readValue()` to deserialize JSON into APOD POJO
- **Image Loading:** ImageView directly loads from `hdUrl` (high-definition URL)
- **Tooltip System:** Custom styling (dark background, white text, 400px max width, 90s display duration)
- **Click Handler:** MouseListener on ImageView opens `hdUrl` in default browser via `Desktop.browse()`
- **Fallback Logic:** If media_type is "video", loads default fallback image (May 19, 2020)

**APOD Model Class:**
```java
@JsonProperty annotations map JSON fields to Java object:
- copyright → String copyright
- date → String date
- explanation → String explanation
- hdurl → String hdUrl
- media_type → String mediaType
- title → String title
- url → String url
```

---

## Project Structure

```
NasaAPOD/
└── src/
    ├── sample/
    │   ├── Main.java              # JavaFX application entry
    │   ├── Controller.java        # API logic + UI controller (170 lines)
    │   ├── APOD.java             # Data model (Jackson POJO)
    │   ├── Open.java             # Landing screen
    │   ├── sample.fxml           # UI layout
    │   ├── background.jpg        # App background
    │   ├── calendar.png          # Calendar button icon
    │   └── planet.ico            # App icon
    └── META-INF/
        └── MANIFEST.MF           # JAR manifest

Dependencies:
  - Jackson 1.x (codehaus.jackson) for JSON parsing
```

---

## Getting Started

**Requirements:**
- Java 11 or higher
- JavaFX SDK 11+
- Internet connection (for NASA API access)
- Jackson library (org.codehaus.jackson)

**Setup:**

1. **Get Jackson JAR:**
   ```bash
   # Download jackson-all-1.9.13.jar or add Maven dependency
   # Place in lib/ folder
   ```

2. **Compile and run:**
   ```bash
   # Compile with JavaFX and Jackson
   javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
         -cp lib/jackson-all-1.9.13.jar \
         -d bin src/sample/*.java

   # Run
   java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
        -cp bin:lib/jackson-all-1.9.13.jar \
        sample.Main
   ```

**Or use the JAR:**
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar NasaAPOD.jar
```

**Using the application:**

1. **View today's APOD:**
   - Launch app - today's image loads automatically
   - Read title and author at top
   - Hover over image for full description (tooltip)

2. **Explore past images:**
   - Click calendar icon button
   - Select any date since June 20, 1995
   - App fetches and displays that day's APOD

3. **Open HD version:**
   - Click on the displayed image
   - Opens full-resolution version in web browser for downloading/wallpaper use

**API Key note:**
- Code uses a demo NASA API key (rate limited)
- For heavy use, get your own free key at [api.nasa.gov](https://api.nasa.gov)
- Replace key in Controller.java line: `api_key=YOUR_KEY_HERE`

---

## What's Next

Future enhancements under consideration:
- Favorites/bookmarks system (save best images)
- Offline mode (cache recent images)
- Random APOD button (surprise me!)
- Share functionality (social media, email)
- Desktop wallpaper setter (one-click set as background)
- Image gallery view (thumbnails of recent APODs)
- Search by keyword in descriptions
- Download manager for batch saving
- Support for APOD videos (play in app instead of skipping)

---

## 📹 Demo Video

> **Recording Instructions:** A 3-5 minute walkthrough showcasing the key features of this project.

### What to Demonstrate

**Suggested Timeline:**
- **0:00-0:30** - Project overview and startup
- **0:30-2:00** - Core features demonstration
- **2:00-3:30** - Advanced features and interactions
- **3:30-5:00** - Edge cases and wrap-up

### Features to Showcase

- **Daily APOD Loading** - Automatic today's image on startup
- **Date Selection** - Use calendar to browse historical APODs (since 1995)
- **HD Image Viewing** - Display high-resolution space photography
- **Description Tooltips** - Hover to read NASA astronomer explanations
- **Author Credits** - Display photographer/artist information
- **HD Link** - Click image to open full resolution in browser

### Recording Setup

**Prerequisites:**
```bash
# Get NASA API key from https://api.nasa.gov/
# Add API key to the application source code
# Ensure internet connection for API calls
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `java -jar NasaAPOD.jar` or run Main class
2. Open OBS Studio and set up screen capture
3. Record the demonstration following the timeline above
4. Save video as `demo.mp4` in the project root directory
5. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Start application
cd "d:\Personal-Projects\Java projects\NasaAPOD"
java -jar NasaAPOD.jar

# Ensure NASA API key is configured
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
