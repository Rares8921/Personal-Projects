# CYK Algorithm - Context-Free Grammar Parser

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Algorithms](https://img.shields.io/badge/Algorithm-CYK-blue?style=for-the-badge)
![Formal Languages](https://img.shields.io/badge/CFG-Parser-green?style=for-the-badge)

**A Java implementation of the Cocke-Younger-Kasami (CYK) algorithm for parsing context-free grammars. Dynamic programming meets formal language theory.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Algorithm](#the-algorithm) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is an implementation of the **CYK algorithm**, a dynamic programming approach to determine whether a string belongs to a language defined by a context-free grammar (CFG) in Chomsky Normal Form (CNF).

**Key Features:**
- **CYK algorithm** - Classic cubic-time CFG parsing
- **Chomsky Normal Form** - Supports CNF grammars (A → BC or A → a)
- **Dynamic programming** - 3D table for substring/non-terminal tracking
- **File-based grammar** - Reads CFG from text file
- **Efficient parsing** - $O(n^3 \cdot |G|)$ complexity

**Use cases:**
- Compiler design (syntax analysis)
- Natural language processing (sentence parsing)
- Formal language theory education
- Grammar membership testing
- Parse tree generation (with backtracking extension)

CYK is a foundational algorithm for understanding CFG parsing and dynamic programming in formal languages.

---

## The Algorithm

CYK (Cocke-Younger-Kasami) uses dynamic programming to build bottom-up recognition of substrings.

### Algorithm Overview

**Input:**
- Grammar $G$ in Chomsky Normal Form (CNF)
- String $w = w_1 w_2 \ldots w_n$

**CNF Rules:**
- $A \to BC$ (two non-terminals)
- $A \to a$ (single terminal)

**Dynamic Programming Table:**
- `dp[i][j][k]` = true if substring $w[i..j]$ can be derived from non-terminal $k$

**Steps:**
1. **Initialization:** For each single character $w[i]$, set `dp[i][i][A]` = true if $A \to w[i]$ is a rule
2. **Bottom-up filling:** For substrings of increasing length:
   - For each split point, check if $A \to BC$ exists where:
     - Left part derives from $B$
     - Right part derives from $C$
3. **Result:** Check `dp[0][n-1][S]` where $S$ is the start symbol

---

## Tech Stack

**Language:** Java 17+  
**Algorithm:** Dynamic Programming (CYK)  
**Data Structures:** 3D boolean array (DP table), HashMap (grammar rules)  
**I/O:** BufferedReader (file input)

### Architecture

Bottom-up parsing with DP table construction:

```
gramatica3.txt (CFG in CNF)
      ↓
CYK.java
  ├── Parse grammar
  │   ├── Read production rules (A → BC, A → a)
  │   └── Store in data structures
  ├── Initialize DP table
  │   └── dp[i][j][k] = can substring [i,j] derive from non-terminal k?
  ├── Bottom-up DP
  │   ├── Base case: single characters (A → a)
  │   └── Inductive case: combine substrings (A → BC)
  │       For length L = 2 to n:
  │         For position i = 0 to n-L:
  │           j = i + L - 1
  │           For split s = i to j-1:
  │             For each rule A → BC:
  │               if dp[i][s][B] AND dp[s+1][j][C]:
  │                 dp[i][j][A] = true
  └── Check dp[0][n-1][START]
      ↓
[Result: true/false for string membership]
```

---

## Project Structure

```
CYK/
├── CYK.java            # Main CYK implementation
├── gramatica3.txt      # Example grammar in CNF format
├── README.md           # This file
└── (output to console)
```

---

## Getting Started

**Prerequisites:**
Java 17+ (or any recent JDK)

**Compile:**
```bash
javac CYK.java
```

**Run:**
```bash
java CYK
```

**Customize:**
Edit `CYK.java` to change the input word (currently hardcoded as `"uupzupiuupp"`).

---

## Grammar Format

**gramatica3.txt structure (Chomsky Normal Form):**

```
S → AB
S → a
A → CD
A → b
B → c
C → d
D → e
...
```

**CNF Requirements:**
- Productions are either:
  - $A \to BC$ (two non-terminals on right side)
  - $A \to a$ (single terminal on right side)
- Start symbol $S$ doesn't appear on right side (except S → ε if ε is in language)

**Conversion:**
Any CFG can be converted to CNF using standard algorithms:
1. Eliminate ε-productions
2. Eliminate unit productions (A → B)
3. Convert long rules (A → BCDE becomes A → BX, X → CY, Y → DE)
4. Convert terminal mixing (A → aB becomes A → XB, X → a)

---

## How It Works

**Step-by-step CYK execution:**

**Example Grammar (CNF):**
```
S → AB | BC
A → BA | a
B → CC | b
C → AB | a
```

**Input String:** `w = "baaba"` (length n=5)

**DP Table Initialization:**
```
dp[i][i][A] for each character:
dp[0][0] = {A, C} (b derives from A and C)
dp[1][1] = {A, C} (a derives from A and C)
dp[2][2] = {A, C} (a derives from A and C)
dp[3][3] = {B}     (b derives from B)
dp[4][4] = {A, C} (a derives from A and C)
```

**Inductive DP Filling (length 2, 3, ..., n):**
For substring `w[0..1]` = "ba":
- Check all rules $A \to BC$:
  - $S \to AB$: check if $A$ derives "b" (yes) and $B$ derives "a" (no) → false
  - $A \to BA$: check if $B$ derives "b" (yes) and $A$ derives "a" (yes) → true!
- Result: `dp[0][1][A] = true`

Continue for all substring lengths...

**Final Check:**
- `dp[0][n-1][S]` = true → String is in language
- `dp[0][n-1][S]` = false → String not in language

---

## Complexity

**Time Complexity:** $O(n^3 \cdot |G|)$
- $n$ = string length
- $|G|$ = number of productions in grammar
- Three nested loops:
  - Length of substring: $O(n)$
  - Starting position: $O(n)$
  - Split point: $O(n)$
  - Grammar rule checking: $O(|G|)$

**Space Complexity:** $O(n^2 \cdot |V|)$
- $n^2$ substrings
- $|V|$ non-terminals
- 3D boolean array: `dp[n][n][|V|]`

**Practical Performance:**
- Fast for small-to-medium strings (n < 1000)
- Efficient for teaching/demonstration purposes
- Production parsers use more advanced techniques (Earley, GLR, LR)

---

## Example Run

**Grammar (gramatica3.txt):**
```
S → NP VP
NP → Det N
VP → V NP
Det → a
N → cat | dog
V → chased
```

**Input:** `"a cat chased a dog"` (tokenized)

**CYK Process:**
1. Base case: `Det → a`, `N → cat`, `V → chased`, etc.
2. Combine: `NP → Det N` for "a cat" and "a dog"
3. Combine: `VP → V NP` for "chased a dog"
4. Combine: `S → NP VP` for full sentence

**Output:** `true` (sentence grammatically valid)

---

## Chomsky Normal Form

**Why CNF?**
- Simplifies parsing to binary recursive case
- Enables efficient DP table structure
- Guarantees $O(n^3)$ complexity
- Any CFG can be converted to CNF (preserving language)

**CNF Conversion Example:**

Original:
```
S → aSb | ε
```

CNF equivalent:
```
S → AB | ε
A → a
B → SB'
B' → b
```

---

## What's Next

Potential extensions:
- Parse tree construction (backtracking through DP table)
- Probabilistic CYK (PCFG parsing for NLP)
- Interactive grammar editor
- Grammar CNF converter (automatic preprocessing)
- Visualization (DP table heatmap, parse tree rendering)
- Performance benchmarking on large corpora

---

## Educational Value

This project demonstrates:
- Dynamic programming in formal language theory
- Context-free grammar parsing fundamentals
- Chomsky Normal Form and its advantages
- Algorithm design for NP-complete-adjacent problems
- Compiler design principles (syntax analysis)

Perfect for learning:
- CFG parsing techniques
- Dynamic programming strategies
- Formal language theory (Chomsky hierarchy)
- Compiler construction basics

---

## Theoretical Context

**Context-Free Grammars:**
- More powerful than regular languages (can't be recognized by finite automata)
- Recognized by pushdown automata (PDA)
- Used for programming language syntax, natural language structure
- CYK provides polynomial-time membership testing

**Chomsky Hierarchy:**
- Type 3: Regular languages (FSM) ⊂
- Type 2: Context-free languages (PDA, CYK) ⊂
- Type 1: Context-sensitive languages ⊂
- Type 0: Recursively enumerable languages (Turing machines)

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
