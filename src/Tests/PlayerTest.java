import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class PlayerTest {

    @Test
    public void testGetName() {
        Player player = new Player("Alex");
        assertEquals("Alex", player.getName());
    }

    @Test
    public void testAddScoreAndGetScore() {
        Player player = new Player("Sam");
        assertEquals(0, player.getScore());
        player.addScore(10);
        assertEquals(10, player.getScore());
    }

    @Test
    public void testAddTileAndGetRack() {
        Player player = new Player("Jamie");
        Tile tile = new Tile('A', 1);
        player.addTile(tile);
        List<Tile> rack = player.getRack();
        assertEquals(1, rack.size());
        assertEquals(tile, rack.get(0));
    }

    @Test
    public void testRackToString() {
        Player player = new Player("Morgan");
        player.addTile(new Tile('B', 3));
        player.addTile(new Tile('C', 3));
        String rackStr = player.rackToString();
        assertTrue(rackStr.contains("B:3"));
        assertTrue(rackStr.contains("C:3"));
    }

    @Test
    public void testFindTileInRack() {
        Player player = new Player("Taylor");
        Tile tile = new Tile('D', 2);
        player.addTile(tile);
        Tile found = player.findTileInRack('D');
        assertNotNull(found);
        assertEquals(tile, found);
    }

    @Test
    public void testCanFormWordTrue() {
        Player player = new Player("Riley");
        player.addTile(new Tile('C', 3));
        player.addTile(new Tile('A', 1));
        player.addTile(new Tile('T', 1));
        assertTrue(player.canFormWord("CAT"));
    }

    @Test
    public void testCanFormWordFalse() {
        Player player = new Player("Drew");
        player.addTile(new Tile('C', 3));
        player.addTile(new Tile('A', 1));
        assertFalse(player.canFormWord("DOG"));
    }

    @Test
    public void testUseTilesForWord() {
        Player player = new Player("Jordan");
        player.addTile(new Tile('H', 4));
        player.addTile(new Tile('E', 1));
        player.addTile(new Tile('L', 1));
        player.addTile(new Tile('L', 1));
        player.addTile(new Tile('O', 1));

        int initialSize = player.getRack().size();
        player.useTilesForWord("HELLO"); // no return value
        int finalSize = player.getRack().size();

        assertTrue(finalSize < initialSize); // tiles were removed
    }
}