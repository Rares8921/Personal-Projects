# <ins>Lee Visualizer</ins>

**Lee Visualizer** is a Java desktop application designed to simulate various versions of Lee's algorithm, including scenarios with obstacles, portals, lasers, and other custom elements. It provides an interactive and dynamic visualization of the pathfinding algorithm, allowing users to observe its operation in real-time.

## <ins>Application Overview</ins>

The **Lee Visualizer** was developed using JavaFX 15 and provides a user-friendly interface to simulate Lee's algorithm with multiple configurations. The core components of the application include:

- **Grid Panel**: This is the primary visualization area where the algorithm's progress is displayed. The grid represents the matrix where the user can define obstacles, portals, lasers, and other entities.
  
- **Option Buttons**: These allow users to configure the simulation by setting different types of cells in the grid, such as start points, obstacles (`X`), portals (`O`), lasers (`+`), and laser portals (`/`).
  
- **Action Buttons**: These include:
  - **Start**: Initiates the algorithm simulation when the user has provided valid input.
  - **Clear**: Resets the grid by removing all the placed numbers and entities, restoring it to its default state.

- **Help Menu**: The help menu provides detailed instructions on how to use the application, including explanations for each feature, how to place cells on the grid, and the purpose of each option.

## <ins>Code Functionality</ins>

- **Grid Interaction**: The user can select a specific option (obstacle, portal, laser, etc.) by left-clicking on the corresponding button, then left-click on any cell in the grid to place that entity in the chosen location.
  
- **Start Simulation**: Once all the necessary inputs are provided, the user can press the "Start" button to begin the simulation. The application checks if the inputs are valid (e.g., whether both start and end points are defined). If any errors are detected, the application will notify the user.
  
- **Algorithm Execution**: When the input is valid, the app runs Lee's algorithm, filling the matrix (referred to as `matr` in the [Main.java](https://github.com/Rares8921/Projects/blob/master/2022/Lee%20Visualizer/src/sample/Main.java)) with symbols that distinguish between different types of cells:
  - **Obstacle**: Marked with `X`.
  - **Portal**: Marked with `O`.
  - **Laser**: Marked with `+`.
  - **Laser Portal**: Marked with `/`.

- **Real-time Animation**: As Lee's algorithm runs, the application simultaneously animates the grid, updating the visual elements in sync with the algorithm's progress. The animation is designed to complete in real-time alongside the algorithm to ensure a smooth user experience without delay. Although Lee's algorithm runs faster than the animation, the app adjusts so that the user observes both happening concurrently.

## <ins>Complexity Analysis</ins>

- **Lee's Algorithm Complexity**: Lee's algorithm has a time-space complexity of approximately O(n * m), where `n` is the number of rows and `m` is the number of columns in the grid.

- **Grid Animation and Clear Function**: Both the grid animation process and the clear action also have a time complexity of O(n * m), as they depend on the number of cells in the matrix.

## <ins>Requirements</ins>

To run **Lee Visualizer**, ensure that you have Java JRE 15 or a later version installed on your system.


## <ins>Illustration(s)</ins>

Help menu:

![image](https://github.com/Rares8921/Projects/blob/master/2022/Lee%20Visualizer/helpMenu.png?raw=true)

Lasers animation with knight moves:
![image](https://github.com/Rares8921/Projects/blob/master/2022/Lee%20Visualizer/Lasers.gif?raw=true)

Portals' animation:
![image](https://github.com/Rares8921/Projects/blob/master/2022/Lee%20Visualizer/lasersAndTps.gif?raw=true)

Lasers with tps(portals) animation:
![image](https://github.com/Rares8921/Projects/blob/master/2022/Lee%20Visualizer/lasersWithTps.gif?raw=true)

Error message:

![image](https://github.com/Rares8921/Projects/blob/master/2022/Lee%20Visualizer/errorMessage.png?raw=true)
