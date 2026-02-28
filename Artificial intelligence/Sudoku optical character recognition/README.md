# Sudoku OCR Solver

<div align="center">

![Python](https://img.shields.io/badge/Python-3.x-blue?style=for-the-badge&logo=python)
![OpenCV](https://img.shields.io/badge/OpenCV-4.x-green?style=for-the-badge&logo=opencv)
![Tesseract](https://img.shields.io/badge/Tesseract-OCR-red?style=for-the-badge)
![NumPy](https://img.shields.io/badge/NumPy-Math-blue?style=for-the-badge&logo=numpy)

**An OCR system that extracts and recognizes digits from Sudoku puzzle images using computer vision. Point your camera at a puzzle and let the computer read it.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Algorithm](#algorithm) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a computer vision + OCR pipeline that takes an image of a Sudoku puzzle and automatically extracts all the digits into a 9x9 matrix. It handles real-world challenges like rotation, perspective distortion, and varying image quality.

**Key Features:**
- Automatic Sudoku grid detection and extraction
- Perspective correction for skewed images
- Cell segmentation (9x9 = 81 individual cells)
- Tesseract OCR integration for digit recognition
- Robust preprocessing (grayscale, blur, adaptive thresholding)
- Handles empty cells gracefully
- Exports processed digits for inspection

**Use cases:**
- Sudoku solver applications
- Mobile puzzle scanning apps
- OCR research and education
- Computer vision pipeline demonstrations

The system processes the image through: Grid Detection → Perspective Correction → Cell Segmentation → Digit Recognition → Matrix Output.

---

## Tech Stack

**Language:** Python 3.x  
**Computer Vision:** OpenCV 4.x  
**OCR Engine:** Tesseract  
**Image Processing:** NumPy, PIL/Pillow  
**Algorithm:** Suzuki-Abe border-following for contour detection

### Architecture

Two-module design with clear separation of concerns:

```
Input Image (Sudoku puzzle photo)
      ↓
ImageProcessing.py
  ├── imagePreProcessing → Grayscale + Blur + Threshold
  ├── findPuzzle → Contour detection (Suzuki-Abe algorithm)
  ├── rectify → Perspective correction
  ├── splitSudokuCells → 9x9 grid segmentation
  └── writeCellsToFolder → Save processed cells
      ↓
SudokuOCR.py
  ├── extractDigitFromImage → Tesseract OCR per cell
  └── OCR → Build 9x9 matrix
      ↓
[9x9 Sudoku Matrix] or -1 on error
```

**Key Components:**

1. **imagePreProcessing:** Converts to grayscale, applies Gaussian blur (kernel size 11), uses adaptive thresholding (block size 11, C=2) for binary image
2. **findPuzzle:** Detects contours using Suzuki-Abe algorithm, finds largest quadrilateral (assumed to be Sudoku grid)
3. **rectify:** Applies perspective transform to create normalized square grid
4. **splitSudokuCells:** Divides corrected grid into 81 cells (9×9)
5. **writeCellsToFolder:** Preprocesses each cell (inverts colors, cleans up) and saves to disk
6. **extractDigitFromImage:** Runs Tesseract OCR on individual cell images
7. **OCR:** Orchestrates the full pipeline, returns 9x9 matrix

---

## Algorithm Basis

The contour detection uses the **Suzuki-Abe border-following algorithm**, a foundational computer vision technique for topological analysis of binary images.

**Reference:**
> Suzuki, S. and Abe, K., "Topological Structural Analysis of Digitized Binary Images by Border Following"  
> Computer Vision, Graphics, and Image Processing 30(1), pp. 32-46 (1985)

This algorithm efficiently traces borders of connected components in binary images, enabling robust detection of the Sudoku grid even with noise and imperfections.

---

## Project Structure

```
Sudoku optical character recognition/
├── SudokuOCR.py                    # Main OCR logic
├── ImageProcessing.py              # Computer vision utilities
├── processedDigits/                # Output folder for extracted cells
├── sudoku.png                      # Example Sudoku image
├── README.md                       # This file
└── Topological-structural...pdf    # Suzuki-Abe algorithm paper
```

---

## Getting Started

**Prerequisites:**
- Python 3.x
- Tesseract OCR installed on your system

**Install Tesseract:**
```bash
# Windows (via Chocolatey)
choco install tesseract

# Ubuntu/Debian
sudo apt-get install tesseract-ocr

# macOS
brew install tesseract
```

**Install Python dependencies:**
```bash
pip install opencv-python pytesseract pillow numpy
```

**Configure Tesseract path (if needed):**
Edit `SudokuOCR.py` to point to your Tesseract installation:
```python
pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'
```

**Run the OCR:**
```python
from SudokuOCR import OCR

# Process a Sudoku image
sudoku_matrix = OCR("sudoku.png")

if sudoku_matrix != -1:
    print("Extracted Sudoku:")
    for row in sudoku_matrix:
        print(row)
else:
    print("Error: Could not process image")
```

---

## How It Works

**Step-by-step breakdown:**

1. **Image Preprocessing**
   - Convert to grayscale (reduces complexity)
   - Apply Gaussian blur with kernel=11 (removes noise)
   - Adaptive threshold with block size=11, C=2 (binary conversion)

2. **Grid Detection**
   - Find all contours using Suzuki-Abe algorithm
   - Filter for quadrilaterals (4-sided shapes)
   - Select largest contour (assumed to be Sudoku grid)

3. **Perspective Correction**
   - Identify corner points of detected quadrilateral
   - Apply perspective transform to create square 450x450 image
   - Corrects rotation and skew

4. **Cell Segmentation**
   - Divide 450x450 grid into 81 cells (50x50 pixels each)
   - Extract each cell as individual bitmap

5. **Cell Preprocessing**
   - Invert colors (white digits on black background)
   - Clean borders (remove grid lines)
   - Enhance contrast for OCR

6. **Digit Recognition**
   - Run Tesseract OCR on each preprocessed cell
   - Filter results (digits 1-9 only)
   - Empty cells detected as 0

7. **Matrix Construction**
   - Build 9x9 NumPy array from recognized digits
   - Return -1 if any step fails

---

## Performance

**Accuracy:** Depends on image quality and Tesseract configuration. Clean, high-contrast images achieve >95% digit recognition.

**Speed:** ~1-3 seconds per puzzle on modern hardware (dominated by Tesseract OCR time).

**Robustness:**
- ✅ Handles rotation and perspective distortion
- ✅ Works with various lighting conditions (adaptive threshold)
- ✅ Ignores background noise (contour filtering)
- ⚠️ Struggles with handwritten digits (Tesseract trained on printed text)
- ⚠️ Requires clear grid lines for detection

---

## Example Output

**Input:** `sudoku.png` (photo of Sudoku puzzle)

**Intermediate:** 81 preprocessed cell images saved to `processedDigits/`

**Output:**
```python
[
  [5, 3, 0, 0, 7, 0, 0, 0, 0],
  [6, 0, 0, 1, 9, 5, 0, 0, 0],
  [0, 9, 8, 0, 0, 0, 0, 6, 0],
  [8, 0, 0, 0, 6, 0, 0, 0, 3],
  [4, 0, 0, 8, 0, 3, 0, 0, 1],
  [7, 0, 0, 0, 2, 0, 0, 0, 6],
  [0, 6, 0, 0, 0, 0, 2, 8, 0],
  [0, 0, 0, 4, 1, 9, 0, 0, 5],
  [0, 0, 0, 0, 8, 0, 0, 7, 9]
]
```
(0 represents empty cells)

---

## What's Next

Potential improvements:
- Train custom OCR model for handwritten digits (CNN-based)
- Real-time detection from video stream
- Mobile app integration (Android/iOS)
- Automatic Sudoku solving after OCR
- Web interface with drag-and-drop upload
- GPU acceleration for faster processing
- Support for irregular/printed Sudoku grids

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.