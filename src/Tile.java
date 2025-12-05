import java.io.Serializable;

/**
 * Represents a single letter tile in Scrabble.
 * Each tile has a letter and a point value.
 */
public class Tile implements Serializable {
    private char letter;
    private int points;
    private final boolean blank;

    /**
     * Constructs a Tile with the specified letter and point value.
     *
     * @param letter the character on the tile (A-Z)
     * @param points the point value of the tile
     */
    public Tile(char letter, int points) {
        this(letter, points, false);
    }

    /**
     * Constructs a Tile with an explicit blank flag (used for wildcard tiles).
     *
     * @param letter the character on the tile (A-Z) or placeholder for blanks
     * @param points the point value of the tile
     * @param isBlank true if this tile is a blank (wildcard)
     */
    public Tile(char letter, int points, boolean isBlank) {
        this.letter = Character.toUpperCase(letter);
        this.points = points;
        this.blank = isBlank;
    }

    /**
     * Returns the letter on the tile.
     *
     * @return the tile's letter
     */
    public char getLetter() {
        return this.letter;
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
     * Indicates whether this tile is a blank (wildcard) tile.
     *
     * @return true if blank; false otherwise
     */
    public boolean isBlank() {
        return blank;
    }

    /**
     * Assigns a display letter to a blank tile (score remains 0).
     *
     * @param newLetter the letter this blank should represent
     */
    public void assignBlankLetter(char newLetter) {
        if (blank) {
            this.letter = Character.toUpperCase(newLetter);
        }
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
