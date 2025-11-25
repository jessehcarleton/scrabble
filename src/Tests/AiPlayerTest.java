import org.junit.Test;
import static org.junit.Assert.*;
import javax.swing.SwingUtilities;
import java.util.*;

public class AiPlayerTest {

    @Test
    public void addPlayerMarksAiFlag() throws Exception {
        Dictionary dict = new Dictionary(new HashSet<>(Arrays.asList("AT")));
        Game game = new Game(dict);
        game.addPlayer("Bot", true);
        game.addPlayer("Human", false);

        assertEquals(2, game.getPlayers().size());
        assertTrue(game.getPlayers().get(0).isAi());
        assertFalse(game.getPlayers().get(1).isAi());
    }

    @Test
    public void aiPlaysLegalMoveCoveringCenter() throws Exception {
        Set<String> words = new HashSet<>(Arrays.asList("CAT", "AT"));
        Dictionary dict = new Dictionary(words);
        Game game = new Game(dict);
        game.addPlayer("Bot", true);
        game.addPlayer("Human", false);

        Player ai = game.getPlayers().get(0);
        ai.getRack().clear();
        ai.addTile(new Tile('C', 3));
        ai.addTile(new Tile('A', 1));
        ai.addTile(new Tile('T', 1));

        GameController controller = new GameController(game, null);
        // Flush the Swing queue so the AI's invokeLater turn runs before assertions
        SwingUtilities.invokeAndWait(() -> { });

        Tile center = game.getBoard().getTileAt(7, 7);
        assertNotNull(center); // first move must cover center
        assertEquals(1, game.getCurrentPlayerIndex()); // AI passed turn to the human
        assertEquals(10, ai.getScore()); // "CAT" on center double-word
    }
}