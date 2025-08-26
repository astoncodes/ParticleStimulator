# Particle Simulator

The **Particle Simulator** is a Java-based simulation that models the behavior of different particle types and their interactions within a grid. Users can interact with the simulation by selecting particle types and placing them on the grid, observing realistic particle behaviors such as gravity, spreading, floating, and reactions.

## Features

- **Interactive Simulation**: Users can place different particle types on a grid using a simple UI.
- **Realistic Behaviors**:
  - Sand falls and slides.
  - Water flows and spreads.
  - Oil floats above water.
  - Fire spreads, interacts with sand to form glass, and dissipates over time.
  - Acid dissolves metal and spreads.
  - Steam rises and dissipates.
- **Dynamic Grid Updates**: Particle interactions are updated in real-time, creating an engaging visual experience.

## Particle Types

The simulation supports the following particle types:

| Particle Type | Behavior                                                                 |
|---------------|-------------------------------------------------------------------------|
| **Empty**     | Represents empty space on the grid.                                     |
| **Metal**     | Static and unaffected by other particles.                              |
| **Sand**      | Falls due to gravity, can slide left or right, and turns into glass when in contact with fire. |
| **Water**     | Flows downward or spreads left and right.                              |
| **Oil**       | Floats above water and spreads.                                        |
| **Acid**      | Dissolves metal and moves randomly.                                   |
| **Fire**      | Spreads, dissipates over time, and interacts with other particles.     |
| **Steam**     | Rises and dissipates.                                                 |
| **Glass**     | Forms when sand is exposed to fire.                                    |
