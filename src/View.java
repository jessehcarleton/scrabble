import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class View extends JFrame{
    private Game game;
    private Dictionary dictionary;
    private JButton[][] boardButtons;
    private JButton playButton, swapButton, passButton;
    ArrayList<String> players = new ArrayList<>();
    JButton [] playerRack = new JButton[7];


    public View() throws Exception {
        super("Scrabble");

        // Game Setup
        this.dictionary = new Dictionary();
        this.game = new Game(dictionary);
        makePlayers();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Board Initialization
        JPanel boardPanel = new JPanel(new GridLayout(15, 15));
        boardButtons = new JButton[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                boardButtons[i][j] = new JButton(" ");
                boardPanel.add(boardButtons[i][j]);
            }
        }

        // Controls Initialization
        JPanel controlPanel = new JPanel();
        playButton = new JButton("Play");
        swapButton = new JButton("Swap");
        passButton = new JButton("Pass");
        controlPanel.add(playButton);
        controlPanel.add(swapButton);
        controlPanel.add(passButton);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);



    }

    // Initialize players
    public ArrayList<String> makePlayers() {

        String[] options = {"2", "3", "4"};
        // Choose Number of players
        int choice = Integer.parseInt( (String)JOptionPane.showInputDialog(null,
                "Select number of players:",
                "Player Setup",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        ));
        // Get each player's name
        for (int i = 1; i < choice + 1; i++) {
            String name = JOptionPane.showInputDialog("Player" + i + "'s name: ");
            game.addPlayer(name);
            players.add(name);
        }
        return players;
    }

    public void playerInfoSection(Player player) {
        JPanel playerInfo = new JPanel();
        JLabel turn = new JLabel();

        playerInfo.setLayout(new BorderLayout());
        turn.setText(player.getName() + "'s turn");

        // Make a button for each tile in player's rack
        for (int i = 0; i < player.getRack().size(); i++) {
            playerRack[i] = new JButton();
            playerRack[i].setText(player.getRack().get(i).toString());
            playerInfo.add(playerRack[i]);
        }

        playerInfo.add(turn);





    }



    public JButton[][] getBoard() { return boardButtons; }
    public JButton getPlayButton() { return playButton; }
    public JButton getSwapButton() { return swapButton; }
    public JButton getPassButton() { return passButton; }

    public static void main(String[] args) throws Exception {

        View view = new View();


    }
}

