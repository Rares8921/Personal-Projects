# Image Compression Script

## Overview

This Python script is designed to compress `.jpg` and `.jpeg` image files in the current working directory. Using the `Pillow` library, the script compresses the images and allows the user to save the compressed versions with optimized quality. A graphical user interface (GUI) dialog is used to select the destination path for the compressed files.

### Key Features:
- Compresses `.jpg` and `.jpeg` image files.
- Uses the `Pillow` library to optimize image quality.
- Allows users to choose the save location for each compressed image through a GUI.
- Easy to use and modify.

## Code Breakdown

### Imports:
- `os`: Used to handle file paths and interact with the file system.
- `PIL` (Pillow): Provides methods for opening and manipulating images.
- `tkinter.filedialog`: Used to open a GUI save dialog for specifying where to save the compressed images.


### Program Workflow:

    1. The script scans the current working directory for .jpg and .jpeg files. <br/>
    2. When it finds an image file, it passes it to the compress() function. <br/>
    3. The compress() function opens a dialog to let the user choose a save location for the compressed image. <br/>
    4. The image is compressed using the Pillow library with a quality setting of 55 and saved in JPEG format. <br/>
    5. The process continues for all image files in the directory, and finally, a completion message is printed. <br/>

### Time and Space Complexity
    Time Complexity:
        O(n) where n is the number of files in the current directory. The script must iterate through all files in the directory and perform compression on valid image files.

    Space Complexity:
        The space complexity depends on the size of the images being compressed. Each image is loaded into memory, processed, and saved, so the space complexity is approximately O(1) for each image as the script processes one image at a time.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.