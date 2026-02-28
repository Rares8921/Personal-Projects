# URL Shortener

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![API](https://img.shields.io/badge/API-TinyURL-red?style=for-the-badge)
![Requests](https://img.shields.io/badge/Library-urllib-blue?style=for-the-badge)

**Bidirectional URL shortening tool. Compress long URLs or expand shortened ones to reveal their destinations.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a command-line URL shortening tool that integrates with TinyURL's API to create compact links and expand shortened URLs to reveal their real destinations. Perfect for sharing long URLs or verifying suspicious links.

**Two modes:**
1. **Shorten:** Convert long URLs to TinyURL short links (e.g., `tinyurl.com/abc123`)
2. **Expand:** Reveal the real destination of any shortened URL

**Features:**
- Uses TinyURL API (via pyshorteners library)
- Supports URL expansion (follows redirects)
- Displays both original and shortened/expanded URLs
- CLI interface with menu-driven selection

**Why use URL shorteners:**
- Character limits (Twitter, SMS, printed materials)
- Cleaner appearance in presentations/documents
- Track click analytics (not in this basic version)
- Hide referrer information

**Security note:**
Expanding shortened URLs helps verify link destinations before clicking (phishing protection).

---

## Tech Stack

**Language:** Python 3.7+  
**Libraries:**  
- pyshorteners (TinyURL API wrapper)  
- urllib.request (HTTP redirection following)

**API:** TinyURL public shortening service

### Architecture

```
User Input
├── Mode 1: Shorten URL
│   ├── Input: Original URL
│   ├── pyshorteners.Shortener()
│   ├── shortener.tinyurl.short(url)
│   └── Output: Short URL
│
└── Mode 2: Expand URL
    ├── Input: Shortened URL
    ├── urlopen(url) → follow redirects
    ├── geturl() → extract final destination
    └── Output: Real URL
```

**How shortening works:**

```
Original: https://www.example.com/very/long/path/to/resource?param1=value1&param2=value2
         ↓
TinyURL API Request
         ↓
Short: https://tinyurl.com/abc123

(TinyURL stores mapping: abc123 → original URL)
```

**How expansion works:**

```
Shortened URL: https://tinyurl.com/abc123
         ↓
HTTP GET request
         ↓
Server responds with 301/302 redirect
         ↓
Follow redirect chain
         ↓
Final destination URL extracted
```

---

## Project Structure

```
Url shortener/
├── Shortener.py                # Main logic
├── NormalToShortened.png       # Shortening demo screenshot
├── ShortenedToNormal.png       # Expansion demo screenshot
└── README.md
```

**Code structure:**
- Lines 1-2: Imports (pyshorteners, urllib.request)
- Lines 5-9: `link_shortener()` function
- Lines 12-17: `link_opener()` function
- Lines 20-26: User menu and input handling

**Functions:**

```python
link_shortener(link)   # Original → Short
link_opener(link)      # Short → Original
```

---

## Getting Started

**Requirements:**
- Python 3.7+
- pyshorteners library

**Installation:**

```bash
pip install pyshorteners
```

**Run it:**

```bash
python Shortener.py
```

**Usage examples:**

**1. Shorten a URL:**

```
1. Type 1 for shortening the link
2. Type 2 for extracting the real link from a shortened link
1
Enter the link: https://www.youtube.com/watch?v=dQw4w9WgXcQ
Original Link: https://www.youtube.com/watch?v=dQw4w9WgXcQ
Shortened Link: https://tinyurl.com/2s3wxyz
```

**2. Expand a shortened URL:**

```
1. Type 1 for shortening the link
2. Type 2 for extracting the real link from a shortened link
2
Enter the link: https://tinyurl.com/2s3wxyz
Shortened Link: https://tinyurl.com/2s3wxyz
Real Link: https://www.youtube.com/watch?v=dQw4w9WgXcQ
```

**Supported shorteners (for expansion):**
- TinyURL (tinyurl.com)
- Bit.ly (bit.ly)
- Shortened YouTube links (youtu.be)
- Any service using HTTP redirects

---

## What's Next

**Possible enhancements:**
- Support multiple URL shortening services (bit.ly, is.gd, etc.)
- Custom short URL aliases (tinyurl.com/my-custom-link)
- Batch processing (shorten multiple URLs from file)
- QR code generation for shortened URLs
- Click analytics (track how many times link was accessed)
- GUI with copy-to-clipboard button
- Browser extension integration
- URL validation before shortening

**Additional services:**

```python
# Use different shortening services
shortener.bitly.short(url)      # Bit.ly (requires API key)
shortener.isgd.short(url)       # is.gd (free, no key needed)
shortener.chilpit.short(url)    # Chilp.it
```

**Security enhancements:**
- Check URL against malware/phishing databases
- Display URL preview before expanding
- Warn if destination domain differs from expected
- Log expansion history for reference

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
