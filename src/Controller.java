import javax.swing.*;
import java.awt.event.*;

public class Controller implements ActionListener{
    private Game game;
    private View view;

    public Controller(Game game, View view) {
        this.game = game;
        this.view = view;
//        for (String name: view.players) {
//            game.addPlayer(name);
//        }

        view.getPassButton().addActionListener(this);
        view.getPlayButton().addActionListener(this);
        view.getSwapButton().addActionListener(this);
        for (int i = 0; i < 7; i++) {
            view.playerRack[i].addActionListener(this);
        }

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
        }
    }

    private void handlePlaceWord () {
        //game.placeWord();
        String[] directions = {"Horizontal", "Vertical"};
        String direction = (String)JOptionPane.showInputDialog(null,
                "Select the direction to place the word",
                "Direction of word placement",
                JOptionPane.QUESTION_MESSAGE,
                null,
                directions,
                directions[0]
        );
        String word = JOptionPane.showInputDialog(null, "Please enter the word you would like to place");
    }
    private void handleSwapTiles() {
    }
    private void handlePass() {

    }
}