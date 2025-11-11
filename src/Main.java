import java.util.Scanner;

/**
 * Entry point for the Scrabble game.
 * Prompts for number of players (2–4), collects player names, and starts the game.
 */
public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the game of Scrabble!");

            // Prompt for number of players (2–4)
            int numPlayers = 0;
            while (numPlayers < 2 || numPlayers > 4) {
                System.out.print("Enter number of players (2–4): ");
                if (scanner.hasNextInt()) {
                    numPlayers = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    if (numPlayers < 2 || numPlayers > 4) {
                        System.out.println("Invalid number. Please enter between 2 and 4 players.");
                    }
                } else {
                    System.out.println("Please enter a valid number.");
                    scanner.nextLine(); // consume invalid input
                }
            }

            // Create game and dictionary
            Dictionary dictionary = new Dictionary();
            Game game = new Game(dictionary);

            // Prompt for each player's name
            for (int i = 1; i <= numPlayers; i++) {
                System.out.print("Please enter name for Player " + i + ": ");
                String name = scanner.nextLine().trim();
                game.addPlayer(name);
            }

            // Start the game
            game.start();
        } catch (Exception e) {
            System.out.println("Error initializing game: " + e.getMessage());
        }
    }
}