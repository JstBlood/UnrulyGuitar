package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

import java.util.Random;

import commons.Board;
import commons.Card;
import commons.CardList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import server.database.TestBoardsRepository;
import server.database.TestColorPresetRepository;
import server.database.TestUserRepository;
import server.services.*;

public class BoardControllerTest {

    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final Card SOME_CARD = new Card("title", "description", SOME_CARDLIST);
    public int nextInt;
    private MyRandom random;
    private TestUserRepository uRepo;
    private TestBoardsRepository repo;
    private BoardsController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        uRepo = new TestUserRepository();
        repo = new TestBoardsRepository();

        TestUserRepository uRepo = new TestUserRepository();
        SocketRefreshService sockets = new TestSocketRefresher();
        RepositoryBasedAuthService pwd = new RepositoryBasedAuthService(uRepo);
        var tests = new TestColorPresetRepository();

        BoardsService service = new BoardsService(repo, uRepo, sockets, pwd, tests);

        sut = new BoardsController(service);
    }

    @Test
    public void cannotAddNullBoard() {
        var actual = sut.add(null, "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addBoard() {
        var actual = sut.add(SOME_BOARD, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotJoinNonexistentBoard() {
        repo.save(SOME_BOARD);
        var actual1 = sut.join("some board key string that doesnt exist", "", "");
        var actual2 = sut.join(null, "", "");

        Assertions.assertEquals(NOT_FOUND, actual1.getStatusCode());
        Assertions.assertEquals(BAD_REQUEST, actual2.getStatusCode());
    }

    @Test
    public void joinBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.join(SOME_BOARD.key, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNullBoard() {
        var actual = sut.leave(null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNonexistentBoard() {
        var actual = sut.leave("some board key that doesnt exist", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNotJoinedBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.leave(SOME_BOARD.key, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        //I don't know how to implement this without calling uRepo
    }

    @Test
    public void leaveBoard() {
        repo.save(SOME_BOARD);
        sut.join(SOME_BOARD.key, "", "");
        var actual = sut.leave(SOME_BOARD.key, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
    }


    @Test
    public void cannotUpdateTitleWithNullBoardKey() {
        var actual = sut.updateTitle(null, "", "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateTitleOfNonexistentBoard() {
        var actual = sut.updateTitle("Some board key that doesn't exist", "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateToNullTitle() {
        repo.save(SOME_BOARD);
        var actual = sut.updateTitle(SOME_BOARD.key, null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNoPasswordAccess() {
        //TODO: Implement
        Assertions.assertEquals(FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateTitle() {
        repo.save(SOME_BOARD);
        var actual = sut.updateTitle(SOME_BOARD.key,"New title", "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteBoardWithNullKey() {
        var actual = sut.delete(null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentBoard() {
        var actual = sut.delete("Some board key that doesn't exist", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void deleteBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.delete(SOME_BOARD.key, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.calledMethods.contains("delete"));
        Assertions.assertFalse(repo.boardList.contains(SOME_BOARD));
    }

    @Test
    public void cantGetAllNoPasswordAccess() {
        //TODO: Implement
        Assertions.assertEquals(FORBIDDEN, FORBIDDEN);
    }

    @Test
    public void cannotGetAllNoPasswordAccess() {
        var actual = sut.all("", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void getAll() {
       var actual = sut.all("", "xyz");

        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void getPrevious() {
        var actual = sut.previous("", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotForceRefreshNullKeyBoard() {
        var actual = sut.previous(null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotForceRefreshNonexistentBoard() {
        var actual = sut.previous("Some nonexistent board key", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void forceRefresh() {
        repo.save(SOME_BOARD);
        var actual = sut.previous(SOME_BOARD.key, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @SuppressWarnings("serial")
    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }
}
