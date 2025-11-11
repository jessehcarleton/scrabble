# scrabble
Scrabble group project built by Mohamed Cherif Bah, James Bian, Hamson Olotu, and Jesse Handa.

# Scrabble — Milestone 2 (README)

# Overview
In this milestone we implemented a GUI, MVC-structured Scrabble. The app runs in a JFrame, supports 2–4 players, displays the board and current player rack, and lets players place words, swap tiles, or pass via mouse/GUI controls. This README:
- explains the rest of the deliverables included with this submission
- lists team contributions for the current milestone, and addresses issues


# Deliverables

Source Code (Java)

- Model
  - Board.java – 15×15 grid model; validates placement and updates board state.
  - Game.java – game state + turn orchestration (no Swing); commands: place/swap/pass; scoring updates.
  - Player.java – player name, rack (≤7), score; rack operations (find/remove/add).
  - TileBag.java – standard distribution; random draw/return.
  - Tile.java – letter + points.
  - Dictionary.java – word validation via uppercase set (bundled list or URL).

- View
  - GameView.java – main Swing window (JFrame) rendering the board, rack, scores/status, and controls.

- Controller
  - GameController.java – wires UI events to model calls (place/swap/pass); refreshes the view after model changes.

- Main
  - Main.java – entry point; builds Dictionary, Game, GameView, GameController, and starts the UI.

- UML Diagrams (docs/uml/) — class diagram (MVC with signatures) and sequence diagrams (start game, place word, swap tiles, pass turn).

- JUnit Tests (test/) — model smoke tests (placement checks, rack/bag behavior, dictionary calls, basic scoring).

- Executable scrabble-m2.jar — runnable GUI build.

- This README.


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

