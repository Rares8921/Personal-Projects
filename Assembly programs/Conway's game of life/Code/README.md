## Conway's game of life

This document describes the functionality and time complexity analysis of the provided assembly code that simulates the Game of Life on a 20x20 matrix.

## Functionality

The assembly program reads the initial configuration of the Game of Life from an input file and performs a specified number of evolutions. The final state of the matrix is written to an output file. The matrix is extended with a border to handle edge cases without additional boundary checks.

### Data Section

- **Direction Arrays:**
  - `di` and `dj` store the relative positions for the eight possible directions (up, down, left, right, and the four diagonals).

- **Variables:**
  - `nrLinii` and `nrColoane`: Number of rows and columns of the game board.
  - `nrCeluleVii`: Number of live cells in the initial configuration.
  - `x` and `y`: Temporary storage for the row and column indices of live cells.
  - `k`: Number of evolutions to simulate.
  - `evolutiaCurenta`: Current evolution number being processed.
  - `linieCurenta` and `coloanaCurenta`: Current row and column being processed.
  - `directiaCurenta`: Current direction being checked for neighbors.
  - `linieVecinCurent` and `coloanaVecinCurent`: Row and column indices of the neighbor cell being checked.
  - `nrVeciniVii`: Number of live neighbors of the current cell.
  - `nrDirectii`: Constant value of 8, representing the eight possible directions.
  - `dimMax`: Maximum dimension of the matrix (20x20).
  - `matrCelule` and `matrCopie`: Matrices representing the current state of the game and a copy used for updates.

- **File Pointers and Formats:**
  - `finPtr` and `foutPtr`: Pointers to the input and output files.
  - `read` and `write`: Modes for opening files.
  - `fin` and `fout`: Filenames for input and output.
  - `formatIntregFscanf` and `formatIntregFprintf`: Format strings for reading and writing integers.
  - `newLine`: Newline character.

### Text Section

- **Main Function:**
  - Opens the input and output files using `fopen`.

#### Reading Input

1. **Read Dimensions:**
   - Reads the number of rows (`nrLinii`) and columns (`nrColoane`).

2. **Read Live Cells:**
   - Reads the number of initial live cells (`nrCeluleVii`).
   - For each live cell, reads its row (`x`) and column (`y`) indices and sets the corresponding position in `matrCelule` to 1.

3. **Read Evolutions:**
   - Reads the number of evolutions (`k`).

#### Initialization

- Initializes the matrices with zeros.
- Sets the live cells in `matrCelule` based on the input.

#### Simulating Evolutions

- For each evolution, iterates over the matrix and counts live neighbors for each cell.
- Updates the state of each cell in `matrCopie` based on the Game of Life rules:
  - A live cell with fewer than two or more than three live neighbors dies.
  - A dead cell with exactly three live neighbors becomes alive.
- Copies the updated matrix back to `matrCelule`.

#### Output the Matrix

- Writes the final state of the matrix to the output file.

#### Exit Program

- Closes the input and output files and exits the program.

## Time Complexity Analysis

### Reading Input
- **Time Complexity:** O(n), where n is the number of live cells plus the number of evolutions and matrix dimensions. Each fscanf operation reads a single integer or a pair of integers.

### Initializing the Matrix
- **Time Complexity:** O(1) for setting up the matrices as the maximum size is constant (20x20).

### Simulating Evolutions
- **Time Complexity:** O(k * m^2 * d), where k is the number of evolutions, m is the dimension of the matrix (20), and d is the number of directions (8). Each cell is processed in constant time for each direction in each evolution.

### Output the Matrix
- **Time Complexity:** O(m^2), where m is the dimension of the matrix (20). Each cell's state is written to the output file.

### Overall Time Complexity
- **Overall Time Complexity:** O(k * m^2 * d), which simplifies to O(k) due to the constant matrix size and number of directions.