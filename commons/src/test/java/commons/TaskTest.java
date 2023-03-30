package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TaskTest {

    private final Card SOME_CARD = new Card("blabla", "",
            new CardList("abc", new Board("abc", "123")));

    @Test
    public void checkConstructor() {
        var s = new Task("Nothing yet", SOME_CARD);
        assertEquals("Nothing yet", s.title);
        assertEquals(SOME_CARD, s.parentCard);
    }

    @Test
    public void checkEmptyConstructor() {
        var t = new Task();
        assertNotEquals(null, t);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Task("Nothing yet", SOME_CARD);
        var b = new Task("Nothing yet", SOME_CARD);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Task("Nothing yet", SOME_CARD);
        var b = new Task("Nothing yet...", SOME_CARD);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var s = new Task("Nothing yet", SOME_CARD).toString();
        assertTrue(s.contains(Task.class.getSimpleName()));
        assertTrue(s.contains("\n"));
        assertTrue(s.contains("parentCard"));
    }
}
