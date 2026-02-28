# Traffic Signal Control with Deep Reinforcement Learning

<div align="center">

![Python](https://img.shields.io/badge/Python-3.x-blue?style=for-the-badge&logo=python)
![PyTorch](https://img.shields.io/badge/PyTorch-DL-red?style=for-the-badge&logo=pytorch)
![SUMO](https://img.shields.io/badge/SUMO-Traffic-green?style=for-the-badge)
![OpenCV](https://img.shields.io/badge/OpenCV-CV-blue?style=for-the-badge&logo=opencv)

**A comprehensive deep reinforcement learning platform for intelligent traffic signal optimization. Six state-of-the-art algorithms battle traffic congestion in realistic SUMO simulations.**

[What It Does](#what-it-does) • [Algorithms](#implemented-algorithms) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started)

</div>

---

## What It Does

This is a complete deep RL research platform that trains AI agents to optimize traffic light timing at intersections. Instead of fixed timing patterns, the agents learn adaptive strategies that minimize vehicle waiting times based on real-time traffic conditions.

**Key Features:**
- **6 RL algorithms implemented:** Deep SARSA, DQN, Double DQN, Multi-Step DQN, Dueling Double DQN, A2C
- **Realistic simulation:** SUMO (Simulation of Urban MObility) integration via TraCI
- **Comparative analysis:** Train all algorithms with multiple seeds for statistical significance
- **Computer vision bonus:** Automated traffic network generation from intersection images
- **Full metrics:** Episode returns, convergence rates, learning curves
- **Production-ready:** PyTorch implementations with experience replay and target networks

**Use cases:**
- Smart city traffic management
- RL algorithm benchmarking
- Intelligent transportation systems research
- Traffic signal optimization studies
- Educational demonstrations of deep RL

The system simulates realistic traffic dynamics and trains agents to make optimal signal timing decisions based on lane occupancy, halting vehicles, and current phase state.

---

## Implemented Algorithms

The project features six state-of-the-art reinforcement learning approaches:

### Value-Based Methods

1. **Deep SARSA** - On-policy temporal difference learning
   - Updates Q-values using actual next action taken
   - More conservative, safer exploration

2. **DQN (Deep Q-Network)** - Foundation value-based approach
   - Experience replay for sample efficiency
   - Target network for stability
   - Off-policy learning

3. **Double DQN** - Addresses Q-value overestimation bias
   - Decouples action selection from value estimation
   - More accurate Q-value approximation
   - Improved stability over vanilla DQN

4. **Multi-Step DQN** - Balances bias-variance tradeoff
   - N-step returns for faster credit assignment
   - Better sample efficiency
   - Smoother learning curves

5. **Dueling Double DQN** - Separates state value and advantage functions
   - Independent value and advantage streams
   - Better generalization across actions
   - State-of-the-art value-based performance

### Policy-Based Methods

6. **A2C (Advantage Actor-Critic)** - Policy gradient with variance reduction
   - Actor network outputs action probabilities
   - Critic network estimates state values
   - Advantage function reduces variance
   - On-policy learning with better exploration

---

## Tech Stack

**Language:** Python 3.x  
**Deep Learning:** PyTorch  
**Simulation:** SUMO (Simulation of Urban MObility) + TraCI  
**Computer Vision:** OpenCV (for bonus features)  
**Visualization:** Matplotlib  
**Computation:** NumPy

### Architecture

Modular design with clear separation between RL algorithms and environment:

```
SUMO Traffic Simulator (TraCI API)
      ↓
Custom Environment (envs/)
  ├── State: [lane counts, halting vehicles, phase]
  ├── Actions: 4 discrete phase combinations
  └── Reward: -1 × total waiting time
      ↓
RL Agents (DQN/DDQN/DDDQN/MSDQN/SARSA/A2C)
  ├── Neural Networks (PyTorch)
  ├── Experience Replay Buffers
  ├── Target Networks
  └── Epsilon-Greedy Exploration
      ↓
Training Loop (proiect.ipynb)
  ├── Episode rollouts
  ├── Gradient updates
  ├── Metrics logging
  └── Model checkpointing
      ↓
[Trained Models + Learning Curves]
```

**Key Components:**

- **State Space:** Vector containing vehicle counts per lane, number of halting vehicles, and current traffic light phase
- **Action Space:** 4 discrete actions representing different phase combination patterns
- **Reward Function:** Negative total waiting time (agent minimizes congestion)
- **Episode Length:** Fixed simulation steps with realistic traffic flow
- **Neural Networks:** MLP architectures (2-3 hidden layers, 128-256 units, ReLU activation)
- **Optimization:** Adam optimizer with learning rate ~1e-3
- **Exploration:** Epsilon-greedy with decay (typically 1.0 → 0.01 over training)

---

## Project Structure

```
Traffic Signal Control with Deep Reinforcement Learning/
├── envs/                    # SUMO network configurations (.net.xml, .rou.xml)
├── dqn/                     # Trained DQN models (.pth checkpoints)
├── ddqn/                    # Trained Double DQN models
├── dddqn/                   # Trained Dueling Double DQN models
├── msdqn/                   # Trained Multi-Step DQN models
├── sarsa/                   # Trained Deep SARSA models
├── a2c/                     # Trained A2C models (actor + critic)
├── proiect.ipynb            # Main implementation notebook (training + eval)
├── DoubleDQN.pth            # Best performing model checkpoint
├── i1.jpg, i2.jpg, ...      # Example intersection images (CV module)
├── Documentatie_RL.pdf      # Project documentation (Romanian)
└── README.md                # This file
```

---

## Getting Started

**Prerequisites:**
- Python 3.x
- SUMO traffic simulator (with TraCI support)

**Install SUMO:**
```bash
# Ubuntu/Debian
sudo apt-get install sumo sumo-tools sumo-doc

# Windows - Download from: https://www.eclipse.org/sumo/

# macOS
brew install sumo
```

**Install Python dependencies:**
```bash
pip install torch numpy matplotlib opencv-python
pip install traci  # SUMO Python interface
```

**Configure SUMO path:**
Set the `SUMO_HOME` environment variable:
```bash
# Linux/macOS
export SUMO_HOME="/usr/share/sumo"

# Windows (adjust path as needed)
set SUMO_HOME=C:\Program Files\SUMO
```

**Run the training:**
```bash
jupyter notebook proiect.ipynb
```

Or execute cells programmatically to train a specific algorithm.

---

## How It Works

**Training Pipeline:**

1. **Environment Setup**
   - Load SUMO network configuration (road layout, intersections)
   - Initialize traffic demand (vehicle generation rates)
   - Define state/action spaces

2. **Agent Initialization**
   - Create neural network (Q-network or Actor-Critic)
   - Initialize experience replay buffer
   - Set hyperparameters (learning rate, discount factor, epsilon)

3. **Episode Loop**
   - Reset environment, get initial state
   - For each timestep:
     - Agent selects action (epsilon-greedy)
     - Environment executes action, returns next state and reward
     - Store transition in replay buffer
     - Sample mini-batch and perform gradient update
   - Log episode return and metrics

4. **Evaluation**
   - Run trained agents on test scenarios
   - Compare performance across algorithms
   - Plot learning curves (episode return vs. timesteps)

5. **Model Saving**
   - Checkpoint best-performing models
   - Save to algorithm-specific folders

**State Representation:**
- Lane occupancy counts (vehicles per lane)
- Number of halting vehicles (waiting at red light)
- Current traffic signal phase (encoded)

**Reward Design:**
Negative waiting time encourages the agent to minimize congestion. Lower (more negative) rewards indicate poor performance.

---

## Results

Each algorithm is trained across multiple random seeds to ensure statistical robustness. Key findings:

- **Dueling Double DQN:** Best overall performance, fastest convergence
- **Double DQN:** Strong baseline, stable learning
- **Multi-Step DQN:** Better sample efficiency, slightly higher variance
- **A2C:** Good exploration, competitive with value-based methods
- **Deep SARSA:** More conservative, slower but stable
- **DQN:** Baseline reference, prone to overestimation bias

Performance metrics include:
- Average episode return (cumulative reward)
- Convergence time (episodes to reach threshold)
- Learning curve stability (variance across seeds)
- Generalization to unseen traffic patterns

Visualizations in `proiect.ipynb` show comparative learning curves and final performance distributions.

---

## Bonus: Computer Vision Module

The project includes an experimental module that generates SUMO networks from intersection images using computer vision:

1. Load intersection image (aerial/overhead view)
2. Detect road lanes and intersections (OpenCV edge detection)
3. Extract geometric features (angles, lane counts)
4. Generate SUMO `.net.xml` configuration automatically

This enables rapid prototyping of new traffic scenarios without manual network design.

---

## What's Next

Potential improvements:
- Multi-agent RL (coordinate multiple intersections)
- Real-world deployment (hardware integration)
- Transfer learning (pretrain on diverse scenarios)
- Safety constraints (avoid emergency vehicle delays)
- Pedestrian and cyclist modeling
- Integration with Google Maps traffic data
- Web dashboard for live visualization

---

## Academic Context

This project was developed as part of a Reinforcement Learning course, demonstrating practical applications of deep RL to intelligent transportation systems. The implementation follows best practices from seminal papers:

- Mnih et al. (2015) - DQN
- Van Hasselt et al. (2016) - Double DQN
- Wang et al. (2016) - Dueling DQN
- Mnih et al. (2016) - A3C/A2C

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.
