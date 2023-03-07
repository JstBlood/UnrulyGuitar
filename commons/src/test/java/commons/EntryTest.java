package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class EntryTest {
    private final static Card SOME_CARD = new Card("myCard", "Nothing yet",
            new Board("board1", "myBoard", "Nothing yet", Color.RED));

    @Test
    public void checkConstructor() {
        var e = new Entry("blabla", Color.BLUE, 12, "someDeco", SOME_CARD);
        assertEquals("blabla", e.text);
        assertEquals(Color.BLUE, e.textColor);
        assertEquals(12, e.fontSize);
        assertEquals("someDeco", e.fontDecoration);
        assertEquals(SOME_CARD, e.parentCard);
    }
    @Test
    public void addSubentry() {
        var e = new Entry("blabla", Color.BLUE, 12, "someDeco", SOME_CARD);
        var testSubentry1 = new Subentry("todo1",  e);
        var testSubentry2 = new Subentry("todo2",  e);
        List<Subentry> SOME_SUBENTRIES = new ArrayList<>(Arrays.asList(testSubentry1, testSubentry2));
        e.addSubentry(testSubentry1);
        e.addSubentry(testSubentry2);
        assertEquals(SOME_SUBENTRIES, e.subEntries);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Entry("blabla", Color.BLUE, 12, "someDeco", SOME_CARD);
        var b = new Entry("blabla", Color.BLUE, 12, "someDeco", SOME_CARD);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Entry("blabla", Color.BLUE, 12, "someDeco", SOME_CARD);
        var b = new Entry("bla", Color.BLUE, 12, "someDeco", SOME_CARD);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var e = new Entry("blabla", Color.BLUE, 12, "someDeco", SOME_CARD).toString();
        assertTrue(e.contains(Entry.class.getSimpleName()));
        assertTrue(e.contains("\n"));
        assertTrue(e.contains("subEntries"));
    }

}
