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
import commons.Card;
import commons.CardList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.BoardsService;
import server.services.CardService;
import server.services.RepositoryBasedAuthService;

public class CardControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestCardRepository repo;
    private Board pBoard;
    private TestUserRepository uRepo;
    private TestBoardsRepository bRepo;
    private CardController sut;

    @BeforeEach
    public void setup() {
        pBoard = new Board("parent", "title");
        random = new MyRandom();
        repo = new TestCardRepository();
        uRepo = new TestUserRepository();
        bRepo = new TestBoardsRepository();
        sut = new CardController(new CardService(repo,  new BoardsService(bRepo, uRepo,
                null, new RepositoryBasedAuthService(uRepo))));
    }

    @Test
    public void cannotAddNullTitle() {
        var actual = sut.add(getCard(null, "p1"), "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullParentList() {
        var actual = sut.add(new Card("title", "description", null), "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNullList() {
        var actual = sut.delete(-1, "", "");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNullList() {
        var actual = sut.update(-1, "title", "", "", "");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedAdd() {
        sut.add(getCard("q1", "p1"), "", "");
        repo.calledMethods.contains("save");
    }

    @Test
    public void databaseIsUsedDelete() {
        Card card = getCard("q1", "p1");
        sut.add(card, "", "");
        sut.delete(card.id, "", "");
        repo.calledMethods.contains("deleteById");
    }

    @Test
    public void databaseIsUsedUpdate() {
        Card card = getCard("q1", "p1");
        sut.add(card, "", "");
        sut.update(card.id, "title", "", "", "");
        repo.calledMethods.contains("saveAndFlush");
    }
    private static Card getCard(String q, String p) {
        Board pBoard = new Board("key", "title");
        CardList pList = new CardList("title", pBoard);
        return new Card(q, p, pList);
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
