import java.util.List;

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
        view.setController(this);
        view.renderGameState(game);
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
            view.showError("Please enter a word.");
            return;
        }
        if (row < 0 || row > 14 || col < 0 || col > 14) {
            view.showError("Row/Column out of bounds.");
            return;
        }
        if (!game.getDictionary().isValid(word)) {
            view.showError("Invalid word! Please try again.");
            return;
        }

        boolean success = board.placeWord(word, row, col, horizontal, currentPlayer);
        if (success) {
            refillRack(currentPlayer);
            advanceTurn();
        } else {
            view.showError("Word placement failed. Check connectivity, conflicts, bounds, and first-move center.");
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
            view.showError("No valid indices selected for swap.");
            return;
        }
        if (bag.isEmpty()) {
            view.showError("Cannot swap: tile bag is empty.");
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
        view.renderGameState(game);
    }

    /**
     * Returns the current Game model managed by this controller.
     *
     * @return the Game instance
     */
    public Game getGame() {
        return game;
    }
}
