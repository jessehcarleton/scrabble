# Scrabble

## Overview
This version represents **Milestone 2** of the Scrabble project.  
It extends the original text-based implementation into a **fully interactive GUI application** using Java Swing and a **Model–View–Controller (MVC)** architecture.

Players can now interact directly with the board by **selecting tiles from their rack and placing them on the grid**.  
All word validation, scoring, and placement logic are handled by the model layer.

---

## Deliverables

### Source Code (Java)

#### Core Game Logic
- **Board.java** – Represents the 15×15 Scrabble board. Handles word placement, rule validation (center start, adjacency), scoring, and display.  
- **Game.java** – Manages player turns, rack refilling, and high-level game flow.  
- **Player.java** – Maintains player state (name, rack, score) and helper methods for tile usage.  
- **TileBag.java** – Initializes the full Scrabble tile set and manages random draws and returns.  
- **Tile.java** – Defines a letter tile with its character and point value.  
- **Dictionary.java** – Loads and validates words using the MIT word list, with an offline fallback set for reliability.

#### MVC Components (New for Milestone 2)
- **GameController.java** – Coordinates between the GUI and model. Handles placement, swapping, passing, and view updates.  
- **GameView.java** – Swing-based graphical interface. Allows players to:
  - Select rack tiles  
  - Click on board cells to place them  
  - Commit, swap, or pass their turn visually  
  - View current scores and tiles dynamically  

#### Supporting Files
- **Main.java** – Entry point; initializes the dictionary, model, controller, and GUI view.  
- **JUnit Tests** – Added tests for:
  - Board placement rules (center, adjacency, overlap)  
  - Scoring validation  
  - Rack refilling  
  - Word dictionary validity  
- **UML Class Diagram** – Updated to include the MVC structure.  
- **Sequence Diagram** – Simplified flow of the “Commit Move” process in the GUI.  

---

## How to Run

### Option 1 — IDE
Open the project in IntelliJ IDEA or Eclipse and run `Main.java`.  
The GUI window will launch automatically.

### Option 2 — Executable JAR
```bash
java -jar scrabble-m2.jar
