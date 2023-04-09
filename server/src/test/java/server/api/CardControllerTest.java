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

import commons.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import server.ConfigTest;
import server.database.*;
import server.services.TestAuthService;

@SpringBootTest
@Import(ConfigTest.class)
public class CardControllerTest {
    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final Card SOME_CARD = new Card("title", "description", SOME_CARDLIST);
    private final Tag SOME_TAG = new Tag("title", SOME_BOARD);
    private final ColorPreset SOME_PRESET = new ColorPreset();

    @Autowired
    private TestCardRepository repo;
    @Autowired
    private TestUserRepository uRepo;
    @Autowired
    private TestBoardsRepository bRepo;
    @Autowired
    private TestCardListRepository clRepo;
    @Autowired
    private TestTagRepository tagRepo;
    @Autowired
    private CardController sut;
    @Autowired
    private TestColorPresetRepository colorRepo;
    @Autowired
    private TestAuthService auth;

    @BeforeEach
    public void setup() {
        repo.clean();
        uRepo.clean();
        bRepo.clean();
        clRepo.clean();
        tagRepo.clean();
        colorRepo.clean();
        auth.setNoFail();
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

        Assertions.assertTrue(repo.getCalled().contains("save"));
        Assertions.assertEquals(CREATED, actual.getStatusCode());
    }

    @Test
    public void cannotAddEmptyTitle() {
        SOME_CARD.title = "";
        var actual = sut.add(SOME_CARD, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
        SOME_CARD.title = "title";
    }

    @Test
    public void cannotAddInvalidPassword() {
        auth.setFail();
        var actual = sut.add(SOME_CARD, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentCard() {
        var actual = sut.delete(-1, "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotFindNonexistentCard() {
        var actual = sut.updateIndex(1234567890, "2", "", "");
        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsedDelete() {
        repo.save(SOME_CARD);
        repo.shiftCardsUp(SOME_CARD.index, SOME_CARD.parentCardList.id);
        var actual = sut.delete(SOME_CARD.id, "", "");

        Assertions.assertTrue(repo.getCalled().contains("deleteById"));
        Assertions.assertTrue(repo.getCalled().contains("shiftCardsUp"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdatePresetBadId() {
        var actual = sut.updatePreset(-1, 0, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdatePresetNonexistentCard() {
        var actual = sut.updatePreset(123567890, 0, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNonexistentPreset() {
        repo.save(SOME_CARD);

        var actual = sut.updatePreset(SOME_CARD.id, 1234567890, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void resetColors() {
        repo.save(SOME_CARD);

        var actual = sut.updatePreset(SOME_CARD.id, -1, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertEquals(2, repo.getCalled().stream().filter(x -> x.equals("saveAndFlush")).count());
        Assertions.assertNull(SOME_CARD.colors);
    }

    @Test
    public void updatePreset() {
        repo.save(SOME_CARD);
        colorRepo.save(SOME_PRESET);

        var actual = sut.updatePreset(SOME_CARD.id, SOME_PRESET.id, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertEquals(2, repo.getCalled().stream().filter(x -> x.equals("saveAndFlush")).count());

        Assertions.assertEquals(SOME_PRESET.id, SOME_CARD.colors.id);
        Assertions.assertEquals(Board.getDefaultBackground(), SOME_CARD.colors.background);
        Assertions.assertEquals(Board.getDefaultForeground(), SOME_CARD.colors.foreground);
    }

    @Test
    public void cannotDeleteInvalidPassword() {
        auth.setFail();
        repo.save(SOME_CARD);
        var actual = sut.delete(SOME_CARD.id, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNonexistentCard() {
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
    public void updateTitleDatabaseUsed() {
        repo.save(SOME_CARD);
        var actual = sut.updateTitle(SOME_CARD.id, "newTitle", "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateDescBadId() {
        var actual = sut.updateDescription(-1, "", "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateDescNonexistentCard() {
        var actual = sut.updateDescription(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void updateNullDesc() {
        repo.save(SOME_CARD);

        var actual = sut.updateDescription(SOME_CARD.id, null, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void updateEmptyDesc() {
        repo.save(SOME_CARD);

        var actual = sut.updateDescription(SOME_CARD.id, "", "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void updateDesc() {
            repo.save(SOME_CARD);

            var actual = sut.updateDescription(SOME_CARD.id, "Some new Description String", "", "");

            Assertions.assertEquals(OK, actual.getStatusCode());
            Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void cannotUpdateInvalidPassword() {
        auth.setFail();
        repo.save(SOME_CARD);
        var actual = sut.updateTitle(SOME_CARD.id, "newTitle", "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateParentNonexistentCard() {
        var actual = sut.updateParent(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadParent() {
        repo.save(SOME_CARD);
        var actual = sut.updateParent(SOME_CARD.id, -1, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNonexistentParent() {
        repo.save(SOME_CARD);
        var actual = sut.updateParent(SOME_CARD.id, 1234567890, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotAddTagBadId() {
        var actual = sut.updateAddTag(-1, "", "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddTagNonexistentCard() {
        var actual = sut.updateAddTag(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotAddNonexistentTag() {
        repo.save(SOME_CARD);

        var actual = sut.updateAddTag(SOME_CARD.id, 1234567890, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void addTag() {
        repo.save(SOME_CARD);
        tagRepo.save(SOME_TAG);

        var actual = sut.updateAddTag(SOME_CARD.id, SOME_TAG.id, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void cannotRemoveTagBadId() {
        var actual = sut.updateRemoveTag(-1, "", "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotRemoveTagNonexistentCard() {
        var actual = sut.updateRemoveTag(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotRemoveNonexistentTag() {
        repo.save(SOME_CARD);

        var actual = sut.updateRemoveTag(SOME_CARD.id, 1234567890, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void removeTagNotOnCard() {
        repo.save(SOME_CARD);
        tagRepo.save(SOME_TAG);

        var actual = sut.updateRemoveTag(SOME_CARD.id, SOME_TAG.id, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void removeTag() {
        repo.save(SOME_CARD);
        tagRepo.save(SOME_TAG);

        var actual = sut.updateAddTag(SOME_CARD.id, SOME_TAG.id, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));

        actual = sut.updateRemoveTag(SOME_CARD.id, SOME_TAG.id, "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void updateList() {
        repo.save(SOME_CARD);
        clRepo.save(SOME_CARDLIST);
        var actual = sut.updateParent(SOME_CARD.id, SOME_CARDLIST.id, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
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

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void updateDescription() {
        repo.save(SOME_CARD);
        var actual = sut.updateDescription(SOME_CARD.id, "upup", "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertEquals("upup", SOME_CARD.description);
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void removePreset() {
        repo.save(SOME_CARD);
        colorRepo.save(SOME_PRESET);
        var actual = sut.updatePreset(SOME_CARD.id, -1, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertNull(SOME_CARD.colors);
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotSetNonexistentPreset() {
        repo.save(SOME_CARD);
        var actual = sut.updatePreset(SOME_CARD.id, -2, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotPresetNoPassword() {
        auth.setFail();
        repo.save(SOME_CARD);
        var actual = sut.updatePreset(SOME_CARD.id, -2, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateDescriptionNoPassword() {
        auth.setFail();
        repo.save(SOME_CARD);
        var actual = sut.updateDescription(SOME_CARD.id, "asd", "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
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
    public void cannotUpdateDDNonexistentCard() {
        var actual = sut.updateDragAndDrop(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateDDEmptyParent() {
        repo.save(SOME_CARD);
        var actual = sut.updateDragAndDrop(SOME_CARD.id, 1234567890, "", "");

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

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertTrue(repo.getCalled().contains("shiftCardsUp"));
        Assertions.assertTrue(repo.getCalled().contains("shiftCardsDown"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateListDragAndDropBadId() {
        var actual = sut.updateListDragAndDrop(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBadLDD() {
        repo.save(SOME_CARD);
        var actual = sut.updateListDragAndDrop(SOME_CARD.id, -1, "", "");
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNonexistentListDragAndDrop() {
        repo.save(SOME_CARD);
        var actual = sut.updateListDragAndDrop(SOME_CARD.id, 1234567890, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void updateLDD() {
        var newList = new CardList("asda", SOME_BOARD);

        SOME_CARDLIST.cards.add(SOME_CARD);

        clRepo.save(SOME_CARDLIST);
        clRepo.save(newList);

        repo.save(SOME_CARD);

        var actual = sut.updateListDragAndDrop(SOME_CARD.id, SOME_CARDLIST.id, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertTrue(repo.getCalled().contains("shiftCardsUp"));
        Assertions.assertEquals(OK, actual.getStatusCode());

        actual = sut.updateListDragAndDrop(SOME_CARD.id, newList.id, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertTrue(repo.getCalled().contains("shiftCardsUp"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateSwapBadCurrId() {
        var actual = sut.updateSwap(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateSwapBadTargetId() {
        repo.save(SOME_CARD);
        var actual = sut.updateSwap(SOME_CARD.id, "-1", "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotRemoveBadTag() {
        repo.save(SOME_CARD);
        var actual = sut.updateRemoveTag(SOME_CARD.id, -1, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void updateAddTag() {
        repo.save(SOME_CARD);
        tagRepo.save(SOME_TAG);

        var actual = sut.updateAddTag(SOME_CARD.id, SOME_TAG.id
                , "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void cannotUpdateAddTag() {
        auth.setFail();
        repo.save(SOME_CARD);
        tagRepo.save(SOME_TAG);

        var actual = sut.updateAddTag(SOME_CARD.id, SOME_TAG.id
                , "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateAddTagNonExistent() {
        repo.save(SOME_CARD);

        var actual = sut.updateAddTag(SOME_CARD.id, -2, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateRemoveTag() {
        SOME_CARD.tags.add(SOME_TAG);
        repo.save(SOME_CARD);
        tagRepo.save(SOME_TAG);

        var actual = sut.updateRemoveTag(SOME_CARD.id, SOME_TAG.id
                , "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void updateRemoveInvalidPassword() {
        auth.setFail();
        SOME_CARD.tags.add(SOME_TAG);
        repo.save(SOME_CARD);
        tagRepo.save(SOME_TAG);

        var actual = sut.updateRemoveTag(SOME_CARD.id, SOME_TAG.id
                , "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void updateSwap() {
        var newCard = new Card("abc", "fasf", SOME_CARDLIST);
        repo.save(SOME_CARD);
        repo.save(newCard);

        var actual = sut.updateSwap(SOME_CARD.id, newCard.id, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }
}