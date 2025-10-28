import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    @Test
    public void testTileCreation() {
        Tile tile = new Tile('A', 1);
        assertEquals('A', tile.getLetter());
        assertEquals(1, tile.getPoints());
    }

    @Test
    public void testToStringFormat() {
        Tile tile = new Tile('Z', 10);
        assertEquals("Z:10", tile.toString());
    }
}