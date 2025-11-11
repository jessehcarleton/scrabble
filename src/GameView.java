import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * GameView is the graphical user interface (GUI) component of the Scrabble game.
 * It displays the board, player rack, score, and interactive buttons.
 * Users can select tiles from their rack and place them on the board by clicking.
 * This class follows the MVC pattern and communicates with GameController.
 */
public class GameView extends JFrame {
    private GameController controller;

    private JPanel boardPanel;
    private JPanel rackPanel;
    private JLabel playerLabel;
    private JLabel scoreLabel;
    private JButton placeButton;
    private JButton swapButton;
    private JButton passButton;

    // Interaction state
    private Tile selectedTile = null;
    private JButton selectedRackButton = null;

    // Board buttons for rendering
    private JButton[][] tileButtons = new JButton[15][15];

    /**
     * Constructs the GameView window and initializes all GUI components.
     */
    public GameView() {
        setTitle("Scrabble Game");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        setVisible(true);
    }

    /**
     * Initializes and lays out all GUI components including board, rack, and control buttons.
     * Adds row and column labels to the board for easier tile referencing.
     */
    private void initComponents() {
        // Top panel for player info
        JPanel topPanel = new JPanel(new FlowLayout());
        playerLabel = new JLabel("Player: ");
        scoreLabel = new JLabel("Score: 0");
        topPanel.add(playerLabel);
        topPanel.add(scoreLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for board with labels (16x16: labels + 15x15 grid)
        boardPanel = new JPanel(new GridLayout(16, 16));

        // Top-left corner (empty)
        boardPanel.add(new JLabel(""));

        // Column labels A–O
        for (int col = 0; col < 15; col++) {
            JLabel label = new JLabel(String.valueOf((char) ('A' + col)), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            boardPanel.add(label);
        }

        // Rows with row labels and tile buttons
        for (int row = 0; row < 15; row++) {
            JLabel rowLabel = new JLabel(String.valueOf(row + 1), SwingConstants.CENTER);
            rowLabel.setFont(new Font("Arial", Font.BOLD, 12));
            boardPanel.add(rowLabel);

            for (int col = 0; col < 15; col++) {
                JButton tileBtn = new JButton("");
                tileBtn.setPreferredSize(new Dimension(40, 40));
                tileBtn.setFont(new Font("Arial", Font.BOLD, 16));
                tileButtons[row][col] = tileBtn;

                final int r = row;
                final int c = col;

                tileBtn.addActionListener(e -> {
                    if (controller == null || controller.getGame() == null) {
                        showError("Game is not initialized.");
                        return;
                    }
                    if (selectedTile == null) {
                        // No tile selected from rack
                        return;
                    }
                    // Example rule: center enforcement for first placement, if your Board supports it
                    Board board = controller.getGame().getBoard();
                    if (board != null && board.isFirstMove() && !(r == 7 && c == 7)) {
                        showError("First tile must be placed at center (H8).");
                        return;
                    }

                    // Place on board and remove from rack
                    board.setTileAt(selectedTile, r, c);
                    controller.getGame().getCurrentPlayer().getRack().remove(selectedTile);

                    // Disable the rack button to show it's used
                    if (selectedRackButton != null) {
                        selectedRackButton.setEnabled(false);
                        selectedRackButton.setBackground(null);
                    }

                    // Clear selection
                    selectedTile = null;
                    selectedRackButton = null;

                    // Re-render
                    renderGameState(controller.getGame());
                });

                boardPanel.add(tileBtn);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        // Bottom panel for rack and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        rackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        bottomPanel.add(rackPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        placeButton = new JButton("Place Word");
        swapButton = new JButton("Swap Tiles");
        passButton = new JButton("Pass Turn");

        buttonPanel.add(placeButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(passButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Button listeners (wire as needed)
        placeButton.addActionListener(e -> {
            showError("Use rack and board clicks to place tiles.");
        });

        swapButton.addActionListener(e -> {
            if (controller != null) {
                controller.handleSwapTiles(java.util.Arrays.asList(0, 2));
            }
        });

        passButton.addActionListener(e -> {
            if (controller != null) {
                controller.handlePassTurn();
            }
        });
    }

    /**
     * Sets the controller for this view.
     * @param controller the GameController instance
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Updates the GUI to reflect the current game state.
     * Displays current player's name, score, rack, and board.
     * @param game the current Game model
     */
    public void renderGameState(Game game) {
        if (game == null) return;

        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        playerLabel.setText("Player: " + current.getName());
        scoreLabel.setText("Score: " + current.getScore());

        // Render rack (use String.valueOf to reliably show char letters)
        rackPanel.removeAll();
        for (Tile t : current.getRack()) {
            JButton tileButton = new JButton(String.valueOf(t.getLetter()));
            tileButton.setPreferredSize(new Dimension(50, 50));
            tileButton.setFont(new Font("Arial", Font.BOLD, 16));
            tileButton.addActionListener(e -> {
                // Set selection and highlight
                selectedTile = t;

                if (selectedRackButton != null && selectedRackButton != tileButton) {
                    selectedRackButton.setBackground(null);
                }
                selectedRackButton = tileButton;
                tileButton.setBackground(Color.YELLOW);
            });
            rackPanel.add(tileButton);
        }
        rackPanel.revalidate();
        rackPanel.repaint();

        // Render board from the model
        Board board = game.getBoard();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Tile tile = board.getTileAt(row, col);
                tileButtons[row][col].setText(tile == null ? "" : String.valueOf(tile.getLetter()));
            }
        }
    }

    /**
     * Displays an error message dialog to the user.
     * @param message the error message to show
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Swaps selected tiles from the player's rack.
     * @param player the current player
     * @param indices the indices of tiles to swap
     */
    public void swapTiles(Player player, List<Integer> indices) {
        // TODO: implement tile swap logic or delegate to controller
    }
}