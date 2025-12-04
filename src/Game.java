import java.util.*;

/**
 * Main class that manages the Scrabble game loop, player turns, and game state.
 *
 * Milestone 4 additions:
 * - Overloaded constructor that accepts a BoardLayout so the Game can
 *   be initialized with a custom board configuration loaded from XML.
 */
public class Game {
    private Board board;
    private TileBag tileBag;
    private Dictionary dictionary;
    private List<Player> players;
    private int currentPlayerIndex;

    /**
     * Constructs a new Game instance with a given dictionary.
     * Initializes the board, tile bag, and player list.
     *
     * @param dictionary the dictionary used to validate words
     */
    public Game(Dictionary dictionary) {
        this.board = new Board();
        this.tileBag = new TileBag();
        this.dictionary = dictionary;
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
    }

    /**
     * Constructs a new Game instance with a given dictionary and a specific
     * {@link BoardLayout}. This is used in Milestone 4 Phase 1 to support
     * custom board configurations loaded from XML files.
     * <p>
     * If {@code layout} is {@code null}, the standard Scrabble board layout
     * is used.
     *
     * @param dictionary the dictionary used to validate words
     * @param layout     the board layout to use; may be {@code null}
     */
    public Game(Dictionary dictionary, BoardLayout layout) {
        this.board = (layout != null) ? new Board(layout) : new Board();
        this.tileBag = new TileBag();
        this.dictionary = dictionary;
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
    }

    /**
     * Adds a new player to the game and fills their rack with 7 tiles.
     *
     * @param name the name of the player
     */
    public void addPlayer(String name) {
        addPlayer(name, false);
    }

    /** Adds a player and optionally marks them as AI. */
    public void addPlayer(String name, boolean ai) {
        Player player = new Player(name);
        player.setAi(ai);
        for (int i = 0; i < 7; i++) {
            player.getRack().add(tileBag.drawTile());
        }
        players.add(player);
    }

    /**
     * Starts the Scrabble game loop.
     * Prompts players for moves, validates input, updates board and scores.
     * Supports placing words, passing turns, and quitting the game.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);

        // Introductory instructions
        System.out.println("Welcome to Scrabble!");
        System.out.println("To place a word, type: <position> <direction> <word>");
        System.out.println("Example: H8 HORIZONTAL HELLO");
        System.out.println(" - HORIZONTAL means left to right");
        System.out.println(" - VERTICAL means top to bottom");
        System.out.println("You can also type PASS to skip your turn or QUIT to end the game.");
        System.out.println();

        while (true) {
            Player currentPlayer = players.get(currentPlayerIndex);

            // Display current board
            System.out.println("\nCurrent Board:");
            board.printBoard();

            // Shorten player name for display (e.g., "ch" instead of "cherif")
            String shortName = currentPlayer.getName().length() > 2
                    ? currentPlayer.getName().substring(0, 2)
                    : currentPlayer.getName();

            // Show rack and score
            System.out.println(shortName + "'s rack: " + currentPlayer.getRack());
            System.out.println(shortName + "'s score: " + currentPlayer.getScore());

            // Ask for player's move
            System.out.println("Enter your move (e.g., H8 HORIZONTAL HELLO) or PASS/QUIT:");
            String input = scanner.nextLine().trim();
            long startTime = System.currentTimeMillis(); // Start timing

            if (input.equalsIgnoreCase("QUIT")) {
                // Confirm quitting the game
                System.out.println(formatMessage("confirm", "Are you sure you want to quit? (yes/no)", true));
                String confirm = scanner.nextLine().trim();
                if (confirm.equalsIgnoreCase("yes")) {
                    System.out.println("Game over! Final scores:");
                    printScores();
                    break;
                } else {
                    System.out.println("Continuing the game.");
                    continue;
                }
            } else if (input.equalsIgnoreCase("PASS")) {
                // Confirm passing the turn
                System.out.println(formatMessage("confirm", "Are you sure you want to pass your turn? (yes/no)", true));
                String confirm = scanner.nextLine().trim();
                if (confirm.equalsIgnoreCase("yes")) {
                    System.out.println(currentPlayer.getName() + " passes their turn.");
                    nextPlayer();
                    continue;
                } else {
                    System.out.println("Turn not passed. Enter your move.");
                    continue;
                }
            }

            String[] parts = input.split("\\s+");
            if (parts.length != 3) {
                System.out.println(formatMessage("error", "Invalid input format. Please try again.", true));
                continue;
            }

            String position = parts[0].toUpperCase();
            String direction = parts[1].toUpperCase();
            String word = parts[2].toUpperCase();

            if (!dictionary.isValid(word)) {
                System.out.println(formatMessage("error", "The word \"" + word + "\" is not in the dictionary.", true));
                continue;
            }

            if (position.length() < 2 || position.length() > 3) {
                System.out.println(formatMessage("error", "Invalid position. Please use format like H8 or A15.", true));
                continue;
            }

            char colChar = position.charAt(0);
            if (colChar < 'A' || colChar > 'O') {
                System.out.println(formatMessage("error", "Invalid column. Must be A to O.", true));
                continue;
            }
            int col = colChar - 'A';

            int row;
            try {
                row = Integer.parseInt(position.substring(1)) - 1;
            } catch (NumberFormatException e) {
                System.out.println(formatMessage("error", "Invalid row. Please use a number from 1 to 15.", true));
                continue;
            }
            if (row < 0 || row >= 15) {
                System.out.println(formatMessage("error", "Invalid row. Must be between 1 and 15.", true));
                continue;
            }

            boolean horizontal;
            if (direction.equals("H") || direction.equals("HORIZONTAL")) {
                horizontal = true;
            } else if (direction.equals("V") || direction.equals("VERTICAL")) {
                horizontal = false;
            } else {
                System.out.println(formatMessage("error", "Invalid direction. Use H or V (HORIZONTAL or VERTICAL).", true));
                continue;
            }

            boolean success = board.placeWord(word, row, col, horizontal, currentPlayer);
            if (!success) {
                System.out.println(formatMessage("error", "Failed to place the word. Check for conflicts, bounds, and connectivity.", true));
                continue;
            }

            refillRack(currentPlayer);
            long endTime = System.currentTimeMillis(); // End timing
            long moveTime = endTime - startTime; // Calculate the time taken
            System.out.println(formatMessage("success", "Word placed successfully! Your new score: " + currentPlayer.getScore(), true));
            System.out.println("Time taken for last move: " + moveTime + "ms");

            if (isGameOver()) {
                System.out.println("The game has ended!");
                printScores();
                break;
            }

            nextPlayer();
        }
    }

    /**
     * Formats messages with types and truncates if too long.
     * - Types: error, success, confirm
     *
     * @param type    the type of the message ("error", "success", "confirm")
     * @param message the message content
     * @param truncate whether to truncate long messages
     * @return the formatted message
     */
    private String formatMessage(String type, String message, boolean truncate) {
        if (truncate && message.length() > 50) {
            message = message.substring(0, 47) + "...";
        }
        switch (type.toLowerCase()) {
            case "error":
                return "[ERROR] " + message;
            case "success":
                return "[SUCCESS] " + message;
            case "confirm":
                return "[CONFIRM] " + message;
            default:
                return message;
        }
    }

    /**
     * Refills a player's rack up to 7 tiles from the tile bag.
     *
     * @param player the player whose rack should be refilled
     */
    public void refillRack(Player player) {
        while (player.getRack().size() < 7 && !tileBag.isEmpty()) {
            player.getRack().add(tileBag.drawTile());
        }
    }

    /**
     * Advances the turn to the next player in the list.
     * Wraps around to the first player after the last.
     */
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Checks if the game is over.
     * The game is over when the tile bag is empty and a player has used all their tiles.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        if (!tileBag.isEmpty()) {
            return false;
        }
        for (Player player : players) {
            if (player.getRack().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints the final scores of all players.
     * Indicates the winner based on the highest score.
     */
    public void printScores() {
        int highestScore = -1;
        Player winner = null;
        System.out.println("Final Scores:");
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getScore());
            if (player.getScore() > highestScore) {
                highestScore = player.getScore();
                winner = player;
            }
        }
        if (winner != null) {
            System.out.println("Winner: " + winner.getName() + " with " + highestScore + " points!");
        }
    }

    /**
     * Gets the board used in this game.
     *
     * @return the Board instance
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the tile bag used in this game.
     *
     * @return the TileBag instance
     */
    public TileBag getTileBag() {
        return tileBag;
    }

    /**
     * Gets the dictionary used for word validation.
     *
     * @return the Dictionary instance
     */
    public Dictionary getDictionary() {
        return dictionary;
    }

    /**
     * Gets the list of players participating in the game.
     *
     * @return the list of Player objects
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the index of the player whose turn it currently is.
     *
     * @return zero-based index of current player
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current Player
     */
    public Player getCurrentPlayer() {
        return this.players.get(currentPlayerIndex);
    }
}
