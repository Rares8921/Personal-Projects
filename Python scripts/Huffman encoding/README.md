# Huffman Encoding

<div align="center">

![Python](https://img.shields.io/badge/Python-3.7+-blue?style=for-the-badge&logo=python)
![Algorithm](https://img.shields.io/badge/Algorithm-Huffman-red?style=for-the-badge)
![Data Structures](https://img.shields.io/badge/Data%20Structures-Binary%20Tree-orange?style=for-the-badge)

**Information theory in action. Optimal lossless compression using binary trees and probability mathematics.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is an implementation of Huffman coding, the foundational algorithm behind ZIP compression, JPEG encoding, and MP3 audio. It generates optimal variable-length binary codes based on symbol probabilities, approaching Shannon's entropy limit.

**What makes it special:**
- **Optimal prefix-free codes:** No code is a prefix of another (unambiguous decoding)
- **Probability-based:** Frequent symbols get shorter codes, rare symbols get longer codes
- **Lossless compression:** Perfect reconstruction of original data
- **Mathematically sound:** Average code length approaches theoretical minimum (entropy)

**The algorithm:**
1. Build min-heap from symbol probabilities
2. Repeatedly merge two lowest-probability nodes
3. Assign '0' to left child, '1' to right child during merges
4. Root contains complete Huffman tree
5. Path from root to each symbol = its binary code

**Example output:**
Given 25 symbols with varying probabilities, generates codes like:
- High-probability symbol (0.111): `00` (2 bits)
- Medium-probability symbol (0.0555): `101` (3 bits)  
- Low-probability symbol (0.00694): `111111` (6 bits)

---

## Tech Stack

**Language:** Python 3.7+  
**Data Structures:** Min-heap (priority queue), binary tree  
**Modules:** heapq (heap operations), math (entropy calculation)

### Architecture

```
Symbol Probabilities
         ↓
Shannon Entropy Calculation
H = -Σ(p_i × log₂(p_i))
         ↓
Huffman Tree Construction
(Min-heap based greedy algorithm)
         ↓
Code Assignment
(Left=0, Right=1)
         ↓
Average Code Length Calculation
L = Σ(p_i × |code_i|)
         ↓
Verification: L ≥ H (always satisfies)
```

**Huffman Algorithm Details:**

```
Initial: Heap = [(p₁, s₁), (p₂, s₂), ..., (pₙ, sₙ)]

While heap.size > 1:
  node1 = heap.pop_min()  # Lowest probability
  node2 = heap.pop_min()  # Second lowest
  
  Assign '0' to all codes in node1
  Assign '1' to all codes in node2
  
  merged = Node(node1.prob + node2.prob, children=[node1, node2])
  heap.push(merged)

Root = heap.pop()  # Single node remaining
```

**Complexity:**
- **Time:** O(n log n) where n = number of symbols
- **Space:** O(n) for heap storage
- **Optimality:** Produces minimum average code length among all prefix-free codes

---

## Project Structure

```
Huffman encoding/
├── huffman.py           # Complete Huffman implementation
└── README.md
```

**Code structure:**
- Lines 1-8: `Entropy()` function (Shannon entropy calculation)
- Lines 11-31: `Huffman()` function (tree construction + code generation)
- Lines 34-37: `HuffmanLength()` function (average code length)
- Lines 40-47: Test probabilities (25 symbols)
- Lines 48-51: Compute entropy, codes, and average length

**Key functions:**

```python
Entropy(probabilities)       # Returns H = -Σ(p×log₂(p))
Huffman(probabilities)       # Returns [(symbol, code), ...]
HuffmanLength(codes)         # Returns average code length
```

---

## Getting Started

**Requirements:**
Python 3.7+ (heapq and math are standard library)

**Run it:**

```bash
python huffman.py
```

**Output format:**

```
3.904566...                  # Shannon entropy (theoretical minimum)
[0, '00']                    # Symbol 0 → code "00"
[1, '010']                   # Symbol 1 → code "010"
[2, '1100']                  # Symbol 2 → code "1100"
...
[24, '111111']               # Symbol 24 → code "111111"
3.945833...                  # Average Huffman code length
```

**Interpretation:**
- Entropy (3.90 bits): Theoretical minimum average bits per symbol
- Huffman length (3.95 bits): Actual achieved average
- Overhead (0.05 bits): Due to discrete code lengths (can't use fractional bits)

**Using with your data:**

```python
# Define symbol probabilities (must sum to 1.0)
my_probs = [0.4, 0.3, 0.2, 0.1]  # 4 symbols

# Generate Huffman codes
codes = Huffman(my_probs)
print(codes)  # [(0, '0'), (1, '10'), (2, '110'), (3, '111')]

# Encode message
message = [0, 0, 1, 2, 0, 3]  # Symbol sequence
encoded = ''.join(codes[s][1] for s in message)
print(encoded)  # '0010110011'
```

---

## What's Next

**Possible enhancements:**
- Build actual file compressor (read file → encode → save bits)
- Implement decoder (binary → original data)
- Visualize Huffman tree (ASCII art or graphviz)
- Compare compression ratios on real files
- Adaptive Huffman (update tree as data streams in)
- Support arbitrary data (not just probabilities)
- Benchmark against other compression algorithms
- Create GUI for encoding/decoding text

**Theoretical extensions:**
- Arithmetic coding (better compression for skewed distributions)
- Huffman coding with escape symbols
- Multi-symbol Huffman (encode pairs/triples of symbols)

---

## License

**Proprietary - All Rights Reserved**

This code is the intellectual property of the author. No copying, modification, or distribution is permitted without explicit written consent.
