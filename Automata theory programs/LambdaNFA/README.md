# Lambda-NFA Simulator (ε-NFA)

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Automata Theory](https://img.shields.io/badge/Automata-Theory-blue?style=for-the-badge)
![Epsilon Transitions](https://img.shields.io/badge/ε--NFA-FSM-purple?style=for-the-badge)

**A Java implementation of a Non-deterministic Finite Automaton with epsilon (λ) transitions. Explores state spaces with spontaneous moves that consume no input.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [How It Works](#how-it-works) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a Lambda-NFA (ε-NFA) simulator that extends standard NFAs with **epsilon transitions** - state transitions that occur spontaneously without consuming input symbols. This allows more compact automaton representations for complex patterns.

**Key Features:**
- **Epsilon transitions** - λ/ε moves that consume no input
- **Epsilon closure** - Automatic expansion to all ε-reachable states
- **Non-deterministic simulation** - Parallel state exploration
- **File-based configuration** - Reads from `input.dat`
- **Complete lambda calculus** - Handles arbitrary ε-transition chains

**Use cases:**
- Regular expression compilation (Thompson's construction produces ε-NFAs)
- Pattern matching with optional components
- Compiler design (lexical analysis with lookahead)
- Formal language theory education
- NFA simplification and optimization studies

Lambda-NFAs are the most expressive finite automaton variant while still recognizing only regular languages.

---

## Tech Stack

**Language:** Java 17+  
**Data Structures:** HashMap (transitions), HashSet (state closures)  
**Algorithms:** Epsilon closure (transitive closure), BFS state exploration  
**I/O:** BufferedReader/BufferedWriter

### Architecture

Epsilon-aware simulation with automatic closure computation:

```
input.dat
  ├── Initial state
  ├── Final states
  ├── Transition functions (including λ/ε transitions)
  └── Test words
      ↓
Lambda-NFA Simulator (LambdaNFA.java)
  ├── Parse configuration
  │   ├── Read initial state
  │   ├── Read final states (HashSet)
  │   └── Build transition map: (state, symbol) → List<states>
  │       (symbol can be λ/ε for epsilon transitions)
  ├── For each word:
  │   ├── Compute epsilon closure of {initial_state}
  │   ├── For each character:
  │   │   ├── Expand active states via normal transitions
  │   │   └── Compute epsilon closure of result
  │   └── Check if any final active state is accepting
  └── Write results
      ↓
output.dat ("DA" if accepted, "NU" otherwise)
```

**Key Components:**

- **Epsilon Closure:** For state set $S$, compute $\varepsilon\text{-closure}(S)$ = all states reachable via λ-transitions
- **Transition Function:** `HashMap<Pair<State, Symbol>, List<States>>` where symbol can be `λ` or `ε`
- **Active States:** `HashSet<State>` - includes all ε-reachable states after each step
- **Simulation:** Character transitions followed by epsilon closure expansion

---

## Project Structure

```
LambdaNFA/
├── LambdaNFA.java    # Main simulator with ε-closure logic
├── input.dat         # Lambda-NFA configuration + test words
├── output.dat        # Results (generated)
└── README.md         # This file
```

---

## Getting Started

**Prerequisites:**
Java 17+ (or any recent JDK)

**Compile:**
```bash
javac LambdaNFA.java
```

**Run:**
```bash
java LambdaNFA
```

Reads configuration from `input.dat`, outputs to `output.dat`.

---

## Input Format

**input.dat structure:**

```
<initial_state>
<final_state_1> <final_state_2> ...
<num_transitions>
<state> <symbol_or_lambda> <next_state>
...                # λ or ε represents epsilon transition
<num_words>
<word_1>
<word_2>
...
```

**Example (with epsilon transitions):**
```
q0
q3
6
q0 λ q1
q0 a q0
q1 b q2
q2 λ q3
q2 c q2
q3 λ q0
3
abc
ab
a
```

**Explanation:**
- `q0 λ q1` - Can move from q0 to q1 without consuming input
- `q2 λ q3` - Spontaneous transition to accepting state
- Epsilon transitions create "shortcuts" and optional paths

---

## Output Format

```
DA
NU
DA
```

One line per word:
- **DA** (YES) - Word accepted (considering all ε-transitions)
- **NU** (NO) - Word rejected

---

## How It Works

**Epsilon-NFA Simulation Algorithm:**

1. **Configuration Parsing**
   - Read initial state, final states, transitions
   - Identify epsilon transitions (symbol = `λ` or `ε`)

2. **Epsilon Closure Function** `εClosure(S):`
   - Initialize `closure = S`
   - Repeat until no new states added:
     - For each state `s` in `closure`:
       - Add all states reachable via single ε-transition
   - Return `closure`

3. **Word Processing**
   - Initialize `activeStates = εClosure({initial_state})`
   - For each character `c` in the word:
     - `nextStates = ∅`
     - For each state in `activeStates`:
       - Add all states reachable via `c`-transition
     - `activeStates = εClosure(nextStates)`
   - After processing: check if `activeStates ∩ finalStates ≠ ∅`

4. **Acceptance**
   - Accept if any final active state is in accepting set

**Epsilon Closure Example:**

Given transitions:
```
q0 → λ → q1
q1 → λ → q2
q2 → a → q3
```

- `εClosure({q0}) = {q0, q1, q2}` (all reachable via λ-chain)
- On input `a` from `{q0}`:
  - First compute `εClosure({q0}) = {q0, q1, q2}`
  - Find `a`-transitions: `q2 →^a q3`
  - Result: `εClosure({q3}) = {q3}`

---

## Complexity

**Time Complexity:**
- **Epsilon closure:** $O(s^2)$ per closure computation (worst case: fully connected ε-graph)
- **Per word:** $O(m \cdot s^2)$ where $m$ = word length, $s$ = number of states
  - Each character requires: transition expansion $O(s)$ + ε-closure $O(s^2)$
- **Total:** $O(n + t + w \cdot m \cdot s^2)$

**Space Complexity:**
- **Transition map:** $O(t)$ including epsilon transitions
- **Active states + closures:** $O(s)$
- **Total:** $O(t + s)$

Epsilon transitions add computational overhead but allow significantly more compact automaton representations.

---

## Example Run

**Input (input.dat):**
```
q0
q2
5
q0 λ q1
q0 a q0
q1 a q2
q1 λ q2
q2 b q2
2
a
ab
```

**Simulation:**

**Word `a`:**
1. Start: `εClosure({q0}) = {q0, q1, q2}` (q0 → λ → q1 → λ → q2)
2. Read `a`:
   - From `{q0, q1, q2}`, `a`-transitions lead to `{q0, q2}`
   - `εClosure({q0, q2}) = {q0, q1, q2}`
3. Final active states: `{q0, q1, q2}`, includes final `q2` → **DA**

**Word `ab`:**
1. Start: `εClosure({q0}) = {q0, q1, q2}`
2. Read `a`: `εClosure({q0, q2}) = {q0, q1, q2}`
3. Read `b`:
   - From `{q0, q1, q2}`, only `q2` has `b`-transition → `{q2}`
   - `εClosure({q2}) = {q2}`
4. Final: `{q2}` is accepting → **DA**

**Output (output.dat):**
```
DA
DA
```

---

## Automata Theory Context

**ε-NFA Definition:**
An ε-NFA is a 5-tuple $(Q, \Sigma, \delta, q_0, F)$ where:
- $Q$ = finite set of states
- $\Sigma$ = input alphabet
- $\delta: Q \times (\Sigma \cup \\{\varepsilon\\}) \to \mathcal{P}(Q)$ = transition function (includes ε-transitions)
- $q_0 \in Q$ = initial state
- $F \subseteq Q$ = accepting states

**Key Properties:**
- Epsilon transitions allow "free" moves without consuming input
- Recognizes exactly the **regular languages** (same as DFA/NFA)
- Every ε-NFA can be converted to NFA (by removing ε-transitions via closure)
- Every ε-NFA can be converted to DFA (via ε-closure + subset construction)
- Often more compact than NFAs for representing complex patterns

**Equivalence:**
ε-NFA ≡ NFA ≡ DFA ≡ Regular Expressions (all describe regular languages)

---

## What's Next

Potential extensions:
- ε-NFA to NFA conversion (remove epsilon transitions)
- ε-NFA to DFA conversion (combined closure + subset construction)
- Regular expression to ε-NFA (Thompson's construction)
- Graphical visualization (show ε-transition chains)
- Interactive step-by-step simulation with closure visualization
- Optimization (minimize epsilon transitions)

---

## Educational Value

This project demonstrates:
- Epsilon transitions and their role in automata theory
- Transitive closure algorithms (epsilon closure computation)
- Advanced non-deterministic simulation
- Foundation of regular expression engines
- Automaton equivalence proofs (ε-NFA ≡ NFA ≡ DFA)

Perfect for learning:
- Lambda calculus concepts in automata
- Closure operations on state sets
- Regular expression implementation
- Automata theory fundamentals

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
