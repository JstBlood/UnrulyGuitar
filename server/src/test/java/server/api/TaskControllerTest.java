package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Random;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.services.BoardsService;
import server.services.SocketRefreshService;
import server.services.TaskService;

public class TaskControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestTaskRepository repo;
    private Board pBoard;
    private TestUserRepository uRepo;
    private TestBoardsRepository bRepo;
    private TaskController sut;
    private SimpMessagingTemplate simp;
    private SocketRefreshService messages;

    @BeforeEach
    public void setup() {
        messages = new SocketRefreshService(simp);
        pBoard = new Board("parent", "title");
        random = new MyRandom();
        repo = new TestTaskRepository();
        uRepo = new TestUserRepository();
        bRepo = new TestBoardsRepository();
        sut = new TaskController(new TaskService(repo ,new BoardsService(bRepo, uRepo, messages , null)));
    }

    @Test
    public void cannotAddNullTitle() {
        var actual = sut.add(getTask(null), "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullParentCard() {
        var actual = sut.add(new Task("title", null), "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteInexistentTask() {
        var actual = sut.delete(-1, "", "");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateInexistentTask() {
        var actual = sut.update(-1, "title", "newTitle", "", "");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedAdd() {
        sut.add(getTask("q1"), "", "");
        repo.calledMethods.contains("save");
    }

    @Test
    public void databaseIsUsedDelete() {
        Task task = getTask("q1");
        sut.add(task, "", "");
        sut.delete(task.id, "", "");
        repo.calledMethods.contains("deleteById");
    }

    @Test
    public void databaseIsUsedUpdate() {
        Task task = getTask("q1");
        sut.add(task, "", "");
        sut.update(-1, "title", "newTitle", "", "");
        repo.calledMethods.contains("saveAndFlush");
    }
    private static Task getTask(String q) {
        Board pBoard = new Board("key", "title");
        CardList pList = new CardList("title", pBoard);
        Card pCard = new Card("title", "description", pList);
        return new Task(q, pCard);
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