import javax.swing.*;
import java.awt.*;

public class View extends JFrame{
    private JButton[][] boardButtons;
    private JButton placeButton, swapButton, nextButton;
    private JLabel rackLabel;

    public View(Board model) {
        super("Scrabble");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Board grid
        JPanel boardPanel = new JPanel(new GridLayout(15, 15));
        boardButtons = new JButton[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                boardButtons[i][j] = new JButton(" ");
                boardPanel.add(boardButtons[i][j]);
            }
        }

        // Controls for player
        JPanel controlPanel = new JPanel();
        rackLabel = new JLabel("Rack: ");
        placeButton = new JButton("Place Word");
        swapButton = new JButton("Swap Tiles");
        nextButton = new JButton("Next Player");
        controlPanel.add(rackLabel);
        controlPanel.add(placeButton);
        controlPanel.add(swapButton);
        controlPanel.add(nextButton);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

    }

    public JButton[][] getButtons() { return boardButtons; }
    public JButton getPlaceButton() { return placeButton; }
    public JButton getSwapButton() { return swapButton; }
    public JButton getNextButton() { return nextButton; }
    public JLabel getRackLabel() { return rackLabel; }

    public static void main(String[] args) throws Exception {


        Dictionary dict = new Dictionary();
        Game game = new Game(dict);
        game.addPlayer("A");
        game.addPlayer("B");

        Board board = game.getBoard();
        View view = new View(board);


    }
}

