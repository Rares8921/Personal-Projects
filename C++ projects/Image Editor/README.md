# Image Editor

<div align="center">

![C++](https://img.shields.io/badge/C++-17-00599C?style=for-the-badge&logo=cplusplus)
![OpenCV](https://img.shields.io/badge/OpenCV-4.x-5C3EE8?style=for-the-badge&logo=opencv)
![Caffe](https://img.shields.io/badge/Caffe-Deep_Learning-red?style=for-the-badge)
![Raylib](https://img.shields.io/badge/Raylib-GUI-black?style=for-the-badge)

**A professional-grade image editing application powered by AI, showcasing OOP mastery with 30+ filters, AI colorization, background removal, and advanced template programming.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a fully-featured image editor built entirely in C++ that rivals commercial tools like Photoshop Lite. It combines traditional image processing with cutting-edge AI - all running locally on your machine.

**Core Features:**

**Traditional Editing:**
- **30+ Filters** - Blur, sharpen, chrome, vignette, grayscale, inverted, fade, flare, shine, sparkle, difference of Gaussians, erosion
- **Color Adjustments** - Brightness, contrast, saturation, exposure, temperature, tint, highlights, shadows, vibrance, clarity, grain, glamour, bloom, dehaze, black/white points
- **Transformations** - Rotate, flip, crop, aspect ratio adjustments
- **Drawing Tools** - Brush (customizable size/color), eraser, fill/paint bucket, text tool with fonts
- **Text System** - Add text with Arial/Consolas fonts, adjustable size, spacing, borders, shadows

**AI-Powered Tools:**
- **Automatic Colorization** - Uses a pretrained Caffe CNN (`colorization_release_v2.caffemodel`) to colorize black & white images
- **Background Removal** - AI-based segmentation to extract subjects from backgrounds
- **Facial Recognition** - Haar Cascade implementation for face detection (`haarcascade_frontalface_default.xml`)
- **OCR (Text Recognition)** - Extract text from images using Tesseract (`eng.traineddata`)

**Advanced Features:**
- **Undo/Redo System** - Memento design pattern with full history stack (unlimited undos)
- **Composite Operations** - RGB channel manipulation, additive compositing
- **Smart Preprocessing** - Convolutional pipelines for noise reduction before AI operations
- **QuadTree Compression** - Efficient spatial data structure for image regions

**What Makes It Professional:**
- **7 OOP Principles:** Encapsulation, inheritance, polymorphism, composition, abstraction, operator overloading, dynamic binding
- **Smart Pointer Management** - `std::unique_ptr` and `std::shared_ptr` throughout (zero memory leaks)
- **Template Programming** - Generic algorithms for image processing operations
- **Design Patterns:** Memento (undo/redo), Strategy (filters), Composite (layers), Factory (tool creation)

---

## Tech Stack

**Language:** C++17  
**Image Processing:** OpenCV 4.x (cv::Mat, filters, transformations)  
**Deep Learning:** Caffe (CNN inference for colorization)  
**GUI:** Raylib (custom GUI system with scrollable menus)  
**OCR Engine:** Tesseract 4.x  
**Build:** Visual Studio (Windows native)

### Architecture

Multi-layered architecture with separation of concerns:

```
GUI Layer (Raylib + Custom Components)
      ↓
┌─────────────────────────────────────────┐
│  ImageEditor (main controller)          │
│  - Event handling                       │
│  - Menu/submenu state management        │
│  - Tool switching                       │
└─────────────────────────────────────────┘
      ↓
┌─────────────────────────────────────────┐
│  Tool Layer (polymorphic tools)         │
│  - Filters (blur, sharpen, chrome...)   │
│  - Adjusters (brightness, contrast...)  │
│  - Transformers (rotate, flip, crop)    │
│  - DrawTools (brush, eraser, fill)      │
│  - TextTag (text rendering system)      │
└─────────────────────────────────────────┘
      ↓
┌─────────────────────────────────────────┐
│  AI Layer                               │
│  - AutomaticColorization (Caffe CNN)    │
│  - BackgroundRemover (segmentation)     │
│  - FacialRecognition (Haar Cascade)     │
│  - OCR (Tesseract integration)          │
└─────────────────────────────────────────┘
      ↓
┌─────────────────────────────────────────┐
│  Memento Pattern (Undo/Redo)            │
│  - Originator (holds current state)     │
│  - Caretaker (manages history stack)    │
│  - Memento (immutable snapshots)        │
└─────────────────────────────────────────┘
      ↓
OpenCV cv::Mat (image buffer)
```

**Design Pattern Implementation:**

1. **Memento Pattern (Undo/Redo):**
   - `Originator.cpp` - Stores current image state
   - `Caretaker.cpp` - Manages undo/redo history stack
   - `Memento.cpp` - Immutable image snapshots
   - Supports unlimited undo/redo with `imageIndex` tracking

2. **Strategy Pattern (Filters):**
   - `Filter.h` - Abstract filter interface
   - 15+ concrete implementations (BlurAdjust, SharpenAdjust, ChromeFilter, etc.)
   - Runtime filter swapping via polymorphism

3. **Composite Pattern:**
   - `AddComposite.cpp` / `RGBComposite.cpp` - Layer blending operations
   - Recursive image compositing

4. **Factory Pattern:**
   - `DrawTool` factory creates Brush, Eraser, FillTool dynamically
   - `std::unordered_map<std::string, std::unique_ptr<DrawTool>>` for tool management

**OOP Techniques:**
- **Operator Overloading:** `ImageEditor& operator=(const ImageEditor&)` for deep copy
- **Templates:** Generic `Tuple3f`, `Tuple4f`, `Vector3f` for color/vector math
- **Smart Pointers:** All dynamic resources managed via `std::unique_ptr` and `std::shared_ptr`
- **Inheritance & Polymorphism:** `Filter`, `Adjuster`, `Transformer`, `DrawTool` base classes
- **Composition:** `ImageEditor` composed of GUI, filters, adjusters, tools
- **Dynamic Binding:** Virtual functions throughout (e.g., `Filter::apply()`)

---

## Project Structure

```
Image Editor/
├── Core/
│   ├── main.cpp                          # Entry point
│   ├── ImageEditor.cpp / ImageEditor.h   # Main controller (900+ lines)
│   ├── GUI.cpp / GUI.h                   # Custom GUI system
│   └── Includes.h                        # Global headers
│
├── Filters/ (Strategy Pattern)
│   ├── Filter.cpp / Filter.h             # Abstract base class
│   ├── BlurAdjust.cpp / BlurAdjust.h
│   ├── SharpenAdjust.cpp / SharpenAdjust.h
│   ├── ChromeFilter.cpp / ChromeFilter.h
│   ├── VignetteAdjust.cpp / VignetteAdjust.h
│   ├── GrayscaleFilter.cpp / GrayscaleFilter.h
│   ├── InvertedFilter.cpp / InvertedFilter.h
│   ├── FadeFilter.cpp / FadeFilter.h
│   ├── FlareFilter.cpp / FlareFilter.h
│   ├── ShineFilter.cpp / ShineFilter.h
│   ├── SparkleFilter.cpp / SparkleFilter.h
│   ├── DiffOfGaussiansFilter.cpp / DiffOfGaussiansFilter.h
│   ├── ErodeFilter.cpp / ErodeFilter.h
│   └── FlipFilter.cpp / FlipFilter.h
│
├── Adjusters/
│   ├── Adjusters.cpp / Adjusters.h       # Base adjuster class
│   ├── BrightnessAdjust.cpp / BrightnessAdjust.h
│   ├── ContrastAdjust.cpp / ContrastAdjust.h
│   ├── SaturationAdjust.cpp / SaturationAdjust.h
│   ├── ExposureAdjust.cpp / ExposureAdjust.h
│   ├── TemperatureAdjust.cpp / TemperatureAdjust.h
│   ├── TintAdjust.cpp / TintAdjust.h
│   ├── HighlightsAdjust.cpp / HighlightsAdjust.h
│   ├── ShadowsAdjust.cpp / ShadowsAdjust.h
│   ├── VibranceAdjust.cpp / VibranceAdjust.h
│   ├── ClarityAdjust.cpp / ClarityAdjust.h
│   ├── GrainAdjust.cpp / GrainAdjust.h
│   ├── GlamourAdjust.cpp / GlamourAdjust.h
│   ├── BloomAdjust.cpp / BloomAdjust.h
│   ├── DehazeAdjust.cpp / DehazeAdjust.h
│   ├── BlackAdjust.cpp / BlackAdjust.h
│   ├── WhiteAdjust.cpp / WhiteAdjust.h
│   ├── HueAdjust.cpp / HueAdjust.h
│   ├── OpacityAdjust.cpp / OpacityAdjust.h
│   └── SmoothnessAdjust.cpp / SmoothnessAdjust.h
│
├── Transformers/
│   ├── Transformer.cpp / Transformer.h   # Base transformer class
│   ├── Crop.cpp / Crop.h
│   ├── AspectRatio.cpp / AspectRatio.h
│   └── RotateFilter.cpp / RotateFilter.h
│
├── DrawTools/
│   ├── DrawTool.cpp / DrawTool.h         # Abstract drawing tool
│   ├── Brush.cpp / Brush.h
│   ├── Eraser.cpp / Eraser.h
│   └── FillTool.cpp / FillTool.h
│
├── AI/
│   ├── AutomaticColorization.cpp / .h    # Caffe CNN colorization
│   ├── BackgroundRemover.cpp / .h        # AI segmentation
│   ├── FacialRecognition.cpp / .h        # Haar Cascade face detection
│   └── OCR.cpp / OCR.h                   # Tesseract text extraction
│
├── Memento Pattern/
│   ├── Originator.cpp / Originator.h     # Current state holder
│   ├── Caretaker.cpp / Caretaker.h       # History manager
│   └── Memento.cpp / Memento.h           # Snapshots
│
├── Utilities/
│   ├── Preprocessing.cpp / .h            # Image preprocessing pipelines
│   ├── QuadTree.cpp / QuadTree.h         # Spatial data structure
│   ├── ConvexHull.cpp / ConvexHull.h     # Geometry algorithms
│   ├── FFT.cpp / FFT.h                   # Fast Fourier Transform
│   ├── TextTag.cpp / TextTag.h           # Text rendering
│   ├── Tuple3f / Tuple4f / Vector3f      # Math primitives (templates)
│   └── gui_window_file_dialog.h          # File picker
│
├── Compositing/
│   ├── AddComposite.cpp / AddComposite.h
│   └── RGBComposite.cpp / RGBComposite.h
│
└── Assets/
    ├── appIcon.ico                       # Application icon
    ├── Arial.TTF, Consolas.TTF           # Fonts (with bold/italic variants)
    ├── colorization_release_v2.caffemodel # Pretrained CNN model
    ├── haarcascade_frontalface_default.xml # Face detection model
    └── eng.traineddata                   # Tesseract English data
```

---

## Getting Started

**Prerequisites:**
- Visual Studio 2019+ (Windows)
- OpenCV 4.x (libraries pre-configured)
- Caffe (included with project - CPU mode)
- Tesseract 4.x
- 4GB+ RAM (for AI operations)

**Setup (3 minutes):**

1. **Open the project**
```bash
# Navigate to project folder
cd "Image Editor"

# Open Visual Studio solution
start ImageEditor.sln
```

2. **Build and run**
- Press `F5` or click "Local Windows Debugger"
- The editor will launch with a blank canvas

**Basic Workflow:**

1. **Load an image:**
   - Click `File → Open` or drag & drop
   - Supports: PNG, JPG, JPEG, BMP, TGA, PSD, DDS, HDR

2. **Apply filters:**
   - Open `Adjustments` menu for brightness/contrast/saturation
   - Use `Filters` menu for blur, sharpen, chrome, vignette
   - Drag sliders to adjust intensity (real-time preview)

3. **AI Tools:**
   - `AI Tools → Colorize` - Automatically colorize B&W images (takes ~5 seconds)
   - `AI Tools → Remove Background` - Extract subject from background
   - `AI Tools → Detect Faces` - Highlight faces with bounding boxes
   - `AI Tools → Extract Text` - OCR text from image

4. **Drawing:**
   - Select `Brush`, adjust size/color, paint on canvas
   - Use `Eraser` to remove strokes
   - `Fill Tool` for flood-filling regions

5. **Text:**
   - Click `Add Text`, type your message
   - Customize font (Arial/Consolas), size, color, shadow, border

6. **Undo/Redo:**
   - `Ctrl+Z` - Undo
   - `Ctrl+Y` - Redo
   - Unlimited undo history

7. **Save:**
   - `File → Save` or `File → Export`
   - Supports all input formats

---

## What's Next

Planned enhancements:

**Features:**
- **Layer System** - Photoshop-style layers with blending modes
- **GPU Acceleration** - CUDA/OpenCL for real-time filter previews
- **More AI Models** - Super-resolution (upscaling), style transfer, denoising
- **Batch Processing** - Apply filters to entire folders
- **Plugins** - Scripting API for custom filters (Lua/Python bindings)

**Performance:**
- **Multithreading** - Parallelize filter operations with `std::async`
- **Streaming** - Load/process large images in chunks (10000x10000+)
- **Optimize Memento** - Incremental snapshots instead of full copies

**Polish:**
- **Keyboard Shortcuts** - Fully customizable hotkeys
- **Dark Mode** - GUI theme system
- **Presets** - Save/load filter chains (like Lightroom presets)
- **Cross-Platform** - Port to Linux/Mac using CMake

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
