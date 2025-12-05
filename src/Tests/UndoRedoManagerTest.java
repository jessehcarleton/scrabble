import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * JUnit tests for the undo/redo infrastructure introduced in Milestone 4.
 * <p>
 * These tests exercise {@link UndoRedoManager} and {@link GameState} at the
 * model level (no GUI), ensuring that:
 * <ul>
 *     <li>States are recorded in order</li>
 *     <li>Undo restores an earlier snapshot</li>
 *     <li>Redo re-applies an undone snapshot</li>
 * </ul>
 */
public class UndoRedoManagerTest {

    /**
     * Creates a simple two-player Game instance backed by a tiny dictionary.
     * The dictionary contains just a small set of words and is sufficient for
     * testing score changes and turn order.
     *
     * @return a new Game instance
     */
    private Game createSimpleGame() {
        Set<String> words = new HashSet<>(Arrays.asList("CAT", "DOG", "AAA"));
        Dictionary dictionary = new Dictionary(words);
        Game game = new Game(dictionary);
        game.addPlayer("Player 1");
        game.addPlayer("Player 2");
        return game;
    }

    /**
     * Verifies that a single undo restores the previous game snapshot.
     */
    @Test
    public void testSingleUndoRestoresPreviousScore() {
        UndoRedoManager manager = new UndoRedoManager();
        Game game = createSimpleGame();

        // Baseline state: both players at score 0.
        GameState initial = GameState.from(game);
        manager.push(initial);

        // Mutate: give current player some points and push new state.
        game.getCurrentPlayer().addScore(15);
        GameState afterScore = GameState.from(game);
        manager.push(afterScore);

        assertTrue("Undo should be possible after at least two states", manager.canUndo());
        assertFalse("Redo should not be possible before any undo", manager.canRedo());

        // Undo should restore the baseline snapshot (score 0).
        GameState previous = manager.undo();
        Game restored = previous.getGame();
        assertEquals("Score after undo should match baseline",
                0, restored.getCurrentPlayer().getScore());

        // After undo, redo should now be available.
        assertTrue("Redo should be possible after an undo", manager.canRedo());
    }

    /**
     * Verifies multi-step undo and redo over three different snapshots.
     */
    @Test
    public void testMultiStepUndoAndRedo() {
        UndoRedoManager manager = new UndoRedoManager();
        Game game = createSimpleGame();

        // Snapshot 1: score 0
        GameState s0 = GameState.from(game);
        manager.push(s0);

        // Snapshot 2: score 10
        game.getCurrentPlayer().addScore(10);
        GameState s1 = GameState.from(game);
        manager.push(s1);

        // Snapshot 3: score 25
        game.getCurrentPlayer().addScore(15);
        GameState s2 = GameState.from(game);
        manager.push(s2);

        // Undo once: expect score 10
        GameState step1 = manager.undo();
        assertEquals(10, step1.getGame().getCurrentPlayer().getScore());

        // Undo again: expect score 0
        GameState step2 = manager.undo();
        assertEquals(0, step2.getGame().getCurrentPlayer().getScore());
        assertFalse("Cannot undo beyond the oldest recorded state", manager.canUndo());

        // Redo once: expect score 10 again
        GameState redo1 = manager.redo();
        assertEquals(10, redo1.getGame().getCurrentPlayer().getScore());
    }
}
