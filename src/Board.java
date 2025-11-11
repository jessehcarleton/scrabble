/**
 * Represents the Scrabble board as a 15x15 grid.
 * Handles word placement and board display.
 */
public class Board {
    private Tile[][] grid;

    /**
     * Constructs a new empty 15x15 Scrabble board.
     */
    public Board() {
        grid = new Tile[15][15];
    }

    /**
     * Places a word on the board if it fits, doesn't conflict, and connects to existing tiles.
     * Supports both horizontal (left to right) and vertical (top to bottom) placement.
     *
     * @param word the word to place
     * @param row starting row index (0–14)
     * @param col starting column index (0–14)
     * @param horizontal true for horizontal (left to right), false for vertical (top to bottom)
     * @param player the player placing the word
     * @return true if placement was successful; false otherwise
     */
    public boolean placeWord(String word, int row, int col, boolean horizontal, Player player) {
        // Check board bounds
        if (horizontal && col + word.length() > 15) return false;
        if (!horizontal && row + word.length() > 15) return false;

        // Check for tile conflicts
        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            Tile existing = grid[r][c];
            if (existing != null && existing.getLetter() != word.charAt(i)) {
                return false; // Conflict with existing tile
            }
        }

        // Check adjacency to existing tiles (unless it's the first move)
        boolean connectsToExistingTile = false;

        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;

            // Reuse existing tile
            if (grid[r][c] != null) {
                connectsToExistingTile = true;
                break;
            }

            // Check adjacent tiles (up, down, left, right)
            if ((r > 0 && grid[r - 1][c] != null) ||
                    (r < 14 && grid[r + 1][c] != null) ||
                    (c > 0 && grid[r][c - 1] != null) ||
                    (c < 14 && grid[r][c + 1] != null)) {
                connectsToExistingTile = true;
            }
        }

        // Reject if no connection and not first move
        if (!connectsToExistingTile && !isFirstMove()) {
            System.out.println("Word must connect to existing tiles.");
            return false;
        }

        // Place tiles and consume from rack
        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            if (grid[r][c] == null) {
                Tile tile = player.findTileInRack(word.charAt(i));
                if (tile == null) return false; // Player doesn't have required tile
                grid[r][c] = tile;
                player.getRack().remove(tile);
            }
        }

        // Update score
        int score = calculateScore(word);
        player.addScore(score);
        return true;
    }
    /**
     * Prints the current state of the board to the console.
     * Empty tiles are shown as dots (.), placed tiles show their letters.
     */
    public void printBoard() {
        System.out.println("   A B C D E F G H I J K L M N O");
        for (int r = 0; r < 15; r++) {
            //System.out.printf("%2d ", r + 1);
            System.out.print(r < 9 ? " " + (r + 1) : r + 1);
            for (int c = 0; c < 15; c++) {
                Tile tile = grid[r][c];
                System.out.print(c == 0 ? " " : "");
                System.out.print(tile == null ? ". " : tile.getLetter() + " ");
            }
            System.out.println();
        }
    }
    /**
     * Checks if the board is empty, indicating it's the first move of the game.
     * Used to allow the first word to be placed without adjacency constraints.
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
     * Calculates the score for a word based on tile points.
     * Does not yet account for premium squares.
     *
     * @param word the word to score
     * @return the total score
     */
    private int calculateScore(String word) {
        int score = 0;
        for (char c : word.toCharArray()) {
            score += getTilePoints(c);
        }
        return score;
    }

    /**
     * Returns the point value for a given letter.
     * Uses standard Scrabble scoring.
     *
     * @param letter the letter to score
     * @return the point value
     */
    private int getTilePoints(char letter) {
        switch (letter) {
            case 'A': case 'E': case 'I': case 'O': case 'U': case 'L': case 'N': case 'S': case 'T': case 'R': return 1;
            case 'D': case 'G': return 2;
            case 'B': case 'C': case 'M': case 'P': return 3;
            case 'F': case 'H': case 'V': case 'W': case 'Y': return 4;
            case 'K': return 5;
            case 'J': case 'X': return 8;
            case 'Q': case 'Z': return 10;
            default: return 0;
        }
    }

    /**
     * Finds and removes a tile with the specified letter from the player's rack.
     *
     * @param player the player whose rack to search
     * @param letter the letter to find
     * @return the matching tile, or null if not found
     */
    private Tile findTileInRack(Player player, char letter) {
        for (int i = 0; i < player.getRack().size(); i++) {
            Tile tile = player.getRack().get(i);
            if (tile.getLetter() == letter) {
                player.getRack().remove(i);
                return tile;
            }
        }
        return null;
    }
}