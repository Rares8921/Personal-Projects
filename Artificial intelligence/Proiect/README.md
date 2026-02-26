# Traffic Signal Control with Deep Reinforcement Learning

A comprehensive implementation of multiple deep reinforcement learning algorithms for intelligent traffic signal control using SUMO (Simulation of Urban MObility) simulator.

## Overview

This project explores and compares various DRL approaches to optimize traffic light timing at intersections, aiming to minimize vehicle waiting times and improve traffic flow. The implementation includes both value-based and policy-based methods, demonstrating the evolution of modern reinforcement learning techniques applied to real-world traffic management challenges.

## Implemented Algorithms

The project features implementations of six state-of-the-art reinforcement learning algorithms:

- **Deep SARSA** - On-policy temporal difference learning
- **DQN** (Deep Q-Network) - Foundation value-based approach
- **Double DQN** - Addresses overestimation bias in Q-learning
- **Multi-Step DQN** - Balances bias-variance tradeoff with n-step returns
- **Dueling Double DQN** - Separates state value and advantage functions
- **A2C** (Advantage Actor-Critic) - Policy gradient method with variance reduction

## Environment

The custom SUMO environment features:
- **State Space**: Lane-based vehicle counts, halting vehicles, and current traffic light phase
- **Action Space**: 4 discrete actions corresponding to different phase combinations
- **Reward Function**: Negative waiting time to minimize traffic congestion
- **Simulation**: Realistic traffic dynamics with configurable road networks

## Technical Features

- PyTorch-based neural network implementations
- Experience replay buffers for sample efficiency
- Target networks with soft updates
- Epsilon-greedy exploration strategies
- Integration with SUMO traffic simulator via TraCI

## Bonus: Computer Vision Integration

An additional module demonstrates automated traffic network generation from intersection images using computer vision techniques, enabling rapid prototyping of new traffic scenarios.

## Requirements

- Python 3.x
- PyTorch
- SUMO (with TraCI)
- NumPy, Matplotlib
- OpenCV (for CV module)

## Project Structure

```
├── envs/              # SUMO network configurations
├── dqn/               # Trained DQN models
├── ddqn/              # Trained Double DQN models
├── dddqn/             # Trained Dueling DQN models
├── msdqn/             # Trained Multi-Step DQN models
├── sarsa/             # Trained Deep SARSA models
├── a2c/               # Trained A2C models
└── proiect.ipynb      # Main implementation notebook
```

## Results

Each algorithm is trained and evaluated across multiple random seeds to ensure statistical significance. Performance metrics include average episode returns and convergence rates, with visualization of learning curves for comparative analysis.

---

*This project was developed as part of a Reinforcement Learning course, demonstrating practical applications of DRL to intelligent transportation systems.*
