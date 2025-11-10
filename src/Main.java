import javax.swing.*;

/**
 * Entry point for the Scrabble game.
 * Prompts for number of players (2–4), collects player names via dialog,
 * and launches the GUI using MVC architecture.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Prompt for number of players
                String[] options = {"2", "3", "4"};
                String selected = (String) JOptionPane.showInputDialog(
                        null,
                        "Select number of players:",
                        "Player Setup",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (selected == null) return; // user cancelled
                int numPlayers = Integer.parseInt(selected);

                // Prompt for player names
                Dictionary dictionary = new Dictionary();
                Game game = new Game(dictionary);

                for (int i = 1; i <= numPlayers; i++) {
                    String name = JOptionPane.showInputDialog(
                            null,
                            "Enter name for Player " + i + ":",
                            "Player Name",
                            JOptionPane.PLAIN_MESSAGE
                    );
                    if (name == null || name.trim().isEmpty()) {
                        name = "Player " + i;
                    }
                    game.addPlayer(name.trim());
                }

                // Launch GUI and controller
                GameView view = new GameView();
                GameController controller = new GameController(game, view);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error initializing game: " + e.getMessage(),
                        "Initialization Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}