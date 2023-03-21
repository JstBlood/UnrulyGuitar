package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Random;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestTaskRepository repo;
    private Board pBoard;
    private TestUserRepository uRepo;
    private TestBoardsRepository bRepo;
    private TaskController sut;

    @BeforeEach
    public void setup() {
        pBoard = new Board("parent", "title");
        random = new MyRandom();
        repo = new TestTaskRepository();
        uRepo = new TestUserRepository();
        bRepo = new TestBoardsRepository();
        sut = new TaskController(random, repo, new BoardsController(random, bRepo, uRepo, null));
    }

    @Test
    public void cannotAddNullTitle() {
        var actual = sut.add(getTask(null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullParentCard() {
        var actual = sut.add(new Task("title", null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNullTask() {
        var actual = sut.delete(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNullTask() {
        var actual = sut.update(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedAdd() {
        sut.add(getTask("q1"));
        repo.calledMethods.contains("save");
    }

    @Test
    public void databaseIsUsedDelete() {
        Task task = getTask("q1");
        sut.add(task);
        sut.delete(task);
        repo.calledMethods.contains("deleteById");
    }

    @Test
    public void databaseIsUsedUpdate() {
        Task task = getTask("q1");
        sut.add(task);
        sut.update(task);
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