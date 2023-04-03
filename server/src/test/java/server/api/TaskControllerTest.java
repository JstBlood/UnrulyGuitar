package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

import java.util.Random;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.*;
import server.services.BoardsService;
import server.services.RepositoryBasedAuthService;
import server.services.SocketRefreshService;
import server.services.TaskService;

public class TaskControllerTest {
    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final Card SOME_CARD = new Card("title", "description", SOME_CARDLIST);
    private final Task SOME_TASK = new Task("title", SOME_CARD);
    public int nextInt;
    private MyRandom random;
    private TestTaskRepository repo;
    private TaskController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        repo = new TestTaskRepository();

        TestUserRepository uRepo = new TestUserRepository();
        TestBoardsRepository bRepo = new TestBoardsRepository();
        SocketRefreshService sockets = new SocketRefreshService(null);
        RepositoryBasedAuthService pwd = new RepositoryBasedAuthService(uRepo);
        TaskService service = new TaskService(repo, new BoardsService(bRepo, uRepo, sockets, pwd));

        sut = new TaskController(service);
    }

    @Test
    public void cannotAddNullTask() {
        var actual = sut.add(null, "", "");
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

        Assertions.assertTrue(repo.calledMethods.contains("save"));
        Assertions.assertEquals(CREATED, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentTask() {
        var actual = sut.delete(-1, "", "");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedDelete() {
        repo.save(SOME_TASK);
        var actual = sut.delete(SOME_CARD.id, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("deleteById"));
        Assertions.assertEquals(OK, actual.getStatusCode());
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
        var actual = sut.updateTitle(SOME_CARD.id, "newTitle", "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateEmptyValue() {
        repo.save(SOME_TASK);
        var actual = sut.update(SOME_CARD.id, "title", "", "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadComponent() {
        repo.save(SOME_TASK);
        var actual = sut.update(SOME_CARD.id, "margin", "12", "", "");
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
        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
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
        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
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