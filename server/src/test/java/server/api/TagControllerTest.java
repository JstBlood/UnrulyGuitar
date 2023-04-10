package server.api;

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
import server.database.TestTagRepository;
import server.database.TestUserRepository;
import server.helpers.TestAuthService;

@SpringBootTest
@Import(ConfigTest.class)
public class TagControllerTest {

    private final Board SOME_BOARD = new Board("key", "title");
    private final CardList SOME_CARDLIST = new CardList("title", SOME_BOARD);
    private final Card SOME_CARD = new Card("title", "description", SOME_CARDLIST);
    private final Tag SOME_TAG = new Tag("name",  SOME_BOARD);
    @Autowired
    private TestUserRepository uRepo;
    @Autowired
    private TestBoardsRepository bRepo;
    @Autowired
    private TestTagRepository repo;
    @Autowired
    private TagController sut;

    @Autowired
    private TestColorPresetRepository colorRepo;
    @Autowired
    private TestAuthService auth;

    @BeforeEach
    public void setup() {
        repo.clean();
        uRepo.clean();
        colorRepo.clean();
        bRepo.clean();
        auth.setNoFail();

        SOME_TAG.colors = new ColorPreset();
    }

    @Test
    public void cannotAddNullTag() {
        var actual = sut.add(null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddTagNullParent() {
        var temp = SOME_TAG.parentBoard;
        SOME_TAG.parentBoard = null;
        var actual = sut.add(SOME_TAG, "", "");

        SOME_TAG.parentBoard = temp;
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddTagNullName() {
        var temp = SOME_TAG.name;
        SOME_TAG.name = null;
        var actual = sut.add(SOME_TAG, "", "");

        SOME_TAG.name = temp;
        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addTag() {
        var actual = sut.add(SOME_TAG, "", "");

        Assertions.assertEquals(CREATED, actual.getStatusCode());
    }

    @Test
    public void cannotAddTagNoPassword() {
        auth.setFail();
        var actual = sut.add(SOME_TAG, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteNonexistentTag() {
        var actual = sut.delete(1234567890, "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void deleteTag() {
        repo.save(SOME_TAG);
        var actual = sut.delete(SOME_TAG.id, "", "");

        Assertions.assertTrue(repo.getCalled().contains("deleteById"));
        Assertions.assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteReferencing() {
        repo.setFail();
        repo.save(SOME_TAG);
        var actual = sut.delete(SOME_TAG.id, "", "");

        Assertions.assertEquals(FAILED_DEPENDENCY, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteTagNoPassword() {
        auth.setFail();
        repo.save(SOME_TAG);
        var actual = sut.delete(SOME_TAG.id, "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNameNonexistentTask() {
        var actual = sut.updateName(1234567890, "", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNameToNull() {
        repo.save(SOME_TAG);
        var actual = sut.updateName(SOME_TAG.id, null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateNameNoPassword() {
        auth.setFail();
        repo.save(SOME_TAG);
        var actual = sut.updateName(SOME_TAG.id, "test", "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void updateName() {
        repo.save(SOME_TAG);
        var actual = sut.updateName(SOME_TAG.id, "some name", "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void cannotBackgroundNoPassword() {
        auth.setFail();
        repo.save(SOME_TAG);
        var actual = sut.updateBackground(SOME_TAG.id, "test", "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotBackgroundNull() {
        SOME_TAG.colors = null;
        repo.save(SOME_TAG);
        var actual = sut.updateBackground(SOME_TAG.id, "test", "", "");

        Assertions.assertEquals(EXPECTATION_FAILED, actual.getStatusCode());
    }

    @Test
    public void cannotBackgroundNonexistentTag() {
        repo.save(SOME_TAG);
        var actual = sut.updateBackground(-1, "test", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void updateBackground() {
        repo.save(SOME_TAG);
        var actual = sut.updateBackground(
                SOME_TAG.id, "some name", "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void cannotForegroundNoPassword() {
        auth.setFail();
        repo.save(SOME_TAG);
        var actual = sut.updateForeground(SOME_TAG.id, "test", "", "");

        Assertions.assertEquals(FORBIDDEN, actual.getStatusCode());
    }

    @Test
    public void cannotForegroundNull() {
        SOME_TAG.colors = null;
        repo.save(SOME_TAG);
        var actual = sut.updateForeground(SOME_TAG.id, "test", "", "");

        Assertions.assertEquals(EXPECTATION_FAILED, actual.getStatusCode());
    }

    @Test
    public void cannotForegroundNonexistentTag() {
        repo.save(SOME_TAG);
        var actual = sut.updateForeground(-1, "test", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void updateForeground() {
        repo.save(SOME_TAG);
        var actual = sut.updateForeground(
                SOME_TAG.id, "some name", "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }

    @Test
    public void cannotUpdateColorNonexistentTask() {
        var actual = sut.updateForeground(1234567890, "#121212", "", "");

        Assertions.assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateToNullColor() {
        repo.save(SOME_TAG);
        var actual = sut.updateForeground(SOME_TAG.id, null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateToNullColorBackground() {
        repo.save(SOME_TAG);
        var actual = sut.updateBackground(SOME_TAG.id, null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void updateColor() {
        repo.save(SOME_TAG);
        var actual = sut.updateBackground(SOME_TAG.id, "#101010", "", "");

        Assertions.assertEquals(OK, actual.getStatusCode());
        Assertions.assertTrue(repo.getCalled().contains("saveAndFlush"));
    }
}