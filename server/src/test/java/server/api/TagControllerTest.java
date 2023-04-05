package server.api;

import static org.springframework.http.HttpStatus.*;

import java.util.Random;

import commons.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import server.ConfigTest;
import server.database.TestBoardsRepository;
import server.database.TestColorPresetRepository;
import server.database.TestTagRepository;
import server.database.TestUserRepository;
import server.services.*;

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

    @BeforeEach
    public void setup() {
        repo.clean();
        uRepo.clean();
        colorRepo.clean();
        bRepo.clean();

        SOME_TAG.colors = new ColorPreset();
    }

    @Test
    public void cannotAddNullTag() {
        var actual = sut.add(null, "", "");

        Assertions.assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addTag() {
        var actual = sut.add(SOME_TAG, "", "");

        Assertions.assertEquals(CREATED, actual.getStatusCode());
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

        Assertions.assertEquals(OK, actual.getStatusCode());
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
    public void updateName() {
        repo.save(SOME_TAG);
        var actual = sut.updateName(SOME_TAG.id, "some name", "", "");

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