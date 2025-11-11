import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GameView is the GUI component of the Scrabble game.
 * It displays the board, player rack, score, and interactive controls.
 *
 * Interaction model:
 * - Players click a rack tile to select it, then click board cells to stage placement.
 * - Orientation is chosen via radio buttons (Horizontal/Vertical). All staged drops must align.
 * - Staged letters are previewed (ghost) until "Commit Move" is pressed.
 * - "Clear Move" removes the preview and re-enables rack tiles.
 *
 * MVC:
 * - This view never mutates the model directly; it forwards intents to the controller.
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

    // Board buttons (display + preview only; no model mutation here)
    private final JButton[][] tileButtons = new JButton[15][15];

    // ---------- Pending placement state (View-only buffer) ----------
    /** Pending letters preview; 0 char means empty. */
    private final char[][] pendingLetters = new char[15][15];
    /** Ordered list of pending cells for convenience. Stores points as (x=col, y=row). */
    private final List<Point> pendingCells = new ArrayList<>();

    /** Rack buttons so we can disable/enable during staging. */
    private final List<JButton> rackButtons = new ArrayList<>();
    /** Snapshot of the rack's tiles to read letters/points without touching model. */
    private final List<Tile> rackSnapshot = new ArrayList<>();

    /** Selected rack tile index; -1 means none. */
    private int selectedRackIndex = -1;
    /** Selected rack tile letter (cached). */
    private Character selectedRackLetter = null;

    // ---------- UI size constants (bigger tiles) ----------
    private static final Dimension BOARD_CELL_SIZE = new Dimension(52, 52);
    private static final float BOARD_FONT_SIZE = 22f;
    private static final Dimension RACK_TILE_SIZE = new Dimension(72, 72);
    private static final float RACK_FONT_SIZE = 28f; // used as fallback if HTML not honored

    /**
     * Constructs the GameView window and initializes all GUI components.
     * Enlarges the window to fit bigger cells and rack tiles.
     */
    public GameView() {
        setTitle("Scrabble Game");
        setSize(1280, 900);
        setMinimumSize(new Dimension(1100, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Initializes all GUI components and listeners.
     * - Top info bar (player, score, bag, status)
     * - Board grid with coordinate labels (16x16 with header/side labels)
     * - Rack panel
     * - Controls panel with orientation + action buttons
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

        // Center: Board with coordinate labels (16x16 grid)
        boardPanel = new JPanel(new GridLayout(16, 16, 4, 4));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(new Color(245, 245, 245));

        // Top-left corner (empty)
        boardPanel.add(new JLabel(""));

        // Column labels A..O
        for (int col = 0; col < 15; col++) {
            JLabel label = new JLabel(String.valueOf((char) ('A' + col)), SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
            boardPanel.add(label);
        }

        // Rows with row labels and tile buttons
        for (int row = 0; row < 15; row++) {
            JLabel rowLabel = new JLabel(String.valueOf(row + 1), SwingConstants.CENTER);
            rowLabel.setFont(rowLabel.getFont().deriveFont(Font.BOLD, 14f));
            boardPanel.add(rowLabel);

            for (int col = 0; col < 15; col++) {
                final int r = row;
                final int c = col;

                JButton t = new JButton("");
                t.setPreferredSize(BOARD_CELL_SIZE);
                t.setMinimumSize(BOARD_CELL_SIZE);
                t.setMaximumSize(BOARD_CELL_SIZE);
                t.setMargin(new Insets(0, 0, 0, 0));
                t.setFocusPainted(false);
                t.setContentAreaFilled(true);
                t.setOpaque(true);
                t.setFont(t.getFont().deriveFont(Font.BOLD, BOARD_FONT_SIZE));
                t.setHorizontalTextPosition(SwingConstants.CENTER);

                if (row == 7 && col == 7) {
                    t.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 2));
                }

                t.addActionListener(ev -> onBoardCellClicked(r, c));

                tileButtons[row][col] = t;
                boardPanel.add(t);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // Bottom: Rack and controls
        JPanel bottomPanel = new JPanel(new BorderLayout());

        rackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        rackPanel.setBorder(BorderFactory.createTitledBorder("Your Rack (click to select; click board to drop)"));
        bottomPanel.add(rackPanel, BorderLayout.NORTH);

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBorder(BorderFactory.createTitledBorder("Turn Controls"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;

        controls.add(new JLabel("Orientation:"), gbc);
        gbc.gridx = 1;
        JPanel orientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        horizontalRadio = new JRadioButton("Horizontal", true);
        verticalRadio = new JRadioButton("Vertical");
        ButtonGroup group = new ButtonGroup();
        group.add(horizontalRadio);
        group.add(verticalRadio);
        orientPanel.add(horizontalRadio);
        orientPanel.add(verticalRadio);
        controls.add(orientPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        commitButton = new JButton("Commit Move");
        clearButton = new JButton("Clear Move");
        swapButton = new JButton("Swap Tiles");
        passButton = new JButton("Pass Turn");

        Dimension actionSize = new Dimension(150, 36);
        for (JButton b : new JButton[]{commitButton, clearButton, swapButton, passButton}) {
            b.setPreferredSize(actionSize);
            b.setFocusPainted(false);
        }

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
     * Clears staged placements, updates labels, rebuilds rack, and paints the board.
     *
     * @param game the current Game model
     */
    public void renderGameState(Game game) {
        if (game == null) return;

        clearPendingPlacement();

        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        playerLabel.setText("Player: " + current.getName());
        scoreLabel.setText("Score: " + current.getScore());
        bagLabel.setText("Tiles in bag: " + game.getTileBag().remainingTiles());
        statusLabel.setText("It's " + current.getName() + "'s turn. Select a rack tile, then click cells.");

        rebuildRackButtons(current);

        Board board = game.getBoard();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Tile tile = board.getTileAt(row, col);
                JButton btn = tileButtons[row][col];
                btn.setText(tile == null ? "" : String.valueOf(tile.getLetter()));
                btn.setBackground(null);
            }
        }

        boardPanel.revalidate();
        rackPanel.revalidate();
        boardPanel.repaint();
        rackPanel.repaint();
    }

    /**
     * Displays an error dialog and mirrors the message in the status bar.
     *
     * @param message the error message to show
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText(message);
    }

    /**
     * Builds the rack UI for the active player with LARGE buttons and **explicit points shown**:
     * - Uses Swing HTML text to render a big letter with a small score underneath.
     * - Also sets a tooltip (LETTER (points)) as a backup.
     *
     * @param current the active player
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

            // HTML text: big letter + small points on a new line (centered)
            String html = "<html><div style='text-align:center;'>"
                    + "<div style='font-size:28px; font-weight:bold; line-height:28px;'>"
                    + t.getLetter()
                    + "</div>"
                    + "<div style='font-size:12px; line-height:12px;'>"
                    + t.getPoints()
                    + "</div></div></html>";

            JButton tileButton = new JButton(html);
            tileButton.setToolTipText(t.getLetter() + " (" + t.getPoints() + ")");
            tileButton.setPreferredSize(RACK_TILE_SIZE);
            tileButton.setMinimumSize(RACK_TILE_SIZE);
            tileButton.setMaximumSize(RACK_TILE_SIZE);
            tileButton.setMargin(new Insets(0, 0, 0, 0));
            tileButton.setFocusPainted(false);
            tileButton.setContentAreaFilled(true);
            tileButton.setOpaque(true);

            // Fallback font (in case L&F disables HTML sizing)
            tileButton.setFont(tileButton.getFont().deriveFont(Font.BOLD, RACK_FONT_SIZE));
            tileButton.setHorizontalTextPosition(SwingConstants.CENTER);

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
     * Highlights the chosen button and stores its letter/index for staging.
     *
     * @param idx index of the selected rack tile
     * @param btn the JButton clicked
     */
    private void onRackTileSelected(int idx, JButton btn) {
        if (idx < 0 || idx >= rackSnapshot.size()) return;
        if (!btn.isEnabled()) return;

        for (JButton b : rackButtons) b.setBackground(null);
        btn.setBackground(new Color(255, 255, 180));

        selectedRackIndex = idx;
        selectedRackLetter = rackSnapshot.get(idx).getLetter();
        statusLabel.setText("Selected: " + selectedRackLetter + ". Click a board cell to drop.");
    }

    /**
     * Handles a board cell click to stage a pending tile drop (preview only).
     * Enforces: a rack tile must be selected, target must be empty in model,
     * and all staged tiles align to the chosen orientation.
     *
     * @param r row index (0–14)
     * @param c column index (0–14)
     */
    private void onBoardCellClicked(int r, int c) {
        if (controller == null || controller.getGame() == null) {
            showError("Game is not initialized.");
            return;
        }

        if (selectedRackIndex == -1 || selectedRackLetter == null) {
            statusLabel.setText("Select a rack tile first, then click a board cell.");
            return;
        }

        Board board = controller.getGame().getBoard();
        if (board.getTileAt(r, c) != null) {
            statusLabel.setText("That square already has a tile. Choose another square.");
            return;
        }

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

        if (pendingLetters[r][c] != 0) {
            statusLabel.setText("You already placed a pending tile there. Choose another square.");
            return;
        }

        pendingLetters[r][c] = selectedRackLetter;
        pendingCells.add(new Point(c, r));
        JButton cell = tileButtons[r][c];
        cell.setText(String.valueOf(selectedRackLetter));
        cell.setBackground(new Color(190, 230, 255));

        JButton rb = rackButtons.get(selectedRackIndex);
        rb.setEnabled(false);
        rb.setBackground(null);

        selectedRackIndex = -1;
        selectedRackLetter = null;
        statusLabel.setText("Pending tiles: " + pendingCells.size() + ". Select another rack tile or Commit.");
    }

    /**
     * Commits the staged move:
     * - Validates orientation and contiguity (no gaps in the span).
     * - Builds the word including existing board letters in the span.
     * - Delegates to the controller to place the word via the model.
     *
     * @param e action event from "Commit Move"
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

        int fixedRow = pendingCells.get(0).y;
        int fixedCol = pendingCells.get(0).x;

        for (Point p : pendingCells) {
            if (horizontal && p.y != fixedRow) { showError("Pending tiles not on the same row."); return; }
            if (!horizontal && p.x != fixedCol) { showError("Pending tiles not on the same column."); return; }
        }

        if (horizontal) {
            int min = 14, max = 0;
            for (Point p : pendingCells) { min = Math.min(min, p.x); max = Math.max(max, p.x); }

            StringBuilder sb = new StringBuilder();
            for (int c = min; c <= max; c++) {
                Tile existing = board.getTileAt(fixedRow, c);
                char ch = (existing != null) ? existing.getLetter() : pendingLetters[fixedRow][c];
                if (ch == 0) { showError("There is a gap in your placement. Fill the span or clear and retry."); return; }
                sb.append(ch);
            }
            controller.handlePlaceWord(sb.toString(), fixedRow, min, true);
        } else {
            int min = 14, max = 0;
            for (Point p : pendingCells) { min = Math.min(min, p.y); max = Math.max(max, p.y); }

            StringBuilder sb = new StringBuilder();
            for (int r = min; r <= max; r++) {
                Tile existing = board.getTileAt(r, fixedCol);
                char ch = (existing != null) ? existing.getLetter() : pendingLetters[r][fixedCol];
                if (ch == 0) { showError("There is a gap in your placement. Fill the span or clear and retry."); return; }
                sb.append(ch);
            }
            controller.handlePlaceWord(sb.toString(), min, fixedCol, false);
        }
    }

    /**
     * Clears all staged placements, re-enables disabled rack buttons,
     * and repaints the board/rack from the model.
     */
    private void clearPendingPlacement() {
        for (Point p : pendingCells) {
            pendingLetters[p.y][p.x] = 0;
            JButton btn = tileButtons[p.y][p.x];
            btn.setBackground(null);
        }
        pendingCells.clear();

        for (JButton b : rackButtons) {
            if (!b.isEnabled()) b.setEnabled(true);
            b.setBackground(null);
        }

        selectedRackIndex = -1;
        selectedRackLetter = null;

        statusLabel.setText("Cleared pending move. Select a rack tile, then click the board.");

        if (controller != null && controller.getGame() != null) {
            Board board = controller.getGame().getBoard();
            for (int row = 0; row < 15; row++) {
                for (int col = 0; col < 15; col++) {
                    Tile tile = board.getTileAt(row, col);
                    JButton btn = tileButtons[row][col];
                    btn.setText(tile == null ? "" : String.valueOf(tile.getLetter()));
                }
            }
            boardPanel.revalidate();
            boardPanel.repaint();
            rackPanel.revalidate();
            rackPanel.repaint();
        }
    }

    /**
     * Simple dialog to collect indices to swap; validates and delegates to controller.
     *
     * @param e ActionEvent from "Swap Tiles"
     */
    private void onSwapTiles(ActionEvent e) {
        if (controller == null || controller.getGame() == null) return;

        String input = JOptionPane.showInputDialog(this,
                "Enter indices of tiles to swap (e.g., 0 2 5):",
                "Swap Tiles",
                JOptionPane.PLAIN_MESSAGE);

        if (input == null) return;

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
