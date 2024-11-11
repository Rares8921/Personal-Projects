# Regex to DFA Conversion

This Java program converts a regular expression into a Deterministic Finite Automaton (DFA) using syntax trees (abstract syntax trees, AST) and follows specific steps to ensure accurate transformation and efficient processing.

## Code Summary

1. **Input:**
   - The program prompts the user to input a regular expression.

2. **Augmented Regular Expression:**
   - The regular expression is augmented by adding concatenation operators where necessary and an end marker `#`.

3. **Syntax Tree Generation:**
   - The augmented regular expression is parsed into a syntax tree.
   - Each node in the tree corresponds to a subexpression or operator.

4. **FollowPos Calculation:**
   - For each position in the tree, the `followPos` (the positions that can be reached) is calculated.

5. **DFA Construction:**
   - States and transitions of the DFA are generated using a subset construction method.
   - The initial state is derived from the `firstPos` of the root node.
   - Transitions are generated based on the `followPos` values.

6. **Output:**
   - The DFA's states, alphabet, initial state, final states, and transitions are printed.

## Complexity and Efficiency

### Time Complexity

1. **Input Parsing:**
   - Reading and augmenting the regular expression: \(O(n)\), where \(n\) is the length of the regular expression.

2. **Syntax Tree Construction:**
   - Building the syntax tree: \(O(n)\), as each character is processed.

3. **FollowPos Calculation:**
   - Calculating `followPos` involves traversing the syntax tree: \(O(n)\).

4. **DFA Construction:**
   - The subset construction algorithm can result in a DFA with at most \(2^n\) states in the worst case, where \(n\) is the number of positions in the syntax tree.
   - Each state involves processing all positions in the syntax tree: \(O(n \cdot 2^n)\).

### Overall Time Complexity

- The overall time complexity is dominated by the DFA construction step, making it \(O(n \cdot 2^n)\).

### Space Complexity

1. **Syntax Tree Storage:**
   - Storing the syntax tree and related information (nullable, firstPos, lastPos, followPos): \(O(n)\).

2. **DFA Storage:**
   - The number of states can be at most \(2^n\).
   - Storing transitions: \(O(2^n \cdot n)\).

### Overall Space Complexity

- The overall space complexity is \(O(2^n \cdot n)\).