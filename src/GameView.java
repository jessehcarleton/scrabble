import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GameView is the GUI component of the Scrabble game.
 * It displays the board, player rack, score, and interactive controls.
 * <p>
 * Interaction model (Milestone 2 upgrade):
 * <ul>
 *   <li>Players select a rack tile, then click board cells to stage a placement (no free-typing).</li>
 *   <li>Orientation is chosen via radio buttons (Horizontal/Vertical). All staged drops must align.</li>
 *   <li>Staged letters are previewed on the board (ghost). Nothing is committed to the Model until "Commit Move".</li>
 *   <li>"Clear Move" removes the preview and re-enables rack tiles.</li>
 * </ul>
 * <p>
 * MVC compliance:
 * <ul>
 *   <li>This view never mutates the underlying model (no direct board or rack mutation).</li>
 *   <li>All intent is forwarded to the controller, which validates and updates the model.</li>
 * </ul>
 */
public class GameView extends JFrame {
    /** Controller mediator (set after construction). */
    private GameController controller;

    // Panels
    private JPanel boardPanel;
    private JPanel rackPanel;

    // Info labels
    private JLabel playerLabel;
    private JLabel scoreLabel;
    private JLabel bagLabel;
    private JLabel statusLabel;

    // Controls
    private JButton commitButton;
    private JButton clearButton;
    private JButton swapButton;
    private JButton passButton;

    // Orientation controls
    private JRadioButton horizontalRadio;
    private JRadioButton verticalRadio;

    // Board buttons for rendering + preview (no direct mutation to model)
    private final JButton[][] tileButtons = new JButton[15][15];

    // ---------- Pending placement state (View-only buffer) ----------
    /** Pending letters preview; 0 char means empty (no pending). */
    private final char[][] pendingLetters = new char[15][15];

    /** Ordered list of pending cell positions chosen by the user (for visual management). */
    private final List<Point> pendingCells = new ArrayList<>();

    /** Rack buttons, so we can disable/enable as tiles are used/cleared. */
    private final List<JButton> rackButtons = new ArrayList<>();

    /** Mapping from rack-button index to the Tile (letter, points). */
    private final List<Tile> rackSnapshot = new ArrayList<>();

    /** Which rack tile (index in rackButtons/rackSnapshot) is currently selected to place. -1 means none. */
    private int selectedRackIndex = -1;

    /** For quick access to letter on selected rack tile; kept in sync with selectedRackIndex. */
    private Character selectedRackLetter = null;

    /**
     * Constructs the GameView window and initializes all GUI components.
     * Sets size, layout, and centers the window on screen.
     */
    public GameView() {
        setTitle("Scrabble Game");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Initializes and lays out all GUI components, including:
     * <ul>
     *   <li>Top info bar (player, score, bag, status)</li>
     *   <li>Board grid with coordinate labels (16x16: labels + 15x15 board)</li>
     *   <li>Rack panel</li>
     *   <li>Controls panel with orientation + action buttons</li>
     * </ul>
     * Also wires the event listeners for commit/clear/swap/pass actions
     * and for board cell clicks. Board buttons are display-only—no model mutation here.
     */
    private void initComponents() {
        // Top panel: player info and score
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        playerLabel = new JLabel("Player: ");
        scoreLabel = new JLabel("Score: 0");
        bagLabel = new JLabel("Tiles in bag: --");
        statusLabel = new JLabel("Ready.");
        topPanel.add(playerLabel);
        topPanel.add(scoreLabel);
        topPanel.add(bagLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(statusLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center: Board with coordinate labels (16x16 grid: +1 row and +1 col for labels)
        boardPanel = new JPanel(new GridLayout(16, 16));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top-left corner empty
        boardPanel.add(new JLabel(""));

        // Column labels A..O
        for (int col = 0; col < 15; col++) {
            JLabel label = new JLabel(String.valueOf((char) ('A' + col)), SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            boardPanel.add(label);
        }

        // Rows with row labels and tile buttons
        for (int row = 0; row < 15; row++) {
            JLabel rowLabel = new JLabel(String.valueOf(row + 1), SwingConstants.CENTER);
            rowLabel.setFont(rowLabel.getFont().deriveFont(Font.BOLD));
            boardPanel.add(rowLabel);

            for (int col = 0; col < 15; col++) {
                final int r = row;
                final int c = col;

                JButton t = new JButton("");
                t.setPreferredSize(new Dimension(70, 70));
                t.setFont(t.getFont().deriveFont(Font.BOLD, 14f));
                t.setFocusPainted(false);

                // Visual hint for center
                if (row == 7 && col == 7) {
                    t.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
                }

                // Board cell click: stage a drop of the currently selected rack tile (preview only)
                t.addActionListener(ev -> onBoardCellClicked(r, c));

                tileButtons[row][col] = t;
                boardPanel.add(t);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // Bottom: Rack and controls
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Rack panel
        rackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        rackPanel.setBorder(BorderFactory.createTitledBorder("Your Rack (click to select; click board to drop)"));
        bottomPanel.add(rackPanel, BorderLayout.NORTH);

        // Controls panel
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBorder(BorderFactory.createTitledBorder("Turn Controls"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;

        controls.add(new JLabel("Orientation:"), gbc);
        gbc.gridx = 1;
        JPanel orientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        horizontalRadio = new JRadioButton("Horizontal", true);
        verticalRadio = new JRadioButton("Vertical");
        ButtonGroup group = new ButtonGroup();
        group.add(horizontalRadio);
        group.add(verticalRadio);
        orientPanel.add(horizontalRadio);
        orientPanel.add(verticalRadio);
        controls.add(orientPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        commitButton = new JButton("Commit Move");
        clearButton = new JButton("Clear Move");
        swapButton = new JButton("Swap Tiles");
        passButton = new JButton("Pass Turn");
        buttonsPanel.add(commitButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(swapButton);
        buttonsPanel.add(passButton);
        controls.add(buttonsPanel, gbc);

        // Wire up actions
        commitButton.addActionListener(this::onCommitMove);
        clearButton.addActionListener(e -> clearPendingPlacement());
        swapButton.addActionListener(this::onSwapTiles);
        passButton.addActionListener(e -> { if (controller != null) controller.handlePassTurn(); });

        bottomPanel.add(controls, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Binds the controller to this view so that user actions can be delegated.
     *
     * @param controller the GameController instance to use for handling intents
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Re-renders the view to reflect the current game state.
     * <ul>
     *   <li>Clears any staged/pending placement (since a new state is shown).</li>
     *   <li>Updates labels (player, score, tiles in bag, status).</li>
     *   <li>Rebuilds rack buttons for the active player.</li>
     *   <li>Paints the board from the model (existing placed tiles only).</li>
     * </ul>
     *
     * @param game the current Game model (must not be null)
     */
    public void renderGameState(Game game) {
        if (game == null) return;

        // Reset pending when the game state re-renders (e.g., after a commit or pass)
        clearPendingPlacement();

        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        playerLabel.setText("Player: " + current.getName());
        scoreLabel.setText("Score: " + current.getScore());
        bagLabel.setText("Tiles in bag: " + game.getTileBag().remainingTiles());
        statusLabel.setText("It's " + current.getName() + "'s turn. Select a rack tile, then click cells.");

        // Render rack buttons fresh
        rebuildRackButtons(current);

        // Render board from the model (base letters)
        Board board = game.getBoard();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Tile tile = board.getTileAt(row, col);
                JButton btn = tileButtons[row][col];
                btn.setText(tile == null ? "" : String.valueOf(tile.getLetter()));
                btn.setBackground(null);
            }
        }
    }

    /**
     * Displays an error message to the user and mirrors it in the status bar.
     *
     * @param message the error message to show
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText(message);
    }

    /**
     * Rebuilds the rack UI for the current player.
     * <ul>
     *   <li>Creates one button per tile (LETTER-POINTS).</li>
     *   <li>Wires each button to select that tile for staging onto the board.</li>
     *   <li>Resets prior selection state and snapshot of the rack.</li>
     * </ul>
     *
     * @param current the active player whose rack should be rendered
     */
    private void rebuildRackButtons(Player current) {
        rackPanel.removeAll();
        rackButtons.clear();
        rackSnapshot.clear();
        selectedRackIndex = -1;
        selectedRackLetter = null;

        for (int i = 0; i < current.getRack().size(); i++) {
            Tile t = current.getRack().get(i);
            rackSnapshot.add(t);

            JButton tileButton = new JButton(t.getLetter() + "-" + t.getPoints());
            tileButton.setPreferredSize(new Dimension(50, 50));
            tileButton.setFont(tileButton.getFont().deriveFont(Font.BOLD, 16f));
            final int idx = i;

            tileButton.addActionListener(ev -> onRackTileSelected(idx, tileButton));
            rackButtons.add(tileButton);
            rackPanel.add(tileButton);
        }
        rackPanel.revalidate();
        rackPanel.repaint();
    }

    /**
     * Handles selection of a tile from the player's rack.
     * Visually highlights the chosen button and stores its letter/index
     * so it can be staged onto the board on the next cell click.
     *
     * @param idx index of the selected rack tile
     * @param btn the JButton that was clicked (for visual highlight)
     */
    private void onRackTileSelected(int idx, JButton btn) {
        if (idx < 0 || idx >= rackSnapshot.size()) return;
        if (!btn.isEnabled()) return;

        // Visual select highlight
        for (JButton b : rackButtons) b.setBackground(null);
        btn.setBackground(new Color(255, 255, 180));

        selectedRackIndex = idx;
        selectedRackLetter = rackSnapshot.get(idx).getLetter();
        statusLabel.setText("Selected: " + selectedRackLetter + ". Click a board cell to drop.");
    }

    /**
     * Handles a board cell click to stage a pending tile drop (preview only).
     * Rules enforced here:
     * <ul>
     *   <li>A rack tile must have been selected first.</li>
     *   <li>Cannot drop on an already occupied model cell.</li>
     *   <li>All pending cells must align along the chosen orientation (row for H, column for V).</li>
     *   <li>Cannot place two pending tiles in the same cell.</li>
     * </ul>
     * If successful, the cell shows a ghost letter, and the rack button is disabled.
     *
     * @param r row index of the clicked board cell (0–14)
     * @param c column index of the clicked board cell (0–14)
     */
    private void onBoardCellClicked(int r, int c) {
        if (controller == null || controller.getGame() == null) {
            showError("Game is not initialized.");
            return;
        }

        // Must select a rack tile first
        if (selectedRackIndex == -1 || selectedRackLetter == null) {
            statusLabel.setText("Select a rack tile first, then click a board cell.");
            return;
        }

        // If that board cell already has a model tile (occupied), we cannot drop there
        Board board = controller.getGame().getBoard();
        if (board.getTileAt(r, c) != null) {
            statusLabel.setText("That square already has a tile. Choose another square.");
            return;
        }

        // Orientation constraint: all pending cells must share the same row (H) or column (V)
        boolean horizontal = horizontalRadio.isSelected();
        if (!pendingCells.isEmpty()) {
            Point anchor = pendingCells.get(0);
            if (horizontal && r != anchor.y) {
                statusLabel.setText("All tiles must be on row " + (anchor.y + 1) + " for horizontal.");
                return;
            }
            if (!horizontal && c != anchor.x) {
                statusLabel.setText("All tiles must be on column " + (char)('A' + anchor.x) + " for vertical.");
                return;
            }
        }

        // If we already placed a pending letter here, ignore
        if (pendingLetters[r][c] != 0) {
            statusLabel.setText("You already placed a pending tile there. Choose another square.");
            return;
        }

        // Place as pending preview
        pendingLetters[r][c] = selectedRackLetter;
        pendingCells.add(new Point(c, r)); // store as (x=col, y=row) for easier sorting
        tileButtons[r][c].setText(String.valueOf(selectedRackLetter));
        tileButtons[r][c].setBackground(new Color(200, 240, 255)); // ghost color

        // Disable that rack button (consumed for preview)
        JButton rb = rackButtons.get(selectedRackIndex);
        rb.setEnabled(false);
        rb.setBackground(null);

        // Clear current selection (user may pick another rack tile)
        selectedRackIndex = -1;
        selectedRackLetter = null;
        statusLabel.setText("Pending tiles: " + pendingCells.size() + ". Select another rack tile or Commit.");
    }

    /**
     * Commits the staged move:
     * <ol>
     *   <li>Validates that pending tiles exist and align with the selected orientation.</li>
     *   <li>Builds the contiguous word spanning min..max across the oriented axis,
     *       ensuring no gaps (every cell in the span is either an existing board tile
     *       or a newly staged pending tile).</li>
     *   <li>Delegates placement to the controller (which calls the model). On success, the
     *       controller re-renders the game state which clears the preview; on failure, the
     *       preview remains so the user can adjust or clear.</li>
     * </ol>
     *
     * @param e the ActionEvent from the "Commit Move" button
     */
    private void onCommitMove(ActionEvent e) {
        if (controller == null || controller.getGame() == null) return;

        if (pendingCells.isEmpty()) {
            statusLabel.setText("No pending tiles to commit. Select a rack tile and click the board.");
            return;
        }

        Game game = controller.getGame();
        Board board = game.getBoard();
        boolean horizontal = horizontalRadio.isSelected();

        // Determine line (row for horizontal, column for vertical) and span
        int fixedRow = pendingCells.get(0).y;
        int fixedCol = pendingCells.get(0).x;

        // Confirm all aligned on orientation
        for (Point p : pendingCells) {
            if (horizontal && p.y != fixedRow) { showError("Pending tiles not on the same row."); return; }
            if (!horizontal && p.x != fixedCol) { showError("Pending tiles not on the same column."); return; }
        }

        // Determine span: min..max along orientation (we require no gaps)
        int min, max;
        if (horizontal) {
            min = 14; max = 0;
            for (Point p : pendingCells) { min = Math.min(min, p.x); max = Math.max(max, p.x); }

            StringBuilder sb = new StringBuilder();
            for (int c = min; c <= max; c++) {
                Tile existing = board.getTileAt(fixedRow, c);
                char ch = (existing != null) ? existing.getLetter() : pendingLetters[fixedRow][c];
                if (ch == 0) {
                    // disallow gaps: user must fill the span unless covered by existing tiles
                    showError("There is a gap in your placement. Fill all squares in the span or clear and retry.");
                    return;
                }
                sb.append(ch);
            }
            String word = sb.toString();
            int startRow = fixedRow;
            int startCol = min;
            controller.handlePlaceWord(word, startRow, startCol, true);
        } else {
            min = 14; max = 0;
            for (Point p : pendingCells) { min = Math.min(min, p.y); max = Math.max(max, p.y); }

            StringBuilder sb = new StringBuilder();
            for (int r = min; r <= max; r++) {
                Tile existing = board.getTileAt(r, fixedCol);
                char ch = (existing != null) ? existing.getLetter() : pendingLetters[r][fixedCol];
                if (ch == 0) {
                    showError("There is a gap in your placement. Fill all squares in the span or clear and retry.");
                    return;
                }
                sb.append(ch);
            }
            String word = sb.toString();
            int startRow = min;
            int startCol = fixedCol;
            controller.handlePlaceWord(word, startRow, startCol, false);
        }
        // Success path will cause renderGameState(...) → which calls clearPendingPlacement().
        // Failure path: controller calls showError, but pending preview remains so user can adjust or Clear.
    }

    /**
     * Clears all staged/pending placements from the board preview and
     * re-enables any rack buttons that were disabled during staging.
     * Also resets the current rack selection and refreshes the board
     * from the model to ensure consistency with the controller.
     */
    private void clearPendingPlacement() {
        // Clear preview from board
        for (Point p : pendingCells) {
            int r = p.y, c = p.x;
            pendingLetters[r][c] = 0;
            JButton btn = tileButtons[r][c];
            btn.setBackground(null);
        }
        pendingCells.clear();

        // Re-enable any disabled rack buttons
        for (JButton b : rackButtons) {
            if (!b.isEnabled()) {
                b.setEnabled(true);
            }
            b.setBackground(null);
        }

        // Clear current rack selection
        selectedRackIndex = -1;
        selectedRackLetter = null;

        statusLabel.setText("Cleared pending move. Select a rack tile, then click the board.");
        // Repaint board from current model state if controller is present
        if (controller != null) {
            Game game = controller.getGame();
            if (game != null) {
                Board board = game.getBoard();
                for (int row = 0; row < 15; row++) {
                    for (int col = 0; col < 15; col++) {
                        Tile tile = board.getTileAt(row, col);
                        JButton btn = tileButtons[row][col];
                        btn.setText(tile == null ? "" : String.valueOf(tile.getLetter()));
                    }
                }
            }
        }
    }

    /**
     * Prompts the user for a space-separated list of rack indices,
     * validates the input, and delegates to the controller to perform the swap.
     * If the input is invalid, shows an error and returns without action.
     *
     * @param e the ActionEvent from the "Swap Tiles" button
     */
    private void onSwapTiles(ActionEvent e) {
        if (controller == null || controller.getGame() == null) return;

        // Simple input dialog: indices separated by spaces (e.g., "0 2 5")
        String input = JOptionPane.showInputDialog(this,
                "Enter indices of tiles to swap (e.g., 0 2 5):",
                "Swap Tiles",
                JOptionPane.PLAIN_MESSAGE);

        if (input == null) return; // canceled

        input = input.trim();
        if (input.isEmpty()) {
            showError("No indices provided.");
            return;
        }

        String[] parts = input.split("\\s+");
        List<Integer> indices = new ArrayList<>();
        for (String p : parts) {
            try {
                indices.add(Integer.parseInt(p));
            } catch (NumberFormatException ex) {
                showError("Invalid index: " + p);
                return;
            }
        }
        controller.handleSwapTiles(indices);
    }
}
