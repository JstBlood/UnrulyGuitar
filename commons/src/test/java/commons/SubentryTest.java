package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;

import org.junit.jupiter.api.Test;

public class SubentryTest {

    private final static Entry SOME_ENTRY = new Entry("blabla", Color.BLUE, 12, "someDeco",
            new Card("abc", "...", new Board("abc", "123", "...", null)));

    @Test
    public void checkConstructor() {
        var s = new Subentry("Nothing yet", SOME_ENTRY);
        assertEquals("Nothing yet", s.text);
        assertEquals(SOME_ENTRY, s.parentEntry);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Subentry("Nothing yet", SOME_ENTRY);
        var b = new Subentry("Nothing yet", SOME_ENTRY);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Subentry("Nothing yet", SOME_ENTRY);
        var b = new Subentry("Nothing yet...", SOME_ENTRY);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var s = new Subentry("Nothing yet", SOME_ENTRY).toString();
        assertTrue(s.contains(Subentry.class.getSimpleName()));
        assertTrue(s.contains("\n"));
        assertTrue(s.contains("parentEntry"));
    }
}
