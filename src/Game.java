import java.util.*;

/**
 * Main class that manages the Scrabble game loop, player turns, and game state.
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
     * Adds a new player to the game and fills their rack with 7 tiles.
     *
     * @param name the name of the player
     */
    public void addPlayer(String name) {
        Player player = new Player(name);
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
        System.out.println("Note: The first word must cover the center square (H8).");


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
            System.out.println(shortName + "'s Tiles: " + currentPlayer.rackToString() +
                    " " + shortName + "'s score: " + currentPlayer.getScore());

            // Show turn options
            System.out.println("\nYour options:");
            System.out.println("  [0] Place Tile(s)");
            System.out.println("  [1] Swap Tiles");
            System.out.println("  [2] Skip Turn");

            System.out.println("\nEnter your move:");
            System.out.println("  → Format: H8 HORIZONTAL HELLO / H8 VERTICAL HELLO");
            System.out.println("  → Or type: PASS or QUIT");
            System.out.print("> ");
            String input = scanner.nextLine().trim().toUpperCase();


            // Handle quitting
            if (input.equals("QUIT")) {
                System.out.println("Thanks for playing!");
                break;
            }

            // Handle passing
            if (input.equals("PASS") || input.equals("2")) {
                System.out.println(currentPlayer.getName() + " passed.");
                nextPlayer();
                continue;
            }
            if (input.equals("1")) {
                handleSwap(currentPlayer, scanner);
                nextPlayer();
                continue;
            }

            // Parse move input
            String[] parts = input.split(" ");
            if (parts.length != 3) {
                System.out.println("Invalid input. Please use the format: H8 HORIZONTAL HELLO");
                System.out.println(" - HORIZONTAL = left to right");
                System.out.println(" - VERTICAL = top to bottom");
                continue;
            }

            String coord = parts[0];
            boolean horizontal = parts[1].equals("HORIZONTAL");
            String word = parts[2];

            // Validate coordinate format
            if (coord.length() < 2 || coord.length() > 3) {
                System.out.println("Invalid coordinate. Use format like H8 or D12.");
                continue;
            }

            // Extract row and column from coordinate
            char colChar = coord.charAt(0);
            int rowNum;
            try {
                rowNum = Integer.parseInt(coord.substring(1)) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid row number in coordinate.");
                continue;
            }

            int colNum = colChar - 'A';
            if (colNum < 0 || colNum >= 15 || rowNum < 0 || rowNum >= 15) {
                System.out.println("Coordinates out of bounds. Use A–O and 1–15.");
                continue;
            }

            // Validate word against dictionary
            if (!dictionary.isValid(word)) {
                System.out.println("Invalid word! Please enter a valid word from the dictionary.");
                continue;
            }

            // Attempt to place the word on the board
            boolean success = board.placeWord(word, rowNum, colNum, horizontal, currentPlayer);
            if (success) {
                refillRack(currentPlayer); // Refill rack after successful move
                nextPlayer();              // Advance to next player
            } else {
                System.out.println("Word placement failed. Make sure it fits and doesn't conflict.");
            }
        }
    }


    /**
     * Advances to the next player's turn.
     */
    void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Refills the player's rack from the tile bag until it contains 7 tiles.
     * Draws random tiles from the bag and adds them to the player's rack.
     * If the bag is empty, no tiles are added.
     *
     * @param player the player whose rack should be refilled
     */
    void refillRack(Player player) {
        while (player.getRack().size() < 7 && tileBag.remainingTiles() > 0) {
            Tile tile = tileBag.drawTile();
            if (tile != null) {
                player.getRack().add(tile);
            }
        }
    }

    /**
     * Converts a player's rack of tiles into a printable string.
     *
     * @param rack the list of tiles in the player's rack
     * @return a string representation of the rack
     */
    private String rackToString(List<Tile> rack) {
        StringBuilder sb = new StringBuilder();
        for (Tile t : rack) sb.append(t.getLetter()).append(" ");
        return sb.toString().trim();
    }


    /**
     * Allows the player to swap selected tiles from their rack.
     *
     * @param player the current player
     * @param scanner the scanner for input
     */
    private void handleSwap(Player player, Scanner scanner) {
        System.out.println("Your rack: " + player.rackToString());
        System.out.println("Enter the indices of tiles to swap (e.g., 0 2 5), or type CANCEL to go back:");
        String input = scanner.nextLine().trim().toUpperCase();

        if (input.equals("CANCEL")) {
            System.out.println("Swap cancelled.");
            return;
        }

        String[] tokens = input.split(" ");
        List<Tile> toSwap = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (String token : tokens) {
            try {
                int index = Integer.parseInt(token);
                if (index < 0 || index >= player.getRack().size()) {
                    System.out.println("Invalid index: " + index);
                    return;
                }
                indices.add(index);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + token);
                return;
            }
        }

        // Collect tiles to swap
        for (int i : indices) {
            toSwap.add(player.getRack().get(i));
        }

        // Remove and replace
        for (Tile t : toSwap) {
            player.getRack().remove(t);
            tileBag.getTiles().add(t); // return to bag
        }

        refillRack(player);
        System.out.println("Tiles swapped. New rack: " + player.rackToString());
    }

    /// Getters and setters
    public List<Player> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public TileBag getTileBag() {
        return tileBag;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
}