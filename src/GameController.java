import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;

/**
 * GameController acts as the mediator between the Game model and the GameView.
 * It handles user actions from the view and updates the model accordingly.
 *
 * Milestone 2 patches:
 * - Removes double-scoring (Board is the single authority that applies score).
 * - Centralizes placement/swap/pass flows.
 * - Triggers View re-render after each state change.
 */
public class GameController {
    /** The Game model containing players, board, tile bag, and dictionary. */
    private final Game game;

    /** The GameView GUI responsible for rendering the game state. */
    private final GameView view;
    /** Prevents nested AI recursion. */
    private boolean aiTurnInProgress = false;

    /**
     * Constructs a GameController with the given model and view.
     * Initializes the game state and sets up communication with the view.
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
        }
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
            advanceTurn();
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

        List<Tile> toSwap = currentPlayer.getTilesAtIndices(indices);
        if (toSwap.isEmpty()) {
            if (view != null) view.showError("No valid indices selected for swap.");
            return;
        }
        if (bag.isEmpty()) {
            if (view != null) view.showError("Cannot swap: tile bag is empty.");
            return;
        }

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
     * Advances to the next player and updates the view.
     */
    public void handlePassTurn() {
        advanceTurn();
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
     * Advances the turn to the next player in order, wraps at the end,
     * and instructs the view to re-render the current game state.
     */
    void advanceTurn() {
        game.nextPlayer();
        if (view != null) {
            view.renderGameState(game);
        }
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

    /** Simple AI: try a handful of rack-valid dictionary words and place the first legal move. */
    private void runAiTurn(Player aiPlayer) {
        Board board = game.getBoard();
        List<String> candidates = pickCandidateWords(aiPlayer, 30);

        for (String word : candidates) {
            if (tryPlaceAnywhere(word, aiPlayer, board)) {
                refillRack(aiPlayer);
                advanceTurn();
                return;
            }
        }

        // No moves: pass turn
        handlePassTurn();
    }

    /** Attempts to place the word at any coordinate/orientation; returns true on success. */
    private boolean tryPlaceAnywhere(String word, Player player, Board board) {
        for (boolean horizontal : new boolean[]{true, false}) {
            for (int row = 0; row < 15; row++) {
                for (int col = 0; col < 15; col++) {
                    if (board.placeWord(word, row, col, horizontal, player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Picks rack-buildable dictionary words, preferring higher letter sums/lengths.
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

    /** Rough heuristic for word strength: sum of tile points (ignores premiums). */
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
}
