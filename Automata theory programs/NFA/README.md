# NFA Simulator

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Automata Theory](https://img.shields.io/badge/Automata-Theory-blue?style=for-the-badge)
![Non-deterministic](https://img.shields.io/badge/NFA-FSM-purple?style=for-the-badge)

**A Java implementation of a Non-deterministic Finite Automaton simulator. Explores all possible state paths simultaneously to determine word acceptance.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [How It Works](#how-it-works) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is an NFA (Non-deterministic Finite Automaton) simulator that processes input words by exploring all possible state transitions in parallel, accepting a word if *any* execution path leads to an accepting state.

**Key Features:**
- **Non-deterministic simulation** - Tracks multiple active states simultaneously
- **File-based configuration** - Reads NFA definition from `input.dat`
- **Parallel path exploration** - Simulates all possible execution branches
- **Batch word processing** - Tests multiple words in one run
- **Efficient state tracking** - HashSet-based active state management

**Use cases:**
- Automata theory education (NFA vs. DFA comparison)
- Pattern matching with backtracking
- Regular expression engines (under the hood)
- Compiler design (tokenization with lookahead)
- Formal language recognition

Unlike DFAs, NFAs can have multiple transitions for the same (state, symbol) pair, making them more expressive and concise but requiring parallel simulation.

---

## Tech Stack

**Language:** Java 17+  
**Data Structures:** HashMap (transitions to state lists), HashSet (active/final states)  
**I/O:** BufferedReader (input), BufferedWriter (output)  
**Simulation:** Breadth-first state exploration

### Architecture

Non-deterministic simulation with parallel state tracking:

```
input.dat
  ├── Initial state
  ├── Final states
  ├── Transition functions (multiple per (state, symbol))
  └── Test words
      ↓
NFA Simulator (NFA.java)
  ├── Parse configuration
  │   ├── Read initial state
  │   ├── Read final states (HashSet)
  │   └── Build transition map: (state, symbol) → List<states>
  ├── For each word:
  │   ├── Start with {initial_state} as active states
  │   ├── For each character:
  │   │   ├── For each active state:
  │   │   │   └── Add all reachable next states
  │   │   └── Update active states set
  │   └── Check if any active state is accepting
  └── Write results
      ↓
output.dat ("DA" if accepted, "NU" otherwise)
```

**Key Components:**

- **Transition Function:** `HashMap<Pair<State, Symbol>, List<NextStates>>` - One symbol can lead to multiple states
- **Active States:** `HashSet<State>` - All currently reachable states (grows during simulation)
- **Final States:** `HashSet<State>` - Accepting states for membership checks
- **Parallel Simulation:** Processes all active states per character, expands to all successors

---

## Project Structure

```
NFA/
├── NFA.java          # Main simulator implementation
├── input.dat         # NFA configuration + test words
├── output.dat        # Results (generated)
└── README.md         # This file
```

---

## Getting Started

**Prerequisites:**
Java 17+ (or any recent JDK)

**Compile:**
```bash
javac NFA.java
```

**Run:**
```bash
java NFA
```

Reads from `input.dat`, writes to `output.dat`.

---

## Input Format

**input.dat structure:**

```
<initial_state>
<final_state_1> <final_state_2> ...
<num_transitions>
<state> <symbol> <next_state_1>
<state> <symbol> <next_state_2>  # Non-deterministic: same (state, symbol) can repeat
...
<num_words>
<word_1>
<word_2>
...
```

**Example (non-deterministic transitions):**
```
q0
q2
6
q0 a q0
q0 a q1
q1 b q1
q1 b q2
q2 a q2
q2 b q2
2
aab
abb
```

**Explanation:**
- From `q0` on symbol `a`, can go to **both** `q0` and `q1` (non-deterministic!)
- From `q1` on symbol `b`, can go to both `q1` and `q2`
- This creates multiple parallel execution paths

---

## Output Format

```
DA
NU
```

One line per word:
- **DA** (YES) - At least one execution path reaches a final state
- **NU** (NO) - No execution path reaches a final state

---

## How It Works

**Non-deterministic Simulation Algorithm:**

1. **Configuration Parsing**
   - Read initial state
   - Read final states → HashSet
   - Read transitions → HashMap: for each (state, symbol), store **all** possible next states in a List

2. **Word Processing** (for each test word)
   - Initialize `activeStates = {initial_state}`
   - For each character `c` in the word:
     - Create `nextStates = ∅`
     - For each `state` in `activeStates`:
       - Look up all transitions `(state, c) → [s1, s2, ..., sn]`
       - Add `s1, s2, ..., sn` to `nextStates`
     - Update `activeStates = nextStates`
   - After processing: check if `activeStates ∩ finalStates ≠ ∅`

3. **Acceptance Decision**
   - If **any** state in the final `activeStates` is accepting: write "DA"
   - Otherwise: write "NU"

**NFA vs. DFA:**
- **DFA:** Exactly one active state at all times (deterministic)
- **NFA:** Multiple active states tracked simultaneously (non-deterministic)
- **Equivalence:** Every NFA can be converted to an equivalent DFA (but DFA may have exponentially more states)

---

## Complexity

**Time Complexity:**
- **Configuration parsing:** $O(n + t)$ where $n$ = file lines, $t$ = transitions
- **Per word:** $O(s \cdot m)$ where $s$ = number of states, $m$ = word length
  - Worst case: all states active simultaneously
  - Each character requires checking all active states and expanding successors
- **Total:** $O(n + t + w \cdot s \cdot m)$ where $w$ = number of words

**Space Complexity:**
- **Transition map:** $O(t)$ (stores all transitions, including duplicates)
- **Active states:** $O(s)$ (worst case: all states reachable)
- **Total:** $O(t + s)$

Non-deterministic simulation is more expensive than DFA ($O(m)$ per word), but NFAs are often more compact.

---

## Example Run

**Input (input.dat):**
```
q0
q2
4
q0 0 q0
q0 1 q0
q0 1 q1
q1 0 q2
2
110
101
```

**Simulation:**

**Word `110`:**
1. Start: `{q0}`
2. Read `1`: `{q0} → {q0, q1}` (non-deterministic branching!)
3. Read `1`: `{q0, q1} → {q0, q1}` (both paths continue)
4. Read `0`: `{q0, q1} → {q0, q2}` (q1 → q2 is a valid path)
5. Final active states: `{q0, q2}`
6. `q2` is final → **DA**

**Word `101`:**
1. Start: `{q0}`
2. Read `1`: `{q0} → {q0, q1}`
3. Read `0`: `{q0, q1} → {q0, q2}`
4. Read `1`: `{q0, q2} → {q0, q1}` (no transition from q2 on 1, stays in q0 branch)
5. Final active states: `{q0, q1}`
6. Neither is final → **NU**

**Output (output.dat):**
```
DA
NU
```

---

## Automata Theory Context

**NFA Definition:**
An NFA is a 5-tuple $(Q, \Sigma, \delta, q_0, F)$ where:
- $Q$ = finite set of states
- $\Sigma$ = input alphabet
- $\delta: Q \times \Sigma \to \mathcal{P}(Q)$ = transition function (returns **set** of states)
- $q_0 \in Q$ = initial state
- $F \subseteq Q$ = set of accepting states

**Key Properties:**
- Multiple transitions per (state, symbol) pair allowed
- Word accepted if **any** execution path reaches a final state
- Equivalent to DFAs (via subset construction)
- More concise than DFAs for many languages
- Foundation of regular expression matching engines

---

## What's Next

Potential extensions:
- NFA to DFA conversion (subset construction algorithm)
- Graphical visualization (execution tree/state graph)
- Interactive mode with step-by-step simulation
- Regular expression to NFA conversion (Thompson's construction)
- Epsilon-NFA support (λ-transitions)
- Performance optimization (lazy state expansion)

---

## Educational Value

This project demonstrates:
- Non-deterministic computation simulation
- Set-based state management
- Parallel path exploration algorithms
- Automata theory fundamentals (NFA semantics)
- Comparison with deterministic models (DFA)

Perfect for learning:
- Non-determinism in computation
- Automata theory and formal languages
- Regular expression implementation principles
- Algorithm design for parallel state tracking

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
