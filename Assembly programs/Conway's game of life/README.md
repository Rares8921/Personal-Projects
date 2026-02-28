# Conway's Game of Life - Assembly Implementation

<div align="center">

![Assembly](https://img.shields.io/badge/Assembly-x86-red?style=for-the-badge&logo=assemblyscript)
![NASM](https://img.shields.io/badge/NASM-Assembler-blue?style=for-the-badge)
![C](https://img.shields.io/badge/C-File%20I%2FO-green?style=for-the-badge&logo=c)

**A low-level implementation of Conway's Game of Life cellular automaton in x86 assembly. Because simulating life at machine code level is surprisingly poetic.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Algorithm](#the-algorithm) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a pure assembly implementation of Conway's Game of Life, the famous zero-player cellular automaton invented by John Horton Conway in 1970. The program simulates the evolution of a cell grid over multiple generations based on simple birth/death rules.

**Key Features:**
- **Pure x86 assembly** - No abstractions, direct machine code
- **Multiple implementations:**
  - Console I/O version (interactive)
  - File I/O version (batch processing with C library calls)
  - Message encryption variant (bonus feature)
- **Efficient neighbor counting** - Optimized 8-directional checks
- **Configurable grid size** - Dynamic memory allocation
- **Generation tracking** - Simulates N generations from initial state

**What makes it special:**
- Turing-complete system (can theoretically compute anything)
- Demonstrates emergent complexity from simple rules
- Educational resource for assembly programming
- Shows low-level memory manipulation and system calls

The simulation follows Conway's four rules: underpopulation, survival, overpopulation, and reproduction.

---

## The Algorithm

Conway's Game of Life is a cellular automaton with elegantly simple rules that produce complex emergent behaviors.

### Rules

Each cell in the grid can be **alive** (1) or **dead** (0). The next generation is determined by:

1. **Underpopulation:** Live cell with <2 live neighbors → dies
2. **Survival:** Live cell with 2-3 live neighbors → survives
3. **Overpopulation:** Live cell with >3 live neighbors → dies
4. **Reproduction:** Dead cell with exactly 3 live neighbors → becomes alive
5. **Stasis:** All other dead cells remain dead

### Neighbor Definition

For any cell, the 8 neighbors are:

```
NW  N  NE
W  [X]  E
SW  S  SE
```

The algorithm counts living neighbors and applies rules to compute the next generation.

---

## Tech Stack

**Language:** x86 Assembly (32-bit)  
**Assembler:** NASM (Netwide Assembler)  
**System Calls:** Linux syscalls (read/write) + C stdlib (fopen/fread/fwrite)  
**I/O:** Console (stdin/stdout) or file-based (in.txt/out.txt)

### Architecture

Three distinct implementations with different I/O strategies:

```
User Input (console or file)
      ↓
Assembly Program
  ├── Read grid dimensions (N×N)
  ├── Read number of generations (G)
  ├── Read initial live cells (coordinates)
  ├── Allocate grid memory
  ├── Initialize grid state
  └── For each generation:
      ├── Count neighbors for each cell (8 directions)
      ├── Apply Conway's rules
      └── Update grid state
      ↓
Output final grid state (console or file)
```

**Implementation Variants:**

1. **FirstQuery.s** - Console I/O version
   - Reads from stdin, writes to stdout
   - Uses Linux syscalls directly (sys_read, sys_write)
   - Compact, self-contained

2. **FirstQueryWithFiles.s** - File I/O version
   - Reads from `in.txt`, writes to `out.txt`
   - Calls C library functions (fopen, fread, fwrite, fclose)
   - Requires linking with libc

3. **SecondQuery.s** - Encryption variant
   - Uses Game of Life rules for message encryption
   - Key generation from ASCII binary concatenation
   - Outputs hexadecimal ciphertext

---

## Project Structure

```
Conway's game of life/
├── Code/
│   ├── FirstQuery.s              # Console I/O implementation
│   ├── FirstQueryWithFiles.s     # File I/O implementation
│   └── SecondQuery.s             # Encryption variant
├── README.md                     # This file
└── in.txt / out.txt              # I/O files (for file-based version)
```

---

## Getting Started

**Prerequisites:**
- Linux or WSL (Windows Subsystem for Linux)
- NASM assembler
- GCC (for linking with C library in file I/O version)

**Install NASM:**
```bash
# Ubuntu/Debian
sudo apt-get install nasm

# Arch Linux
sudo pacman -S nasm

# macOS
brew install nasm
```

**Compile and run (Console version):**
```bash
cd "Code"

# Assemble
nasm -f elf32 FirstQuery.s -o FirstQuery.o

# Link
ld -m elf_i386 FirstQuery.o -o FirstQuery

# Run
./FirstQuery
```

**Compile and run (File I/O version):**
```bash
# Assemble
nasm -f elf32 FirstQueryWithFiles.s -o FirstQueryWithFiles.o

# Link with C library
gcc -m32 FirstQueryWithFiles.o -o FirstQueryWithFiles

# Prepare input file (in.txt)
echo "18
18
14
0 1
0 2
1 0
..." > in.txt

# Run
./FirstQueryWithFiles

# Check output
cat out.txt
```

---

## Input Format

**For Console/File versions:**

```
N              # Grid size (N×N)
M              # Number of initial live cells
G              # Number of generations to simulate
x1 y1          # Coordinates of first live cell
x2 y2          # Coordinates of second live cell
...
xM yM          # Coordinates of Mth live cell
```

**Example:**
```
18             # 18×18 grid
14             # 14 initially live cells
0              # 0 generations (show initial state)
1 0            # Cell at (1,0) is alive
2 1            # Cell at (2,1) is alive
2 2            # and so on...
2 3
12 13
12 14
12 15
12 16
13 12
13 13
16 17
17 16
17 17
```

---

## Output Format

The program outputs the final grid state after G generations. Alive cells are typically marked with `1` or `*`, dead cells with `0` or ` `.

---

## How It Works

**Step-by-step execution:**

1. **Initialization**
   - Read grid dimensions (N×N)
   - Read number of generations (G)
   - Read initial live cell coordinates
   - Allocate memory for grid (N×N bytes)
   - Initialize all cells to 0 (dead)
   - Set specified cells to 1 (alive)

2. **Simulation Loop** (repeat G times)
   - For each cell (i, j):
     - Count living neighbors (check 8 directions)
     - Apply Conway's rules:
       - If alive + 2-3 neighbors: stay alive
       - If alive + <2 or >3 neighbors: die
       - If dead + exactly 3 neighbors: become alive
     - Store next state in temporary buffer
   - Copy temporary buffer to main grid (double buffering)

3. **Output**
   - Print final grid state to console or file
   - Format as N×N matrix

**Assembly Techniques Used:**
- Direct memory addressing for grid access
- Register-based arithmetic for neighbor counting
- Conditional jumps for rule applications
- Loop unrolling for 8-directional neighbor checks
- System calls for I/O (or C library wrapper calls)

---

## Complexity

**Time Complexity:** $O(G \times N^2)$ where $G$ = generations, $N$ = grid size  
- Each generation requires visiting all $N^2$ cells
- Each cell checks 8 neighbors = $O(1)$ operation

**Space Complexity:** $O(N^2)$  
- Main grid storage: $N \times N$ bytes
- Temporary buffer for double buffering: $N \times N$ bytes
- Total: $2N^2 + O(1)$

**Optimization Opportunities:**
- Sparse representation (track only live cells)
- Boundary handling (wrap-around vs. fixed borders)
- SIMD instructions for parallel cell updates

---

## Example Patterns

Conway's Game of Life supports fascinating emergent patterns:

- **Still lifes:** Block, Beehive, Loaf (stable patterns)
- **Oscillators:** Blinker (period 2), Pulsar (period 3)
- **Spaceships:** Glider, Lightweight spaceship (LWSS)
- **Methuselahs:** R-pentomino (evolves for 1103 generations)
- **Guns:** Gosper glider gun (infinite glider generation)

Try different initial configurations to discover complex behaviors!

---

## Bonus: Encryption Variant

**SecondQuery.s** implements a creative encryption scheme:

1. Take plaintext message and key
2. Convert key to binary ASCII (concatenate bit representations)
3. Use binary key to initialize Game of Life grid
4. Evolve grid for N generations
5. XOR final grid state with message
6. Output as hexadecimal ciphertext

This demonstrates how cellular automata can be used for cryptographic primitives (though not cryptographically secure).

---

## What's Next

Potential improvements:
- 64-bit version for larger grids
- GPU acceleration (CUDA/OpenCL port)
- Interactive visualizer (SDL/OpenGL)
- Pattern recognition (detect gliders, oscillators)
- HashLife algorithm for exponential speedup
- WebAssembly port for browser-based simulation

---

## Educational Value

This project demonstrates:
- Low-level memory manipulation in assembly
- System call interfaces (Linux syscalls vs. C library)
- Efficient algorithm implementation without high-level abstractions
- Understanding of cellular automata and emergence
- Turing completeness of simple rule systems

Perfect for learning:
- x86 assembly programming
- Algorithm optimization at machine code level
- Cellular automaton theory
- Conway's Game of Life patterns and behaviors

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.

