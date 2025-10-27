/**
 * Represents a single letter tile in Scrabble.
 * Each tile has a letter and a point value.
 */
public class Tile {
    private char letter;
    private int points;

    /**
     * Constructs a Tile with the specified letter and point value.
     *
     * @param letter the character on the tile (A-Z)
     * @param points the point value of the tile
     */
    public Tile(char letter, int points) {
        this.letter = Character.toUpperCase(letter);
        this.points = points;
    }

    /**
     * Returns the letter on the tile.
     *
     * @return the tile's letter
     */
    public char getLetter() {
        return letter;
    }

    /**
     * Returns the point value of the tile.
     *
     * @return the tile's score value
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns a string representation of the tile.
     *
     * @return a string like "A(1)"
     */
    @Override
    public String toString() {
        return letter + "(" + points + ")";
    }
}