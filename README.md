# Pacman AI & Custom Game Engine Project

This project presents a dual-layered approach to the classic Pacman game, developed as part of a Java programming assignment. It includes a high-performance AI algorithm for a competitive environment and a custom-built game engine.

---

## 📺 AI Victory Demo
See the AI in action! Below is a demonstration of the `Ex3Algo` successfully navigating the lecturer's environment and winning the game.

<p align="center">
  

https://github.com/user-attachments/assets/867a357b-bf54-4162-a6ac-c908d7cff755


</p>

*If the animation doesn't appear, you can find the source video in the `/demo` folder.*

---

## 🧠 Part 1: The Competitive AI (`Ex3Algo`)
This component was designed to interface with the lecturer's server environment. The goal was to achieve maximum points while ensuring 100% survival against multiple ghosts.

### Advanced Logic Features:
- **BFS Safe-Space Analysis**: Before every move, the AI runs a Breadth-First Search to calculate the "Safe Space" reachable from a potential position. If a move leads to a "dead-end" where a ghost can trap Pacman, the score for that move is heavily penalized.
- **Dynamic Danger Mapping**: Uses a multi-layered distance matrix. It constantly tracks all ghosts and differentiates between "Danger" (normal ghosts) and "Target" (eatable ghosts) modes.
- **Hysteresis (Persistence) Logic**: Includes a directional weight to prevent "shaking" behavior, ensuring Pacman follows a consistent path toward food unless a high-priority threat appears.
- **Future Potential Heuristic**: Evaluates not just the next cell, but the density of food in the surrounding area to choose the most efficient path for clearing the board.



---

## 🕹️ Part 2: Custom Game Engine (`mygame`)
A standalone implementation of Pacman, including a custom rendering engine and logic handler.

### Key Technical Details:
- **`PacmanAI`**: A dedicated logic class for the standalone engine. It features wrap-around (cyclic) board support and real-time state switching (Normal vs. Super Mode).
- **Custom Map Loader**: A robust utility that parses `.txt` files into a 2D logical grid.
- **Rendering Engine**: Built on the `StdDraw` library, using double-buffering to ensure smooth, flicker-free graphics at 50 FPS.
- **State Management**: Handles complex interactions, including Super Food consumption, ghost vulnerability timers, and collision physics.

---

## 🎮 Map Configuration (map1.txt)
The game utilizes a grid-based coordinate system. The current stable build is optimized for a **10x10 square map**:
- `1`: **Wall** (Impassable)
- `2`: **Food** (Regular points)
- `3`: **Super Food** (Activates Super Mode)
- `0`: **Empty Path**

---

## 🛠 Setup & Execution

### Prerequisites
- Java JDK 8 or higher.
- IntelliJ IDEA.

### Running the Project
1. **Open** the project in IntelliJ.
2. **Set Source Root**: Right-click the `src` folder -> *Mark Directory as* -> *Sources Root*.
3. **Verify Paths**: Ensure the map path in `Ex3MainCustom.java` is set to:
   ```java
   MapLoader.loadMap("src/mygame/map1.txt");
