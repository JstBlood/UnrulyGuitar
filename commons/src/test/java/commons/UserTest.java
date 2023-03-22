package commons;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void checkConstructor() {
        var u = new User("Mickey Mouse");
        assertEquals("Mickey Mouse", u.username);
    }
    @Test
    public void checkIndex() {
        var u = new User("Mickey Mouse");
        u.index = 1;
        assertEquals(1, u.index);
    }

    @Test
    public void checkBoards() {
        var u = new User("Mickey Mouse");
        Board testBoard1 = new Board("board1", "myBoard");
        Board testBoard2 = new Board("board2", "testBoard");
        Board testBoard3 = new Board("board3", "mainBoard");
        Set<Board> SOME_BOARDS = new HashSet<>(Arrays.asList(testBoard1, testBoard2, testBoard3));
        u.boards.add(testBoard1);
        u.boards.add(testBoard2);
        u.boards.add(testBoard3);
        assertEquals(u.boards, SOME_BOARDS);
    }

    @Test
    public void EqualsHashCode() {
        var a = new User("Mickey Mouse");
        var b = new User("Mickey Mouse");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new User("Mickey Mouse");
        var b = new User("Spiderman");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var u = new User("Mickey Mouse").toString();
        assertTrue(u.contains(User.class.getSimpleName()));
        assertTrue(u.contains("\n"));
        assertTrue(u.contains("boards"));
    }

}
