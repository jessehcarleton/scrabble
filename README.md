# scrabble
Scrabble group project built by Jesse Handa (101264747) and Mohamed Cherif Bah (101292844)
README

Scrabble — Milestone 1 (README)
Overview

This is a text-based implementation of the game Scrabble, as specified by the milestone 1 instructions. Users can play the game via the console using their keyboard. This README:

- explains the rest of the deliverables included with this submission,
- lists team contributions for the current milestone, and addresses issues (if applicable)

Deliverables

Source Code (Java)

Board.java – Represents the Scrabble board as a 15x15 grid. Handles word placement and board display.

Game.java – console UI: 2–4 players, place/swap/skip, turn loop, state printing.

Main.java - Entry point for scrabble game. Prompts for player names and starts the game

Player.java – Represents a player in the Scrabble game. Each player has a name, a rack of letter tiles, and a score.

TileBag.java – Represents the bag of tiles in Scrabble. Handles initialization and random drawing of tiles.

Tile.java – Represents a single letter tile in Scrabble. Each tile has a letter and a point value.

Dictionary.java – Represents a dictionary of valid Scrabble words. Loads words from a web-hosted text file and checks word validity.

UML Class Diagram (docs/uml.*) — classes and relationships (Game, Board, Player, TilesBag, Dictionary).

JUnit Tests (test/) — smoke tests for board rules, dictionary, and basic scoring.

Executable scrabble-m1.jar — runnable build of the console app.

This README.

How to run:

JAR: java -jar scrabble-m1.jar

IDE (IntelliJ): run main

Dictionary: ensure URL will be able to extract valid wordlist.

Team Contributions

Replace the placeholders below with actual names and concrete tasks.

Milestone 1 (current)

Mohamed Cherif Bah (101292844): Implemented Board (simulate, place, center/connect rules), text rendering; authored sequence diagram. Implemented Player (rack logic, draw/exchange/pass, scoring), rack visibility; contributed data-structures note.

Jesse Handa (101264747): Implemented TilesBag (distribution, draw/return, values), integrated Dictionary loading & case-insensitive validation; prepared README and run instructions. Created UML class diagram; packaged scrabble-m1.jar.


Known Issues / Assumptions

Scope-limited to M1 by design: no premium squares, no blank tiles, no cross-word scoring beyond validating the main word.

Dictionary dependency: if anything goes wrong extracting the wordlist from the URL, word validation will fail; ensure the URL is present and valid.

Input mode: console prompts accept tile-by-tile placement in a single straight, contiguous line (ACROSS or DOWN). Official Scrabble notation can be added in later milestones if required.
