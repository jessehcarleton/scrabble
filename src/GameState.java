import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Represents a snapshot of the {@link Game} model at a single point in time.
 * <p>
 * This class implements the "Memento" pattern used by the undo/redo system.
 * Internally it stores a deep copy of the Game using Java serialization so
 * that the original Game object can evolve independently of the snapshot.
 */
public class GameState {

    /** Immutable deep copy of the game at the time this state was created. */
    private final Game gameSnapshot;

    private GameState(Game gameSnapshot) {
        this.gameSnapshot = gameSnapshot;
    }

    /**
     * Creates a new {@code GameState} that is a deep copy of the provided game.
     *
     * @param source the current game to snapshot
     * @return a new {@code GameState} containing a deep copy of the game
     * @throws IllegalStateException if the game cannot be cloned
     */
    public static GameState from(Game source) {
        return new GameState(deepCopy(source));
    }

    /**
     * Returns the stored deep copy of the game.
     * <p>
     * Note: this returns the cloned instance inside the snapshot, not the
     * original {@code Game} object.
     *
     * @return the snapshot's Game instance
     */
    public Game getGame() {
        return gameSnapshot;
    }

    /**
     * Performs a deep copy of the given game using Java serialization.
     *
     * @param original the Game instance to clone
     * @return a deep copy of the original Game
     * @throws IllegalStateException if cloning fails
     */
    private static Game deepCopy(Game original) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(original);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                return (Game) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Failed to clone game state for undo/redo", e);
        }
    }
}
