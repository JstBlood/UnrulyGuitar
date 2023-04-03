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
import static org.springframework.http.HttpStatus.*;

import java.util.Random;

import commons.Board;
import commons.Card;
import commons.CardList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.*;
import server.services.*;

public class CardControllerTest {
    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final Card SOME_CARD = new Card("title", "description", SOME_CARDLIST);
    public int nextInt;
    private MyRandom random;
    private TestCardRepository repo;
    private Board pBoard;
    private TestUserRepository uRepo;
    private TestBoardsRepository bRepo;
    private TestCardListRepository clRepo;
    private TestTagRepository tagRepo;
    private CardController sut;

    @BeforeEach
    public void setup() {
        random = new MyRandom();
        repo = new TestCardRepository();
        uRepo = new TestUserRepository();
        bRepo = new TestBoardsRepository();
        SocketRefreshService sockets = new TestSocketRefresher();
        var colorRepo = new TestColorPresetRepository();
        clRepo = new TestCardListRepository();


        RepositoryBasedAuthService pwd = new RepositoryBasedAuthService(uRepo);

        CardService service = new CardService(repo, new BoardsService(bRepo, uRepo, sockets, pwd, colorRepo),
                clRepo, sockets, colorRepo, tagRepo);


        sut = new CardController(service);
    }

    @Test
    public void cannotAddNullCard() {
        var actual = sut.add(null, "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullParent() {
        var actual = sut.add(new Card("title", "description", null), "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedAdd() {
        var actual = sut.add(SOME_CARD, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("save"));
        Assertions.assertEquals(CREATED, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteInexistentCard() {
        var actual = sut.delete(-1, "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedDelete() {
        repo.save(SOME_CARD);
        repo.shiftCardsUp(SOME_CARD.index, SOME_CARD.parentCardList.id);
        var actual = sut.delete(SOME_CARD.id, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("deleteById"));
        Assertions.assertTrue(repo.calledMethods.contains("shiftCardsUp"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateInexistentCard() {
        var actual = sut.updateTitle(-1, "", "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadTitle() {
        repo.save(SOME_CARD);
        var actual = sut.updateTitle(SOME_CARD.id, "", "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateTitle() {
        repo.save(SOME_CARD);
        var actual = sut.updateTitle(SOME_CARD.id, "newTitle", "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadList() {
        repo.save(SOME_CARD);
        var actual = sut.updateParent(SOME_CARD.id, -1, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateList() {
        repo.save(SOME_CARD);
        clRepo.save(SOME_CARDLIST);
        var actual = sut.updateParent(SOME_CARD.id, SOME_CARDLIST.id, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadIndex() {
        repo.save(SOME_CARD);
        var actual = sut.updateIndex(SOME_CARD.id, -1, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateIndex() {
        repo.save(SOME_CARD);
        var actual = sut.updateIndex(SOME_CARD.id, 0, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadDD() {
        repo.save(SOME_CARD);
        var actual = sut.updateDragAndDrop(SOME_CARD.id, -1, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());

        actual = sut.updateDragAndDrop(SOME_CARD.id, SOME_CARD.id, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());

    }

    @Test
    public void updateDD() {
        var newList = new CardList("asda", SOME_BOARD);
        var newCard = new Card("abc", "fasf", newList);
        repo.save(SOME_CARD);
        repo.save(newCard);
        clRepo.save(SOME_CARDLIST);
        clRepo.save(newList);

        var actual = sut.updateDragAndDrop(SOME_CARD.id, newCard.id, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertTrue(repo.calledMethods.contains("shiftCardsUp"));
        Assertions.assertTrue(repo.calledMethods.contains("shiftCardsDown"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadLDD() {
        repo.save(SOME_CARD);
        var actual = sut.updateListDragAndDrop(SOME_CARD.id, -1, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateLDD() {
        var newList = new CardList("asda", SOME_BOARD);

        SOME_CARDLIST.cards.add(SOME_CARD);

        clRepo.save(SOME_CARDLIST);
        clRepo.save(newList);

        repo.save(SOME_CARD);

        var actual = sut.updateListDragAndDrop(SOME_CARD.id, SOME_CARDLIST.id, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertTrue(repo.calledMethods.contains("shiftCardsUp"));
        Assertions.assertEquals(OK, actual.getStatusCode());

        actual = sut.updateListDragAndDrop(SOME_CARD.id, newList.id, "", "");

        Assertions.assertTrue(repo.calledMethods.contains("saveAndFlush"));
        Assertions.assertTrue(repo.calledMethods.contains("shiftCardsUp"));
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
