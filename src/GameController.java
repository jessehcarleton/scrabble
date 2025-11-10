import java.util.List;

/**
 * GameController acts as the mediator between the Game model and the GameView.
 * It handles user actions from the view and updates the model accordingly.
 * It also triggers view updates to reflect changes in game state.
 */
public class GameController {
    private Game game;
    private GameView view;

    /**
     * Constructs a GameController with the given model and view.
     * Initializes the game state and sets up communication with the view.
     *
     * @param game the Game model
     * @param view the GameView GUI
     */
    public GameController(Game game, GameView view) {
        this.game = game;
        this.view = view;
        view.setController(this);
        view.renderGameState(game);
    }

    /**
     * Handles the action of placing a word on the board.
     * Validates the word, attempts placement, updates rack and score, and rotates turn.
     *
     * @param word the word to place
     * @param row the starting row index (0–14)
     * @param col the starting column index (0–14)
     * @param horizontal true if placing left-to-right, false for top-to-bottom
     */
    public void handlePlaceWord(String word, int row, int col, boolean horizontal) {
        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());

        if (!game.getDictionary().isValid(word)) {
            view.showError("Invalid word! Please try again.");
            return;
        }

        boolean success = game.getBoard().placeWord(word, row, col, horizontal, currentPlayer);
        if (success) {
            game.refillRack(currentPlayer);
            game.nextPlayer();
            view.renderGameState(game);
        } else {
            view.showError("Word placement failed. Check tile alignment and board constraints.");
        }
    }

    /**
     * Handles the action of swapping selected tiles from the player's rack.
     * Returns tiles to the bag, refills rack, and rotates turn.
     *
     * @param indices list of tile indices to swap
     */
    public void handleSwapTiles(List<Integer> indices) {
        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());

        List<Tile> toSwap = currentPlayer.getTilesAtIndices(indices);
        for (Tile t : toSwap) {
            currentPlayer.getRack().remove(t);
            game.getTileBag().getTiles().add(t);
        }

        game.refillRack(currentPlayer);
        game.nextPlayer();
        view.renderGameState(game);
    }

    /**
     * Handles the action of passing the current player's turn.
     * Advances to the next player and updates the view.
     */
    public void handlePassTurn() {
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