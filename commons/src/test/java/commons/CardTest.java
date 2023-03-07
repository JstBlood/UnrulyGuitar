package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CardTest {
    private static final Board SOME_BOARD = new Board("board1", "myBoard", "Nothing yet", null);
    @Test
    public void checkConstructor() {
        var c = new Card("myCard", "Nothing yet", SOME_BOARD);
        assertEquals("myCard", c.title);
        assertEquals("Nothing yet", c.description);
        assertEquals(SOME_BOARD, c.parentBoard);
    }
    @Test
    public void addEntry() {
        var c = new Card("myCard", "Nothing yet", SOME_BOARD);
        var testEntry1 = new Entry("todo1", Color.BLUE, 12, "someDeco", c);
        var testEntry2 = new Entry("todo2", Color.RED, 10, "someDeco", c);
        List<Entry> SOME_ENTRIES = new ArrayList<>(Arrays.asList(testEntry1, testEntry2));
        c.addEntry(testEntry1);
        c.addEntry(testEntry2);
        assertEquals(SOME_ENTRIES, c.entries);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Card("myCard", "Nothing yet", SOME_BOARD);
        var b = new Card("myCard", "Nothing yet", SOME_BOARD);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Card("myCard", "Nothing yet", SOME_BOARD);
        var b = new Card("myCard", "Nothing yet", new Board("123", null, null, null));
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var c = new Card("myCard", "Nothing yet", SOME_BOARD).toString();
        assertTrue(c.contains(Card.class.getSimpleName()));
        assertTrue(c.contains("\n"));
        assertTrue(c.contains("entries"));
    }

}
