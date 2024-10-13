# JavaFX 'Fakevirus'

This JavaFX application, defined in the `sample.Main` class, creates a window that displays a looping video using JavaFX's `MediaPlayer` and `MediaView` classes. Below is a detailed breakdown of the code:

## Key Components:

### 1. **Main Class**:
   - The `Main` class extends `Application` and implements `Runnable`.
   - It serves as the entry point for the JavaFX application, and its `start()` method sets up the user interface (UI).

### 2. **start() Method**:
   - The `start(Stage primaryStage)` method sets up the main window (primary stage) for the application.
   - **Pane**: A `Pane` is used as the root layout container.
   - **Media & MediaPlayer**:
     - A `Media` object is created, loading a video file (`video.mp4`) from the class resources.
     - A `MediaPlayer` is then created to control the playback of the video, set to autoplay and loop indefinitely (`CycleCount.INDEFINITE`).
   - **MediaView**: A `MediaView` is used to display the video content from the `MediaPlayer`. It is positioned using `setX()` and `setY()`.
   - The background color of the `Pane` is set to black with `-fx-background-color: #000000`.

### 3. **Stage Configuration**:
   - **Icon**: The application window is set with an icon (`rick.png`).
   - **Window Title**: The title is set to "What happened?".
   - **Stage Style**: The window is undecorated using `StageStyle.UNDECORATED`, meaning it won't have the typical OS window controls (close, minimize, etc.).
   - The window is centered, maximized, non-resizable, and always stays on top of other windows.
   - **Prevent Iconification**: The window cannot be minimized, and the close event is consumed to prevent the user from closing the window.
   
### 4. **Media Looping**:
   - The video loaded in the `MediaView` loops indefinitely, playing in the background.

### 5. **Thread and Beep Sound**:
   - The application spawns a separate thread (`Thread thread = new Thread(new Main())`), where the `run()` method continuously plays a beep sound using `Toolkit.getDefaultToolkit().beep()`. This beep occurs repeatedly as the thread runs in an infinite loop.

### 6. **Main Method**:
   - The `main()` method simply launches the JavaFX application by calling `launch(args)`.

## Summary:
- This program creates an immersive, always-on-top JavaFX window that cannot be minimized or closed via traditional methods. It plays a video file (`video.mp4`) on an infinite loop and produces a beeping sound in the background.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
