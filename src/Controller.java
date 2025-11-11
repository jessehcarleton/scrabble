import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller implements ActionListener{
    private Game game;
    private View view;
    private JButton button;
    int row;
    int col;

    public Controller(Game game, View view, JButton button, int row, int col) {
        this.game = game;
        this.view = view;
        this.button = button;
        this.row = row;
        this.col = col;
        for (String name: view.players) {
            game.addPlayer(name);
        }

//        view.getPassButton().addActionListener(this);
//        view.getPlayButton().addActionListener(this);
//        view.getSwapButton().addActionListener(this);
//        for (int i = 0; i < 7; i++) {
//            view.playerRack[i].addActionListener(this);
//        }
        //view.playerRackGroup.getSelection();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == view.getPlayButton()) {
            handlePlaceWord();
        } else if (source == view.getSwapButton()) {
            handleSwapTiles();
        } else if (source == view.getPassButton()) {
            handlePass();
        } else if (!button.getText().equals(" ")) {
            JOptionPane.showMessageDialog(null,"Please select valid position on the board");
            return;
        } else {
            if (buttonPressedExists(view.playerRack)) {
                JOptionPane.showMessageDialog(null,"Please select a tile to place");
                return;
            }
            if (view.getDirection().isEmpty()) {
                String[] directions = {"Horizontal", "Vertical"};
                String dir = (String)JOptionPane.showInputDialog(null,
                        "Select the direction to place your word",
                        "Word placement direction",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        directions,
                        directions[0]
                );
                view.setDirection(dir);
            }
            for (int i = 0; i < 7; i++) {
                if (view.playerRack.get(i).isSelected() && view.isNextButtonValid(row, col)) {
                    char c = view.playerRack.get(i).getText().charAt(0);
                    int score = Integer.parseInt(String.valueOf(view.playerRack.get(i).getText().charAt(2)));
                    view.scoreGained += score;
                    view.tilesUsed -= 1;
                    button.setText("" + c);
                    view.addToWord(c);
                    view.updatePreviousRowCol(row, col);
                }
            }
        }
    }

    private void handlePlaceWord () {
        //game.placeWord();
        String word = view.getWord();
        Dictionary dic = game.getDictionary();
        if (!word.isEmpty() && dic.isValid(word)){
            view.updateScore(game.getCurrentPlayerIndex(), view.scoreGained);
            view.updatePlayerRack();
            view.updatePreviousRowCol(0, 0);
            view.scoreGained = 0;
            view.tilesUsed = 0;
            view.resetWord();
            view.nextPlayer();
        } else {
            JOptionPane.showMessageDialog(null,"Please select a valid word");
            view.resetTurn();
        }

    }
    private void handleSwapTiles() {
        Player p = view.getCurrentPlayer();
        String[] tiles = new String[7];
        int x = 0;
        for (Tile t : p.getRack()) {
            tiles[x] = t.toString();
            x++;
        }

        String swapTile = (String)JOptionPane.showInputDialog(null,
                "Select the direction to place your word",
                "Word placement direction",
                JOptionPane.QUESTION_MESSAGE,
                null,
                tiles,
                tiles[0]
        );

        for (Tile t : p.getRack()) {
            if (swapTile.equals(t.toString())) {
                p.removeTile(t);
                p.addTile(game.getTileBag().drawTile());
            }
        }
    }
    private void handlePass() {

    }

    private boolean buttonPressedExists(ArrayList<JToggleButton> tbs) {
        for (JToggleButton tb : tbs) {
            if (tb.isSelected()) {
                return true;
            }
        }
        return false;
    }
}