import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class responsible for loading custom Scrabble board layouts
 * from XML files on disk.
 * <p>
 * The XML format is intentionally simple. A layout file looks like:
 *
 * <pre>{@code
 * <boardLayout name="Standard" size="15">
 *     <row>TW . . . DL . . TW . . DL . . . TW</row>
 *     ...
 * </boardLayout>
 * }</pre>
 *
 * Each {@code <row>} element contains exactly {@code size} tokens separated
 * by whitespace. Recognized tokens are:
 * <ul>
 *     <li>{@code .} or {@code NONE} – no bonus</li>
 *     <li>{@code DL} – double letter</li>
 *     <li>{@code TL} – triple letter</li>
 *     <li>{@code DW} – double word</li>
 *     <li>{@code TW} – triple word</li>
 * </ul>
 *
 * This class is used by {@link Main} at startup to populate the list of
 * available custom boards for the user to choose from.
 */
public class BoardLayoutLoader {

    private BoardLayoutLoader() {
        // Utility class; no instances.
    }

    /**
     * Loads all XML layout files from the given directory path.
     *
     * @param directoryPath directory containing {@code *.xml} layout files
     * @return list of successfully loaded layouts (may be empty if none found)
     */
    public static List<BoardLayout> loadLayouts(String directoryPath) {
        List<BoardLayout> layouts = new ArrayList<>();
        File dir = new File(directoryPath);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("BoardLayoutLoader: directory not found: " + directoryPath);
            return layouts;
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml"));
        if (files == null) {
            return layouts;
        }

        for (File file : files) {
            try {
                BoardLayout layout = parseLayout(file);
                layouts.add(layout);
            } catch (Exception e) {
                System.err.println("BoardLayoutLoader: failed to parse " + file.getName()
                        + " - " + e.getMessage());
            }
        }

        return layouts;
    }

    /**
     * Parses a single XML file into a {@link BoardLayout}.
     *
     * @param file XML layout file
     * @return the corresponding {@code BoardLayout}
     * @throws Exception if parsing or validation fails
     */
    private static BoardLayout parseLayout(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(file);
        Element root = doc.getDocumentElement();

        String name = root.getAttribute("name");
        if (name == null || name.trim().isEmpty()) {
            name = file.getName();
        }

        String sizeAttr = root.getAttribute("size");
        int size = 15;
        if (sizeAttr != null && !sizeAttr.trim().isEmpty()) {
            size = Integer.parseInt(sizeAttr.trim());
        }

        Board.Premium[][] premiums = new Board.Premium[size][size];

        NodeList rowNodes = root.getElementsByTagName("row");
        if (rowNodes.getLength() != size) {
            throw new IllegalArgumentException("Expected " + size + " <row> elements but found "
                    + rowNodes.getLength());
        }

        for (int r = 0; r < size; r++) {
            Node node = rowNodes.item(r);
            if (!(node instanceof Element)) {
                continue;
            }
            String rowText = node.getTextContent().trim();
            if (rowText.isEmpty()) {
                throw new IllegalArgumentException("Row " + r + " is empty");
            }
            String[] tokens = rowText.split("\\s+");
            if (tokens.length != size) {
                throw new IllegalArgumentException("Row " + r + " in " + file.getName()
                        + " has " + tokens.length + " tokens, expected " + size);
            }

            for (int c = 0; c < size; c++) {
                premiums[r][c] = codeToPremium(tokens[c]);
            }
        }

        return new BoardLayout(name, size, premiums);
    }

    /**
     * Maps a textual token from the XML to a {@link Board.Premium} value.
     *
     * @param code a string token such as "DL", "TW", or "."
     * @return the corresponding premium enum
     */
    private static Board.Premium codeToPremium(String code) {
        if (code == null) {
            return Board.Premium.NONE;
        }
        String normalized = code.trim().toUpperCase();

        if (normalized.isEmpty() || ".".equals(normalized) || "NONE".equals(normalized)) {
            return Board.Premium.NONE;
        }
        switch (normalized) {
            case "DL": return Board.Premium.DOUBLE_LETTER;
            case "TL": return Board.Premium.TRIPLE_LETTER;
            case "DW":
            case "2W": return Board.Premium.DOUBLE_WORD;
            case "TW":
            case "3W": return Board.Premium.TRIPLE_WORD;
            default:
                System.err.println("BoardLayoutLoader: unrecognized premium code '" + code
                        + "', treating as NONE");
                return Board.Premium.NONE;
        }
    }
}
