# Lambda-NFA (Non-deterministic Finite Automaton with Lambda Transitions)

A Java implementation of a Lambda-NFA that reads its configuration and inputs from a file, processes the inputs based on the Lambda-NFA configuration, and outputs the results.

## Code summary

The script reads the Lambda-NFA configuration and input words from a file named `input.dat`. It then processes each word according to the Lambda-NFA's transition rules and writes the results to `output.dat`.

1. **File Reading:**
   - Reads the entire content of `input.dat` into a list of strings using `BufferedReader`.

2. **Lambda-NFA Configuration:**
   - Parses the initial state, final states, and transition functions from the list of strings.
   - Stores the transition functions in a hashmap where the key is a pair of current state and input letter, and the value is a list of next states.

3. **Word Processing:**
   - For each word in the input, simulates the Lambda-NFA using the transition function.
   - Uses lambda transitions to determine all possible states that can be reached.
   - Checks if any of the possible states after processing the word is an accepted final state.

4. **Output:**
   - Writes "DA" (YES) if the word is accepted by the Lambda-NFA and "NU" (NO) otherwise.

## Complexity and Efficiency

### Time Complexity
- **File Reading:** \(O(n)\), where \(n\) is the number of lines in the file.
- **Lambda-NFA Configuration Parsing:** \(O(t)\), where \(t\) is the number of transitions.
- **Word Processing:** For each word of length \(m\):
  - Processing each character can involve multiple states due to non-determinism and lambda transitions.
  - In the worst case, this involves \(O(s \cdot m)\), where \(s\) is the number of states.
  - Each lambda transition can propagate to multiple states, adding complexity.
- **Overall Time Complexity:** Let \(w\) be the number of words. The total time complexity is \(O(n + t + w \cdot s \cdot m)\).