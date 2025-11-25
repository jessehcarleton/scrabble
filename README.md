# Scrabble SYSC3110 Group Project  
# Group 2

## Members
- **Mohamed Cherif Bah**  
- **Handa, Jesse**  
- **Olotu, Hamson**  
- **Bian, James**

---

# Overview

This version represents **Milestone 3** of the Scrabble project.  
It builds directly on the MVC GUI from Milestone 2 and adds three major features:

1. **Blank tiles** (wildcards)  
2. **Premium board squares** (DL, TL, DW, TW)  
3. **AI players** (automatically play legal moves)

The underlying model, controller, and GUI have been extended to support these new mechanics without breaking any Milestone 2 behavior.

---

# Deliverables

## Source Code (Java)

### Core Game Logic (Updated for M3)

- **Board.java**  
  Implements the full Scrabble premium square layout and updates scoring so premiums apply only to *newly placed* tiles.  
  Adds support for blanks when validating and placing words.

- **Game.java**  
  Manages both human and AI turns. Handles AI decision-making, passing, rack refills, and game-over conditions.

- **Player.java**  
  Stores rack and score. Updated to support blank tile usage and scoring.

- **TileBag.java**  
  Now includes the standard distribution of blank tiles.

- **Tile.java**  
  Supports wildcard tiles: always worth 0 points and can represent any letter when placed.

- **Dictionary.java**  
  Word lookup and validation (same as M2), with a simplified constructor for tests.

### MVC Components

- **GameController.java**  
  Integrates the new AI behavior and blank-tile handling.  
  Coordinates all player actions: placing, swapping, passing, and triggering an AI move.

- **GameView.java**  
  Displays premium square colors and prompts players to assign letters when placing blanks.  
  Shows AI turns and updates the board and rack accordingly.

- **Main.java**  
  Launches the application and wires together the model, controller, and view.  
  Can be run headless for test execution.

### Testing (JUnit)

Milestone 2 tests remain valid.

New Milestone 3 tests include:

- **BlankTileTest** — verifies wildcard behavior and scoring.  
- **PremiumSquaresTest** — ensures correct scoring on DL/TL/DW/TW and that premiums are ignored for reused tiles.  
- **AiPlayerTest** — checks that AI players take legal moves and correctly advance the turn.

### Diagrams

- Updated **UML class diagram** to reflect blanks, premium squares, and AI logic.  
- Two updated **sequence diagrams**:
  1. Human placing a word using a blank tile.  
  2. AI choosing and performing a move.

---

# What’s New in Milestone 3 (Summary)

- Added **blank tiles** and integrated them into scoring and placement.  
- Implemented the **premium square layout** and scoring rules.  
- Added **AI players** that select and play legal words.  
- Expanded JUnit tests and updated documentation.  
- MVC components extended to support new mechanics without rewriting existing code.

---

# How to Run

### JAR:
```bash
java -jar ScrabbleGame.jar
```

### IDE (IntelliJ):
Run `Main`.  
This initializes the dictionary, model, controller, and GUI.

---

# Team Contributions

Milestone 3 (current)

- **Mohamed Cherif Bah** — Implemented premium square logic, updated scoring, integrated blanks into Board and Player logic, added tests for premiums and blanks, and updated Board/Player documentation.

- **Jesse Handa** — Implemented AI player turn logic, rack/move generation for AI, updated controller and GUI for AI flow, wrote AI tests, updated UML diagrams, sequence diagrams, and prepared the final M3 writeup/README.

- **Hamson Olotu** — Assisted with TileBag updates and blank tile distribution. Verified scoring and turn sequence stability with multiple AI players. Helped check M2 regression tests.

- **James Bian** — Updated GUI visuals for premium squares and blank tile prompts. Ensured layout remained consistent with M2. Helped integrate AI move visualization in the view.

Milestone 2

(See Milestone 2 README — unchanged.)

Milestone 1

(See Milestone 1 README — unchanged.)

---

# Known Issues / Assumptions

- AI strategy is intentionally simple: it selects the first valid playable word, not the highest-scoring one.  
- Dictionary lookup requires either network access (for the online list) or a local fallback list.  
- GUI may briefly freeze during AI moves on slower machines (due to word-search loops).

