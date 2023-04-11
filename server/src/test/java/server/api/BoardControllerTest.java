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
import server.database.TestBoardsRepository;
import server.database.TestColorPresetRepository;
import server.database.TestUserRepository;
import server.helpers.TestAuthService;

@SpringBootTest
@Import(ConfigTest.class)
public class BoardControllerTest {

    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final User SOME_USER = new User("test");
    private final User SOME_USER2 = new User("test1");

    @Autowired
    private BoardsController sut;
    @Autowired
    private TestBoardsRepository repo;
    @Autowired
    private TestUserRepository uRepo;
    @Autowired
    private TestColorPresetRepository colorRepo;
    @Autowired
    private TestAuthService auth;

    @BeforeEach
    public void setup() {
        repo.clean();
        colorRepo.clean();
        uRepo.clean();
        auth.setNoFail();
        SOME_BOARD.colors = new ColorPreset();
        SOME_BOARD.cardListColors = new ColorPreset();
        SOME_USER.boards.clear();
        SOME_USER2.boards.clear();
        SOME_USER.boards.add(SOME_BOARD);
        SOME_USER2.boards.add(SOME_BOARD);
    }

    @Test
    public void cannotAddNullBoard() {
        var actual = sut.add(null, "", "");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddBoardWithNoTitle() {
        var temp = SOME_BOARD.title;
        SOME_BOARD.title = "";

        var actual = sut.add(SOME_BOARD, "", "");

        SOME_BOARD.title = temp;
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddBoardWithNoKey() {
        var temp = SOME_BOARD.key;
        SOME_BOARD.key = "";

        var actual = sut.add(SOME_BOARD, "", "");

        SOME_BOARD.key = temp;
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addBoard() {
        var actual = sut.add(SOME_BOARD, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotJoinNonexistentBoard() {
        repo.save(SOME_BOARD);
        var actual1 = sut.join("some board key string that doesnt exist", "", "");
        var actual2 = sut.join(null, "", "");

        assertEquals(NOT_FOUND, actual1.getStatusCode());
        assertEquals(BAD_REQUEST, actual2.getStatusCode());
    }

    @Test
    public void joinBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.join(SOME_BOARD.key, "", "");

        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void addPreset() {
        repo.save(SOME_BOARD);
        var actual = sut.addPreset(new ColorPreset(), "", SOME_BOARD.key, "");

        Assertions.assertEquals(SOME_BOARD.cardPresets.size(), 1);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotRemoveNonexistentPreset() {
        repo.save(SOME_BOARD);
        var actual = sut.removePreset(-1L, "", SOME_BOARD.key, "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotRemoveNonexistentBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.removePreset(-1L, "", "-1", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void updateBackground() {
        repo.save(SOME_BOARD);
        var actual = sut.updateBack(SOME_BOARD.key, "a", "-1", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBackground() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.updateBack(SOME_BOARD.key, "a", "-1", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void updateForeground() {
        repo.save(SOME_BOARD);
        var actual = sut.updateFore(SOME_BOARD.key, "a", "-1", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateForegroundBadPassword() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.updateFore(SOME_BOARD.key, "a", "-1", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotValidateNoBoard() {
        var actual = sut.validate(SOME_BOARD.key, "a", "-1");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotValidateFail() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.validate(SOME_BOARD.key, "a", "-1");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void validate() {
        repo.save(SOME_BOARD);
        var actual = sut.validate(SOME_BOARD.key, "a", "-1");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void changePassword() {
        repo.save(SOME_BOARD);
        var actual = sut.changePass(SOME_BOARD.key, "a", "a", "a");

        assertEquals(OK, actual.getStatusCode());
        assertEquals(SOME_BOARD.isPasswordProtected, true);
    }

    @Test
    public void changePasswordNoBoard() {
        var actual = sut.changePass(SOME_BOARD.key, "a", "a", "a");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void removePassword() {
        repo.save(SOME_BOARD);
        var actual = sut.removePass(SOME_BOARD.key, "a", "a");

        assertEquals(OK, actual.getStatusCode());
        assertEquals(SOME_BOARD.isPasswordProtected, false);
    }

    @Test
    public void removePasswordNoBoard() {
        var actual = sut.removePass(SOME_BOARD.key, "a", "a");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }
    @Test
    public void cannotUpdateForegroundEmpty() {
        repo.save(SOME_BOARD);
        var actual = sut.updateFore(SOME_BOARD.key, "", "-1", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBackgroundEmpty() {
        repo.save(SOME_BOARD);
        var actual = sut.updateBack(SOME_BOARD.key, "", "-1", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBackgroundListEmpty() {
        repo.save(SOME_BOARD);
        var actual = sut.updateBackList(SOME_BOARD.key, "", "-1", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateForegroundListEmpty() {
        repo.save(SOME_BOARD);
        var actual = sut.updateForeList(SOME_BOARD.key, "", "-1", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNonexistentBackgroundPreset() {
        repo.save(SOME_BOARD);

        var actual = sut.updateBackPreset(SOME_BOARD.key, 1234567890L, "", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNullNewVal() {
        repo.save(SOME_BOARD);
        colorRepo.save(SOME_BOARD.colors);

        var actual = sut.updateBackPreset(SOME_BOARD.key, SOME_BOARD.colors.id, null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateBackgroundPreset() {
        repo.save(SOME_BOARD);
        colorRepo.save(SOME_BOARD.colors);
        var actual = sut.updateBackPreset(SOME_BOARD.key, SOME_BOARD.colors.id,
                "a", "", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void updatePreset() {
        repo.save(SOME_BOARD);
        colorRepo.save(SOME_BOARD.colors);
        var actual = sut.updateDefPreset(SOME_BOARD.colors.id, SOME_BOARD.key,
                "", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdatePresetInvalidPassword() {
        auth.setFail();
        repo.save(SOME_BOARD);
        colorRepo.save(SOME_BOARD.colors);
        var actual = sut.updateDefPreset(SOME_BOARD.colors.id, SOME_BOARD.key,
                "", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdatePresetNonExistent() {
        repo.save(SOME_BOARD);
        var actual = sut.updateDefPreset(SOME_BOARD.colors.id, SOME_BOARD.key,
                "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBackgroundPreset() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.updateBackPreset(SOME_BOARD.key, SOME_BOARD.colors.id,
                "a", "", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void updateForegroundPreset() {
        repo.save(SOME_BOARD);
        colorRepo.save(SOME_BOARD.colors);
        var actual = sut.updateForePreset(SOME_BOARD.key, SOME_BOARD.colors.id,
                "a", "", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateForegroundPreset() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.updateForePreset(SOME_BOARD.key, SOME_BOARD.colors.id,
                "a", "", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateForegroundPresetDoesntExist() {
        repo.save(SOME_BOARD);
        var actual = sut.updateForePreset(SOME_BOARD.key, -1L,
                "a", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateForegroundPresetEmpty() {
        repo.save(SOME_BOARD);
        colorRepo.save(SOME_BOARD.colors);
        var actual = sut.updateForePreset(SOME_BOARD.key, SOME_BOARD.colors.id,
                "", "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBackgroundPresetDoesntExist() {
        repo.save(SOME_BOARD);
        var actual = sut.updateBackPreset(SOME_BOARD.key, -1L,
                "a", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBackgroundPresetEmpty() {
        repo.save(SOME_BOARD);
        colorRepo.save(SOME_BOARD.colors);
        var actual = sut.updateBackPreset(SOME_BOARD.key, SOME_BOARD.colors.id,
                "", "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateForegroundList() {
        repo.save(SOME_BOARD);
        var actual = sut.updateForeList(SOME_BOARD.key, "a", "-1", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateForegroundList() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.updateForeList(SOME_BOARD.key, "a", "-1", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void updateBackgroundList() {
        repo.save(SOME_BOARD);
        var actual = sut.updateBackList(SOME_BOARD.key, "a", "-1", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateBackgroundList() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.updateBackList(SOME_BOARD.key, "a", "-1", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void removePreset() {
        repo.save(SOME_BOARD);
        sut.addPreset(new ColorPreset(), "", SOME_BOARD.key, "");

        var second  = new ColorPreset();
        second.id = 14;

        sut.addPreset(second, "", SOME_BOARD.key, "");

        Assertions.assertEquals(SOME_BOARD.cardPresets.size(), 2);

        var actual = sut.removePreset(SOME_BOARD.cardPresets.get(1).id
                , "", SOME_BOARD.key, "");

        Assertions.assertEquals(SOME_BOARD.cardPresets.size(), 1);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotAddPresetToNonexistentBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.addPreset(new ColorPreset(), "-1", "-1", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNullBoard() {
        var actual = sut.leave(null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNonexistentBoard() {
        var actual = sut.leave("some board key that doesnt exist", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotLeaveNotJoinedBoard() {
        repo.save(SOME_BOARD);
        var actual = sut.leave(SOME_BOARD.key, "", "");

        assertEquals(OK, actual.getStatusCode());
        //I don't know how to implement this without calling uRepo
    }

    @Test
    public void leaveBoard() {
        repo.save(SOME_BOARD);
        uRepo.save(SOME_USER);
        uRepo.save(SOME_USER2);
        SOME_BOARD.users.add(SOME_USER);
        SOME_BOARD.users.add(SOME_USER2);
        auth.toRetieve = SOME_USER;
        var second = new Board("asd", "ad");
        second.id = 14;
        SOME_USER.boards.add(second);
        sut.join(SOME_BOARD.key, "", "");
        var actual = sut.leave(SOME_BOARD.key, "", "");

        assertEquals(OK, actual.getStatusCode());
    }


    @Test
    public void cannotUpdateTitleWithNullBoardKey() {
        var actual = sut.updateTitle(null, "", "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateTitleOfNonexistentBoard() {
        var actual = sut.updateTitle("Some board key that doesn't exist", "", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateToNullTitle() {
        repo.save(SOME_BOARD);
        var actual = sut.updateTitle(SOME_BOARD.key, null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateToEmptyTitle() {
        repo.save(SOME_BOARD);
        var actual = sut.updateTitle(SOME_BOARD.key, "", "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNoPasswordAccess() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.updateTitle(SOME_BOARD.key,"New title", "", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void updateTitle() {
        repo.save(SOME_BOARD);
        var actual = sut.updateTitle(SOME_BOARD.key,"New title", "", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteBoardWithNullKey() {
        var actual = sut.delete(null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentBoard() {
        var actual = sut.delete("Some board key that doesn't exist", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void deleteBoard() {
        uRepo.save(SOME_USER);
        uRepo.save(SOME_USER2);
        repo.save(SOME_BOARD);
        SOME_BOARD.users.add(SOME_USER);
        SOME_BOARD.users.add(SOME_USER2);
        var actual = sut.delete(SOME_BOARD.key, "", "");


        SOME_BOARD.users.clear();

        assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("delete"));
        Assertions.assertFalse(repo.boardList.contains(SOME_BOARD));
    }

    @Test
    public void cannotDeleteInvalidPassword() {
        auth.setFail();
        repo.save(SOME_BOARD);
        var actual = sut.delete(SOME_BOARD.key, "", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotGetAllNoPasswordAccess() {
        auth.setFail();
        var actual = sut.all("", "");

        assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void getAll() {
       var actual = sut.all("", "xyz");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void getPrevious() {
        var actual = sut.previous("", "");

        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotForceRefreshNullKeyBoard() {
        var actual = sut.previous(null, "", "");

        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotForceRefreshNonexistentBoard() {
        var actual = sut.previous("Some nonexistent board key", "", "");

        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void forceRefresh() {
        repo.save(SOME_BOARD);
        var actual = sut.previous(SOME_BOARD.key, "", "");

        assertEquals(OK, actual.getStatusCode());
    }
}
