import math
import heapq


# Calculate shanon's entropy: sum of(pi * log2(1/pi)) <=> - sum(pi * log2(pi))
def Entropy(probabilities):
    entropy = -sum(p * math.log2(p) for p in probabilities if p > 0)
    return entropy


#Huffman encoding
def Huffman(probabilities):
    # Min heap ordering by probabilities
    # The symbols are the numbers from 0 to len(probabilities) - 1
    heap = [[probability, [symbol, ""]] for symbol, probability in enumerate(probabilities)]
    heapq.heapify(heap)
    # Until we find the root, combine the elements with the least probabilities/frequencies
    while len(heap) > 1:
        first = heapq.heappop(heap)
        second = heapq.heappop(heap)
        # Convention: 0 on the left, 1 on the right
        for pair in first[1:]:
            pair[1] = '0' + pair[1]
        for pair in second[1:]:
            pair[1] = '1' + pair[1]
        # Add the new node to heap: the new probability and the two children of the node
        heapq.heappush(heap, [first[0] + second[0]] + first[1:] + second[1:])
    # Now for a nice visualization, return pairs in form (character, huffman code)
    # They will be sorted increasingly by the code's length
    # If two codes share the same length, compare the character
    return sorted(heapq.heappop(heap)[1:], key=lambda p: (len(p[-1]), p))


# Calculate according to formula
def HuffmanLength(huff):
    length = sum(probabilities[symbol] * len(code) for symbol, code in huff)
    return length
    

# A bunch of probabilities
probabilities = [
    0.111111, 0.0833333, 0.0555556, 0.0555556, 0.0277778, 0.0833333,
    0.0625, 0.0416667, 0.0416667, 0.0208333, 0.0555556, 0.0416667,
    0.0277778, 0.0277778, 0.0138889, 0.0555556, 0.0416667, 0.0277778,
    0.0277778, 0.0138889, 0.0277778, 0.0208333, 0.0138889, 0.0138889, 0.00694444
]
entropy = Entropy(probabilities)
huff = Huffman(probabilities)
huffman_length = HuffmanLength(huff)
print(entropy, '\n', *huff, '\n', huffman_length, sep = '')