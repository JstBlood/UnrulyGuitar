package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CardTest {
    private static CardList SOME_CARDLIST = new CardList("myCard",
            new Board("board1", "myBoard"));

    @Test
    public void checkConstructor() {
        var e = new Card("blabla", "", SOME_CARDLIST);
        assertEquals("blabla", e.title);
        assertEquals(SOME_CARDLIST, e.parentCardList);
        assertEquals("", e.description);
        assertEquals(SOME_CARDLIST.cards.size(), e.index);
    }

    @Test
    public void checkEmptyConstructor() {
        var c = new Card();
        assertNotEquals(null, c);
    }

    @Test
    public void addTask() {
        var e = new Card("blabla", "", SOME_CARDLIST);
        var testTask = new Task("todo1",  e);
        List<Task> SOME_SUBENTRIES = new ArrayList<>();
        SOME_SUBENTRIES.add(testTask);
        e.addTask(testTask);
        assertEquals(SOME_SUBENTRIES, e.tasks);
    }

    @Test
    public void setDescription() {
        var c = new Card("blabla", "", SOME_CARDLIST);
        c.setDescription("abc");
        assertEquals("abc", c.description);
    }

    @Test
    public void setTitle() {
        var c = new Card("blabla", "", SOME_CARDLIST);
        c.setTitle("title");
        assertEquals("title", c.title);
    }

    @Test
    public void setFile() {
        var c = new Card("blabla", "", SOME_CARDLIST);
        c.setFile("img12");
        assertEquals("img12", c.file);
    }

    @Test
    public void removeTag() {
        var c = new Card("blabla", "", SOME_CARDLIST);
        var t1 = new Tag();
        var t2 = new Tag();
        c.tags.add(t1);
        c.tags.add(t2);
        c.removeTag(t2);
        assertFalse(c.tags.contains(t2));
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
        var e = new Card("blabla", "description", SOME_CARDLIST).toString();
        assertTrue(e.contains(Card.class.getSimpleName()));
        assertTrue(e.contains("description"));
        assertTrue(e.contains("blabla"));
        assertTrue(e.contains("\n"));
        assertTrue(e.contains("tasks"));
    }

}
