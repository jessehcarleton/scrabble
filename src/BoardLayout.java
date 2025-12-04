/**
 * Immutable data holder that describes a Scrabble board layout.
 * <p>
 * A layout captures:
 * <ul>
 *     <li>A human-readable name for display in the GUI</li>
 *     <li>The size of the board (number of rows == number of columns)</li>
 *     <li>The premium-square configuration for every cell</li>
 * </ul>
 *
 * <p>
 * Instances of this class are created by {@link BoardLayoutLoader} when
 * parsing XML files. The {@link Board} uses a {@code BoardLayout} to
 * initialize its premiumGrid in the Milestone 4 custom-board feature.
 */
public class BoardLayout {

    private final String name;
    private final int size;
    private final Board.Premium[][] premiums;

    /**
     * Constructs a new layout instance.
     *
     * @param name     a human-readable name that will be shown in the GUI
     * @param size     the board size (number of rows and columns)
     * @param premiums a size x size matrix describing the premium squares
     */
    public BoardLayout(String name, int size, Board.Premium[][] premiums) {
        this.name = name;
        this.size = size;
        this.premiums = premiums;
    }

    /**
     * Returns the layout's display name.
     *
     * @return the layout name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the board dimension (for a 15x15 board, this returns 15).
     *
     * @return the board size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the premium value at the specified coordinate.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return the premium at that cell (never {@code null})
     */
    public Board.Premium getPremiumAt(int row, int col) {
        return premiums[row][col];
    }

    /**
     * Returns the underlying premium matrix. This is primarily used by
     * the {@link Board} constructor when copying the layout.
     *
     * @return 2D premium array
     */
    public Board.Premium[][] getPremiums() {
        return premiums;
    }

    /**
     * Returns the name so that UI widgets (combo boxes / dialogs) show
     * a friendly label automatically.
     *
     * @return the layout name
     */
    @Override
    public String toString() {
        return name;
    }
}
