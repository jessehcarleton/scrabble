import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the undo and redo history for a Scrabble game.
 * <p>
 * Internally maintains two stacks of {@link GameState}:
 * <ul>
 *     <li>undoStack – history of states (top is the current state)</li>
 *     <li>redoStack – states that were undone and can potentially be redone</li>
 * </ul>
 *
 * The controller is responsible for:
 * <ul>
 *     <li>pushing a new state after each completed move/turn</li>
 *     <li>using {@link #undo()} and {@link #redo()} to retrieve states</li>
 * </ul>
 */
public class UndoRedoManager {

    /** Maximum number of states to keep in undo history to avoid memory bloat. */
    private static final int MAX_HISTORY = 50;

    private final Deque<GameState> undoStack = new ArrayDeque<>();
    private final Deque<GameState> redoStack = new ArrayDeque<>();

    /**
     * Clears all undo and redo history.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Pushes a new current state onto the undo stack and discards
     * any redo history (standard undo/redo semantics).
     *
     * @param state the new current state after a completed move
     */
    public void push(GameState state) {
        if (state == null) {
            return;
        }
        undoStack.push(state);
        redoStack.clear();
        trimHistory();
    }

    /**
     * Returns whether there is at least one previous state to undo to.
     *
     * @return true if undo is possible, false otherwise
     */
    public boolean canUndo() {
        return undoStack.size() > 1; // need at least one "previous" state
    }

    /**
     * Returns whether there is at least one redone state that can be restored.
     *
     * @return true if redo is possible, false otherwise
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Performs an undo operation.
     * <p>
     * The current state is moved from the undo stack to the redo stack,
     * and the new top of the undo stack becomes the active state.
     *
     * @return the previous GameState to restore, or null if undo is not possible
     */
    public GameState undo() {
        if (!canUndo()) {
            return null;
        }
        GameState current = undoStack.pop();
        redoStack.push(current);
        return undoStack.peek();
    }

    /**
     * Performs a redo operation.
     * <p>
     * The top state from the redo stack becomes the new current state
     * and is pushed back onto the undo stack.
     *
     * @return the GameState to restore, or null if redo is not possible
     */
    public GameState redo() {
        if (!canRedo()) {
            return null;
        }
        GameState state = redoStack.pop();
        undoStack.push(state);
        return state;
    }

    /**
     * Ensures the undo history does not exceed {@link #MAX_HISTORY} entries.
     * Oldest states are dropped first.
     */
    private void trimHistory() {
        while (undoStack.size() > MAX_HISTORY) {
            undoStack.removeLast();
        }
    }
}
