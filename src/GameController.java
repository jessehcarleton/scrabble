import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.ArrayDeque;
import java.util.Deque;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

/**
 * GameController acts as the mediator between the Game model and the GameView.
 * It handles user actions from the view and updates the model accordingly.
 *
 * <p><b>Milestone 2:</b></p>
 * <ul>
 *     <li>Removes double-scoring (Board is the single authority that applies score).</li>
 *     <li>Centralizes placement/swap/pass flows.</li>
 *     <li>Triggers View re-render after each state change.</li>
 * </ul>
 *
 * <p><b>Milestone 3:</b></p>
 * <ul>
 *     <li>Implements a simple AI that plays automatically on its turn.</li>
 * </ul>
 *
 * <p><b>Milestone 4 – Phase 2 (Undo/Redo):</b></p>
 * <ul>
 *     <li>Adds multi-step undo/redo support using {@link GameState} and
 *         {@link UndoRedoManager} logic (see fields and methods below).</li>
 *     <li>Undo/redo operates purely on the model (Game/Board/Player/TileBag) and
 *         then re-renders the view from that model.</li>
 * </ul>
 *
 * <p><b>Milestone 4 – Phase 3 (Save/Load):</b></p>
 * <ul>
 *     <li>Adds the ability to serialize the entire {@link Game} to disk and
 *         load it back later.</li>
 *     <li>Save/load reuses the same serialized form used by undo/redo, so the
 *         entire game state is preserved (board, players, scores, tile bag, etc.).</li>
 *     <li>The controller exposes both low-level {@code saveGameToFile}/{@code loadGameFromFile}
 *         methods and high-level dialog-based methods
 *         {@code showSaveGameDialog}/{@code showLoadGameDialog} for the GUI.</li>
 * </ul>
 */
public class GameController {

    /** The Game model containing players, board, tile bag, and dictionary. */
    private Game game;

    /** The GameView GUI responsible for rendering the game state. */
    private GameView view;

    /** Prevents nested AI recursion. */
    private boolean aiTurnInProgress = false;

    /** Manages undo/redo history for the game. */
    private final UndoRedoManager undoRedoManager = new UndoRedoManager();

    /**
     * Constructs a GameController with the given model and view.
     * Initializes the game state and sets up communication with the view.
     * Also records the initial game state for undo/redo.
     *
     * @param game the Game model instance
     * @param view the GameView GUI instance
     */
    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;

        if (view != null) {
            view.setController(this);
            view.renderGameState(game);
            // Initially nothing to undo/redo
            view.setUndoRedoEnabled(false, false);
        }

        // Record initial state as the baseline in the undo stack.
        undoRedoManager.push(GameState.from(game));

        // If the first active player is AI, let it play automatically.
        maybePlayAiTurn();
    }

    /**
     * Handles the action of placing a word on the board.
     * Validates the word via dictionary, attempts placement on the model,
     * refills the rack on success, and advances the turn.
     *
     * @param word       the word to place
     * @param row        the starting row index (0–14)
     * @param col        the starting column index (0–14)
     * @param horizontal true if placing left-to-right, false for top-to-bottom
     */
    public void handlePlaceWord(String word, int row, int col, boolean horizontal) {
        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
        Board board = game.getBoard();

        if (word == null || word.isEmpty()) {
            if (view != null) view.showError("Please enter a word.");
            return;
        }
        if (row < 0 || row > 14 || col < 0 || col > 14) {
            if (view != null) view.showError("Row/Column out of bounds.");
            return;
        }
        if (!game.getDictionary().isValid(word)) {
            if (view != null) view.showError("Invalid word! Please try again.");
            return;
        }

        boolean success = board.placeWord(word, row, col, horizontal, currentPlayer);
        if (success) {
            refillRack(currentPlayer);
            advanceTurn(); // will re-render and possibly trigger AI
        } else {
            if (view != null) {
                view.showError("Word placement failed. Check connectivity, conflicts, bounds, and first-move center.");
                view.renderGameState(game);
            }
        }
    }

    /**
     * Handles the action of swapping selected tiles from the player's rack.
     * Returns tiles to the bag, refills rack, and rotates turn.
     *
     * @param indices list of tile indices to swap from the current player's rack
     */
    public void handleSwapTiles(List<Integer> indices) {
        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
        TileBag bag = game.getTileBag();

        if (bag.remainingTiles() < indices.size()) {
            if (view != null) view.showError("Not enough tiles in bag to swap that many.");
            return;
        }
        if (indices.isEmpty()) {
            if (view != null) view.showError("No tiles selected to swap.");
            return;
        }

        // Collect tiles to swap
        List<Tile> toSwap = indices.stream()
                .distinct()
                .sorted()
                .map(i -> currentPlayer.getRack().get(i))
                .collect(Collectors.toList());

        // Return selected tiles to bag
        for (Tile t : toSwap) {
            currentPlayer.getRack().remove(t);
            bag.getTiles().add(t);
        }

        // Draw replacements up to rack size 7 or until bag is empty
        refillRack(currentPlayer);
        advanceTurn();
    }

    /**
     * Handles the action of passing the current player's turn.
     * Simply advances the turn to the next player.
     */
    public void handlePassTurn() {
        advanceTurn();
    }

    /**
     * Performs an undo operation (if possible) and refreshes the view.
     * <p>
     * Undo reverts the game to the previous recorded state, including:
     * board tiles, player racks/scores, tile bag contents, and current turn.
     */
    public void handleUndo() {
        GameState previous = undoRedoManager.undo();
        if (previous == null) {
            return;
        }
        this.game = previous.getGame();
        if (view != null) {
            view.renderGameState(game);
            updateUndoRedoButtons();
        }
    }

    /**
     * Performs a redo operation (if possible) and refreshes the view.
     * <p>
     * Redo restores a state that was previously undone.
     */
    public void handleRedo() {
        GameState next = undoRedoManager.redo();
        if (next == null) {
            return;
        }
        this.game = next.getGame();
        if (view != null) {
            view.renderGameState(game);
            updateUndoRedoButtons();
        }
    }

    /**
     * Refills the player's rack from the tile bag until it contains 7 tiles
     * or the tile bag is empty.
     *
     * @param player the player whose rack should be refilled
     */
    void refillRack(Player player) {
        TileBag bag = game.getTileBag();
        while (player.getRack().size() < 7 && !bag.isEmpty()) {
            Tile drawn = bag.drawTile();
            if (drawn == null) break;
            player.getRack().add(drawn);
        }
    }

    /**
     * Advances the turn to the next player in order, wraps around, re-renders
     * the view, records the new game state for undo/redo, and then optionally
     * lets the AI play if the next player is AI-controlled.
     */
    void advanceTurn() {
        game.nextPlayer();
        if (view != null) {
            view.renderGameState(game);
        }

        // Record new current state after the turn has fully advanced.
        undoRedoManager.push(GameState.from(game));
        updateUndoRedoButtons();

        // Potentially trigger AI move based on the new current player.
        maybePlayAiTurn();
    }

    /**
     * Returns the current Game model managed by this controller.
     *
     * @return the Game instance
     */
    public Game getGame() {
        return game;
    }

    // ---------------------------------------------------------------------
    //                          AI SUPPORT
    // ---------------------------------------------------------------------

    /** If the active player is AI-controlled, attempt to play automatically. */
    private void maybePlayAiTurn() {
        if (aiTurnInProgress) return;

        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        if (!current.isAi()) return;

        aiTurnInProgress = true;
        SwingUtilities.invokeLater(() -> {
            try {
                runAiTurn(current);
            } finally {
                aiTurnInProgress = false;
                // Chain to next AI if applicable after the move/pass.
                maybePlayAiTurn();
            }
        });
    }

    /**
     * Simple AI: try a handful of rack-valid dictionary words and place the first legal move.
     *
     * @param aiPlayer the AI-controlled player
     */
    private void runAiTurn(Player aiPlayer) {
        Board board = game.getBoard();
        List<String> candidates = pickCandidateWords(aiPlayer, 30);

        for (String word : candidates) {
            if (tryPlaceAnywhere(word, aiPlayer, board)) {
                refillRack(aiPlayer);
                advanceTurn(); // this will also push new undo state
                return;
            }
        }

        // No moves: pass turn
        handlePassTurn();
    }

    /**
     * Attempts to place the word somewhere on the board for the AI player.
     * Tries each cell as a starting point in both orientations.
     *
     * @param word     the word to place
     * @param player   the AI player
     * @param board    the board
     * @return true if a placement succeeded, false otherwise
     */
    private boolean tryPlaceAnywhere(String word, Player player, Board board) {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                if (board.placeWord(word, row, col, true, player)) {
                    return true;
                }
                if (board.placeWord(word, row, col, false, player)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Picks rack-buildable dictionary words, preferring higher letter sums/lengths.
     *
     * @param player the player whose rack is used as constraint
     * @param limit  maximum number of candidate words to return
     * @return list of candidate dictionary words
     */
    private List<String> pickCandidateWords(Player player, int limit) {
        int blanks = (int) player.getRack().stream().filter(Tile::isBlank).count();
        return game.getDictionary().getWords().stream()
                .filter(w -> w.length() <= player.getRack().size() + blanks && w.length() >= 2)
                .filter(player::canFormWord)
                .sorted(Comparator
                        .comparingInt(String::length)
                        .thenComparingInt(this::roughLetterScore)
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Rough letter scoring heuristic used by the AI to sort candidate words.
     *
     * @param word candidate word
     * @return approximate score
     */
    private int roughLetterScore(String word) {
        int score = 0;
        for (char c : word.toCharArray()) {
            switch (Character.toUpperCase(c)) {
                case 'D': case 'G': score += 2; break;
                case 'B': case 'C': case 'M': case 'P': score += 3; break;
                case 'F': case 'H': case 'V': case 'W': case 'Y': score += 4; break;
                case 'K': score += 5; break;
                case 'J': case 'X': score += 8; break;
                case 'Q': case 'Z': score += 10; break;
                default: score += 1; break;
            }
        }
        return score;
    }

    // ---------------------------------------------------------------------
    //                        UNDO/REDO HELPER
    // ---------------------------------------------------------------------

    /**
     * Updates the enabled/disabled state of the Undo and Redo buttons
     * based on the history currently available in {@link UndoRedoManager}.
     */
    private void updateUndoRedoButtons() {
        if (view != null) {
            view.setUndoRedoEnabled(undoRedoManager.canUndo(), undoRedoManager.canRedo());
        }
    }

    // ---------------------------------------------------------------------
    //                         SAVE / LOAD (PHASE 3)
    // ---------------------------------------------------------------------

    /**
     * Saves the entire {@link Game} object to the specified file using Java serialization.
     * <p>
     * The saved file will contain the full game state (board tiles, players, scores,
     * tile bag contents, current turn, dictionary, etc.).
     *
     * @param file the destination file chosen by the user
     */
    public void saveGameToFile(File file) {
        if (file == null) {
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(game);
            JOptionPane.showMessageDialog(
                    null,
                    "Game saved successfully to:\n" + file.getAbsolutePath(),
                    "Save Game",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to save game:\n" + e.getMessage(),
                    "Save Game Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Loads a {@link Game} instance from the specified file using Java serialization.
     * <p>
     * On success:
     * <ul>
     *     <li>{@link #game} is replaced with the loaded instance.</li>
     *     <li>Undo/redo history is cleared and re-seeded with the loaded state.</li>
     *     <li>The view is re-rendered from the loaded model.</li>
     *     <li>If the loaded current player is AI, an AI move may be triggered.</li>
     * </ul>
     *
     * @param file the source file to load from
     */
    public void loadGameFromFile(File file) {
        if (file == null) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (!(obj instanceof Game)) {
                JOptionPane.showMessageDialog(
                        null,
                        "The selected file does not contain a valid saved game.",
                        "Load Game Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            this.game = (Game) obj;

            // Reset undo/redo history to the just-loaded state
            undoRedoManager.clear();
            undoRedoManager.push(GameState.from(game));

            if (view != null) {
                view.renderGameState(game);
                updateUndoRedoButtons();
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Game loaded successfully from:\n" + file.getAbsolutePath(),
                    "Load Game",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // After loading, if it's an AI player's turn, optionally let AI play.
            maybePlayAiTurn();

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to load game:\n" + e.getMessage(),
                    "Load Game Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Opens a file chooser dialog to let the user pick a location and then
     * saves the game to that file.
     * <p>
     * This convenience method is intended to be called by the view, e.g.,
     * from a "Save Game" button or menu item.
     */
    public void showSaveGameDialog() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            saveGameToFile(chooser.getSelectedFile());
        }
    }

    /**
     * Opens a file chooser dialog to let the user select a saved game file
     * and then loads the game from that file.
     * <p>
     * This convenience method is intended to be called by the view, e.g.,
     * from a "Load Game" button or menu item.
     */
    public void showLoadGameDialog() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            loadGameFromFile(chooser.getSelectedFile());
        }
    }
}
