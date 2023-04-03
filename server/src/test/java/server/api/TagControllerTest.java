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

        BoardsService bService = new BoardsService(bRepo, uRepo, sockets, pwd);

        TagService service = new TagService(repo, bService);

        sut = new TagController(service);
    }

    @Test
    public void test() {

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
