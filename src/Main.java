import java.util.Scanner;

/**
 * Entry point for the Scrabble game.
 * Prompts for player names and starts the game.
 */
public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the game of Scrabble!");

            System.out.print("Please enter player 1 name: ");
            String player1 = scanner.nextLine().trim();

            System.out.print("Please enter player 2 name: ");
            String player2 = scanner.nextLine().trim();

            Dictionary dictionary = new Dictionary();
            Game game = new Game(dictionary);
            game.addPlayer(player1);
            game.addPlayer(player2);
            game.start();
        } catch (Exception e) {
            System.out.println("Error initializing game: " + e.getMessage());
        }
    }
}