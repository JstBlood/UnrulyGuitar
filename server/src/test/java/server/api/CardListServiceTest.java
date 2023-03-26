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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Random;

import commons.Board;
import commons.CardList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.services.CardListService;
import server.services.RepositoryBasedAuthService;
import server.services.SocketRefreshService;
public class CardListServiceTest {

    public int nextInt;
    private MyRandom random;
    private TestCardListRepository repo;
    private TestBoardsRepository bRepo;
    private TestUserRepository uRepo;
    private SocketRefreshService sockets;
    private SimpMessagingTemplate simp;
    private RepositoryBasedAuthService pwd;
    private CardListService sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        repo = new TestCardListRepository();
        bRepo = new TestBoardsRepository();
        uRepo = new TestUserRepository();
        sockets = new SocketRefreshService(simp);
        pwd = new RepositoryBasedAuthService(uRepo);
        sut = new CardListService(repo, new BoardsController(bRepo, uRepo, sockets, pwd));
    }

    @Test
    public void cannotAddNullTitle() {
        var actual = sut.add(getCardList(null));
        Assertions.assertEquals(BAD_REQUEST, actual);
    }

    @Test
    public void cannotDeleteInexistentList() {
        var actual = sut.delete(-1);
        Assertions.assertEquals(NOT_FOUND, actual);
    }

    @Test
    public void cannotUpdateInexistentComponent() {
        CardList cardList = getCardList("test");
        repo.save(cardList);
        var actual = sut.update(cardList.id, "margin", "12");
        Assertions.assertEquals(BAD_REQUEST, actual);
    }

    @Test
    public void databaseIsUsedAdd() {
        sut.add(getCardList("q1"));
        Assertions.assertTrue(repo.calledMethods.contains("save"));
    }

    @Test
    public void databaseIsUsedDelete() {
        CardList cardList = getCardList("q1");
        sut.add(cardList);
        sut.delete(cardList.id);
        Assertions.assertFalse(repo.calledMethods.contains("deleteById"));
    }

    @Test
    public void databaseIsUsedUpdate() {
        CardList cardList = getCardList("q1");
        repo.save(cardList);
        System.out.println(sut.update(cardList.id, "title", "newTitle"));
        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
    }
    private CardList getCardList(String q) {
        Board pBoard = new Board("key", "title");
        return new CardList(q, pBoard);
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