Scrabble SYSC3110 Group project
Group 2
Members: Mohamed Cherif Bah, Handa, Jesse, Olotu, Hamson, Bian, James

Overview
This version represents Milestone 3 of the Scrabble project. It builds on the Swing-based MVC app by adding blank tiles, premium squares, startup Human/AI player selection, and updated documentation/tests.

Deliverables
Source Code (Java)
Board.java – Adds blank tile assignment, premium-square layout (DL/TL/DW/TW), and scoring that applies multipliers only to newly placed tiles; still enforces center-start and connectivity.
Game.java – Stores AI flags on players, tracks pass/game-over conditions, and manages refills after placements.
Player.java – Blank-aware rack helpers (wildcard checks, blank lookup).
Tile.java – Supports a blank flag and runtime letter assignment while keeping score at 0.
TileBag.java – Includes the two standard blank tiles.
Dictionary.java – Word validation (online list with offline fallback).
GameController.java – Wires view intents to model (place/swap/pass) and refreshes the UI after each turn.
GameView.java – Prompts for a blank’s chosen letter during placement preview, colors premium squares, and supports headless-friendly instantiation for tests.
Main.java – Prompts for 2–4 players and lets the user choose Human or AI per seat.
Tests (JUnit)
Coverage for blank tiles (assignment remains 0 points), premium-square scoring, rack refills, dictionary validity, and board rules (center start, bounds/conflicts, connectivity).
AI hooks are present (player flag), but AI move-selection logic is still pending; no AI-behavior tests yet.
Modeling & Docs
UML Class Diagram – Updated with blank/premium attributes, AI flag on Player/Game, and view additions.
Sequence Diagrams – Provided for:
Human places a word containing a blank tile.
AI player selects a move and takes their turn.
Data Structures & AI Explanation – Notes on premium-grid storage and blank handling; AI strategy currently stubbed (flag only, no move generator yet).
How to run
JAR: java -jar scrabble-m3.jar
IDE (IntelliJ): run Main (loads Dictionary, creates Game, GameView, GameController).
Dictionary: needs network to fetch the word list; otherwise point to a local word list for offline use.

Team Contributions
Milestone 3 (current)

Mohamed Cherif Bah — Board premium grid and scoring, blank tile handling/assignment, GameView premium coloring and blank-letter prompt, headless-friendly GUI tweaks, sequencing/UML updates, and commit packaging.
Jesse Handa — Game AI-flag plumbing, pass/game-over tracking, Tile/TileBag blank support, JUnit additions for blanks/premiums/board rules, README/UML/sequence diagrams, and final submission package.
Hamson Olotu — (no new Milestone 3 commits; listed per course requirement).
James Bian — (no new Milestone 3 commits; listed per course requirement).
Milestone 2

Mohamed Cherif Bah — Core board rules (parse/place, conflicts, dictionary checks), controller place/swap/pass flows, error handling, and board validation tests.
James Bian — Swing GameView (board grid, rack display, score/turn refresh), listener wiring for commit/swap/pass, and view↔model sync checks.
Hamson Olotu — TileBag/Player flow (distribution, draws/swaps, rack size enforcement), 2–4 player setup, and bag/rack/turn/scoring tests.
Jesse Handa — UML and sequence diagrams, data-structure write-up, README/run docs, repo organization and packaging.
Milestone 1

Mohamed Cherif Bah — Board (simulate/place, center/connect rules), text rendering, sequence diagram; Player (rack, draw/exchange/pass, scoring), data-structures note.
Jesse Handa — TileBag (distribution, draw/return, values), Dictionary loading/validation, README/run docs, UML class diagram, packaging scrabble-m1.jar.
Known Issues / Assumptions
AI move logic is not yet implemented; AI players are flagged but make no autonomous moves.
Dictionary still depends on network if a remote list is used; use a local list when offline.
Premium/blank rules are modeled and scored in Board, but cross-word scoring beyond the main word is not implemented.
Swing UI requires a display; headless mode is intended for tests only.
Changed to Agent mode
actually first, because the unittests werent changed yet from milestone 2. Can you without altering the current tests from milestone 2, add the appropriate tests for the new functionalities of milestone 3 which were introduced, if you could put it in a sub folder called m3_tests please or something similar


Added new Milestone 3 test suite without touching existing tests:

src/Tests/m3_tests/BlankTilePlacementTest.java – ensures blanks can be assigned a letter, stay 0 points, and score correctly on the center double-word square.
src/Tests/m3_tests/PremiumScoringTest.java – validates combined double-letter + double-word scoring on first move and triple-word scoring when extending an existing anchor tile.
src/Tests/m3_tests/AiPlayerFlagTest.java – verifies AI players are created with the AI flag using the test Dictionary constructor.
No tests were run here (existing suite still hits the networked Dictionary); consider running your preferred JUnit runner in a network-enabled setup.

3 files changed
+97
-0


AiPlayerFlagTest.java
+20
-0

BlankTilePlacementTest.java
+30
-0

PremiumScoringTest.java
+47
-0

Changed to Chat mode
can you now provide me the new completed milestone 3 readme updated from milestone 2 please


Here’s the Milestone 3 README content, ready to drop into README.md:

Scrabble SYSC3110 Group project
Group 2
Members: Mohamed Cherif Bah, Handa, Jesse, Olotu, Hamson, Bian, James

Overview
This version represents Milestone 3 of the Scrabble project. It builds on the Swing-based MVC app by adding blank tiles, premium squares, startup Human/AI player selection, and updated documentation/tests.

Deliverables
Source Code (Java)
Board.java – Adds blank tile assignment, standard premium-square layout (DL/TL/DW/TW), and scoring that applies multipliers only to newly placed tiles; still enforces center-start and connectivity.
Game.java – Stores AI flags on players, tracks pass/game-over conditions, and manages refills after placements.
Player.java – Blank-aware rack helpers (wildcard checks, blank lookup).
Tile.java – Supports a blank flag and runtime letter assignment while keeping score at 0.
TileBag.java – Includes the two standard blank tiles.
Dictionary.java – Word validation (online list with offline fallback; test ctor for seeded sets).
GameController.java – Wires view intents to model (place/swap/pass) and refreshes the UI after each turn.
GameView.java – Prompts for a blank’s chosen letter during placement preview, colors premium squares, and supports headless-friendly instantiation for tests.
Main.java – Prompts for 2–4 players and lets the user choose Human or AI per seat.
Tests (JUnit)
Legacy Milestone 2 tests remain unchanged.
New Milestone 3 tests in src/Tests/m3_tests:
BlankTilePlacementTest – Blank assignment, 0-point scoring, center DW interaction.
PremiumScoringTest – DL+DW combination scoring and TW with an existing anchor tile.
AiPlayerFlagTest – Verifies AI player flag via Game.addPlayer(name, true).
Modeling & Docs
UML Class Diagram – Updated with blank/premium attributes, AI flag on Player/Game, and view additions.
Sequence Diagrams – Included for:
Human places a word containing a blank tile.
AI player selects a move and takes their turn.
Data Structures & AI Explanation – Notes on premium-grid storage and blank handling; AI strategy currently stubbed (flag only, no move generator yet).
How to run
JAR: java -jar scrabble-m3.jar
IDE (IntelliJ): run Main (loads Dictionary, creates Game, GameView, GameController).
Dictionary: needs network to fetch the word list; otherwise point to a local word list for offline use.

Team Contributions
Milestone 3 (current)

Mohamed Cherif Bah — Board premium grid/scoring, blank tile handling/assignment, GameView premium coloring and blank-letter prompt, headless-friendly GUI tweaks, sequencing/UML updates, commit packaging.
Jesse Handa — Game AI-flag plumbing, pass/game-over tracking, Tile/TileBag blank support, JUnit additions for blanks/premiums/board rules, README/UML/sequence diagrams, and final submission package.
Hamson Olotu — (no new Milestone 3 commits; listed per course requirement).
James Bian — (no new Milestone 3 commits; listed per course requirement).
Milestone 2

Mohamed Cherif Bah — Core board rules (parse/place, conflicts, dictionary checks), controller place/swap/pass flows, error handling, board validation tests.
James Bian — Swing GameView (board grid, rack display, score/turn refresh), listener wiring for commit/swap/pass, view↔model sync checks.
Hamson Olotu — TileBag/Player flow (distribution, draws/swaps, rack size enforcement), 2–4 player setup, bag/rack/turn/scoring tests.
Jesse Handa — UML and sequence diagrams, data-structure write-up, README/run docs, repo organization and packaging.
Milestone 1

Mohamed Cherif Bah — Board (simulate/place, center/connect rules), text rendering, sequence diagram; Player (rack, draw/exchange/pass, scoring), data-structures note.
Jesse Handa — TileBag (distribution, draw/return, values), Dictionary loading/validation, README/run docs, UML class diagram, packaging scrabble-m1.jar.
Known Issues / Assumptions
AI move logic is not yet implemented; AI players are flagged but make no autonomous moves.
Dictionary still depends on network if a remote list is used; use a local list when offline.
