# Image Editor

## Description

Image Editor is a sophisticated image editing application developed in C++. The application provides a range of features for adjusting and enhancing images, including brightness, contrast, saturation, and various filters such as blur and sharpen. It leverages modern AI techniques for automatic colorization and background removal, providing users with powerful tools to create visually appealing images. The project is designed with a modular architecture, employing object-oriented programming (OOP) techniques and design patterns to ensure scalability and maintainability.

## Features

- **Image Adjustments**: Adjust brightness, contrast, saturation, clarity, and other parameters to enhance images.
- **Filters**: Apply various filters like blur, sharpen, chrome, and vignette for creative effects.
- **Automatic Colorization**: Uses a pretrained Convolutional Neural Network (CNN) for automatic colorization of grayscale images.
- **Background Removal**: AI-powered background removal for isolating subjects from their backgrounds.
- **Brush Tool**: A versatile brush tool for manual edits and touch-ups.
- **Text Tool**: Add text to images with customizable fonts and styles.

## Structure

### C++ Codebase

- **Image Adjustments**: Implements various image adjustment features (e.g., `BrightnessAdjust`, `ContrastAdjust`, `SaturationAdjust`) using efficient image processing algorithms.
- **Filters**: Includes classes for different filters (e.g., `BlurAdjust`, `SharpenAdjust`, `ChromeFilter`), allowing users to apply creative effects.
- **AI Integration**: Utilizes pretrained CNN models for automatic colorization (`colorization_release_v2.caffemodel`) and background removal.
- **Tool Classes**: Provides tools like the `Brush` and `TextTag` for manual image editing.
- **Resource Management**: Manages resources such as fonts (`Arial.TTF`, `Consolas.TTF`) and icons (`appIcon.ico`) for the user interface.
- **Templatized Classes**: Utilizes templatized classes for flexible and reusable components.
  - **Drawing Tools**: Template-based implementation for various drawing tools.
  - **QuadTree**: Template-based QuadTree for efficient spatial partitioning and collision detection.
- **Methods for Numbers**: Implements various utility methods for number manipulation.
  - **Clamping Method**: Ensures values stay within a specified range, preventing overflow and underflow.

### OOP Techniques

- **Encapsulation**: Each class encapsulates its data and provides public methods for interacting with that data.
  - **Example**: The `Image` class encapsulates pixel data and provides methods for loading, saving, and manipulating images. The pixel data is private, ensuring it cannot be accessed directly from outside the class.
- **Inheritance**: The project uses inheritance to create specialized classes from base classes.
  - **Example**: `Adjustments` and `Filters` are derived from a common base class `ImageEffect`, allowing shared functionality to be reused.
- **Polymorphism**: Allows the project to handle different types of image effects through a common interface.
  - **Example**: The `applyEffect()` method in the base class `ImageEffect` can be overridden by subclasses like `BrightnessAdjust` and `ContrastAdjust` to provide specific implementations.
- **Composition**: Uses composition to build complex objects from simpler ones.
  - **Example**: The `ImageEditor` class contains various tool objects such as `Brush` and `TextTag` to provide comprehensive editing capabilities. Each tool object is a member of the `ImageEditor` class.
- **Abstraction**: Abstracts complex operations into simple interfaces.
  - **Example**: The `ImageProcessor` class abstracts the details of image processing algorithms, providing a simple interface for applying effects and adjustments.
- **Operator Overloading**: Provides intuitive usage of objects by defining custom behavior for operators.
  - **Example**: The `Color` class might overload the `+` and `-` operators to allow for easy manipulation of color values.
- **Dynamic Binding**: Enables calling methods of derived classes through pointers or references to base classes.
  - **Example**: A base class pointer `ImageEffect*` can point to an object of derived class `BlurAdjust` and call its overridden `applyEffect()` method at runtime.
- **Smart Pointers**: Utilizes `std::unique_ptr` and `std::shared_ptr` for efficient memory management, preventing memory leaks and dangling pointers.
  - **Example**: The `Image` class may use `std::unique_ptr` for managing dynamically allocated pixel data, ensuring it is properly deallocated when no longer needed.
- **Template Programming**: Uses templates to write generic and reusable code.
  - **Example**: A template function for applying various image transformations could be written to work with different data types.

### Design Patterns and Programming Techniques

- **Factory Pattern**: Used for creating instances of different image adjustment and filter objects, allowing for easy addition of new features.
- **Singleton Pattern**: Ensures a single instance of certain classes, such as the settings manager, to coordinate application-wide settings.
- **Command Pattern**: Implements undo and redo functionality, enabling users to revert and reapply changes.
  - **Example**: Each action (e.g., applying a filter, adjusting brightness) is encapsulated as a command object that can be executed, undone, and redone.
- **Strategy Pattern**: Allows the selection of different algorithms for image processing tasks, making the system flexible and extensible.
  - **Example**: Different algorithms for scaling images can be implemented as separate strategies and selected at runtime based on user preference.
- **Memento Pattern**: Used to save and restore the state of objects, supporting the undo/redo functionality.
  - **Example**: Before applying an effect, the current state of the image is saved as a memento. If the user undoes the effect, the image state is restored from the memento.
- **Decorator Pattern**: Allows behavior to be added to individual objects, such as applying multiple filters to an image sequentially.
  - **Example**: Filters can be applied in a chain, where each filter decorates the image object and passes it to the next filter in the sequence.
- **Observer Pattern**: Implements an event system where UI components can subscribe to changes in image properties, ensuring a responsive user experience.
  - **Example**: When an image property changes, all subscribed UI components are notified and updated accordingly.

### AI Tools

- **Pretrained CNN Models**: Utilizes pretrained models for tasks such as automatic colorization and background removal.
- **Caffe**: A deep learning framework used for deploying the pretrained colorization model (`colorization_release_v2.caffemodel`).
- **OpenCV**: An open-source computer vision library used for various image processing tasks, including Canny edge detection and convex hull calculations.
  - **Canny Edge Detection**: Detects edges in images, used for various image processing tasks.
  - **Convex Hull**: Finds the convex hull of a set of points in an image, useful for shape analysis.
- **Haar Cascades**: Implements the Haar feature-based cascade classifiers for object detection, such as face detection.
  - **Haar Faces Algorithm**: Uses Haar-like features to detect faces in images.
- **Cascade Classifier**: Uses a series of increasingly complex classifiers for efficient object detection.
- **KNN Classifier**: Utilizes the K-Nearest Neighbors algorithm for classification tasks.
- **TensorFlow**: Employed for additional machine learning tasks, providing a flexible and comprehensive AI toolkit.
- **Keras**: High-level neural networks API, used alongside TensorFlow for building and deploying models.

## Usage

1. **Set Up Development Environment**: Ensure you have a compatible C++ development environment set up.
2. **Compile the Project**: Use your preferred C++ compiler to build the project.
3. **Run the Application**: Execute the compiled binary to start the image editor.

## Example

Here is an example of the application interface featuring various tools and adjustments:

![Image Editor Interface](example_interface.png)

## Time and Complexity Analysis

### Time Complexity

- **Image Adjustments**: The time complexity for applying image adjustments (e.g., brightness, contrast, saturation, clarity) is O(n), where n is the number of pixels in the image.
  - **Brightness Adjust**: O(n) for iterating over each pixel to adjust its brightness.
  - **Contrast Adjust**: O(n) for iterating over each pixel to adjust its contrast.
  - **Saturation Adjust**: O(n) for iterating over each pixel to adjust its saturation.
  - **Clarity Adjust**: O(n) for iterating over each pixel to adjust its clarity.
- **Filters**: The time complexity for applying filters like blur, sharpen, chrome, and vignette is O(n), ensuring efficient processing for real-time editing.
  - **Blur Filter**: O(n * k^2) where k is the kernel size for the blur operation.
  - **Sharpen Filter**: O(n) for applying the sharpening kernel to each pixel.
  - **Chrome Filter**: O(n) for applying the color transformation.
  - **Vignette Filter**: O(n) for calculating and applying the vignette effect.
- **Automatic Colorization**: The CNN-based colorization operates with a complexity dependent on the network architecture, typically O(n) for inference per image.
- **Background Removal**: Utilizes AI techniques with a complexity of O(n) for inference, ensuring fast processing for practical use.
- **Brush Tool**: The time complexity for applying brush strokes is O(n) per stroke, where n is the number of affected pixels.
- **Text Tool**: The time complexity for rendering text is O(n), where n is the number of characters to be rendered.

### Space Complexity

- **Image Storage**: The space complexity for storing images is O(n), where n is the number of pixels, as each pixel's color information must be stored.
- **Model Storage**: The pretrained models for colorization and background removal have fixed storage requirements, contributing to the overall space complexity.
- **Resource Management**: Efficient data structures are used to manage application resources like fonts and icons, ensuring minimal memory overhead.
- **Application State**: The space complexity for maintaining the application state is O(1), as it depends on fixed-size data structures for storing settings and user preferences.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.