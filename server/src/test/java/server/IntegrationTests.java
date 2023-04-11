package server;

import commons.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import server.api.BoardsController;
import server.api.CardController;
import server.api.CardListController;
import server.database.*;
import server.helpers.TestAuthService;

@SpringBootTest
@Import(ConfigTest.class)
public class IntegrationTests {
    @Autowired
    private BoardsController controller;
    @Autowired
    private CardListController cardListController;
    @Autowired
    private CardController cardController;

    @Autowired
    private TestAuthService auth;

    @Autowired
    private TestBoardsRepository repo;

    @Autowired
    private TestCardListRepository clRepo;
    @Autowired
    private TestCardRepository cRepo;
    @Autowired
    private TestColorPresetRepository colorRepo;

    @Autowired
    private TestUserRepository uRepo;

    private Board someBoard = new Board("abc", "abc");
    private CardList someCardList = new CardList("abc", someBoard);
    private Card someCard = new Card("abc", " ", someCardList);
    private User someUser = new User("abc");

    @BeforeEach
    public void setUp() {
        repo.clean();
        uRepo.clean();
        clRepo.clean();
        cRepo.clean();
        colorRepo.clean();
    }

    @Test
    public void testLoginJoinAndRemoveBoardAdmin() {
        repo.save(someBoard);
        var allBoards = controller.all("", "").getBody();

        Assertions.assertEquals(1, allBoards.size());
        Assertions.assertEquals(someBoard, allBoards.get(0));

        var firstBoard = allBoards.get(0);

        controller.join(firstBoard.key, "", "");

        Assertions.assertEquals(1, repo.getBoards().get(0).users.size());

        controller.delete(firstBoard.key, "", "");

        Assertions.assertEquals(0, controller.all("", "").getBody().size());
    }

    @Test
    public void testLoginJoinAndRemoveBoard() {
        auth.setFail();
        auth.setRetrive(someUser);
        repo.save(someBoard);
        someBoard.users.add(someUser);

        controller.join(someBoard.key, "", "");

        auth.setRetrive(uRepo.users.get(0));

        var previousBoards = controller.previous("", "").getBody();

        Assertions.assertEquals(1, previousBoards.size());
        Assertions.assertEquals(someBoard, previousBoards.stream().findAny().get());

        var firstBoard = previousBoards.stream().findAny().get();

        Assertions.assertEquals(1, repo.getBoards().get(0).users.size());

        auth.setNoFail();

        cardListController.add(someCardList, "", "");

        Assertions.assertEquals(1, clRepo.getLists().size());

        controller.delete(firstBoard.key, "", "");
        uRepo.users.get(0).boards.clear();

        Assertions.assertEquals(0, controller.previous("", "").getBody().size());
    }

    @Test
    public void testLoginJoinAndSetAddPreset() {
        auth.setFail();
        auth.setRetrive(someUser);
        repo.save(someBoard);
        someBoard.users.add(someUser);

        controller.join(someBoard.key, "", "");

        auth.setRetrive(uRepo.users.get(0));

        var previousBoards = controller.previous("", "").getBody();

        auth.setNoFail();
        Assertions.assertEquals(1, previousBoards.size());
        Assertions.assertEquals(someBoard, previousBoards.stream().findAny().get());

        cardListController.add(someCardList, "", "");

        Assertions.assertEquals(someCardList, clRepo.getLists().get(0));

        cardController.add(someCard, "", "");

        Assertions.assertEquals(someCard, cRepo.getCards().get(0));

        var preset = new ColorPreset();
        preset.background = "#131313";
        preset.foreground = "#131313";

        controller.addPreset(preset, "", someBoard.key, "");

        Assertions.assertEquals(HttpStatus.OK,
                cardController.updatePreset(someCard.id, preset.id, "", "").getStatusCode());

        Assertions.assertEquals(someCard.colors, preset);
    }

    @Test
    public void cannotLoginToAdminInvalidPassword() {
        auth.setFail();
        repo.save(someBoard);
        var allBoards = controller.all("", "");

        Assertions.assertEquals(HttpStatus.FORBIDDEN, allBoards.getStatusCode());
    }
}
