// Tests/BoardConnectivityAndConsumptionTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests post-first-move connectivity and that only NEW tiles are consumed
 * when overlapping matching letters already on the board.
 */
public class BoardConnectivityAndConsumptionTest {

    private Board board;
    private Player p1;
    private Player p2;

    @BeforeEach
    void setup() {
        board = new Board();
        p1 = new Player("Alice");
        p2 = new Player("Bob");
        p1.getRack().clear();
        p2.getRack().clear();
    }

    @Test
    void isolatedWordAfterFirstMove_fails() {
        // First move (valid, covers center)
        TestHelpers.addLettersToRack(p1, "CAT");
        assertTrue(board.placeWord("CAT", 7, 6, true, p1));

        // Second move placed far away should fail (no connection)
        TestHelpers.addLettersToRack(p2, "DOG");
        boolean ok = board.placeWord("DOG", 0, 0, true, p2);
        assertFalse(ok, "Isolated word after first move must be rejected.");
    }

    @Test
    void overlappingMatchingLetters_consumesOnlyNewTiles() {
        // First move: CAT at row 7, cols 6..8
        TestHelpers.addLettersToRack(p1, "CAT");
        assertTrue(board.placeWord("CAT", 7, 6, true, p1));

        // Second move: BAT at same row starting col 6. 'A' and 'T' should match existing tiles.
        TestHelpers.addLettersToRack(p2, "BXX"); // only 'B' is actually needed; X fillers ignored
        int rackBefore = p2.getRack().size();

        assertTrue(board.placeWord("BAT", 7, 6, true, p2),
                "Overlapping with matching letters should be allowed.");

        int rackAfter = p2.getRack().size();
        assertEquals(rackBefore - 1, rackAfter,
                "Only one new tile ('B') should be consumed from the rack for the overlap.");
    }
}
