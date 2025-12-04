import javax.swing.*;

/**
 * Application entry point for the Scrabble game.
 *
 * Milestone 2 patches:
 * - Load the Dictionary (network I/O) BEFORE entering the Swing Event Dispatch Thread
 *   to avoid freezing the UI while fetching the word list.
 * - Create and wire MVC on the EDT after resources are ready.
 * Milestone 3 additions:
 * - Prompt for number of players and their types (Human/AI) at startup.
 * - Initialize Game model with the loaded Dictionary before creating the View and Controller.
 * Milestone 4 additions:
 * - Load board layouts from XML files using BoardLayoutLoader.
 * - Prompt the user to select a layout and pass it into the Game model.
 *
 */
public class Main {
    public static void main(String[] args) {
        // Load dictionary outside EDT to avoid UI freeze.
        Dictionary dictionary;
        try {
            dictionary = new Dictionary();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to load dictionary: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Dictionary readyDictionary = dictionary;

        SwingUtilities.invokeLater(() -> {
            try {
                // Load custom board layouts from XML files (Milestone 4, Phase 1)
                java.util.List<BoardLayout> layouts = BoardLayoutLoader.loadLayouts("boards");

                BoardLayout selectedLayout = null;
                if (!layouts.isEmpty()) {
                    Object choice = JOptionPane.showInputDialog(
                            null,
                            "Select a board layout:",
                            "Board Layout",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            layouts.toArray(),
                            layouts.get(0)
                    );
                    if (choice == null) {
                        // User cancelled layout selection.
                        return;
                    }
                    selectedLayout = (BoardLayout) choice;
                } else {
                    // No layouts found; fall back to the standard board layout.
                    JOptionPane.showMessageDialog(
                            null,
                            "No XML board layouts found in the 'boards' directory.\n" +
                                    "The standard Scrabble layout will be used.",
                            "Board Layouts Missing",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

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

                // Create model with ready dictionary and chosen layout
                Game game = new Game(readyDictionary, selectedLayout);

                // Prompt for player names
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
                    String[] typeOptions = {"Human", "AI"};
                    String type = (String) JOptionPane.showInputDialog(
                            null,
                            "Select player type for " + name + ":",
                            "Player Type",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            typeOptions,
                            typeOptions[0]
                    );
                    boolean isAi = "AI".equals(type);
                    game.addPlayer(name.trim(), isAi);
                }

                // Construct View and Controller
                GameView view = new GameView();
                new GameController(game, view);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error starting game: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
