# Regular Expression to DFA Converter

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Compiler Design](https://img.shields.io/badge/Compiler-Design-blue?style=for-the-badge)
![Automata Theory](https://img.shields.io/badge/RegEx→DFA-Conversion-green?style=for-the-badge)

**A Java implementation that converts regular expressions to Deterministic Finite Automata using syntax trees and subset construction. The foundation of lexical analysis.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Algorithm](#the-algorithm) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a complete **regular expression to DFA compiler** that transforms regex patterns into equivalent deterministic finite automata through a multi-stage pipeline: parsing → syntax tree construction → position numbering → followpos calculation → DFA generation.

**Key Features:**
- **Full regex support** - Union (|), concatenation (·), Kleene star (*)
- **Syntax tree construction** - Abstract syntax tree (AST) representation
- **Position-based algorithm** - McNaughton-Yamada-Thompson algorithm
- **Subset construction** - Generates minimal DFA states
- **Automatic augmentation** - Adds concatenation operators and end marker
- **Complete DFA output** - States, alphabet, transitions, initial/final states

**Use cases:**
- Compiler construction (lexical analyzer generators like lex/flex)
- Regular expression engine implementation
- Pattern matching optimization
- Automata theory education
- Text processing tool development

This is the algorithm used by tools like `lex`, `flex`, and modern regex engines to compile patterns into efficient state machines.

---

## The Algorithm

The conversion follows a 5-stage pipeline based on **McNaughton-Yamada-Thompson** algorithm:

### Stage 1: Augmentation

Add explicit concatenation operators (·) and end marker (#):
- Input: `a|b*c`
- Output: `(a|((b)*·c))·#`

### Stage 2: Syntax Tree Construction

Parse augmented regex into Abstract Syntax Tree (AST):

```
      ·
     / \
    |   #
   / \
  a   ·
     / \
    *   c
    |
    b
```

Each node tagged with a position number (leaves only).

### Stage 3: Compute nullable, firstpos, lastpos

For each node:
- **nullable:** Can this subtree produce ε (empty string)?
- **firstpos:** Set of positions that can match first character
- **lastpos:** Set of positions that can match last character

### Stage 4: Compute followpos

For each position $i$, compute `followpos(i)` = set of positions that can follow $i$ in a match:
- For concatenation node `n = c1·c2`: if $i \in \text{lastpos}(c1)$, add $\text{firstpos}(c2)$ to `followpos(i)`
- For star node `n = c1*`: if $i \in \text{lastpos}(c1)$, add $\text{firstpos}(c1)$ to `followpos(i)`

### Stage 5: DFA Construction (Subset Construction)

1. Initial DFA state: `firstpos(root)`
2. For each unmarked DFA state $S$:
   - Mark $S$
   - For each symbol $a$ in alphabet:
     - Let $U$ = union of `followpos(p)` for all $p \in S$ where position $p$ corresponds to $a$
     - If $U$ is non-empty, add transition $S \xrightarrow{a} U$
     - If $U$ is new, add as unmarked DFA state
3. Final states: those containing end marker position (#)

---

## Tech Stack

**Language:** Java 17+  
**Algorithm:** McNaughton-Yamada-Thompson (position-based)  
**Data Structures:** Syntax trees, position sets (BitSet or HashSet)  
**I/O:** Console input/output

### Architecture

Multi-stage compilation pipeline:

```
User Input (Regular Expression)
      ↓
Stage 1: Augmentation
  ├── Add explicit concatenation (·)
  └── Add end marker (#)
      ↓
Stage 2: Syntax Tree Parsing
  ├── Recursive descent parser
  ├── Build AST with union/concat/star nodes
  └── Assign positions to leaf nodes
      ↓
Stage 3: Attribute Computation
  ├── nullable (bottom-up)
  ├── firstpos (bottom-up)
  └── lastpos (bottom-up)
      ↓
Stage 4: followpos Computation
  ├── Traverse tree (top-down)
  ├── For concat nodes: propagate positions
  └── For star nodes: create loops
      ↓
Stage 5: DFA Construction
  ├── Subset construction algorithm
  ├── Initial state = firstpos(root)
  ├── Transition function from followpos
  └── Final states = states containing #
      ↓
[Complete DFA: Q, Σ, δ, q0, F]
```

---

## Project Structure

```
RegEXtoDFA/
├── RegEXtoDFA.java    # Main converter implementation
├── README.md          # This file
└── (console I/O)
```

---

## Getting Started

**Prerequisites:**
Java 17+ (or any recent JDK)

**Compile:**
```bash
javac RegEXtoDFA.java
```

**Run:**
```bash
java RegEXtoDFA
```

**Input prompt:**
```
Enter regular expression: a|b*c
```

**Output:**
```
DFA States: {1, 2, 3, 4}
Alphabet: {a, b, c}
Initial State: 1
Final States: {4}
Transitions:
  δ(1, a) = 4
  δ(1, b) = 2
  δ(2, b) = 2
  δ(2, c) = 3
  δ(3, #) = 4
```

---

## Input Format

**Supported Regex Syntax:**
- `a, b, c, ...` - Literal characters (alphabet symbols)
- `|` - Union (alternation): `a|b` matches "a" or "b"
- `*` - Kleene star (zero or more): `a*` matches "", "a", "aa", "aaa", ...
- `()` - Grouping: `(a|b)*` matches any string of a's and b's
- Concatenation is implicit: `abc` means "a" followed by "b" followed by "c"

**Example Patterns:**
- `a*b*` - Any number of a's followed by any number of b's
- `(a|b)*` - Any string over {a, b}
- `a(b|c)*d` - "a", any string of b's and c's, then "d"
- `(0|1)*1` - Binary strings ending in 1

**Limitations:**
- No character classes `[a-z]` (can be expanded to `a|b|c|...|z`)
- No `+` operator (use `aa*` instead of `a+`)
- No `?` operator (use `(a|ε)` encoding)
- No escape sequences (implement as needed)

---

## How It Works

**Example: Convert `(a|b)*c` to DFA**

**Step 1: Augmentation**
- Input: `(a|b)*c`
- Augmented: `((a|b)*·c)·#`

**Step 2: Syntax Tree**
```
         ·₆
        / \
       ·₅  #₆
      / \
     *₄  c₃
     |
     |₂
    / \
   a₁  b₂
```
(Positions: a=1, b=2, c=3, #=6; subscripts are node IDs)

**Step 3: Compute Attributes**
| Node | nullable | firstpos | lastpos |
|------|----------|----------|---------|
| a₁   | no       | {1}      | {1}     |
| b₂   | no       | {2}      | {2}     |
| \|₂  | no       | {1,2}    | {1,2}   |
| *₄   | yes      | {1,2}    | {1,2}   |
| c₃   | no       | {3}      | {3}     |
| ·₅   | no       | {1,2}    | {3}     |
| #₆   | no       | {6}      | {6}     |
| ·₆   | no       | {1,2}    | {6}     |

**Step 4: followpos**
- Concat ·₅: lastpos(*)={1,2}, firstpos(c)={3} → followpos(1)={3}, followpos(2)={3}
- Concat ·₆: lastpos(c)={3}, firstpos(#)={6} → followpos(3)={6}
- Star *₄: lastpos(|)={1,2}, firstpos(|)={1,2} → followpos(1)∪={1,2}, followpos(2)∪={1,2}

Final:
- followpos(1) = {1, 2, 3}
- followpos(2) = {1, 2, 3}
- followpos(3) = {6}

**Step 5: DFA Construction**

Initial state: $S_0 = \text{firstpos(root)} = \\{1, 2\\}$

From $S_0 = \\{1, 2\\}$:
- On `a` (position 1): followpos(1) = {1, 2, 3} → $S_1 = \\{1, 2, 3\\}$
- On `b` (position 2): followpos(2) = {1, 2, 3} → $S_1 = \\{1, 2, 3\\}$

From $S_1 = \\{1, 2, 3\\}$:
- On `a`: followpos(1) = {1, 2, 3} → $S_1$ (loop)
- On `b`: followpos(2) = {1, 2, 3} → $S_1$ (loop)
- On `c` (position 3): followpos(3) = {6} → $S_2 = \\{6\\}$

From $S_2 = \\{6\\}$ (contains #, so FINAL):
- No outgoing transitions

**Final DFA:**
- States: $\\{S_0, S_1, S_2\\}$
- Initial: $S_0$
- Final: $\\{S_2\\}$
- Transitions:
  - $\delta(S_0, a) = S_1$
  - $\delta(S_0, b) = S_1$
  - $\delta(S_1, a) = S_1$
  - $\delta(S_1, b) = S_1$
  - $\delta(S_1, c) = S_2$

---

## Complexity

**Time Complexity:**
- **Augmentation:** $O(n)$ where $n$ = regex length
- **Syntax tree construction:** $O(n)$
- **Attribute computation:** $O(n)$ (single pass)
- **followpos computation:** $O(n^2)$ (worst case for deeply nested structures)
- **DFA construction:** $O(2^n)$ in worst case (exponential in number of positions)
  - Typical case: $O(n)$ to $O(n^2)$ states

**Space Complexity:**
- Syntax tree: $O(n)$ nodes
- Position sets: $O(n^2)$ total across all followpos sets
- DFA states: $O(2^n)$ worst case, $O(n)$ typical

**Practical Performance:**
Most real-world regexes produce compact DFAs with linear or quadratic state counts.

---

## Theoretical Foundation

**McNaughton-Yamada-Thompson Algorithm:**
- Directly constructs DFA from regex (bypassing ε-NFA)
- Position-based approach avoids epsilon transitions
- More efficient than Thompson's construction + subset construction
- Used in modern compiler generators

**Equivalence:**
All three representations are equivalent:
- Regular Expression ≡ ε-NFA ≡ NFA ≡ DFA

Each recognizes exactly the **regular languages**.

---

## What's Next

Potential extensions:
- DFA minimization (Hopcroft's algorithm)
- Extended regex syntax (+, ?, character classes, escapes)
- Graphical output (DOT format for Graphviz)
- Code generation (C/Java code for DFA)
- Interactive visualizer (step-by-step execution)
- Performance benchmarking vs. backtracking regex engines

---

## Educational Value

This project demonstrates:
- Complete regex-to-automaton compilation pipeline
- Syntax tree construction and traversal algorithms
- Position-based DFA construction (alternative to Thompson's)
- Compiler design principles (lexical analysis)
- Automata theory fundamentals

Perfect for learning:
- How regex engines work internally
- Compiler construction techniques
- Algorithm design for formal language processing
- Automata theory in practice

---

## Compiler Design Context

This algorithm is the foundation of:
- **Lex/Flex** - Lexical analyzer generators
- **Regular expression engines** - (with backtracking for extended features)
- **Tokenizers** - In compilers and interpreters
- **Pattern matching libraries** - grep, sed, awk

Understanding this conversion is essential for:
- Building compilers/interpreters
- Optimizing text processing tools
- Designing DSLs (Domain-Specific Languages)
- Formal verification and model checking

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
