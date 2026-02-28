# QR Code Generator

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![QRCode](https://img.shields.io/badge/Library-qrcode-black?style=for-the-badge)
![PIL](https://img.shields.io/badge/PIL-Pillow-yellow?style=for-the-badge)

**Turn URLs, text, and data into scannable QR codes. Perfect for sharing links, contact info, or WiFi credentials.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a QR code generator that converts text, URLs, or any string data into scannable QR code images. The program uses Reed-Solomon error correction to ensure codes remain readable even if partially damaged.

**Features:**
- Generate QR codes from any text/URL
- Configurable size and border
- Black and white color scheme (customizable)
- Auto-fits data (dynamic size adjustment)
- Saves as PNG image file

**Current configuration:**
- Example data: YouTube URL
- Version: 1 (21×21 modules, fits ~25 alphanumeric characters)
- Box size: 10 pixels per module
- Border: 5 modules (white quiet zone)
- Output: QRCode.png

**Use cases:**
- Share website links (business cards, posters)
- WiFi credentials (auto-connect when scanned)
- Contact information (vCard format)
- Product URLs (redirect to product pages)
- Event tickets (encoding ticket IDs)
- Payment information (Bitcoin addresses, payment links)

---

## Tech Stack

**Language:** Python 3.7+  
**Libraries:** qrcode (QR generation), PIL/Pillow (image creation)  
**Standard:** ISO/IEC 18004:2015 (QR Code specification)

### Architecture

```
Input Data (text/URL)
         ↓
QRCode Instance Creation
(version, box_size, border)
         ↓
Data Encoding
├── Determine optimal data mode
│   (numeric/alphanumeric/binary)
├── Apply Reed-Solomon error correction
└── Add format information
         ↓
Matrix Generation
(black/white module grid)
         ↓
Image Rendering (PIL)
├── Scale to box_size
├── Add border (quiet zone)
└── Apply fill/back colors
         ↓
PNG File Output
```

**QR Code Structure:**

```
┌─────────────────────┐
│ ■■■■■■■ ░ ■ ■■■■■■■ │  ← Finder patterns (3 corners)
│ ■     ■ ░ ■ ■     ■ │
│ ■ ■■■ ■ ■░  ■ ■■■ ■ │  ← Alignment patterns
│ ■ ■■■ ■ ░░░ ■ ■■■ ■ │
│ ■ ■■■ ■ ■ ░ ■ ■■■ ■ │  ← Timing patterns
│ ■     ■ ░░■ ■     ■ │
│ ■■■■■■■ ■░■ ■■■■■■■ │  ← Data modules (encoded)
│         ░ ░ ░       │
│ ░░■■ ░■░■░■░░ ■░ ░■ │  ← Error correction codes
│  ░■░ ░░ ░ ░ ■ ░  ░  │
│ ■  ░■░░ ■ ░░ ■ ░ ■  │
└─────────────────────┘
```

**Parameters explained:**
- **version:** QR code size (1-40, auto if None)
  - Version 1: 21×21 modules (~25 chars)
  - Version 10: 57×57 modules (~395 chars)
  - Version 40: 177×177 modules (~2953 chars)
- **box_size:** Pixels per module (10 = 210×210px for v1)
- **border:** Quiet zone width (recommended: 4 modules minimum)

---

## Project Structure

```
QR Code Generator/
├── QRCode.py            # QR generation logic
├── QRCode.png           # Generated QR code image
└── README.md
```

**Code structure:**
- Line 1: Import qrcode library
- Lines 4-5: Define data to encode
- Lines 7-8: Create QRCode instance with parameters
- Line 9: Add data to QR code
- Lines 11-12: Finalize and generate image
- Line 14: Save to file

---

## Getting Started

**Requirements:**
- Python 3.7+
- qrcode library
- Pillow (PIL)

**Installation:**

```bash
pip install qrcode[pil]
```

**Run it:**

```bash
python QRCode.py
```

This generates `QRCode.png` in the current directory.

**Customize for your data:**

```python
# URL
data = "https://github.com/yourusername"

# Plain text
data = "Hello, World!"

# WiFi credentials (auto-connect format)
data = "WIFI:T:WPA;S:NetworkName;P:password123;;"

# Contact info (vCard format)
data = """BEGIN:VCARD
VERSION:3.0
FN:John Doe
TEL:+1234567890
EMAIL:john@example.com
END:VCARD"""

# Larger QR code (more data)
qr = qrcode.QRCode(
    version=5,        # Bigger grid
    box_size=15,      # Larger pixels
    border=4          # Standard border
)
```

**Scan the generated QR code:**
Use any smartphone camera app or QR scanner app. Most modern phones auto-detect QR codes.

---

## What's Next

**Possible enhancements:**
- Command-line arguments for custom data
- GUI with text input field
- Custom colors (brand colors, gradients)
- Logo embedding (center image overlay)
- Batch generation (multiple QR codes from CSV)
- Error correction level control (L/M/Q/H)
- SVG output (scalable vector format)
- Dynamic QR codes (redirect URLs)

**Advanced features:**

```python
# Higher error correction (survives more damage)
qr = qrcode.QRCode(error_correction=qrcode.constants.ERROR_CORRECT_H)

# Custom colors
img = qr.make_image(fill_color="darkblue", back_color="lightyellow")

# Add logo in center
from PIL import Image
logo = Image.open('logo.png')
# ... overlay logo on QR code
```

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
