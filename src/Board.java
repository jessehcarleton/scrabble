/**
 * Represents the Scrabble board as a 15x15 grid.
 * Handles word placement, tile access, and board display.
 *
 * Milestone 2 patches:
 * - Enforces "first move must cover center (H8)" in the model.
 * - Keeps placement/validation logic inside the model (no GUI mutations).
 * - Removes unused private helper to avoid duplication with Player API.
 * - No scoring duplication: Board remains the single source to apply score for a placed word.
 *
 * Milestone 3 additions:
 * - Supports blank tiles that can represent any letter.
 * - Calculates score considering premium squares only for newly placed tiles.
 * - Scoring now accounts for premiums; calculateScore returns word * wordMultipliers based on new tiles.
 */
public class Board {
    private final Tile[][] grid;
    private final Premium[][] premiumGrid;

    /** Premium squares on board. */
    public enum Premium {
        NONE,
        DOUBLE_LETTER,
        TRIPLE_LETTER,
        DOUBLE_WORD,
        TRIPLE_WORD
    }

    /**
     * Constructs a new empty 15x15 Scrabble board.
     */
    public Board() {
        grid = new Tile[15][15];
        premiumGrid = new Premium[15][15];
        initPremiumSquares();
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

        // Gather placement info without mutating state
        Tile[] placementTiles = new Tile[word.length()];
        char[] placementLetters = new char[word.length()];
        boolean[] isNew = new boolean[word.length()];

        // Copy of rack for availability checks
        java.util.List<Tile> tempRack = new java.util.ArrayList<>(player.getRack());

        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            Tile existing = grid[r][c];
            char needed = word.charAt(i);
            if (existing != null) {
                if (existing.getLetter() != needed) {
                    return false; // conflict
                }
                placementTiles[i] = existing;
                placementLetters[i] = existing.getLetter();
                isNew[i] = false;
            } else {
                Tile tile = removeMatchingTile(tempRack, needed);
                if (tile == null) {
                    tile = removeBlankTile(tempRack);
                    if (tile == null) {
                        return false; // missing tile or blank
                    }
                }
                placementTiles[i] = tile;
                placementLetters[i] = needed;
                isNew[i] = true;
            }
        }

        // Connectivity check (skip if first move)
        if (!isFirstMove()) {
            boolean connectsToExistingTile = false;
            for (int i = 0; i < word.length(); i++) {
                int r = horizontal ? row : row + i;
                int c = horizontal ? col + i : col;

                if (grid[r][c] != null) {
                    connectsToExistingTile = true;
                    break;
                }

                if ((r > 0 && grid[r - 1][c] != null) ||
                        (r < 14 && grid[r + 1][c] != null) ||
                        (c > 0 && grid[r][c - 1] != null) ||
                        (c < 14 && grid[r][c + 1] != null)) {
                    connectsToExistingTile = true;
                }
            }
            if (!connectsToExistingTile) return false;
        }

        // All validations passed: commit placement
        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            if (isNew[i]) {
                Tile tile = placementTiles[i];
                if (tile.isBlank()) {
                    tile.assignBlankLetter(placementLetters[i]);
                }
                grid[r][c] = tile;
                player.getRack().remove(tile);
            }
        }

        // Apply score once (no controller-side add)
        player.addScore(calculateScore(word, row, col, horizontal, isNew));
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
     * Calculates the score for a word based on tile points and premium squares.
     * Premium squares apply only to tiles placed in this move (isNew == true).
     *
     * @param word the word to score
     * @param row starting row
     * @param col starting column
     * @param horizontal orientation
     * @param isNew flags indicating which letters were placed this turn
     * @return the total score
     */
    int calculateScore(String word, int row, int col, boolean horizontal, boolean[] isNew) {
        int score = 0;
        int wordMultiplier = 1;
        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            Tile tile = grid[r][c];
            int tilePoints = tile != null ? tile.getPoints() : getTilePoints(word.charAt(i));

            if (isNew != null && isNew.length > i && isNew[i]) {
                Premium premium = premiumGrid[r][c];
                switch (premium) {
                    case DOUBLE_LETTER:
                        tilePoints *= 2; break;
                    case TRIPLE_LETTER:
                        tilePoints *= 3; break;
                    case DOUBLE_WORD:
                        wordMultiplier *= 2; break;
                    case TRIPLE_WORD:
                        wordMultiplier *= 3; break;
                    default:
                        break;
                }
            }
            score += tilePoints;
        }
        return score * wordMultiplier;
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

    /** Returns the premium type for a given coordinate. */
    public Premium getPremiumAt(int row, int col) {
        if (row < 0 || row >= 15 || col < 0 || col >= 15) return Premium.NONE;
        return premiumGrid[row][col];
    }

    /**
     * Initializes the board's premium square layout to the standard Scrabble board.
     * Coordinates are zero-based (row, col).
     */
    private void initPremiumSquares() {
        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                premiumGrid[r][c] = Premium.NONE;
            }
        }

        // Triple Word Score
        int[][] tw = {
                {0,0},{0,7},{0,14},{7,0},{7,14},{14,0},{14,7},{14,14}
        };
        markPremium(tw, Premium.TRIPLE_WORD);

        // Double Word Score (including center)
        int[][] dw = {
                {1,1},{2,2},{3,3},{4,4},{7,7},{10,10},{11,11},{12,12},{13,13},
                {1,13},{2,12},{3,11},{4,10},{10,4},{11,3},{12,2},{13,1}
        };
        markPremium(dw, Premium.DOUBLE_WORD);

        // Triple Letter Score
        int[][] tl = {
                {1,5},{1,9},{5,1},{5,5},{5,9},{5,13},
                {9,1},{9,5},{9,9},{9,13},{13,5},{13,9}
        };
        markPremium(tl, Premium.TRIPLE_LETTER);

        // Double Letter Score
        int[][] dl = {
                {0,3},{0,11},{2,6},{2,8},{3,0},{3,7},{3,14},
                {6,2},{6,6},{6,8},{6,12},{7,3},{7,11},
                {8,2},{8,6},{8,8},{8,12},{11,0},{11,7},{11,14},
                {12,6},{12,8},{14,3},{14,11}
        };
        markPremium(dl, Premium.DOUBLE_LETTER);
    }

    /** Utility to mark premium squares. */
    private void markPremium(int[][] coords, Premium premium) {
        for (int[] rc : coords) {
            premiumGrid[rc[0]][rc[1]] = premium;
        }
    }

    /** Removes and returns a matching letter tile from the temporary rack. */
    private Tile removeMatchingTile(java.util.List<Tile> rack, char letter) {
        for (int i = 0; i < rack.size(); i++) {
            Tile t = rack.get(i);
            if (!t.isBlank() && t.getLetter() == letter) {
                rack.remove(i);
                return t;
            }
        }
        return null;
    }

    /** Removes and returns a blank tile from the temporary rack. */
    private Tile removeBlankTile(java.util.List<Tile> rack) {
        for (int i = 0; i < rack.size(); i++) {
            Tile t = rack.get(i);
            if (t.isBlank()) {
                rack.remove(i);
                return t;
            }
        }
        return null;
    }
}
