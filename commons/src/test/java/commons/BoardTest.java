package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.util.List;
import java.util.*;

import org.junit.jupiter.api.Test;

public class BoardTest {
    private static final Board SOME_BOARD = new Board("board", "myBoard", "Nothing yet", Color.BLUE);

    @Test
    public void checkConstructor() {
        var b = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        assertEquals("myBoard", b.key);
        assertEquals("work", b.title);
        assertEquals("Nothing yet", b.description);
        assertEquals(Color.blue, b.backgroundColor);
    }

    @Test
    public void addCard() {
        var b = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        Card testCard1 = new Card("testCard", "Nothing yet", SOME_BOARD);
        Card testCard2 = new Card("myCard", "Nothing yet", SOME_BOARD);
        List<Card> SOME_CARDS = new ArrayList<>(Arrays.asList(testCard1, testCard2));
        b.addCard(testCard1);
        b.addCard(testCard2);
        assertEquals(SOME_CARDS, b.cards);
    }

    @Test
    public void addUser() {
        var b = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        User testUser1 = new User("Mickey Mouse");
        User testUser2 = new User("Spiderman");
        Set<User> SOME_USERS = new HashSet<>(Arrays.asList(testUser1, testUser2));
        b.addUser(testUser1);
        b.addUser(testUser2);
        assertEquals(SOME_USERS, b.users);
    }

    @Test
    public void addTag() {
        var b = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        Tag testTag1 = new Tag("urgent", Color.BLACK);
        Tag testTag2 = new Tag("mandatory", Color.BLACK);
        Set<Tag> SOME_TAGS = new HashSet<>(Arrays.asList(testTag1, testTag2));
        b.addTag(testTag1);
        b.addTag(testTag2);
        assertEquals(SOME_TAGS, b.tags);
    }

    @Test
    public void setPassword() {
        var b = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        b.setPassword("abc123");
        assertEquals("abc123", b.passwordHash);
        assertTrue(b.isPasswordProtected);
    }

    @Test
    public void deletePassword() {
        var b = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        b.setPassword("abc123");
        b.deletePassword();
        assertNull(b.passwordHash);
        assertFalse(b.isPasswordProtected);
    }

    @Test
    public void EqualsHashCode() {
        var a = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        var b = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Board("myBoard", "work", "Nothing yet", Color.BLUE);
        var b = new Board("yourBoard", "home", "Nothing yet", Color.BLUE);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var b = new Board("myBoard", "work", "Nothing yet", Color.BLUE).toString();
        assertTrue(b.contains(Board.class.getSimpleName()));
        assertTrue(b.contains("\n"));
        assertTrue(b.contains("cards"));
    }
}
