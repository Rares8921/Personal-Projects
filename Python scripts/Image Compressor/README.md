# Image Compressor

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![PIL](https://img.shields.io/badge/PIL-Pillow-yellow?style=for-the-badge)
![Tkinter](https://img.shields.io/badge/Tkinter-GUI-green?style=for-the-badge)

**Batch image compression with quality control. Reduce file sizes while maintaining visual quality.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a batch image compression tool that reduces JPEG/JPG file sizes by applying lossy compression. It processes all images in the current directory, allowing you to save compressed versions via a file dialog.

**Key features:**
- **Batch processing:** Compresses all .jpg/.jpeg files in current directory
- **Quality control:** Fixed 55% quality (adjustable in code)
- **Optimization:** PIL's optimize flag removes unnecessary metadata
- **File dialog:** Choose output location for each compressed image
- **Format conversion:** Enforces JPEG output format

**Compression technique:**
- Uses JPEG lossy compression algorithm
- Quality setting (1-100): 55 provides good size/quality balance
- Optimize flag: Runs additional compression passes
- Typical compression: 60-80% file size reduction

**Use cases:**
- Preparing images for web upload (faster loading)
- Reducing storage usage for large photo collections
- Email attachments (stay under size limits)
- Mobile app assets (smaller APK/IPA sizes)

---

## Tech Stack

**Language:** Python 3.7+  
**Image Processing:** PIL/Pillow (Python Imaging Library)  
**GUI:** tkinter.filedialog (save dialog)  
**File I/O:** os module (directory listing, path operations)

### Architecture

```
Current Working Directory
         ↓
Scan for .jpg/.jpeg Files
(os.listdir + extension filter)
         ↓
For Each Image:
├── Open with PIL (Image.open)
├── Display save dialog (asksaveasfilename)
├── Save with compression
│   └── Quality=55, Optimize=True
└── Log progress to console
         ↓
Completion Message
```

**Compression Pipeline:**

```
Original JPEG (100% quality, 2 MB)
         ↓
PIL Image.open() → Decode
         ↓
Image.save(quality=55, optimize=True)
├── Reduce quality to 55%
├── Apply DCT coefficient truncation
├── Run optimization passes
└── Remove EXIF metadata
         ↓
Compressed JPEG (~55% quality, 0.5 MB)
```

---

## Project Structure

```
Image Compressor/
├── ImageCompressor.py   # Compression logic
└── README.md
```

**Code structure:**
- Lines 1-3: Imports (os, PIL.Image, tkinter.filedialog)
- Lines 6-11: `compress()` function (single file compression)
- Lines 14-21: `main()` function (batch processing loop)
- Lines 24-25: Entry point (`if __name__ == '__main__'`)

**Key parameters:**
- `quality=55`: JPEG quality (1=worst, 100=best)
- `optimize=True`: Enable multi-pass optimization
- `formats = ('.jpg', '.jpeg')`: Supported input formats

---

## Getting Started

**Requirements:**
- Python 3.7+
- Pillow library

**Installation:**

```bash
pip install pillow
```

**Run it:**

```bash
# Navigate to folder with images
cd path/to/images

# Run compressor
python ImageCompressor.py
```

**What happens:**
1. Script scans current directory for .jpg/.jpeg files
2. For each image, opens save dialog
3. Choose output location and filename
4. Image compressed and saved
5. Console shows: `Compressing filename.jpg`
6. Repeat for all images
7. Final message: `The compressing has been completed`

**Example output:**

```
Compressing IMG_001.jpg
Compressing IMG_002.jpg
Compressing vacation.jpeg
The compressing has been completed
```

---

## What's Next

**Possible enhancements:**
- Add command-line arguments for quality setting
- Support more formats (PNG, WebP, TIFF)
- Preserve EXIF metadata (camera info, GPS, timestamps)
- Show compression ratio (before/after file sizes)
- Create GUI with quality slider
- Batch output to specific folder (no dialog per file)
- Resize images during compression (max width/height)
- Compare image quality (PSNR, SSIM metrics)

**Configuration options:**

```python
# Adjust quality (current: 55)
picture.save(savePath, "JPEG", quality=85, optimize=True)

# Preserve EXIF metadata
picture.save(savePath, "JPEG", quality=55, exif=picture.info.get('exif'))

# Progressive JPEG (loads gradually in browsers)
picture.save(savePath, "JPEG", quality=55, progressive=True)

# Add more formats
formats = ('.jpg', '.jpeg', '.png', '.bmp', '.tiff')
```

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
