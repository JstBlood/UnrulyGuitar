package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

import java.awt.*;
import java.util.Random;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import server.database.TestBoardsRepository;
import server.database.TestColorPresetRepository;
import server.database.TestTagRepository;
import server.database.TestUserRepository;
import server.services.*;

public class TagControllerTest {

    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final Card SOME_CARD = new Card("title", "description", SOME_CARDLIST);
    private final Tag SOME_TAG = new Tag("name", Color.RED, SOME_BOARD);
    public int nextInt;
    private MyRandom random;
    private TestUserRepository uRepo;
    private TestBoardsRepository bRepo;
    private TestTagRepository repo;
    private TagController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        uRepo = new TestUserRepository();
        bRepo = new TestBoardsRepository();
        repo = new TestTagRepository();

        TestUserRepository uRepo = new TestUserRepository();
        SocketRefreshService sockets = new TestSocketRefresher();
        RepositoryBasedAuthService pwd = new RepositoryBasedAuthService(uRepo);
        TestBoardsRepository bRepo = new TestBoardsRepository();
        var tests = new TestColorPresetRepository();

        BoardsService bService = new BoardsService(bRepo, uRepo, sockets, pwd, tests);

        TagService service = new TagService(repo, bService);

        sut = new TagController(service);
    }

    @Test
    public void cannotAddNullTag() {
        var actual = sut.add(null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addTag() {
        var actual = sut.add(SOME_TAG, "", "");

        Assertions.assertEquals(CREATED, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentTag() {
        var actual = sut.delete(1234567890, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void deleteTag() {
        repo.save(SOME_TAG);
        var actual = sut.delete(SOME_TAG.id, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNameNonexistentTask() {
        var actual = sut.updateName(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNameToNull() {
        repo.save(SOME_TAG);
        var actual = sut.updateName(SOME_TAG.id, null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateName() {
        repo.save(SOME_TAG);
        var actual = sut.updateName(SOME_TAG.id, "some name", "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
    }

    @Test
    public void cannotUpdateColorNonexistentTask() {
        var actual = sut.updateColor(1234567890, Color.RED, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateToNullColor() {
        repo.save(SOME_TAG);
        var actual = sut.updateColor(SOME_TAG.id, null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateColor() {
        repo.save(SOME_TAG);
        var actual = sut.updateColor(SOME_TAG.id, Color.RED, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
    }

    @SuppressWarnings("serial")   public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }
}
