import org.junit.Test;
import static org.junit.Assert.*;

public class PremiumSquaresTest {

    // helper method to add tiles manually (replaces TestHelpers.addLettersToRack)
    private void addLettersToRack(Player player, String letters) {
        for (char c : letters.toCharArray()) {
            // You can adjust the Tile constructor if it uses a different signature
            player.getRack().add(new Tile(c, getLetterValue(c)));
        }
    }

    // helper method to get letter scores (basic Scrabble values)
    private int getLetterValue(char c) {
        c = Character.toUpperCase(c);
        switch (c) {
            case 'Q': case 'Z': return 10;
            case 'J': case 'X': return 8;
            case 'K': return 5;
            case 'F': case 'H': case 'V': case 'W': case 'Y': return 4;
            case 'B': case 'C': case 'M': case 'P': return 3;
            case 'D': case 'G': return 2;
            default: return 1;
        }
    }

    @Test
    public void tripleWordAndDoubleLetterAppliedToNewTiles() {
        Board board = new Board();
        // Seed an adjacent tile so the placement is connected (bypasses first-move rule for this test)
        board.setTileAt(new Tile('A', 1), 0, 6);

        Player player = new Player("Morgan");
        player.getRack().clear();
        addLettersToRack(player, "QUIZ");

        boolean placed = board.placeWord("QUIZ", 0, 7, false, player); // vertical starting on a triple-word square
        assertTrue(placed);
        // Q(10) + U(1) + I(1) + Z(10*2 on double-letter) = 32, then triple-word => 96
        assertEquals(96, player.getScore());
    }

    @Test
    public void premiumsNotReappliedToExistingTiles() {
        Board board = new Board();

        Player first = new Player("P1");
        first.getRack().clear();
        addLettersToRack(first, "HELLO");
        assertTrue(board.placeWord("HELLO", 7, 5, true, first)); // first move covers center (double word)

        Player second = new Player("P2");
        second.getRack().clear();
        addLettersToRack(second, "CELL");

        boolean placed = board.placeWord("CELL", 5, 7, false, second); // reuses the L on the center square
        assertTrue(placed);
        // Only new tiles count toward premiums; reused center double-word square should not multiply
        assertEquals(6, second.getScore());
    }
}
