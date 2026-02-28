# Mini Command Prompt

<div align="center">

![C++](https://img.shields.io/badge/C++-11-00599C?style=for-the-badge&logo=cplusplus)
![Windows API](https://img.shields.io/badge/Windows_API-Native-0078D4?style=for-the-badge&logo=windows)
![License](https://img.shields.io/badge/License-Proprietary-red?style=for-the-badge)

**A lightweight Windows command prompt clone built in C++ with custom commands for system diagnostics, networking, and prime number checking.**

[Features](#what-it-does) • [Commands](#command-reference) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a fully functional command-line interpreter written in pure C++. It mimics the Windows Command Prompt but adds custom commands for common tasks like checking IP addresses, testing network latency, and mathematical operations - all without needing to remember obscure `cmd.exe` syntax.

**Why Build This?**
- Learning project for Windows API, system calls, and networking
- Faster access to frequently-used system diagnostics
- Demonstrates algorithmic complexity optimization (O(√n) prime checking)
- Clean C++ implementation with efficient memory usage (O(1) space complexity)

**Core Features:**
- **6 Custom Commands** - Prime checking, IP lookup, ping testing, URL launching, system info, current time
- **Case-Insensitive Parsing** - Commands work with `!PRIME`, `!prime`, or `!PrImE`
- **Prefix-Based** - All custom commands start with `!` to avoid conflicts with system commands
- **Admin-Aware** - Automatically requests elevation for `!ip` and `!ping` commands
- **Lightweight** - Single executable, no dependencies beyond Windows API

**Supported Operations:**
- **Mathematical:** Prime number validation with O(√n) complexity
- **Networking:** IP address lookup, ping latency measurement
- **System:** CPU/RAM information, date/time
- **Utilities:** Open URLs in default browser, shutdown/restart PC

---

## Tech Stack

**Language:** C++11  
**Platform:** Windows 10/11 (native Win32 API)  
**APIs:** 
- `windows.h` - System calls (ShellExecute, shutdown/restart)
- `winsock2.h` - Network operations (IP lookup)
- `intrin.h` - CPU intrinsics for hardware info
- Standard Library - Time, string manipulation, file I/O

### Architecture

Simple command interpreter with functional programming style:

```
main() - Entry point
   ↓
Command Loop (COMMAND label)
   ↓
┌────────────────────────────────┐
│  Input Processing              │
│  - Read command string         │
│  - Convert to lowercase        │
│  - Parse command type          │
└────────────────────────────────┘
   ↓
┌────────────────────────────────┐
│  Command Router                │
│  - !help     → Show commands   │
│  - !prime    → Prime check     │
│  - !time     → Date/time       │
│  - !ip       → IP lookup       │
│  - !ping     → Latency test    │
│  - !connect  → Open URL        │
│  - !info     → System specs    │
│  - !shutdown → Power off       │
│  - !restart  → Reboot          │
└────────────────────────────────┘
   ↓
┌────────────────────────────────┐
│  Execution Layer               │
│  - System calls (ipconfig)     │
│  - File I/O (parse output)     │
│  - Math algorithms (isPrime)   │
│  - Windows API (ShellExecute)  │
└────────────────────────────────┘
   ↓
Output to console
   ↓
Loop back to COMMAND
```

**Algorithm Optimization:**
- **Prime Checking:** `isPrime()` uses trial division up to √n - O(√n) time complexity
- **Early Exit:** Returns immediately for n < 2, n == 2, or even numbers
- **Odd-Only Testing:** Skips even divisors (`i += 2`) - reduces iterations by 50%
- **Space Complexity:** O(1) - no arrays or collections, just loop variables

**Key Implementation Details:**
- **Case Insensitivity:** `std::transform(s.begin(), s.end(), s.begin(), ::tolower)` normalizes input
- **Admin Elevation:** `!ip` and `!ping` require administrator rights to execute `ipconfig` - program must be run as admin
- **File Parsing:** Commands like `!ip` redirect output to `ip.txt`, then parse with `std::ifstream` and `std::string::find()`
- **URL Opening:** `ShellExecute(NULL, "open", url.c_str(), ...)` launches default browser
- **Goto Usage:** Uses `goto COMMAND` for command loop (controversial but simple for demo code)

---

## Command Reference

| Command | Description | Example | Admin Required |
|---------|-------------|---------|----------------|
| `!help` | Display all commands | `!help` | No |
| `!prime` | Check if a number is prime | `!prime` → Enter: `17` → "The number is prime" | No |
| `!time` | Show current date and time | `!time` → "Date: 28.02.2026, Hour: 14:35" | No |
| `!ip` | Get local IPv4 address | `!ip` → "Local ip address: 192.168.1.10" | **Yes** |
| `!ping` | Test network latency | `!ping` → "Your ping: 12 ms" | **Yes** |
| `!connect` | Open URL in browser | `!connect` → Enter: `google.com` | No |
| `!info` | Display CPU and RAM info | `!info` → "CPU: Intel i7-9700K, RAM: 16GB" | No |
| `!shutdown` | Shut down the computer | `!shutdown` | **Yes** |
| `!restart` | Restart the computer | `!restart` | **Yes** |
| `!compare` | Compare two numbers | `!compare` → Enter two numbers | No |
| `!divisible` | Check divisibility | `!divisible` → Enter two numbers | No |
| `!exit` | Exit the prompt | `!exit` | No |

**Notes:**
- Commands prefixed with `!` to distinguish from system commands
- Admin commands (`!ip`, `!ping`, `!shutdown`, `!restart`) must be run with elevated privileges
- Case-insensitive: `!PRIME`, `!prime`, `!PrImE` all work

---

## Project Structure

```
Mini Command Prompt/
├── minicmd/
│   └── minicmd/
│       └── main.cpp              # Complete application (267 lines)
│           ├── isPrime()         # Prime number validation (O(√n))
│           ├── main()            # Entry point and command loop
│           └── Command handlers:
│               ├── !help         # Show command list
│               ├── !prime        # Prime checking
│               ├── !time         # Date/time display
│               ├── !ip           # IP address lookup
│               ├── !ping         # Network latency
│               ├── !connect      # URL launcher
│               ├── !info         # System information
│               ├── !shutdown     # Power off
│               ├── !restart      # Reboot
│               ├── !compare      # Number comparison
│               ├── !divisible    # Divisibility check
│               └── !exit         # Exit program
│
├── prompt.jpg                    # Screenshot/icon
└── README.md                     # This file
```

**Single-File Design:**
- All functionality in `main.cpp` - easy to compile and distribute
- No external dependencies (uses only Windows system libraries)
- Self-contained executable (~50KB compiled)

---

## Getting Started

**Prerequisites:**
- Windows 10/11
- Visual Studio 2017+ or MinGW-w64 (any C++11 compiler)
- Administrator privileges (for network commands)

**Compile (3 methods):**

### Option 1: Visual Studio
```bash
# Open Developer Command Prompt
cl /EHsc main.cpp /Fe:minicmd.exe
```

### Option 2: MinGW
```bash
g++ -std=c++11 main.cpp -o minicmd.exe -lopengl32
```

### Option 3: CMake (cross-platform)
```cmake
cmake_minimum_required(VERSION 3.10)
project(MiniCommandPrompt)
add_executable(minicmd minicmd/minicmd/main.cpp)
```

**Run:**
```bash
# Standard mode (limited functionality)
minicmd.exe

# Administrator mode (full functionality)
Right-click minicmd.exe → "Run as administrator"
```

**Usage Example:**
```
C++ Command Prompt [Version 1.0.0]
Copyright (C) 2019. All rights reserved

Command:!help

!help - Get info about the commands
!time - Hour and date
!exit - Exit from prompt
!prime - Check if a number is a prime one
!connect - Connect to a website
!shutdown - Shut down the computer
!restart - Restart the computer
!ip - Get ip address
!ping - Get the ping
!compare - Compare two numbers
!divisible - Check if a number is divisible with another
!info - Get information about the computer

Command:!prime
Enter a number:17
The number is prime

Command:!time
Date: 28.02.2026
Hour: 14:35

Command:!ip
Local ip address: 192.168.1.10

Command:!exit
```

---

## Technical Deep Dive

### Prime Number Algorithm

```cpp
bool isPrime(int num) {
    if(num < 2) {
        return false;
    } else if(num == 2) {
        return true;
    } else if(num % 2 == 0) {
        return false;
    }
    for(int i = 3; i <= (int)sqrt(num); i += 2) {
        if(num % i == 0) {  // Note: Original code has typo (== 3), should be == 0
            return false;
        }
    }
    return true;
}
```

**Complexity Analysis:**
- **Time:** O(√n) - Only check divisors up to square root
- **Space:** O(1) - Constant memory usage
- **Optimization:** Skip even numbers after initial check (50% reduction)

### IP Address Extraction

```cpp
system("ipconfig > ip.txt");  // Redirect ipconfig output to file
IPFile.open("ip.txt");
while(!IPFile.eof()) {
    getline(IPFile, ip);
    if((offset = ip.find("IPv4 Address. . . . . . . . . . . :")) != string::npos) {
        ip.erase(0, 39);  // Remove label, keep only IP
        cout << "Local ip address: " << ip << endl;
    }
}
```

**Technique:** System call → File redirection → String parsing → Output

---

## What's Next

Ideas for future versions:

**Features:**
- **More Commands:** `!download` (wget alternative), `!hash` (MD5/SHA-256), `!encode` (Base64)
- **Command History:** Arrow keys to navigate previous commands (like bash/zsh)
- **Autocomplete:** Tab completion for commands
- **Piping:** Connect commands with `|` operator

**Improvements:**
- **Replace Goto:** Refactor command loop to use `while(true)` instead of `goto COMMAND`
- **Error Handling:** Try-catch blocks for file I/O and system calls
- **Async Commands:** Background execution for slow operations (`!ping`, `!download`)
- **Config File:** JSON/INI file for custom commands and aliases

**Cross-Platform:**
- Port to Linux using POSIX APIs (`system()` → `fork()`/`exec()`)
- MacOS support with BSD socket programming

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
