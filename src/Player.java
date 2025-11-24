import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Scrabble game.
 * Each player has a name, a rack of letter tiles, and a score.
 */
public class Player {
    private String name;
    private List<Tile> rack;
    private int score;
    private boolean ai;

    /**
     * Constructs a new Player with the given name.
     * Initializes an empty rack and sets the score to zero.
     *
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
        this.rack = new ArrayList<>();
        this.score = 0;
        this.ai = false;
    }

    /**
     * Returns the player's name.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of tiles currently in the player's rack.
     *
     * @return the player's rack of tiles
     */
    public List<Tile> getRack() {
        return rack;
    }

    /**
     * Returns the player's current score.
     *
     * @return the score of the player
     */
    public int getScore() {
        return score;
    }

    /** Marks this player as an AI-controlled player. */
    public void setAi(boolean ai) {
        this.ai = ai;
    }

    /** Returns true if this player is AI-controlled. */
    public boolean isAi() {
        return ai;
    }

    /**
     * Adds the specified number of points to the player's score.
     *
     * @param points the number of points to add
     */
    public void addScore(int points) {
        score += points;
    }

    /**
     * Removes tiles from the rack that match the letters in the given word.
     * Assumes the word is valid and the player has the necessary tiles.
     *
     * @param word the word to remove tiles for
     */
    public void useTilesForWord(String word) {
        word = word.toUpperCase();
        for (char c : word.toCharArray()) {
            for (int i = 0; i < rack.size(); i++) {
                if (rack.get(i).getLetter() == c) {
                    rack.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * Adds a tile to the player's rack.
     *
     * @param tile the tile to add
     */
    public void addTile(Tile tile) {
        if (tile != null && rack.size() < 7) {
            rack.add(tile);
        }
    }

    /**
     * Returns a string representation of the player's rack.
     * Each tile is shown as Letter:Points (e.g., R:1 E:1 A:1).
     *
     * @return formatted rack string
     */
    public String rackToString() {
        StringBuilder sb = new StringBuilder();
        for (Tile tile : rack) {
            sb.append(tile.getLetter()).append(":").append(tile.getPoints()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Searches the player's rack for a tile matching the given letter.
     * Returns the first matching tile found, or null if none exists.
     *
     * @param letter the letter to search for
     * @return the matching Tile object, or null if not found
     */
    public Tile findTileInRack(char letter) {
        for (Tile tile : rack) {
            if (tile.getLetter() == Character.toUpperCase(letter)) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Checks if the player has the necessary tiles to form the given word.
     * Accounts for blank tiles by treating them as wildcards.
     *
     * @param word the word to check
     * @return true if the player can form the word, false otherwise
     */
    public boolean canFormWord(String word) {
        word = word.toUpperCase();
        List<Character> rackLetters = new ArrayList<>();
        int blanks = 0;
        for (Tile t : rack) {
            if (t.isBlank()) {
                blanks++;
            } else {
                rackLetters.add(t.getLetter());
            }
        }

        for (char c : word.toCharArray()) {
            if (!rackLetters.remove((Character) c)) {
                if (blanks > 0) {
                    blanks--;
                } else {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Returns a list of tiles from the player's rack at the specified indices.
     *
     * @param indices the list of indices to retrieve tiles from
     * @return a list of Tile objects at the given indices
     */
    public List<Tile> getTilesAtIndices(List<Integer> indices) {
        List<Tile> selectedTiles = new ArrayList<>();
        for (int index : indices) {
            if (index >= 0 && index < rack.size()) {
                selectedTiles.add(rack.get(index));
            }
        }
        return selectedTiles;
    }

    /**
     * Finds and returns (without removing) the first blank tile in the rack.
     *
     * @return a blank Tile or null if none remain
     */
    public Tile findBlankTile() {
        for (Tile tile : rack) {
            if (tile.isBlank()) {
                return tile;
            }
        }
        return null;
    }
}
