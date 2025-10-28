import org.junit.Test;
import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testIsFirstMoveTrue() {
        Board board = new Board();
        assertTrue(board.isFirstMove());
    }

    @Test
    public void testIsFirstMoveFalseAfterPlacement() {
        Board board = new Board();
        Player player = new Player("Alex");
        player.getRack().clear();
        player.addTile(new Tile('C', 3));
        player.addTile(new Tile('A', 1));
        player.addTile(new Tile('T', 1));
        board.placeWord("CAT", 7, 7, true, player);
        assertFalse(board.isFirstMove());
    }

    @Test
    public void testPlaceWordHorizontalSuccess() {
        Board board = new Board();
        Player player = new Player("Jamie");
        player.getRack().clear();
        player.addTile(new Tile('H', 4));
        player.addTile(new Tile('E', 1));
        player.addTile(new Tile('L', 1));
        player.addTile(new Tile('L', 1));
        player.addTile(new Tile('O', 1));
        boolean placed = board.placeWord("HELLO", 7, 7, true, player);
        assertTrue(placed);
    }

    @Test
    public void testPlaceWordVerticalSuccess() {
        Board board = new Board();
        Player player = new Player("Sam");
        player.getRack().clear();
        player.addTile(new Tile('D', 2));
        player.addTile(new Tile('O', 1));
        player.addTile(new Tile('G', 2));
        boolean placed = board.placeWord("DOG", 7, 7, false, player);
        assertTrue(placed);
    }

    @Test
    public void testPlaceWordConflictFails() {
        Board board = new Board();
        Player player = new Player("Taylor");
        player.getRack().clear();
        player.addTile(new Tile('C', 3));
        player.addTile(new Tile('A', 1));
        player.addTile(new Tile('T', 1));
        board.placeWord("CAT", 7, 7, true, player);

        player.getRack().clear();
        player.addTile(new Tile('D', 2));
        player.addTile(new Tile('O', 1));
        player.addTile(new Tile('G', 2));
        boolean result = board.placeWord("DOG", 7, 7, true, player); // conflict at C vs D
        assertFalse(result);
    }

}