package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CardListTest {
    private static final Board SOME_BOARD = new Board("board1", "myBoard");
    @Test
    public void checkConstructor() {
        var c = new CardList("myCard", SOME_BOARD);
        assertEquals("myCard", c.title);
        assertEquals(SOME_BOARD, c.parentBoard);
    }
    @Test
    public void addEntry() {
        var c = new CardList("myCard", SOME_BOARD);
        var testEntry1 = new Card("todo1", "", c);
        var testEntry2 = new Card("todo2", "", c);
        List<Card> SOME_ENTRIES = new ArrayList<>(Arrays.asList(testEntry1, testEntry2));
        c.addCard(testEntry1);
        c.addCard(testEntry2);
        assertEquals(SOME_ENTRIES, c.cards);
    }

    @Test
    public void EqualsHashCode() {
        var a = new CardList("myCard", SOME_BOARD);
        var b = new CardList("myCard", SOME_BOARD);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new CardList("myCard", SOME_BOARD);
        var b = new CardList("myCard", new Board("123", null));
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var c = new CardList("myCard", SOME_BOARD).toString();
        assertTrue(c.contains(CardList.class.getSimpleName()));
        assertTrue(c.contains("\n"));
        assertTrue(c.contains("cards"));
    }

}
