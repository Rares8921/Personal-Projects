# Virtual Piano

## Description

This project is a **Virtual Piano** developed using **JavaFX**. It simulates a piano where each key on the keyboard is mapped to a musical note. The application provides visual feedback when a key is pressed or released, changing the color of the corresponding piano key in the UI.
The application includes both white keys (natural notes) and black keys (accidentals), with the Shift key modifier used to activate the accidental notes.
Additionally, the `jcodec` and `jcodec-javase` libraries are used in the project for potential future multimedia features, such as sound or video rendering.

## Key Features

- **Graphical Interface**: Simulates a piano keyboard with responsive key visualizations.
- **Keyboard Mappings**: Maps physical keys on the computer keyboard to virtual piano keys.
- **Shift Functionality**: Users can play sharp notes by holding the Shift key while pressing the corresponding key.
- **Real-time Sound Generation:** The application uses Java libraries to generate sound in real time when keys are pressed.
- **Visual Feedback:** Each key changes color based on its state (pressed/released), giving instant feedback on user actions.
- **Playback:** Users can load and play previously recorded audio files.
- **Metronome:** A built-in metronome to assist users in maintaining a steady tempo while practicing.


## Code Structure

The application is structured into several classes, each responsible for different functionalities. Below is an overview of the classes used in the application:

1. **AudioRecorder**
   - Responsible for managing audio recording functionalities. It handles the capture of audio data and saves it to files.

2. **Controller**
   - Acts as the main controller for the application, coordinating between the UI and backend logic. It manages the application state and user interactions.

3. **CustomButtonSkin**
   - A custom skin for buttons in the GUI, providing unique visual styles and interactions for piano keys.

4. **KeyAssistController and KeyAssistMenu**
   - Handles key assistance features, helping users learn the piano keys and their corresponding notes. The menu provides additional options for assistance.

5. **Main**
   - The entry point of the application where keybindings are set up and the main application window is initialized. It also handles user inputs for playing notes.

6. **MetafileUpdater**
   - Updates and manages metadata for audio files. It ensures that all audio recordings have appropriate labels and information.

7. **Metronome and MetronomeController and MetronomeMenu**
   - The Metronome class generates a steady beat for practice. The MetronomeController manages the metronome's state and settings, while the MetronomeMenu provides options for customization.

8. **MusicPlayer**
   - Responsible for playing back audio files. It manages the loading, playback, and stopping of music.

9. **SemanticsController and SemanticsMenu**
   - Manages semantic analysis features that provide feedback on the user's performance. The menu allows users to access various semantics-related options.

10. **VolumeController and VolumeMenu**
    - Handles volume control for the application. The VolumeController adjusts the audio levels, while the VolumeMenu provides options for users to set their preferred volume.

  
## Key Mappings

The following mappings are used for playing notes:

- **Numbers and Letters**: Digits (0-9), letters (A-Z), and Shift-modified keys are mapped to specific piano keys.
- **Shift for Black Keys**: Holding Shift while pressing certain keys triggers black (accidental) keys like C# (Db), D# (Eb), etc.

## Programming Techniques

### 1. **Event-Driven Programming**

The program relies heavily on **JavaFX’s event-driven architecture**. The two main event listeners are:
  
- **`setOnKeyPressed`**: Handles the pressing of a key, determining which note corresponds to the input and updating the virtual keyboard visually by changing the background color of the key.
- **`setOnKeyReleased`**: Resets the key’s visual state when the key is released by restoring the background color.

### 2. **State Management**

An **ArrayList** `keyPressed` is used to manage the state of each key. Each index in the list corresponds to a specific piano key, with a boolean value indicating whether the key is pressed or not.

### 3. JavaFX Scene Graph and Styles

The application uses JavaFX's Scene Graph for rendering the UI, and the styles of keys are updated dynamically using setStyle() to change their background color upon keypress.

### 4. Shift Key Modifier

JavaFX provides the ability to check if the Shift key is pressed using e.isShiftDown(). This feature allows the application to differentiate between natural and accidental notes when the same physical key is pressed with or without the Shift modifier.

### 5. Efficient Key Mapping Using Switch-Case

The mapping of keys is handled using a switch-case structure, allowing efficient O(1) lookups for determining which virtual piano key should be activated based on the key press.

### 6. jcodec and jcodec-javase Libraries

This project incorporates the jcodec and jcodec-javase libraries, although they are not actively used in the current state of the application. These libraries provide functionality for audio and video processing, which could be integrated in the future to add sound or multimedia features such as video rendering or note recording.
These libraries are open-source and lightweight, making them ideal candidates for multimedia enhancements in future versions of the application.


## Requirements to Run the Application

### 1. Software Requirements
    JDK: Java Development Kit version 8 or higher.<br/>
    JavaFX SDK: Ensure that the JavaFX libraries are correctly set up in your IDE or build system.<br/>
    jcodec and jcodec-javase: These libraries must be added as dependencies for future multimedia enhancements.<br/>
    IDE: The application can be developed and run on any Java-compatible IDE, such as IntelliJ IDEA, Eclipse, or NetBeans.<br/>

### 2. Hardware Requirements
    Operating System: The application is cross-platform and can run on Windows, macOS, and Linux.<br/>
    RAM: A minimum of 2GB RAM is recommended, but the application is lightweight.<br/>
    CPU: Any modern processor capable of running Java applications.<br/>

### 3. Installation Steps
    Install JDK and JavaFX SDK.<br/>
    Add the jcodec and jcodec-javase libraries to your project dependencies.<br/>
    Clone or download the project.<br/>
    Open the project in an IDE that supports JavaFX.<br/>
    Run the main() method to start the application.<br/>

## Time and Space Complexity

### Time Complexity
    Key Press/Release Handling: Each key press or release is handled in constant time O(1). The switch-case structure and keyPressed state management allow for immediate actions.
    State Update: The state of each key (pressed or released) and its corresponding visual update also operate in O(1) time.

### Space Complexity
    Key State Management: The keyPressed ArrayList uses O(n) space, where n is the number of keys (61 keys, including accidentals from C1 to C6).
    UI Components: JavaFX buttons representing the piano keys also take up O(n) space.

Overall, the time complexity per operation (keypress/release) is O(1), and the space complexity is O(n).

## Examples and illustrations

Here is an example of the application interface featuring various tools and adjustments:

![image](example_interface.png)

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.