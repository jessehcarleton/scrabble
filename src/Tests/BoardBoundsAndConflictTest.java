// Tests/BoardBoundsAndConflictTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers remaining edge cases reinforced in M2:
 * - bounds checking with the new center-rule flow
 * - conflict rejection when an existing letter differs
 */
public class BoardBoundsAndConflictTest {

    private Board board;
    private Player p1;
    private Player p2;

    @BeforeEach
    void setup() {
        board = new Board();
        p1 = new Player("P1");
        p2 = new Player("P2");
        p1.getRack().clear();
        p2.getRack().clear();
    }

    @Test
    void horizontalOverflow_rejected() {
        // First move attempt that would overflow horizontally
        TestHelpers.addLettersToRack(p1, "ABCDEFGHIJKLM"); // len=13
        boolean ok = board.placeWord("ABCDEFGHIJKLM", 7, 5, true, p1); // uses col 5..17 -> OOB
        assertFalse(ok, "Horizontal out-of-bounds must be rejected.");
    }

    @Test
    void conflictOnOverlap_rejected() {
        // Valid first move
        TestHelpers.addLettersToRack(p1, "CAT");
        assertTrue(board.placeWord("CAT", 7, 6, true, p1));

        // Try vertical word that would place 'O' over 'A' at (7,7) -> conflict
        TestHelpers.addLettersToRack(p2, "DOG");
        boolean ok = board.placeWord("DOG", 6, 7, false, p2);
        assertFalse(ok, "Overlapping with mismatched letter must be rejected.");
    }
}
