import java.util.*;

/**
 * Represents the bag of tiles in Scrabble.
 * Handles initialization and random drawing of tiles.
 */
public class TileBag {
    private List<Tile> tiles;

    /**
     * Constructs a TileBag and fills it with the standard Scrabble tile distribution.
     * Milestone 3: includes blank tiles.
     */
    public TileBag() {
        tiles = new ArrayList<>();
        initializeTiles();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    /**
     * Draws a random tile from the bag.
     *
     * @return a Tile if available, or null if the bag is empty
     */
    public Tile drawTile() {
        if (tiles.isEmpty()) return null;
        return tiles.remove((int)(Math.random() * tiles.size()));
    }

    /**
     * Returns the number of tiles remaining in the bag.
     *
     * @return the count of remaining tiles
     */
    public int remainingTiles() {
        return tiles.size();
    }

    /**
     * Initializes the tile bag with the standard Scrabble letter distribution.
     * Point values follow official rules. Blank tiles are omitted for Milestone 1.
     */
    private void initializeTiles() {
        addTiles('A', 1, 9);
        addTiles('B', 3, 2);
        addTiles('C', 3, 2);
        addTiles('D', 2, 4);
        addTiles('E', 1, 12);
        addTiles('F', 4, 2);
        addTiles('G', 2, 3);
        addTiles('H', 4, 2);
        addTiles('I', 1, 9);
        addTiles('J', 8, 1);
        addTiles('K', 5, 1);
        addTiles('L', 1, 4);
        addTiles('M', 3, 2);
        addTiles('N', 1, 6);
        addTiles('O', 1, 8);
        addTiles('P', 3, 2);
        addTiles('Q', 10, 1);
        addTiles('R', 1, 6);
        addTiles('S', 1, 4);
        addTiles('T', 1, 6);
        addTiles('U', 1, 4);
        addTiles('V', 4, 2);
        addTiles('W', 4, 2);
        addTiles('X', 8, 1);
        addTiles('Y', 4, 2);
        addTiles('Z', 10, 1);
        // Two blank tiles (wildcards, 0 points)
        addBlankTiles(2);
    }

    /**
     * Adds multiple tiles of the same letter to the bag.
     *
     * @param letter the letter to add
     * @param points the point value of the tile
     * @param count the number of tiles to add
     */
    private void addTiles(char letter, int points, int count) {
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile(letter, points));
        }
    }

    /** Adds blank tiles (wildcards worth 0 points). */
    private void addBlankTiles(int count) {
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile('?', 0, true));
        }
    }

    /**
     * Returns the number of tiles remaining in the bag.
     *
     * @return the number of tiles left
     */
    public int size() {
        return tiles.size(); // or whatever your internal list is called
    }

    /**
     * Checks whether the collection of tiles is empty.
     *
     * @return true if there are no tiles left; false otherwise
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
