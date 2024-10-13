# Huffman Encoding and Entropy Calculation

An algorithm to compute the entropy and perform Huffman encoding for a given set of probabilities. This algorithm calculates Shannon's entropy and builds a Huffman tree to determine the average length of the Huffman code.

## Code Summary

### Shannon's Entropy
The Shannon entropy of a discrete probability distribution is calculated using the formula:
\[ H = -\sum_{i} p_i \log_2(p_i) \]

### Huffman Encoding
Huffman encoding is a method of lossless data compression. The algorithm works as follows:
1. **Min-Heap Initialization:** Start with a min-heap where each node represents a symbol with its corresponding probability.
2. **Build the Huffman Tree:**
    - Repeatedly combine the two nodes with the smallest probabilities.
    - Assign '0' to the left edge and '1' to the right edge for each combination.
    - Push the new combined node back into the heap.
    - Continue this process until there is only one node left, which becomes the root of the Huffman tree.
3. **Generate Codes:** Traverse the Huffman tree to generate the codes for each symbol. The path from the root to a leaf node gives the Huffman code for that symbol.

### Time and Space Complexity

#### Time Complexity
- **Entropy Calculation:** The time complexity for calculating the entropy is \(O(N)\), where \(N\) is the number of probabilities.
- **Huffman Encoding:** The time complexity for building the Huffman tree is \(O(N \log N)\). This is due to the heap operations involved in repeatedly combining the two smallest nodes.

#### Space Complexity
- **Entropy Calculation:** The space complexity is \(O(1)\), as it only requires a constant amount of space for the calculation.
- **Huffman Encoding:** The space complexity is \(O(N)\), as it requires space to store the heap and the resulting Huffman codes.

### Results
- **Entropy:** The entropy of the given probabilities.
- **Huffman Codes:** The Huffman codes for each symbol.
- **Huffman Length:** The average length of the Huffman code.

The provided script calculates these values and prints them, allowing for efficient data compression analysis based on the given probabilities.

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
