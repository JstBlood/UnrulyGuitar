package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.util.List;
import java.util.*;

import org.junit.jupiter.api.Test;

public class BoardTest {
    private final Board SOME_BOARD = new Board("board", "myBoard");

    @Test
    public void checkConstructor() {
        var b = new Board("myBoard", "work");
        assertEquals("myBoard", b.key);
        assertEquals("work", b.title);
    }

    @Test
    public void checkEmptyConstructor() {
        var b = new Board();
        assertNotEquals(null, b);
    }

    @Test
    public void addCard() {
        var b = new Board("myBoard", "work");
        CardList testCardList1 = new CardList("testCard", SOME_BOARD);
        CardList testCardList2 = new CardList("myCard", SOME_BOARD);
        List<CardList> SOME_CARDLISTS = new ArrayList<>(Arrays.asList(testCardList1, testCardList2));
        b.addCard(testCardList1);
        b.addCard(testCardList2);
        assertEquals(SOME_CARDLISTS, b.cardLists);
    }

    @Test
    public void addUser() {
        var b = new Board("myBoard", "work");
        User testUser1 = new User("Mickey Mouse");
        User testUser2 = new User("Spiderman");
        Set<User> SOME_USERS = new HashSet<>(Arrays.asList(testUser1, testUser2));
        b.addUser(testUser1);
        b.addUser(testUser2);
        assertEquals(SOME_USERS, b.users);
    }

    @Test
    public void addTag() {
        var b = new Board("myBoard", "work");
        Tag testTag1 = new Tag("urgent", SOME_BOARD);
        Tag testTag2 = new Tag("mandatory",  SOME_BOARD);
        List<Tag> SOME_TAGS = new ArrayList<>(Arrays.asList(testTag1, testTag2));
        b.addTag(testTag1);
        b.addTag(testTag2);
        assertEquals(SOME_TAGS, b.tags);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Board("myBoard", "work");
        var b = new Board("myBoard", "work");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Board("myBoard", "work");
        var b = new Board("yourBoard", "home");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var b = new Board("myBoard", "work").toString();
        assertTrue(b.contains(Board.class.getSimpleName()));
        assertTrue(b.contains("\n"));
        assertTrue(b.contains("cardLists"));
    }
}
