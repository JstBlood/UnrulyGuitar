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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Random;

import commons.Board;
import commons.CardList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.RepositoryBasedAuthService;

public class CardListControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestCardListRepository repo;
    private Board pBoard;
    private TestUserRepository uRepo;
    private TestBoardsRepository bRepo;
    private CardListController sut;

    @BeforeEach
    public void setup() {
        pBoard = new Board("parent", "title");
        random = new MyRandom();
        repo = new TestCardListRepository();
        uRepo = new TestUserRepository();
        bRepo = new TestBoardsRepository();
        sut = new CardListController(random, repo, new BoardsController(random, bRepo, uRepo,
                null, new RepositoryBasedAuthService(uRepo)), new RepositoryBasedAuthService(uRepo));
    }

    @Test
    public void cannotAddNullTitle() {
        var actual = sut.add(getCardList(null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullParentBoard() {
        var actual = sut.add(new CardList("title", null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNullList() {
        var actual = sut.delete(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNullList() {
        var actual = sut.update("", "pwd", -1, "title");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedAdd() {
        sut.add(getCardList("q1"));
        repo.calledMethods.contains("save");
    }

    @Test
    public void databaseIsUsedDelete() {
        CardList cardList = getCardList("q1");
        sut.add(cardList);
        sut.delete(cardList);
        repo.calledMethods.contains("deleteById");
    }

    @Test
    public void databaseIsUsedUpdate() {
        CardList cardList = getCardList("q1");
        sut.add(cardList);
        repo.calledMethods.contains("saveAndFlush");
    }
    private static CardList getCardList(String q) {
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