import org.junit.Test;
import static org.junit.Assert.*;

public class DictionaryTest {

    @Test
    public void testValidWord() throws Exception {
        Dictionary dict = new Dictionary();
        assertTrue(dict.isValid("HELLO")); // "HELLO" is in the MIT list
    }

    @Test
    public void testInvalidWord() throws Exception {
        Dictionary dict = new Dictionary();
        assertFalse(dict.isValid("XYZXYZ")); // unlikely to be in the list
    }

    @Test
    public void testLowercaseWord() throws Exception {
        Dictionary dict = new Dictionary();
        assertTrue(dict.isValid("hello")); // should still be valid
    }

    @Test
    public void testMixedCaseWord() throws Exception {
        Dictionary dict = new Dictionary();
        assertTrue(dict.isValid("HeLLo")); // should still be valid
    }

    @Test
    public void testEmptyString() throws Exception {
        Dictionary dict = new Dictionary();
        assertFalse(dict.isValid(""));
    }


}