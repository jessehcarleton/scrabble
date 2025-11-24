import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a dictionary of valid Scrabble words.
 * Loads words from a web-hosted text file and checks word validity.
 * Mohamed
 */
public class Dictionary {
    private Set<String> validWords;

    /**
     * Constructs a Dictionary by loading words from the MIT word list.
     *
     * @throws Exception if the word list cannot be loaded
     */
    public Dictionary() throws Exception {
        validWords = new HashSet<>();
        loadWordsFromURL("https://www.mit.edu/~ecprice/wordlist.10000");
    }

    /**
     * Checks if a given word is valid according to the dictionary.
     *
     * @param word the word to check
     * @return true if the word is valid, false otherwise
     */
    public boolean isValid(String word) {
        return validWords.contains(word.toUpperCase());
    }

    /**
     * Loads words from the specified URL into the dictionary.
     * Assumes one word per line.
     *
     * @param urlString the URL of the word list
     * @throws Exception if the URL cannot be read
     */
    private void loadWordsFromURL(String urlString) throws Exception {
        URL url = new URL(urlString);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                validWords.add(line.trim().toUpperCase());
            }
        }
    }
}