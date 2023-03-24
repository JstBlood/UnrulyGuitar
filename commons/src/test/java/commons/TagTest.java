package commons;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {

    @Test
    public void checkConstructor() {
        var t = new Tag("urgent", new Color(0, 0, 0));
        assertEquals("urgent", t.name);
        assertEquals(new Color(0, 0, 0), t.color);
    }

    @Test
    public void addBoard() {
        var t = new Tag("urgent", new Color(0, 0, 0));
        Board testBoard1 = new Board("board1", "myBoard");
        Board testBoard2 = new Board("board2", "testBoard");
        Set<Board> SOME_BOARDS = new HashSet<>(Arrays.asList(testBoard1, testBoard2));
        t.addBoard(testBoard1);
        t.addBoard(testBoard2);
        assertEquals(SOME_BOARDS, t.boards);
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
        assertTrue(t.contains("boards"));
    }

}
