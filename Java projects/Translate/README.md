# English-Romanian translator

## Project Overview

This project is a graphical user interface (GUI) application built using Java Swing for text translation between two languages: English (EN) and Romanian (RO). The program allows users to input text, translate it, and manage languages and files using a menu bar. The translation functionality relies on an external API from MyMemory to perform the language conversion.

## Features and Functionality
1. **Text Input and Translation:**
   - Users can input text into a JTextArea field, and upon clicking the "Translate" button, the text is translated from the source language to the target language.
   
2. **Language Switcher:**
   - The GUI provides buttons to switch between English and Romanian as the source and target languages. Users can easily switch languages via a "Switch Language" button or through key binds.

3. **File Management:**
   - The program includes file handling capabilities where users can upload text files for translation using the `JFileChooser`. The content of the selected file will be displayed in the input text area and can then be translated.

4. **Key Bindings:**
   - The program supports various keyboard shortcuts to simplify user interactions:
     - `Ctrl + C`: Copy text.
     - `Ctrl + V`: Paste text.
     - `Ctrl + X`: Delete text.
     - `Ctrl + Z`: Undo.
     - `Ctrl + R`: Redo.
     - `Ctrl + H`: Replace text.
     - `Ctrl + F`: Find text and highlight it.
     - `Ctrl + F1`: Remove highlights.
     - `Ctrl + F2`: Switch languages.
     - `Ctrl + F3`: Translate text.
     - `Ctrl + F4`: Select a file for translation.
     - `Ctrl + F5`: Change the UI language.

5. **Custom Components:**
   - The program uses several custom components to provide a personalized appearance, including:
     - `CustomJToolTip`: A custom tooltip that adapts to the program's dark mode theme.
     - `MyKeyListener`: A custom key listener that handles text editing shortcuts like Undo, Redo, Find, and Replace.

6. **API Integration:**
   - The translation is done using the MyMemory API, where HTTP requests are made to fetch translated text from the server. The translation method `translate()` sends the text input and language pair to the API and returns the translated result.

## Program Structure

### Main Class
- **GUI Components:**
  - **`JTextArea field1, field2`**: Input and output fields for text in English and Romanian.
  - **`JButton lang, send, folders, switch_lang, first_lang, second_lang`**: Buttons to trigger translation, switch languages, open file dialog, and select languages.
  - **`JMenuBar`**: Menu bar for controlling settings like language selection.
  
- **Action Listeners:**
  - The buttons use custom `ActionListener` implementations to perform tasks such as switching languages, sending text for translation, and opening files.

- **Highlighting and Text Manipulation:**
  - **Text Find & Highlighting**: Users can search and highlight text within the input and output fields using `Ctrl + F`.
  - **Text Replace**: Users can replace occurrences of text using `Ctrl + H`.
  - **Undo/Redo**: Undo/Redo functionality is managed by the `UndoManager`.

### MyKeyListener Class
- This class extends `KeyAdapter` and provides custom key bindings for editing, text search, and translation. It handles the keyboard shortcuts that manage actions such as translation (`Ctrl + F3`), language switching, text search, and replacement.

### Translation Method
- **`translate(String query, String sourceLang, String targetLang)`**: 
  - This method sends an HTTP GET request to the MyMemory API with the text to be translated (`query`) and the source and target languages.
  - The API response is processed, and the translated text is returned.
  
- **`getJsonObject(HttpURLConnection connection)`**: 
  - Parses the API response as a JSON object to extract the translation result.

### Custom Components
- **CustomJToolTip**: 
  - A custom tooltip to match the dark theme of the application.
  
- **MyHighlightPainter**: 
  - Used to highlight search results in the JTextArea fields.

## Time Complexity Analysis

- **Translation Method:**
  - The translation method involves sending an HTTP request and receiving a response. The time complexity of the translation process depends on the external API's response time, but from the program's perspective, it performs in O(n), where `n` is the length of the text being encoded and sent.
  
- **Text Search and Replace:**
  - **Search**: The text search function highlights all occurrences of the search term, which requires scanning the entire text in the JTextArea, leading to a time complexity of O(m*n), where `m` is the number of occurrences of the search term and `n` is the length of the text.
  - **Replace**: Text replacement has a time complexity of O(n), where `n` is the size of the string to be replaced, as the entire text needs to be scanned and replaced.

- **Undo/Redo:**
  - Undo/Redo functionality operates in O(1) time as it merely reverses the last action taken on the text.

## Space Complexity Analysis

- **Translation Method:**
  - Space complexity is O(n), where `n` is the length of the text being translated. The response is stored in memory as a string.

- **Text Search and Replace:**
  - The search and replace methods have a space complexity of O(n) because they store the input text and the modified text as separate strings during processing.

## Summary
This project offers a comprehensive GUI-based text translation tool with integrated keyboard shortcuts, API-based translation, file handling, and text manipulation features. The user interface is customized for a dark theme, making it visually appealing, and the program is highly interactive due to its extensive use of keyboard shortcuts.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.