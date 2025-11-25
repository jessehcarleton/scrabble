import org.junit.Test;
import static org.junit.Assert.*;

public class BlankTileTest {

    @Test
    public void blankTileActsAsWildcardAndScoresZero() {
        Board board = new Board();
        Player player = new Player("Casey");
        player.getRack().clear();
        player.addTile(new Tile('C', 3));
        player.addTile(new Tile('?', 0, true)); // blank tile
        player.addTile(new Tile('T', 1));

        boolean placed = board.placeWord("CAT", 7, 6, true, player); // uses center square
        assertTrue(placed);

        Tile blankTile = board.getTileAt(7, 7); // blank became the A
        assertNotNull(blankTile);
        assertTrue(blankTile.isBlank());
        assertEquals('A', blankTile.getLetter());
        assertEquals(0, blankTile.getPoints());

        // Center is a double-word square; blank contributes 0 points
        assertEquals((3 + 0 + 1) * 2, player.getScore());
    }

    @Test
    public void playerCanFormWordUsingBlankTile() {
        Player player = new Player("Avery");
        player.getRack().clear();
        player.addTile(new Tile('?', 0, true));
        player.addTile(new Tile('T', 1));

        assertTrue(player.canFormWord("AT"));   // blank covers the A
        assertFalse(player.canFormWord("BATS")); // missing letters even with blank
    }
}