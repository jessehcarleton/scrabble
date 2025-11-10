import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class View extends JFrame{
    private  Dictionary dictionary;
    private Game game;
    private JButton[][] boardButtons;
    private JButton playButton, swapButton, passButton;
    ArrayList<String> players = new ArrayList<>();
    ArrayList<JTextField> scoreFields = new ArrayList<>();
    JButton [] playerRack = new JButton[7];
    JPanel boardPanel;
    JPanel playerTilePanel;
    JPanel tilebagPanel;
    JPanel playerPanel;
    JPanel controlPanel;
    JPanel scorePanel;

    JLabel turn;
    JLabel tilesLeft;
    JTextField tileTotal;


    public View() throws Exception {
        super("Scrabble");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        // Controls Initialization
        this.dictionary = new Dictionary();
        this.game = new Game(dictionary);
        controlPanel = new JPanel();
        playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerTilePanel = new JPanel();
        turn = new JLabel();
        tileTotal = new JTextField();




        // Board Initialization
        boardPanel = new JPanel(new GridLayout(15, 15));
        boardButtons = new JButton[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                boardButtons[i][j] = new JButton(" ");
                boardPanel.add(boardButtons[i][j]);
            }
        }


        playButton = new JButton("Play");
        swapButton = new JButton("Swap");
        passButton = new JButton("Pass");
        controlPanel.add(playButton);
        controlPanel.add(swapButton);
        controlPanel.add(passButton);


        // Initialize players
        players = buildPlayers();
        for (String player: players) {
            game.addPlayer(player);
        }

        // Panel setup
        buildPlayerNamePanel(players);
        buildTileBagPanel();
        buildPlayerTilesPanel(game.getPlayers().getFirst());
        playerPanel.add(tilebagPanel);
        playerPanel.add(scorePanel);
        playerPanel.add(playerTilePanel);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(playerPanel,BorderLayout.WEST);


        setSize(600,600);
        setVisible(true);
    }

    // Initialize players
    public ArrayList<String> buildPlayers() {
        players.clear();
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
            players.add(name);
        }
        return players;
    }

    public void buildPlayerNamePanel(ArrayList<String> players) {
        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));

        for (String name : players) {
            JLabel scoreLabel = new JLabel(name + "Score: ");
            JTextField score = new JTextField("0");
            score.setEditable(false);

            JPanel scoreDisplay = new JPanel();
            scoreDisplay.setLayout(new BoxLayout(scoreDisplay, BoxLayout.X_AXIS));
            scoreDisplay.add(scoreLabel);
            scoreDisplay.add(score);

            scoreFields.add(score);
            scorePanel.add(scoreDisplay);
        }


    }
    public void buildTileBagPanel() {
        tilebagPanel = new JPanel();
        tilebagPanel.setLayout(new BoxLayout(tilebagPanel, BoxLayout.X_AXIS));

        tilesLeft = new JLabel("Tiles Left: ");
        tileTotal.setText(String.valueOf(game.getTileBag().remainingTiles()));
        tileTotal.setEditable(false);

        tilebagPanel.add(tilesLeft);
        tilebagPanel.add(tileTotal);

    }
    public void buildPlayerTilesPanel(Player player) {
        playerTilePanel = new JPanel();
        playerTilePanel.setLayout(new BoxLayout(playerTilePanel, BoxLayout.X_AXIS));

        turn = new JLabel(player.getName() + "'s Turn");
        playerTilePanel.add(turn);

        for (int i = 0; i < 7; i++) {
            playerRack[i] = new JButton(player.getRack().get(i).toString());
            playerTilePanel.add(playerRack[i]);
        }
    }

    // For model when player makes a move
    public void updatePlayerTiles(Player player) {
        turn.setText(player.getName() + "'s Turn");

        for (int i = 0; i < 7; i++) {
            if (i < player.getRack().size()) {
                playerRack[i].setText(player.getRack().get(i).toString());
                playerRack[i].setEnabled(true);
            } else {
                playerRack[i].setText(" ");
                playerRack[i].setEnabled(false);
            }
        }

    }

    public void updateTileBagPanel() {
        tileTotal.setText(String.valueOf(game.getTileBag().remainingTiles()));
    }
    public void updateScore(int playerIndex, int newScore) {
        scoreFields.get(playerIndex).setText(String.valueOf(newScore));
    }



    public JButton[][] getBoard() { return boardButtons; }
    public JButton getPlayButton() { return playButton; }
    public JButton getSwapButton() { return swapButton; }
    public JButton getPassButton() { return passButton; }

    public static void main(String[] args) throws Exception {

        View view = new View();


    }
}

