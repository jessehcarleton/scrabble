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
     * Does not account for blank tiles yet (Milestone 3).
     *
     * @param word the word to check
     * @return true if the player can form the word, false otherwise
     */
    public boolean canFormWord(String word) {
        word = word.toUpperCase();
        List<Character> rackLetters = new ArrayList<>();
        for (Tile t : rack) {
            rackLetters.add((Character) t.getLetter());
        }

        for (char c : word.toCharArray()) {
            if (!rackLetters.remove((Character) c)) {
                return false;
            }
        }
        return true;
    }

    public boolean removeTile(Tile tile) {
        return rack.remove(tile);
    }

}