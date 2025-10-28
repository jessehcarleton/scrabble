import org.junit.Test;
import static org.junit.Assert.*;

public class TileBagTest {

    @Test
    public void testDrawTileReturnsTile() {
        TileBag bag = new TileBag();
        Tile tile = bag.drawTile();
        assertNotNull(tile);
        assertTrue(Character.isLetter(tile.getLetter()));
    }

    @Test
    public void testDrawTileReturnsNullWhenEmpty() {
        TileBag bag = new TileBag();
        // Exhaust the bag
        for (int i = 0; i < 100; i++) {
            bag.drawTile();
        }
        Tile tile = bag.drawTile();
        assertNull(tile);
    }

    @Test
    public void testSizeDecreasesAfterDraw() {
        TileBag bag = new TileBag();
        int initialSize = bag.size(); // assuming you have a size() method
        bag.drawTile();
        assertEquals(initialSize - 1, bag.size());
    }

    @Test
    public void testDrawAllTiles() {
        TileBag bag = new TileBag();
        int count = 0;
        while (bag.drawTile() != null) {
            count++;
        }
        assertEquals(98, count); // assuming your bag starts with 100 tiles
        assertEquals(0, bag.size());
    }
}