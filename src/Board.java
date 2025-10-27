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
     * Places a word on the board if it fits and doesn't conflict.
     *
     * @param word the word to place
     * @param row starting row (0–14)
     * @param col starting column (0–14)
     * @param horizontal true for horizontal, false for vertical
     * @param player the player placing the word
     * @return true if placement was successful, false otherwise
     */
    public boolean placeWord(String word, int row, int col, boolean horizontal, Player player) {
        // Check bounds
        if (horizontal && col + word.length() > 15) return false;
        if (!horizontal && row + word.length() > 15) return false;

        // Check for conflicts
        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            Tile existing = grid[r][c];
            if (existing != null && existing.getLetter() != word.charAt(i)) {
                return false;
            }
        }

        // Place tiles and consume from rack
        for (int i = 0; i < word.length(); i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            if (grid[r][c] == null) {
                Tile tile = player.findTileInRack(word.charAt(i));
                if (tile == null) return false;
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