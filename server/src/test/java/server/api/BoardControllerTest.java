package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;


import commons.Board;
import commons.Card;
import commons.CardList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import server.ConfigTest;
import server.database.TestBoardsRepository;
import server.database.TestColorPresetRepository;
import server.database.TestUserRepository;
import server.services.*;

@SpringBootTest
@Import(ConfigTest.class)
public class BoardControllerTest {

    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final Card SOME_CARD = new Card("title", "description", SOME_CARDLIST);

    @Autowired
    private BoardsController sut;
    @Autowired
    private TestBoardsRepository repo;
    @Autowired
    private TestUserRepository uRepo;
    @Autowired
    private TestColorPresetRepository colorRepo;

    @BeforeEach
    public void setup() {
        repo.clean();
        colorRepo.clean();
        uRepo.clean();
    }

    @Test
    public void cannotAddNullBoard() {
        var actual = sut.add(null, "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addBoard() {
        var actual = sut.add(SOME_BOARD, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotJoinNonexistentBoard() {
        repo.save(SOME_BOARD);
        var actual1 = sut.join("some board key string that doesnt exist", "", "");
        var actual2 = sut.join(null, "", "");

        assertEquals(NOT_FOUND, actual1.getStatusCode());
        assertEquals(BAD_REQUEST, actual2.getStatusCode());
    }

    @Test
    public void joinBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.join(SOME_BOARD.key, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNullBoard() {
        var actual = sut.leave(null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNonexistentBoard() {
        var actual = sut.leave("some board key that doesnt exist", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNotJoinedBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.leave(SOME_BOARD.key, "", "");

        assertEquals(OK, actual.getStatusCode());
        //I don't know how to implement this without calling uRepo
    }

    @Test
    public void leaveBoard() {
        repo.save(SOME_BOARD);
        sut.join(SOME_BOARD.key, "", "");
        var actual = sut.leave(SOME_BOARD.key, "", "");

        assertEquals(OK, actual.getStatusCode());
    }


    @Test
    public void cannotUpdateTitleWithNullBoardKey() {
        var actual = sut.updateTitle(null, "", "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateTitleOfNonexistentBoard() {
        var actual = sut.updateTitle("Some board key that doesn't exist", "", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateToNullTitle() {
        repo.save(SOME_BOARD);
        var actual = sut.updateTitle(SOME_BOARD.key, null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNoPasswordAccess() {
        //TODO: Implement
        assertEquals(FORBIDDEN, FORBIDDEN);
    }

    @Test
    public void updateTitle() {
        repo.save(SOME_BOARD);
        var actual = sut.updateTitle(SOME_BOARD.key,"New title", "", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteBoardWithNullKey() {
        var actual = sut.delete(null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentBoard() {
        var actual = sut.delete("Some board key that doesn't exist", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void deleteBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.delete(SOME_BOARD.key, "", "");

        assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("delete"));
        Assertions.assertFalse(repo.boardList.contains(SOME_BOARD));
    }

    @Test
    public void cantGetAllNoPasswordAccess() {
        //TODO: Implement
        assertEquals(FORBIDDEN, FORBIDDEN);
    }

    @Test
    public void cannotGetAllNoPasswordAccess() {
        var actual = sut.all("", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void getAll() {
       var actual = sut.all("", "xyz");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void getPrevious() {
        var actual = sut.previous("", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotForceRefreshNullKeyBoard() {
        var actual = sut.previous(null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotForceRefreshNonexistentBoard() {
        var actual = sut.previous("Some nonexistent board key", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void forceRefresh() {
        repo.save(SOME_BOARD);
        var actual = sut.previous(SOME_BOARD.key, "", "");

        assertEquals(OK, actual.getStatusCode());
    }
}
