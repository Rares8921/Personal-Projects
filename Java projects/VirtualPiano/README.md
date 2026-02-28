# Virtual Piano

<div align="center">

![Java](https://img.shields.io/badge/Java-JavaFX-orange?style=for-the-badge&logo=java)
![MIDI](https://img.shields.io/badge/MIDI-javax.sound.midi-blue?style=for-the-badge&logo=music)
![Music](https://img.shields.io/badge/Music-Instruments-green?style=for-the-badge&logo=piano)

**A fully playable virtual piano with 61 keys, multiple instruments, recording, playback, metronome, and volume control. Make music with your keyboard or mouse.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a feature-rich virtual piano built with JavaFX and Java's MIDI API. Play 61 keys (5 octaves) using your keyboard or mouse, switch between different instruments, record performances, play them back, and keep rhythm with an integrated metronome.

**Core features:**
- **61-key piano**: 36 white keys + 25 black keys spanning 5 octaves
- **Keyboard or mouse control**: Play with keyboard shortcuts or click keys with mouse
- **Multiple instruments**: Piano, organ, guitar, strings, brass, and more (MIDI instrument bank)
- **Recording functionality**: Record your performance and save it
- **Playback**: Play back recorded sessions with accurate timing
- **Metronome**: Built-in metronome with adjustable BPM for practice
- **Volume control**: Adjust master volume on the fly
- **Visual feedback**: Keys light up when pressed (white keys → gray, black keys → darker)
- **Key labels**: Each key shows its keyboard shortcut
- **Custom window**: Transparent background with rounded corners

**Advanced features:**
- **61-key mapping**: Full keyboard layout (1-9, Q-P, A-L, Z-M, etc.)
- **Shift key modifier**: Access black keys with Shift+number
- **AudioRecorder integration**: Export recordings to audio files
- **Metronome controller**: Separate panel for tempo and time signature
- **Volume menu**: Real-time volume adjustment slider
- **Semantics menu**: Key assist and music theory helpers

**Why it's awesome:**
- Realistic MIDI sound using javax.sound.midi
- Low latency key response
- Professional instrument library
- Recording and playback for learning
- Clean, modern JavaFX interface

The piano uses JavaFX Pane with styled Rectangle nodes for keys, and javax.sound.midi for sound synthesis. Key presses are mapped to MIDI note numbers (C1-C6).

---

## Tech Stack

**Language:** Java (JavaFX 17+)  
**Audio Engine:** javax.sound.midi (`MidiChannel`, `Synthesizer`, `Instrument`)  
**UI Framework:** JavaFX (Pane, Rectangle, Button, Scene, FXML)  
**Controllers:** FXML controllers for UI interactions  
**Recording:** Custom AudioRecorder class  
**Metronome:** Timer-based beat generator with MIDI output

### Architecture

Event-driven architecture with MIDI synthesis and UI feedback:

```
JavaFX Application
      ↓
FXML UI (61 Key Rectangles)
      ↓
Event Handlers (Keyboard + Mouse)
      ↓
┌──────────────┬──────────────┬──────────────┬───────────────┐
│  Key Mapping │  MIDI Engine │  Recording   │  Metronome    │
│  Handler     │  (Synth)     │  System      │  Engine       │
└──────────────┴──────────────┴──────────────┴───────────────┘
      ↓              ↓              ↓               ↓
KeyCode → Note  MidiChannel    AudioRecorder   Timer Beats
(e.g., 1→C1)    noteOn/Off     WAV Export      BPM Control
```

**How it works:**
- **Key Press**: Keyboard or mouse event → map to note number (0-60) → call `playMedia(note, label)`
- **MIDI Output**: `MidiChannel.noteOn(noteNumber, velocity)` triggers sound
- **Visual Feedback**: Key Rectangle's background color changes on press, resets on release
- **Recording**: Captures MIDI events with timestamps, stores in list
- **Playback**: Replays stored events with original timing
- **Metronome**: Timer fires at BPM interval, plays click sound via MIDI
- **Instrument Change**: Switch MIDI program (e.g., 0=Piano, 40=Violin, 73=Flute)

**Key Implementation Details:**
- **Key Layout**: 36 white keys (0-35) displayed as wide rectangles; 25 black keys (36-60) as narrow rectangles on top
- **Note Mapping**: KeyCode.DIGIT1 → C1, KeyCode.DIGIT2 → D1, Shift+DIGIT1 → Db1, etc.
- **MIDI Notes**: C1=36, D1=38, E1=40, F1=41, G1=43, A1=45, B1=47 (standard MIDI numbering)
- **Synthesizer**: Opens default MIDI synthesizer, gets first channel, loads SoundFont
- **KeyPressed ArrayList**: Tracks which keys are currently pressed to prevent repeated noteOn while key held

---

## Project Structure

```
VirtualPiano/
├── src/
│   ├── main/
│   │   ├── Main.java               # Entry point + JavaFX setup
│   │   ├── Controller.java         # Main piano controller
│   │   ├── AudioRecorder.java      # Recording functionality
│   │   ├── MusicPlayer.java        # Playback engine
│   │   ├── Metronome.java          # Metronome logic
│   │   ├── MetronomeController.java # Metronome UI
│   │   ├── MetronomeMenu.java      # Metronome popup
│   │   ├── VolumeController.java   # Volume slider
│   │   ├── VolumeMenu.java         # Volume popup
│   │   ├── SemanticsController.java # Music theory helpers
│   │   ├── SemanticsMenu.java      # Helper popup
│   │   ├── KeyAssistController.java # Keyboard layout guide
│   │   ├── KeyAssistMenu.java      # Key assist popup
│   │   ├── MetafileUpdater.java    # Metadata handler
│   │   └── CustomButtonSkin.java   # Styled button
│   └── resources/
│       ├── virtual-piano.fxml      # Main UI layout
│       ├── icon.png                # App icon
│       └── sounds/                 # MIDI soundfonts (optional)
└── Piano.iml                       # IntelliJ module file
```

**Main components:**
- `Main.java`: JavaFX application, scene setup, key event handling
- `Controller.java`: Key mapping, MIDI playback, UI updates
- `AudioRecorder.java`: Recording system with WAV export
- `Metronome.java`: Timer-based beat generator
- 61 keys: 36 white Rectangle nodes + 25 black Rectangle nodes

---

## Getting Started

### Prerequisites

- **Java 17+** with JavaFX SDK
- Maven or Gradle (for building from source)
- MIDI synthesizer (included with Java)

### Running the Application

**Option 1: JAR**
```bash
# Run with JavaFX modules:
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.media -jar Piano.jar
```

**Option 2: Maven**
```bash
mvn clean javafx:run
```

**Option 3: From Source**
```bash
# Compile:
javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -d bin src/main/*.java

# Run:
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp bin main.Main
```

### How to Play

**Keyboard controls:**
- **Numbers (1-9)**: White keys (C, D, E, F, G, A, B, C, D)
- **Shift+Numbers**: Black keys (C#/Db, D#/Eb, F#/Gb, G#/Ab, A#/Bb)
- **Letters (Q-P, A-L, Z-M)**: Additional octaves
- **Space**: Play/pause recording
- **Esc**: Exit

**Mouse controls:**
- Click on any key (white or black) to play note
- Use menu buttons for instrument change, recording, metronome, etc.

**Features:**
1. **Change instrument**: Click instrument menu → select from list
2. **Record**: Click record button → play something → click stop
3. **Playback**: Click play button to hear recording
4. **Metronome**: Open metronome menu → set BPM → start
5. **Volume**: Adjust volume slider in volume menu

---

## What's Next

**Potential improvements:**
- Add sustain pedal support
- Implement chord detection and display
- Add sheet music display (real-time notation)
- Export to MIDI file format
- Add drum pad section
- Multiple track recording (overdubbing)
- Effects (reverb, delay, chorus)
- Looping functionality

---

## License

**Proprietary License**  
© 2026. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, modification, distribution, or use of this software, via any medium, is strictly prohibited without explicit written permission from the owner.
