/**
 * Represents the Scrabble board as a 15x15 grid.
 * Handles word placement, tile access, and board display.
 *
 * Milestone 2 patches:
 * - Enforces "first move must cover center (H8)" in the model.
 * - Keeps placement/validation logic inside the model (no GUI mutations).
 * - Removes unused private helper to avoid duplication with Player API.
 * - No scoring duplication: Board remains the single source to apply score for a placed word.
 */
public class Board {
    private final Tile[][] grid;

    /**
     * Constructs a new empty 15x15 Scrabble board.
     */
    public Board() {
        grid = new Tile[15][15];
    }

    /**
     * Places a word on the board if it fits, doesn't conflict, and connects to existing tiles.
     * Supports both horizontal (left to right) and vertical (top to bottom) placement.
     * Also enforces that the very first move must cover the center square (row 7, col 7 == H8).
     *
     * NOTE: This method consumes tiles from the player's rack if placement succeeds
     * and applies the score exactly once (no controller-side re-scoring).
     *
     * @param word       the word to place (case-insensitive; treated as uppercase)
     * @param row        starting row index (0–14)
     * @param col        starting column index (0–14)
     * @param horizontal true for horizontal (left to right), false for vertical (top to bottom)
     * @param player     the player placing the word
     * @return true if placement was successful; false otherwise
     */
    public boolean placeWord(String word, int row, int col, boolean horizontal, Player player) {
        if (word == null || word.isEmpty()) return false;
        word = word.toUpperCase();

        // Bounds check
        if (horizontal && col + word.length() > 15) return false;
        if (!horizontal && row + word.length() > 15) return false;

        // First-move center coverage rule (must cover H8 => (7,7))
        if (isFirstMove()) {
            boolean coversCenter = false;
            for (int i = 0; i < word.length(); i++) {
                int r = horizontal ? row : row + i;
                int c = horizontal ? col + i : col;
                if (r == 7 && c == 7) {
                    coversCenter = true;
                    break;
                }
            }
            if (!coversCenter) return false;
        }

        // Conflict check with existing tiles (must match placed letters)
        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            Tile existing = grid[r][c];
            if (existing != null && existing.getLetter() != word.charAt(i)) {
                return false;
            }
        }

        // Connectivity check (skip if first move)
        if (!isFirstMove()) {
            boolean connectsToExistingTile = false;
            for (int i = 0; i < word.length(); i++) {
                int r = horizontal ? row : row + i;
                int c = horizontal ? col + i : col;

                // Shares a cell with an existing tile
                if (grid[r][c] != null) {
                    connectsToExistingTile = true;
                    break;
                }

                // Adjacent to an existing tile (orthogonally)
                if ((r > 0 && grid[r - 1][c] != null) ||
                        (r < 14 && grid[r + 1][c] != null) ||
                        (c > 0 && grid[r][c - 1] != null) ||
                        (c < 14 && grid[r][c + 1] != null)) {
                    connectsToExistingTile = true;
                }
            }
            if (!connectsToExistingTile) return false;
        }

        // Place the word: only fill empty cells; consume player's tiles for those cells
        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            if (grid[r][c] == null) {
                Tile tile = player.findTileInRack(word.charAt(i));
                if (tile == null) {
                    // Not enough letters in rack; abort (nothing placed yet, so safe)
                    return false;
                }
                grid[r][c] = tile;
                player.getRack().remove(tile);
            }
        }

        // Apply score once (no controller-side add)
        player.addScore(calculateScore(word));
        return true;
    }

    /**
     * Returns the tile located at the specified row and column on the board.
     *
     * @param row the row index (0–14)
     * @param col the column index (0–14)
     * @return the Tile at the given position, or null if the cell is empty
     */
    public Tile getTileAt(int row, int col) {
        if (row < 0 || row >= 15 || col < 0 || col >= 15) return null;
        return grid[row][col];
    }

    /**
     * Sets a tile at the specified position on the board.
     * Useful for testing or manual placement (not used by the View in MVC flow).
     *
     * @param tile the Tile to place
     * @param row  the row index (0–14)
     * @param col  the column index (0–14)
     */
    public void setTileAt(Tile tile, int row, int col) {
        if (row >= 0 && row < 15 && col >= 0 && col < 15) {
            grid[row][col] = tile;
        }
    }

    /**
     * Checks if the board is empty, indicating it's the first move of the game.
     *
     * @return true if no tiles are placed yet; false otherwise
     */
    public boolean isFirstMove() {
        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                if (grid[r][c] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prints the current state of the board to the console.
     * Empty tiles are shown as dots (.), placed tiles show their letters.
     */
    public void printBoard() {
        System.out.println("   A B C D E F G H I J K L M N O");
        for (int r = 0; r < 15; r++) {
            System.out.printf("%2d ", r + 1);
            for (int c = 0; c < 15; c++) {
                Tile tile = grid[r][c];
                System.out.print(tile == null ? ". " : tile.getLetter() + " ");
            }
            System.out.println();
        }
    }

    /**
     * Calculates the score for a word based on tile points.
     * Does not yet account for premium squares or cross-word bonuses.
     *
     * @param word the word to score
     * @return the total score
     */
    int calculateScore(String word) {
        int score = 0;
        for (char c : word.toCharArray()) {
            score += getTilePoints(c);
        }
        return score;
    }

    /**
     * Returns the point value for a given letter using standard Scrabble scoring.
     *
     * @param letter the letter to score
     * @return the point value
     */
    private int getTilePoints(char letter) {
        switch (Character.toUpperCase(letter)) {
            case 'A': case 'E': case 'I': case 'O': case 'U':
            case 'L': case 'N': case 'S': case 'T': case 'R': return 1;
            case 'D': case 'G': return 2;
            case 'B': case 'C': case 'M': case 'P': return 3;
            case 'F': case 'H': case 'V': case 'W': case 'Y': return 4;
            case 'K': return 5;
            case 'J': case 'X': return 8;
            case 'Q': case 'Z': return 10;
            default: return 0;
        }
    }
}
