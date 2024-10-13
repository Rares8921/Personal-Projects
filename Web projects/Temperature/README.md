# Temperature Converter

## Introduction

This document describes the functionality and time complexity analysis of the provided HTML and JavaScript code, which implements a temperature converter that converts temperatures between Celsius, Fahrenheit, and Kelvin.

## Functionality

The webpage provides a user interface for converting temperatures from one scale to another. The user can input a temperature value and select the scale to convert from. The converted values in the other scales are then displayed.

### HTML Structure

#### Head Section

- **Stylesheets and Scripts:**
  - Links to external stylesheets for Font Awesome, Bootstrap, and internal custom styles.
  - Links to external scripts for jQuery, Popper.js, and Bootstrap.

#### Style Section

- **Body Styles:**
  - Sets background color and font family.
  - Styles the heading (`h3`) and various input sections (`prim`, `secund`, `trei`).

- **Form Styles:**
  - Resets and customizes input field styles for better UX.
  - Defines core, presentation, and interaction styles for the input fields.

- **Button Styles:**
  - Styles for conversion buttons, including hover and active states.

- **Dropdown Styles:**
  - Styles for dropdown buttons and content.

#### Body Section

- **Heading:**
  - Displays the main heading of the converter: "Convert from °C to °F and °K".

- **Dropdown Button:**
  - A button with a thermometer icon to toggle between Celsius, Fahrenheit, and Kelvin.

- **Input Field:**
  - A labeled input field for entering the temperature value to be converted.

- **Convert Button:**
  - A button to trigger the conversion process.

- **Output Fields:**
  - Paragraph elements to display the converted temperature values in Fahrenheit and Kelvin.

### JavaScript Section

#### Functions

1. **validate(evt):**
   - Validates the input to ensure only numeric values are entered.
   - Uses regular expressions to allow numbers, negative signs, and decimal points.

2. **runScript():**
   - Retrieves the input temperature and converts it to the other scales.
   - Updates the output fields with the converted values.
   - Handles conversion from Celsius, Fahrenheit, and Kelvin based on the input placeholder text.

3. **myFunction():**
   - Toggles the input placeholder and heading text to switch between temperature scales.
   - Updates the input placeholder and heading to reflect the new conversion scale.

## Time Complexity Analysis

### HTML Rendering
- **Time Complexity:** O(1), as the structure and styles of the webpage are fixed and do not change dynamically.

### Input Validation (validate function)
- **Time Complexity:** O(1), since the validation checks a single character input at a time.

### Conversion Calculation (runScript function)
- **Time Complexity:** O(1), as the conversion formulae are executed a constant number of times regardless of the input size.

### Toggling Placeholder and Heading (myFunction function)
- **Time Complexity:** O(1), as the function updates the DOM elements' properties based on fixed conditions.

### Overall Time Complexity
- **Overall Time Complexity:** O(1) for all interactive functions, ensuring efficient execution regardless of user interactions.

This analysis shows that the program is efficient with constant time complexity for all operations.