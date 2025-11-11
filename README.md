# Scrabble SYSC3110 Group project
# Group 2
# Members:
- **Mohamed Cherif Bah**
- **Handa, Jesse** 
- **Olotu, Hamson** 
- **Bian, James**

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

# How to run
JAR:
`java -jar scrabble-m2.jar`

IDE (IntelliJ): run Main (which creates Game, GameView, and GameController)
Dictionary: ensure URL will be able to extract valid wordlist; otherwise use a local word list.

# Team Contributions

Milestone 2 (current)

Mohamed Cherif Bah — Setup the core game rules: parsing coordinates and direction, checking words against dictionary, conflict checks, and made sure placed tiles can’t be overwritten. Setup place/swap/pass in the controller and added the error messages. Wrote a few JUnit tests for board/validation.

James Bian — Built the GUI parts in GameView: Created the 15×15 board, showed the current player’s rack, and refreshed scores/next player after each turn. Cleaned up mouse/keyboard input and the listener code so moves update the view correctly. Added small tests for view↔model sync.

Hamson Olotu — Finished TileBag and Player flow: Tile distribution, random draw on success, swap tiles back into bag, pass turn logic, and kept racks at max 7. Set up 2–4 player support. Wrote tests for bag/rack/turn flow and basic scoring.

Jesse Handa — Made the UML class diagram and sequence diagrams, wrote the data structures explanation, the README and how to run, listed known issues/assumptions, organized the repo (commit messages/branches), and put together the final submission package.

Milestone 1

Mohamed Cherif Bah — Implemented Board (simulate, place, center/connect rules), text rendering; authored sequence diagram. Implemented Player (rack logic, draw/exchange/pass, scoring), rack visibility; contributed data-structures note.

Jesse Handa — Implemented TilesBag (distribution, draw/return, values), integrated Dictionary loading & case-insensitive validation; prepared README and run instructions. Created UML class diagram; packaged scrabble-m1.jar.

# Known Issues / Assumptions

- Dictionary dependency: if using a URL, needs network otherwise requires local wordlist.
