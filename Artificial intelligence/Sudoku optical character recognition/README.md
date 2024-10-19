# OCR Sudoku Solver

This project is a Python-based Optical Character Recognition (OCR) system that is designed to extract and recognize digits from a Sudoku puzzle image. The system processes the input image, segments it into individual cells, and then extracts each digit using the Tesseract OCR engine. The recognized digits are then used to form a Sudoku matrix, which can be further solved or processed.

## Algorithm Basis. License and Ownership

The border-following algorithm used in contour detection is based on the paper by Suzuki and Abe:

- **Suzuki, S. and Abe, K., Topological Structural Analysis of Digitized Binary Images by Border Following. CVGIP 30 1, pp 32-46 (1985)**

This algorithm efficiently detects the borders of shapes within binary images, allowing the system to identify the largest square within the image, which is assumed to be the Sudoku grid.

## Project Structure

The project is composed of two main Python files:

1. **SudokuOCR.py**: Handles the overall OCR process, extracts digits from Sudoku puzzle images, and builds the Sudoku matrix.
2. **ImageProcessing.py**: Contains helper functions for pre-processing the image, finding the puzzle grid, and segmenting the cells.

### SudokuOCR.py Overview

- **extractDigitFromImage**: This function reads an individual Sudoku cell image and attempts to recognize the digit within it using the Tesseract OCR engine.
- **OCR**: This function processes the entire Sudoku puzzle image, divides it into 9x9 cells, and populates a matrix with the recognized digits. It handles possible errors and edge cases by returning -1 when a cell can't be processed.
  
### ImageProcessing.py Overview

- **rectify**: Used to adjust the shape of the puzzle into a square grid by correcting the corners of the detected puzzle using geometric transformations.
- **imagePreProcessing**: Prepares the input image by applying grayscale conversion, Gaussian blur, and adaptive thresholding for better segmentation of the binary image.
- **findPuzzle**: Finds the Sudoku grid within the image by detecting contours using techniques from the paper "Topological Structural Analysis of Digitized Binary Images by Border Following" by Suzuki and Abe.
- **splitSudokuCells**: Splits the detected Sudoku grid into 81 individual cells, stored as a bitmap for further processing.
- **writeCellsToFolder**: Processes each cell, saves the preprocessed images into a folder, and prepares them for OCR by inverting pixel values and applying necessary transformations.

## Image Preprocessing

Before recognizing digits, the image undergoes several preprocessing steps:

1. **Grayscale Conversion**: Converts the original image to grayscale for simplicity.
2. **Gaussian Blur**: Applies a blur to reduce noise.
3. **Thresholding**: Segments the image into black and white pixels for clear contour detection.

After preprocessing, the largest square contour (assumed to be the Sudoku grid) is detected and extracted. The grid is then transformed into a standard 9x9 layout.