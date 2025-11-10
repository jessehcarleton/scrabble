import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * GameView is the graphical user interface (GUI) component of the Scrabble game.
 * It displays the board, player rack, score, and interactive buttons.
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

        // Center panel for board with labels
        boardPanel = new JPanel(new GridLayout(16, 16)); // 15x15 board + labels

        // Top-left corner (empty)
        boardPanel.add(new JLabel(""));

        // Column labels A–O
        for (int col = 0; col < 15; col++) {
            JLabel label = new JLabel(String.valueOf((char) ('A' + col)), SwingConstants.CENTER);
            boardPanel.add(label);
        }

        // Rows with row labels and tile buttons
        for (int row = 0; row < 15; row++) {
            JLabel rowLabel = new JLabel(String.valueOf(row + 1), SwingConstants.CENTER);
            boardPanel.add(rowLabel);

            for (int col = 0; col < 15; col++) {
                JButton tile = new JButton("");
                tile.setPreferredSize(new Dimension(40, 40));
                boardPanel.add(tile);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        // Bottom panel for rack and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        rackPanel = new JPanel(new FlowLayout());
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

        // Button listeners (to be wired to controller)
        placeButton.addActionListener(e -> {
            // Example placeholder — replace with dialog input
            controller.handlePlaceWord("HELLO", 7, 7, true);
        });

        swapButton.addActionListener(e -> {
            controller.handleSwapTiles(java.util.Arrays.asList(0, 2)); // Java 8 compatible
        });

        passButton.addActionListener(e -> {
            controller.handlePassTurn();
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
        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        playerLabel.setText("Player: " + current.getName());
        scoreLabel.setText("Score: " + current.getScore());

        rackPanel.removeAll();
        for (Tile t : current.getRack()) {
            JButton tileButton = new JButton(String.valueOf(t.getLetter()));
            rackPanel.add(tileButton);
        }
        rackPanel.revalidate();
        rackPanel.repaint();

        // TODO: render board tiles from game.getBoard()
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