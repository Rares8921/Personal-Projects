# Command Bot Webpage

## Description

This is a simple webpage that simulates a command bot interface on a smartphone-like screen. The webpage allows users to input specific commands and performs actions based on those commands. It uses HTML, CSS, and JavaScript to create an interactive and visually appealing interface.

## Features

- **Command Input**: Users can enter commands like `!date`, `!site`, and `!calc`.
- **Change Color**: Users can change the background color of the command response area.
- **Date Display**: Shows the current date when the `!date` command is entered.
- **Open Website**: Prompts the user to enter a website URL and opens it in a new tab when the `!site` command is entered.
- **Calculator**: Displays a simple calculator interface when the `!calc` command is entered.
- **Text-to-Speech**: Converts user-entered text to speech.
- **Smartphone Frame**: Displays the interface inside a smartphone-like frame for a more realistic appearance.

## Structure

### HTML

- The HTML structure includes a smartphone frame, an image, a command input field, action buttons, and a hidden calculator interface.
- The `img` tag is used to display an image representing the bot.
- The `weltext` div displays a list of available commands.
- The `cmd` div contains the input field for entering commands.
- The `actions` div includes buttons for submitting the command, changing the color, and activating the text-to-speech feature.
- The hidden calculator interface is included inside the `calc` div.

### CSS

- **Reset Styles**: Basic reset styles for input fields.
- **Core Styles**: Basic styles for input fields and labels.
- **Presentation Styles**: Enhanced styles for the input fields, including transitions and animations.
- **Smartphone Frame**: Styles to create a smartphone-like frame around the main content.
- **Command Response Area**: Styles for the area where command responses are displayed.
- **Calculator Styles**: Styles for the simple calculator interface.

### JavaScript

- **changeColor()**: Prompts the user for a color and changes the background color of the command response area.
- **doCommand()**: Handles the input command and performs the appropriate action.
- **sae()**: Prompts the user for text and converts it to speech.
- **Calculator Functions**: Basic functions to handle the calculator interface, including adding values to the display and calculating the result.

## Usage

1. **Input Commands**: Enter a command in the input field and press the enter button.
   - `!date`: Displays the current date.
   - `!site`: Prompts for a website URL and opens it in a new tab.
   - `!calc`: Displays a simple calculator interface.
2. **Change Color**: Click the color palette button to change the background color of the command response area.
3. **Text-to-Speech**: Click the speech bubble button to enter text and convert it to speech.

## Example

Here is an example of how the webpage might look when the `!date` command is entered:

![Command Bot Example](example.png)

## License

This project is open-source and available under the MIT License.