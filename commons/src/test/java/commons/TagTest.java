package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;

import org.junit.jupiter.api.Test;

public class TagTest {
    private final Board SOME_BOARD = new Board("board", "myBoard");

    @Test
    public void checkConstructor() {
        var t = new Tag("urgent", new Color(0, 0, 0), SOME_BOARD);
        assertEquals("urgent", t.name);
        assertEquals(new Color(0, 0, 0), t.color);
    }

    @Test
    public void checkEmptyConstructor() {
        var t = new Tag();
        assertNotEquals(null, t);
    }

    @Test
    public void addBoard() {
        var t = new Tag("urgent", new Color(0, 0, 0), SOME_BOARD);
        Board testBoard1 = new Board("board1", "myBoard");
        t.parentBoard = testBoard1;

        assertEquals(testBoard1, t.parentBoard);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Tag("urgent", new Color(0, 0, 0), SOME_BOARD);
        var b = new Tag("urgent", new Color(0, 0, 0), SOME_BOARD);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Tag("urgent", new Color(0, 0, 0), SOME_BOARD);
        var b = new Tag("mandatory", Color.BLUE, SOME_BOARD);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var t = new Tag("urgent", new Color(0, 0, 0), SOME_BOARD).toString();
        assertTrue(t.contains(Tag.class.getSimpleName()));
        assertTrue(t.contains("\n"));
        assertTrue(t.contains("parentBoard"));
    }

}
