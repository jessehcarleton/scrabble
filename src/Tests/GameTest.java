import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class GameTest {

    @Test
    public void testAddPlayerInitializesRack() throws Exception {
        Dictionary dict = new Dictionary();
        Game game = new Game(dict);
        game.addPlayer("Alex");
        List<Player> players = game.getPlayers();
        assertEquals(1, players.size());
        assertEquals(7, players.get(0).getRack().size());
    }

    @Test
    public void testRefillRackAfterPlacement() throws Exception {
        Dictionary dict = new Dictionary();
        Game game = new Game(dict);
        game.addPlayer("Jamie");
        Player player = game.getPlayers().get(0);

        // Simulate using 3 tiles
        player.getRack().clear();
        player.addTile(new Tile('C', 3));
        player.addTile(new Tile('A', 1));
        player.addTile(new Tile('T', 1));
        game.refillRack(player);
        assertEquals(7, player.getRack().size());
    }

    @Test
    public void testValidWordPlacement() throws Exception {
        Dictionary dict = new Dictionary();
        Game game = new Game(dict);
        game.addPlayer("Sam");
        Player player = game.getPlayers().get(0);

        player.getRack().clear();
        player.addTile(new Tile('C', 3));
        player.addTile(new Tile('A', 1));
        player.addTile(new Tile('T', 1));

        boolean success = game.getBoard().placeWord("CAT", 7, 7, true, player);
        assertTrue(success);
        game.refillRack(player);
        assertEquals(7, player.getRack().size());
    }

    @Test
    public void testInvalidWordRejected() throws Exception {
        Dictionary dict = new Dictionary();
        Game game = new Game(dict);
        game.addPlayer("Taylor");
        Player player = game.getPlayers().get(0);

        player.getRack().clear();
        player.addTile(new Tile('X', 8));
        player.addTile(new Tile('Y', 4));
        player.addTile(new Tile('Z', 10));

        boolean valid = dict.isValid("XYZXYZ");
        assertFalse(valid);
    }

    @Test
    public void testNextPlayerRotation() throws Exception {
        Dictionary dict = new Dictionary();
        Game game = new Game(dict);
        game.addPlayer("Alex");
        game.addPlayer("Jamie");
        assertEquals(0, game.getCurrentPlayerIndex());
        game.nextPlayer();
        assertEquals(1, game.getCurrentPlayerIndex());
        game.nextPlayer();
        assertEquals(0, game.getCurrentPlayerIndex());
    }
}