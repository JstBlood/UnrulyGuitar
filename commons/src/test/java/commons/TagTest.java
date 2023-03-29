package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void checkConstructor() {
        var t = new Tag("urgent", new Color(0, 0, 0));
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
        var t = new Tag("urgent", new Color(0, 0, 0));
        Board testBoard1 = new Board("board1", "myBoard");
        t.board = testBoard1;

        assertEquals(testBoard1, t.board);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Tag("urgent", new Color(0, 0, 0));
        var b = new Tag("urgent", new Color(0, 0, 0));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Tag("urgent", new Color(0, 0, 0));
        var b = new Tag("mandatory", Color.BLUE);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var t = new Tag("urgent", new Color(0, 0, 0)).toString();
        assertTrue(t.contains(Tag.class.getSimpleName()));
        assertTrue(t.contains("\n"));
        assertTrue(t.contains("board"));
    }

}
