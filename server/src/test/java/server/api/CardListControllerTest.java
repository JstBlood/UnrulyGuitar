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
 *
 */

package server.api;

import static org.springframework.http.HttpStatus.*;

import commons.Board;
import commons.CardList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import server.ConfigTest;
import server.database.TestBoardsRepository;
import server.database.TestCardListRepository;
import server.database.TestColorPresetRepository;
import server.database.TestUserRepository;
import server.helpers.TestAuthService;

@SpringBootTest
@Import(ConfigTest.class)
public class CardListControllerTest {

    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    @Autowired
    private TestCardListRepository repo;
    @Autowired
    private TestBoardsRepository bRepo;
    @Autowired
    private TestUserRepository uRepo;

    @Autowired
    private TestColorPresetRepository colorRepo;
    @Autowired
    private TestAuthService auth;

    @Autowired
    private CardListController sut;

    @BeforeEach
    public void setup() {
        repo.clean();
        bRepo.clean();
        uRepo.clean();
        colorRepo.clean();
        auth.setNoFail();
    }

    private boolean isEmptyOrNull(String s) {
        return s == null || s.isEmpty();
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
    public void cannotDeleteNonexistentCardList() {
        var actual = sut.delete(1234567890, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedAdd() {
        var actual = sut.add(SOME_CARDLIST, "", "");

        Assertions.assertTrue(repo.getCalled().contains("save"));
        Assertions.assertEquals(CREATED, actual.getStatusCode());
    }

    @Test
    public void cannotAddInvalidPassword() {
        auth.setFail();
        var actual = sut.add(SOME_CARDLIST, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentList() {
        var actual = sut.delete(-1, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedDelete() {
        repo.save(SOME_CARDLIST);
        var actual = sut.delete(SOME_CARDLIST.id, "", "");

        Assertions.assertTrue(repo.getCalled().contains("deleteById"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteWrongPassword() {
        auth.setFail();
        repo.save(SOME_CARDLIST);
        var actual = sut.delete(SOME_CARDLIST.id, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNonexistentList() {
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
    public void databaseIsUsedUpdate() {
        repo.save(SOME_CARDLIST);
        var actual = sut.updateTitle(SOME_CARDLIST.id, "newTitle", "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateWrongPassword() {
        auth.setFail();
        repo.save(SOME_CARDLIST);
        var actual = sut.updateTitle(SOME_CARDLIST.id, "newTitle", "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void updates() {
        String actual = sut.getUpdates().toString();

        Assertions.assertFalse(isEmptyOrNull(actual));
    }
}