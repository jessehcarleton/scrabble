// Tests/TestHelpers.java
/**
 * Simple helpers for tests: add tiles to racks with standard Scrabble points,
 * compute word scores, and read rack letters.
 */
public final class TestHelpers {
    private TestHelpers() {}

    /** Adds the given letters to the player's rack with standard point values. */
    public static void addLettersToRack(Player player, String letters) {
        for (char ch : letters.toUpperCase().toCharArray()) {
            player.addTile(new Tile(ch, points(ch)));
        }
    }

    /** Returns the total Scrabble letter-score for the given word (no premiums). */
    public static int scoreOf(String word) {
        int s = 0;
        for (char c : word.toUpperCase().toCharArray()) s += points(c);
        return s;
    }

    /** Standard Scrabble letter points (no blanks). */
    private static int points(char c) {
        switch (Character.toUpperCase(c)) {
            case 'A': case 'E': case 'I': case 'O': case 'U':
            case 'L': case 'N': case 'S': case 'T': case 'R': return 1;
            case 'D': case 'G': return 2;
            case 'B': case 'C': case 'M': case 'P': return 3;
            case 'F': case 'H': case 'V': case 'W': case 'Y': return 4;
            case 'K': return 5;
            case 'J': case 'X': return 8;
            case 'Q': case 'Z': return 10;
            default: return 0;
        }
    }
}
