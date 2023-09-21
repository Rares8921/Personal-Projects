# Rolling dice

A console game where the users plays against the computer. There are at most 3 turns and whoever rolls a higher value gets a point. If the values are equal, both the player and the computer get 1 point. The possible endings for a game are win, loss or draw.

## Code summary

(random library included)
The program generates, at each turn, 4 random numbers, 2 for the player and 2 for the computer. Then, the program verifies whose values summed up are the highest and the point is given to the one that has a larger number. If the values are the same, both get the point. After each point won, it is checked if the game was won by someone, formally if the player or the computer has 2 points, or if the game ended in a draw. 

## Time complexity

The randint function has O(1) time and space complexity.
The algorithm utilises a series of if and else statements, which result in a O(1) time complexity.
In the program there are a few variables that are used, so the space complexity is O(1).
Therefore, the overall time complexity is O(1), as well as the space complexity.

## Illustrations

Game won:

![image]()

Game lost:

![image]()

Game drawn:

![image]()