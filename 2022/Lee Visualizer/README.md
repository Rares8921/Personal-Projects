# Lee Visualizer

A java desktop application that simulates a variety of lee's algorihtm version: with obstacles, with portals etc.

## Code summary
The application was created using JavaFX 15. <br/>
The components of the app are: the grid where the algorithm is simulated, the option buttons, the action buttons(start and clear) and the help menu. <br/>
The help menu contains all the necessary explanation about the application and how to use it correctly.<br/>
The user can select an option or set a cell in the matrix by pressing left click on the option, respectively on a certain cell in the grid.<br/>
The start button beggins to simulate the algorithm if all the given input was correct. <br/>
The clear button removes the numbers from all fields. <br/>
Firstly, the user has to give input for the algorithm and if the input is correct the animation will start, else the app will give an error </br>
Secondly, if the input is correct, the algorithm fills the matrix (matr in Main.class) with certain symbols to distinguish if a cell is an obstacle, a portal etc. <br/>
Coding: An <ins>ostable cell</ins> is marked as 'X'; a <ins>portal</ins> is marked with 'O'; a <ins>laser</ins> is marked with '+' and a <ins>laser with portals</ins> is marked as '/' <br/>
Then, both the animation algorithm and lee's algorithm are executed 'simoultaniously'(In practice the lee's algorithm works faster so the animation will not crash), in order to complete the grid in the same time as the algorithm is ran so that the user will not wait for the algorithm to finish and then get the animation. <br/>
More conditions about buttons are found in <ins>Controller.java</ins> 


## Complexity analysis
The well-known lee's algorithm has a time-space complexity of ~O(n*m) where n is the number of lines and m is the number of columns. <br/>
The grid animation algorithm and the clear action have both, as well, a time complexity of O(n*m)<br/>

## Requirement(s):
Java JRE 15 or a later version installed.

## Illustration(s)

Help menu:

![image](https://github.com/Rares8921/Projects/blob/master/2022/Lee%20Visualizer/helpMenu.png?raw=true)

Obstacles animation:

Lasers animation:

Portals' animation:

Lasers as tps(portals) animation:

Only with knight moves:

Error message:
