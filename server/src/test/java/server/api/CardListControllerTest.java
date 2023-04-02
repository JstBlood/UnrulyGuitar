/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import static org.springframework.http.HttpStatus.*;

import java.util.Random;

import commons.Board;
import commons.CardList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.TestBoardsRepository;
import server.database.TestCardListRepository;
import server.database.TestUserRepository;
import server.services.*;

public class CardListControllerTest {

    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    public int nextInt;
    private MyRandom random;
    private TestCardListRepository repo;
    private CardListController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        repo = new TestCardListRepository();

        TestBoardsRepository bRepo = new TestBoardsRepository();
        TestUserRepository uRepo = new TestUserRepository();
        SocketRefreshService sockets = new TestSocketRefresher();
        RepositoryBasedAuthService pwd = new RepositoryBasedAuthService(uRepo);
        CardListService service = new CardListService(repo, new BoardsService(bRepo, uRepo, sockets, pwd), sockets);

        sut = new CardListController(service);
    }

    @Test
    public void cannotAddNullList() {
        var actual = sut.add(null, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullParent() {
        var actual = sut.add(new CardList("title", null), "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullTitle() {
        var actual = sut.add(new CardList(null, SOME_BOARD), "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedAdd() {
        var actual = sut.add(SOME_CARDLIST, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("save"));
        Assertions.assertEquals(CREATED, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteInexistentList() {
        var actual = sut.delete(-1, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedDelete() {
        repo.save(SOME_CARDLIST);
        var actual = sut.delete(SOME_CARDLIST.id, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("deleteById"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateInexistentList() {
        var actual = sut.updateTitle(-1, "newTitle", "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadValue() {
        repo.save(SOME_CARDLIST);
        var actual = sut.updateTitle(SOME_CARDLIST.id, "", "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadComponent() {
        repo.save(SOME_CARDLIST);
        var actual = sut.update(SOME_CARDLIST.id, "margin", "12", "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedUpdate() {
        repo.save(SOME_CARDLIST);
        var actual = sut.updateTitle(SOME_CARDLIST.id, "newTitle", "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
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