# DFA Simulator

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Automata Theory](https://img.shields.io/badge/Automata-Theory-blue?style=for-the-badge)
![Finite State Machine](https://img.shields.io/badge/DFA-FSM-green?style=for-the-badge)

**A Java implementation of a Deterministic Finite Automaton simulator. Reads configurations, processes words, and determines acceptance. Automata theory made practical.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [How It Works](#how-it-works) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a DFA (Deterministic Finite Automaton) simulator that reads automaton configurations from a file, processes input words according to transition rules, and outputs whether each word is accepted or rejected.

**Key Features:**
- **File-based configuration** - Reads DFA definition from `input.dat`
- **Full DFA simulation** - Implements complete state transition logic
- **Batch processing** - Handles multiple words in a single run
- **Clear output** - "DA" (YES) for accepted, "NU" (NO) for rejected
- **HashMap-based transitions** - O(1) state lookups for efficiency

**Use cases:**
- Automata theory education and demonstrations
- Pattern matching (lexical analysis foundations)
- String acceptance problems (formal languages)
- Compiler design (tokenization)
- Regular language recognition

The simulator follows classic DFA semantics: exactly one transition per (state, symbol) pair, deterministic execution, acceptance when ending in a final state.

---

## Tech Stack

**Language:** Java 17+  
**Data Structures:** HashMap (transition function), HashSet (final states)  
**I/O:** BufferedReader (input), BufferedWriter (output)  
**File Format:** Plain text configuration

### Architecture

Clean separation between configuration parsing, simulation, and output:

```
input.dat
  ├── Initial state
  ├── Final states
  ├── Transition functions
  └── Test words
      ↓
DFA Simulator (DFA.java)
  ├── Parse configuration
  │   ├── Read initial state
  │   ├── Read final states (HashSet)
  │   └── Build transition map (HashMap)
  ├── For each word:
  │   ├── Start from initial state
  │   ├── Process each character (lookup transition)
  │   └── Check if final state is accepting
  └── Write results
      ↓
output.dat ("DA" or "NU" for each word)
```

**Key Components:**

- **Transition Function:** Stored as `HashMap<Pair<State, Symbol>, NextState>` for O(1) lookups
- **Final States:** Stored as `HashSet<State>` for O(1) membership checks
- **Simulation Loop:** Iterates through word characters, applying transitions
- **Acceptance Check:** Verifies if the final reached state is in the accepting set

---

## Project Structure

```
DFA/
├── DFA.java          # Main simulator implementation
├── input.dat         # DFA configuration + test words
├── output.dat        # Results (generated)
└── README.md         # This file
```

---

## Getting Started

**Prerequisites:**
Java 17+ (or any recent JDK)

**Compile:**
```bash
javac DFA.java
```

**Run:**
```bash
java DFA
```

The program reads from `input.dat` and writes results to `output.dat`.

---

## Input Format

**input.dat structure:**

```
<initial_state>
<final_state_1> <final_state_2> ...
<num_transitions>
<state> <symbol> <next_state>
<state> <symbol> <next_state>
...
<num_words>
<word_1>
<word_2>
...
```

**Example:**
```
q0
q2 q3
5
q0 a q1
q0 b q0
q1 a q2
q1 b q3
q2 b q2
3
aab
aba
bba
```

**Explanation:**
- Initial state: `q0`
- Final states: `q2`, `q3`
- 5 transitions defined
- 3 test words to process

---

## Output Format

**output.dat:**
```
DA
NU
DA
```

One line per input word:
- **DA** (YES) - Word is accepted (ended in a final state)
- **NU** (NO) - Word is rejected (ended in a non-final state or invalid transition)

---

## How It Works

**Simulation Algorithm:**

1. **Configuration Parsing**
   - Read initial state (single state identifier)
   - Read final states (space-separated list → HashSet)
   - Read transitions (state, symbol, next_state triples → HashMap)

2. **Word Processing** (for each test word)
   - Initialize `current_state = initial_state`
   - For each character `c` in the word:
     - Look up `transition(current_state, c)` in HashMap
     - Update `current_state` to next state
     - If no transition exists, reject word
   - After processing all characters, check if `current_state ∈ final_states`

3. **Output Generation**
   - If `current_state` is final: write "DA"
   - Otherwise: write "NU"

**DFA Properties:**
- **Determinism:** Exactly one transition per (state, symbol) pair
- **Completeness:** May have undefined transitions (treated as rejection)
- **Acceptance:** Word accepted ⟺ final state after processing is in accepting set

---

## Complexity

**Time Complexity:**
- **Configuration parsing:** $O(n + t)$ where $n$ = file lines, $t$ = transitions
- **Per word:** $O(m)$ where $m$ = word length (constant-time HashMap lookups)
- **Total:** $O(n + t + w \cdot m)$ where $w$ = number of words

**Space Complexity:**
- **Transition map:** $O(t)$ where $t$ = number of transitions
- **Final states:** $O(f)$ where $f$ = number of final states
- **Total:** $O(t + f)$

The HashMap-based approach ensures optimal performance: each state transition is $O(1)$.

---

## Example Run

**Input (input.dat):**
```
q0
q2
4
q0 0 q0
q0 1 q1
q1 0 q2
q1 1 q0
2
101
110
```

**Simulation:**

Word `101`:
1. Start: `q0`
2. Read `1`: `q0 → q1`
3. Read `0`: `q1 → q2`
4. Read `1`: `q2 → ?` (no transition, reject)
   - Result: **NU**

Word `110`:
1. Start: `q0`
2. Read `1`: `q0 → q1`
3. Read `1`: `q1 → q0`
4. Read `0`: `q0 → q0`
5. Final state `q0` is not in `{q2}`
   - Result: **NU**

**Output (output.dat):**
```
NU
NU
```

---

## Automata Theory Context

**DFA Definition:**
A DFA is a 5-tuple $(Q, \Sigma, \delta, q_0, F)$ where:
- $Q$ = finite set of states
- $\Sigma$ = input alphabet
- $\delta: Q \times \Sigma \to Q$ = transition function (deterministic)
- $q_0 \in Q$ = initial state
- $F \subseteq Q$ = set of accepting/final states

**Properties:**
- Recognizes exactly the **regular languages**
- Equivalent to NFAs (but NFAs are more concise)
- Can be minimized to a unique minimal DFA
- Used in lexical analysis, pattern matching, protocol verification

---

## What's Next

Potential extensions:
- DFA minimization (Hopcroft's algorithm)
- Graphical visualization (DOT/Graphviz export)
- Interactive mode (real-time word testing)
- DFA operations (union, intersection, complement)
- Conversion to NFA or regular expression
- Performance benchmarking on large alphabets

---

## Educational Value

This project demonstrates:
- Finite state machine implementation
- HashMap-based efficient state transitions
- File I/O and structured data parsing
- Formal language theory in practice
- Automata simulation algorithms

Perfect for learning:
- Automata theory fundamentals
- DFA design and analysis
- Efficient algorithm implementation in Java
- Regular language recognition

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
