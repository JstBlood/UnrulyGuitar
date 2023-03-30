package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CardListTest {
    private final Board SOME_BOARD = new Board("board1", "myBoard");
    @Test
    public void checkConstructor() {
        var c = new CardList("myCard", SOME_BOARD);
        assertEquals("myCard", c.title);
        assertEquals(SOME_BOARD, c.parentBoard);
    }

    @Test
    public void checkEmptyConstructor() {
        var c= new CardList();
        assertNotEquals(null, c);
    }

    @Test
    public void addCard() {
        var c = new CardList("myCard", SOME_BOARD);
        var testCardList1 = new Card("todo1", "", c);
        var testCardList2 = new Card("todo2", "", c);
        List<Card> SOME_CARDLISTS = new ArrayList<>(Arrays.asList(testCardList1, testCardList2));
        c.addCard(testCardList1);
        c.addCard(testCardList2);
        assertEquals(SOME_CARDLISTS, c.cards);
    }

    @Test
    public void removeCard() {
        var cl = new CardList("myCard", SOME_BOARD);
        var c = new Card("title", "", cl);
        cl.addCard(c);
        cl.removeCard(c);
        assertTrue(cl.cards.isEmpty());
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
