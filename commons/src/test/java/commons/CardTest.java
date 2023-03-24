package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    private final static CardList SOME_CARDLIST = new CardList("myCard",
            new Board("board1", "myBoard"));

    @Test
    public void checkConstructor() {
        var e = new Card("blabla", "", SOME_CARDLIST);
        assertEquals("blabla", e.title);
        assertEquals(SOME_CARDLIST, e.parentCardList);
    }
    @Test
    public void addSubentry() {
        var e = new Card("blabla", "", SOME_CARDLIST);
        var testTask1 = new Task("todo1",  e);
        var testTask2 = new Task("todo2",  e);
        List<Task> SOME_SUBENTRIES = new ArrayList<>(Arrays.asList(testTask1, testTask2));
        e.addTask(testTask1);
        e.addTask(testTask2);
        assertEquals(SOME_SUBENTRIES, e.tasks);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Card("blabla", "", SOME_CARDLIST);
        var b = new Card("blabla", "", SOME_CARDLIST);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Card("blabla", "", SOME_CARDLIST);
        var b = new Card("bla", "", SOME_CARDLIST);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var e = new Card("blabla", "", SOME_CARDLIST).toString();
        assertTrue(e.contains(Card.class.getSimpleName()));
        assertTrue(e.contains("\n"));
        assertTrue(e.contains("tasks"));
    }

}
