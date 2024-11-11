# DFA (Deterministic Finite Automaton)

A Java implementation of a DFA that reads its configuration and inputs from a file, processes the inputs based on the DFA configuration, and outputs the results.

## Code summary

The script reads the DFA configuration and input words from a file named `input.dat`. It then processes each word according to the DFA's transition rules and writes the results to `output.dat`.

1. **File Reading:**
   - Reads the entire content of `input.dat` into a list of strings using `BufferedReader`.

2. **DFA Configuration:**
   - Parses the initial state, final states, and transition functions from the list of strings.
   - Stores the transition functions in a hashmap where the key is a pair of current state and input letter, and the value is the next state.

3. **Word Processing:**
   - For each word in the input, starts from the initial state and processes each letter.
   - Uses the transition function to move to the next state.
   - Checks if the final state after processing the word is an accepted final state.

4. **Output:**
   - Writes "DA" (YES) if the word is accepted by the DFA and "NU" (NO) otherwise.

## Complexity and Efficiency

### Time Complexity
- **File Reading:** \(O(n)\), where \(n\) is the number of lines in the file.
- **DFA Configuration Parsing:** \(O(t)\), where \(t\) is the number of transitions.
- **Word Processing:** For each word of length \(m\), the time complexity is \(O(m)\) because it processes each character once.
- **Overall Time Complexity:** Let \(w\) be the number of words. The total time complexity is \(O(n + t + wm)\).