import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * JUnit tests for saving and loading a {@link Game} instance via Java serialization.
 * <p>
 * These tests ensure that important pieces of game state survive a full
 * serialize/deserialize round trip:
 * <ul>
 *     <li>Board tile placements</li>
 *     <li>Player scores</li>
 *     <li>Tile bag size</li>
 *     <li>Current player index</li>
 * </ul>
 */
public class SerializationTest {

    /**
     * Creates a simple two-player game with a small dictionary.
     *
     * @return a new Game instance
     */
    private Game createSimpleGame() {
        Set<String> words = new HashSet<>(Arrays.asList("CAT", "DOG", "AAA"));
        Dictionary dictionary = new Dictionary(words);
        Game game = new Game(dictionary);
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        return game;
    }

    /**
     * Places a single word ("CAT") on the center of the board for the current
     * player. The rack is overridden to make sure the player actually has the
     * required letters.
     *
     * @param game the game whose board and current player should be modified
     */
    private void placeCatAtCenter(Game game) {
        Board board = game.getBoard();
        Player current = game.getCurrentPlayer();

        // Ensure rack contains exactly the tiles needed to place "CAT"
        current.getRack().clear();
        current.getRack().add(new Tile('C', 3));
        current.getRack().add(new Tile('A', 1));
        current.getRack().add(new Tile('T', 1));

        // First move: must cover center (H8) which is (7,7) in 0-based indices.
        boolean placed = board.placeWord("CAT", 7, 7, true, current);
        assertTrue("Expected word placement at center to succeed", placed);
    }

    /**
     * Verifies that a game can be serialized and deserialized and that
     * key pieces of state are preserved.
     */
    @Test
    public void testGameSerializationRoundTrip() throws Exception {
        Game game = createSimpleGame();
        placeCatAtCenter(game);

        int originalScore = game.getCurrentPlayer().getScore();
        int originalBagSize = game.getTileBag().remainingTiles();
        char centerLetter = game.getBoard().getTileAt(7, 7).getLetter();
        int originalCurrentIndex = game.getCurrentPlayerIndex();

        // Serialize to a temporary file
        File temp = File.createTempFile("scrabble-save", ".ser");
        temp.deleteOnExit();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp))) {
            oos.writeObject(game);
        }

        // Deserialize from the temporary file
        Game loaded;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(temp))) {
            Object obj = ois.readObject();
            assertTrue("Deserialized object should be a Game", obj instanceof Game);
            loaded = (Game) obj;
        }

        // Verify that important state survived the round trip
        assertEquals("Center tile letter should survive serialization",
                centerLetter, loaded.getBoard().getTileAt(7, 7).getLetter());
        assertEquals("Player score should survive serialization",
                originalScore, loaded.getCurrentPlayer().getScore());
        assertEquals("Tile bag size should survive serialization",
                originalBagSize, loaded.getTileBag().remainingTiles());
        assertEquals("Current player index should survive serialization",
                originalCurrentIndex, loaded.getCurrentPlayerIndex());
    }
}
