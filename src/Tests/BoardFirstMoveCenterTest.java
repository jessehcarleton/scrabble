// Tests/BoardFirstMoveCenterTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies the Milestone 2 rule now enforced in the model:
 * the very first move must cover the center square (H8 -> row 7, col 7).
 */
public class BoardFirstMoveCenterTest {

    private Board board;
    private Player p;

    @BeforeEach
    void setup() {
        board = new Board();
        p = new Player("P1");
        p.getRack().clear();
    }

    @Test
    void firstMoveAwayFromCenter_fails() {
        TestHelpers.addLettersToRack(p, "HELLO");
        boolean ok = board.placeWord("HELLO", 0, 0, true, p); // does NOT cross (7,7)
        assertFalse(ok, "First move that does not cross H8 must be rejected by the model.");
        assertEquals(0, p.getScore(), "No score should be awarded for a rejected placement.");
    }

    @Test
    void firstMoveCoveringCenter_succeeds() {
        TestHelpers.addLettersToRack(p, "HELLO");
        // Place horizontally so that one letter hits (7,7). Start at col 5 => cells 5..9 (center=7)
        boolean ok = board.placeWord("HELLO", 7, 5, true, p);
        assertTrue(ok, "First move covering the center must be accepted.");
        assertEquals(TestHelpers.scoreOf("HELLO"), p.getScore(), "Score should equal sum of letter values.");
    }
}
