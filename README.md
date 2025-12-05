# Scrabble SYSC3110 Group Project  
# Group 2

## Members
- **Mohamed Cherif Bah**  
- **Handa, Jesse**  
- **Olotu, Hamson**  
- **Bian, James**

---

# Milestone 4 Overview

Milestone 4 extends the Scrabble game by adding the following required features:

1. Custom Board Layouts loaded from XML files  
2. Full multi-level Undo and Redo support  
3. Save and Load Game functionality using Java serialization  
4. Updated UML Class Diagram, Sequence Diagrams, and Data Structure documentation  
5. JUnit tests for Undo/Redo and Save/Load behavior  

Only **Mohamed Cherif Bah** and **Jesse Handa** contributed to Milestone 4.

---

# New Features in Milestone 4

## 1. Custom Board Layouts (XML)

Premium layouts are no longer hard-coded. They are loaded from XML files found in the `boards/` directory.

### New Classes
- `BoardLayout`
- `BoardLayoutLoader`

### How it Works
- At startup, the program scans the `boards/` directory for `.xml` files.
- If multiple layouts exist, the user is prompted to select one.
- If no layouts are found, a warning is shown and the standard layout is used.
- The selected layout is passed to the `Board` constructor.

### How to Test
1. Add XML layout files inside `/boards`.
2. Launch the application.
3. A dialog appears to choose a board layout.
4. Select a layout and observe the premium colors rendered on the GUI.

---

## 2. Undo / Redo System

The game now supports multi-step Undo and Redo across turns.

### New Classes
- `GameState`  
- `UndoRedoManager`

### Overview
- `GameState` contains a deep snapshot of the full `Game` model.
- `UndoRedoManager` maintains undo and redo stacks of `GameState` objects.
- After each completed turn, a snapshot is pushed onto the undo stack.
- Undo restores the previous game snapshot, and redo reapplies undone states.
- The view enables or disables Undo/Redo buttons based on stack contents.

### How to Test
1. Play several valid moves.
2. Press Undo to revert moves one by one.
3. Press Redo to reapply reverted moves.
4. Undo until no more states are available; Undo should disable.
5. After undoing, play a new move; redo history should clear.

---

## 3. Save and Load Game (Serialization)

The entire `Game` object graph is now serializable, allowing saving and restoring full game state.

### Modified Components
- Added `saveGameToFile(File)` and `loadGameFromFile(File)` in `GameController`.
- Added menu options in `GameView`:
  - Game → Save Game
  - Game → Load Game

### How to Save
1. Open the menu: `Game → Save Game`.
2. Select a file destination.
3. A `.ser` file will be written.

### How to Load
1. Open the menu: `Game → Load Game`.
2. Select a previously saved `.ser` file.
3. Board state, players, racks, tile bag, and turn order are restored.
4. Undo/Redo stacks are cleared and re-initialized with the loaded state.

### How to Test
1. Play several moves.
2. Save a game.
3. Close and reopen the application.
4. Load the saved game.
5. Confirm that:
   - The board contains the same tiles.
   - Player scores and racks are identical.
   - Tile bag size is identical.
   - Current player turn is restored correctly.

---

# UML and Sequence Diagrams

## UML Class Diagram (Milestone 4)

### Diagram Changes from Milestone 3
- Added `BoardLayout` and `BoardLayoutLoader` to support XML-driven board configurations.
- Added `UndoRedoManager` and `GameState` to implement the multi-level undo/redo mechanism.
- `GameController` updated with the following methods:
  - `handleUndo()`
  - `handleRedo()`
  - `saveGameToFile(File)`
  - `loadGameFromFile(File)`
  - `showSaveGameDialog()`
  - `showLoadGameDialog()`
- Added composition relationships:
  - `GameController` owns an `UndoRedoManager`.
  - `UndoRedoManager` owns multiple `GameState` snapshots.
  - `GameState` contains its own copy of `Game`.
- Updated `Board` constructor to accept a `BoardLayout`.

These changes were required to support all new M4 features while keeping MVC separation intact.

---

## Sequence Diagrams

Two sequence diagrams were added for Milestone 4, as required:

### A. Undoing a Move
Illustrates:
- User clicking Undo in the GUI.
- View delegating to controller.
- Controller retrieving a snapshot from `UndoRedoManager`.
- Restoring the game state.
- Rendering the restored state on the view.

### B. Loading a Saved Game
Illustrates:
- User selecting Load Game from the menu.
- Controller opening a file chooser.
- Deserializing a `Game` object.
- Resetting Undo/Redo history.
- Rendering the loaded game.

Both diagrams are included as `.puml` files.

---

# JUnit Tests (Milestone 4)

Two new test classes were created:

### `UndoRedoManagerTest`
- Verifies stack operations.
- Ensures undo restores previous GameState.
- Ensures redo re-applies undone states.
- Ensures redo stack clears when new moves occur after undo.

### `SerializationTest`
- Saves a game with tiles placed.
- Loads the game.
- Confirms that:
  - The board is restored correctly.
  - Player score and rack match.
  - Tile bag size matches.
  - Current player index matches.

---

# How to Run the Program

## Running Using JAR

### JAR:
```bash
java -jar ScrabbleGame.jar
```

### IDE (IntelliJ):

## Running in IntelliJ IDEA
1. Open the project.
2. Run `Main`.
3. The GUI will launch automatically.

---

# Team Contributions for Milestone 4

## Mohamed Cherif Bah
- Implemented the Undo/Redo system.
- Designed and implemented `GameState` and `UndoRedoManager`.
- Integrated Undo/Redo into `GameController`.
- Added serialization logic for Save/Load.
- Implemented JUnit tests for M4.
- Updated UML diagrams, sequence diagrams, and documentation.

## Jesse Handa
- Implemented XML-based custom board layout functionality.
- Built `BoardLayout` and `BoardLayoutLoader`.
- Integrated board selection dialog into application startup.
- Added Save/Load GUI menu options.
- Enhanced `GameView` to support new features.
- Wrote serialization and layout-loading tests.
- Updated UML diagrams and prepared Milestone 4 documentation.

---

# Known Issues and Limitations
- XML files must follow expected board layout schema.
- Undo/Redo history is capped to prevent memory growth.
- Save/Load requires matching Java versions due to serialization.
- AI logic remains intentionally simple.

---

# End of Milestone 4 Documentation

