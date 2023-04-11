package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import server.ConfigTest;
import server.database.TestBoardsRepository;
import server.database.TestColorPresetRepository;
import server.database.TestTaskRepository;
import server.database.TestUserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;
import server.database.*;
import server.helpers.TestAuthService;

@SpringBootTest
@Import(ConfigTest.class)
public class TaskControllerTest {
    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final Card SOME_CARD = new Card("title", "description", SOME_CARDLIST);
    private final Task SOME_TASK = new Task("title", SOME_CARD);
    @Autowired
    private TestTaskRepository repo;

    @Autowired
    private TaskController sut;

    @Autowired
    private TestUserRepository uRepo;
    @Autowired
    private TestBoardsRepository bRepo;
    @Autowired
    private TestColorPresetRepository colorRepo;
    @Autowired
    private TestAuthService auth;

    @BeforeEach
    public void setup() {
        repo.clean();
        uRepo.clean();
        colorRepo.clean();
        bRepo.clean();
        auth.setNoFail();
    }

    @Test
    public void cannotAddNullTask() {
        var actual = sut.add(null, "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullTTitle() {
        var temp = SOME_TASK.title;

        SOME_TASK.title = null;
        var actual = sut.add(SOME_TASK, "", "");

        SOME_TASK.title = temp;
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullParent() {
        var actual = sut.add(new Task("title", null), "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedAdd() {
        var actual = sut.add(SOME_TASK, "", "");

        Assertions.assertTrue(repo.getCalled().contains("save"));
        Assertions.assertEquals(CREATED, actual.getStatusCode());
    }

    @Test
    public void cannotAddNoPassword() {
        auth.setFail();
        var actual = sut.add(SOME_TASK, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentTask() {
        var actual = sut.delete(-1, "", "");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedDelete() {
        repo.save(SOME_TASK);
        var actual = sut.delete(SOME_TASK.id, "", "");

        Assertions.assertTrue(repo.getCalled().contains("deleteById"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNoPassword() {
        auth.setFail();
        repo.save(SOME_TASK);
        var actual = sut.delete(SOME_CARD.id, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadIdTask() {
        var actual = sut.updateTitle(-1, "newTitle", "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNonexistentTask() {
        var actual = sut.updateTitle(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNullOrEmptyTask() {
        repo.save(SOME_TASK);
        var actual = sut.updateTitle(SOME_TASK.id, "", "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateTitle() {
        repo.save(SOME_TASK);
        var actual = sut.updateTitle(SOME_TASK.id, "newTitle", "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateTaskNoPassword() {
        auth.setFail();
        repo.save(SOME_TASK);
        var actual = sut.updateTitle(SOME_CARD.id, "newTitle", "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateEmptyValue() {
        repo.save(SOME_TASK);
        var actual = sut.updateTitle(SOME_TASK.id,  "", "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateIsDoneBadId() {
        var actual = sut.updateIsDone(1234567890,  "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void updateIsDone() {
        repo.save(SOME_TASK);
        var actual = sut.updateIsDone(SOME_TASK.id, Boolean.TRUE, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void cannotUpdateIndexBadId() {
        var actual = sut.updateIndex(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadIndex() {
        repo.save(SOME_TASK);
        var actual = sut.updateIndex(SOME_TASK.id, -1, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateIndex() {
        repo.save(SOME_TASK);
        var actual = sut.updateIndex(SOME_TASK.id, 0, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }
}