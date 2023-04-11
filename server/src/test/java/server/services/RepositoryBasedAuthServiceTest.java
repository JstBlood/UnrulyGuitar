package server.services;

import commons.Board;
import commons.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import server.ConfigTest;
import server.database.TestBoardsRepository;
import server.database.TestUserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(ConfigTest.class)
class RepositoryBasedAuthServiceTest {
    @Autowired
    @Qualifier("repositoryBasedAuthService")
    private RepositoryBasedAuthService pwd;

    @Autowired
    private TestUserRepository uRepo;
    @Autowired
    private TestBoardsRepository repo;

    private User SOME_USER = new User("test");
    private Board SOME_BOARD = new Board("test", "test");

    @BeforeEach
    void setUp() {
        uRepo.clean();
        repo.clean();
        SOME_BOARD.isPasswordProtected = false;
        SOME_BOARD.password = null;
    }

    @Test
    void retriveUser() {
        uRepo.save(SOME_USER);
        Assertions.assertEquals(SOME_USER.id, pwd.retriveUser("test").id);
    }

    @Test
    void retriveUserNoSuchUser() {
        pwd.retriveUser("test");

        Assertions.assertTrue(uRepo.getCalled().contains("saveAndFlush"));
    }

    @Test
    void hasEditAccessAdmin() {
        Assertions.assertTrue(pwd.hasEditAccess("test_admin", "xyz", "any"));
    }

    @Test
    void noEditForNonexistentBoard() {
        Assertions.assertFalse(pwd.hasEditAccess("test", "xyz", "any"));
    }

    @Test
    void editForBoardWithoutPassword() {
        repo.save(SOME_BOARD);
        Assertions.assertTrue(pwd.hasEditAccess("test", "xyz", "test"));
    }

    @Test
    void editForBoardWithCorrectPassword() {
        SOME_BOARD.isPasswordProtected = true;
        SOME_BOARD.password = "asd";

        repo.save(SOME_BOARD);
        Assertions.assertTrue(pwd.hasEditAccess("test", "asd", "test"));
    }

    @Test
    void noEditForBoardWithIncorrectPassword() {
        SOME_BOARD.isPasswordProtected = true;
        SOME_BOARD.password = "asd";

        repo.save(SOME_BOARD);
        Assertions.assertFalse(pwd.hasEditAccess("test", "invalid", "test"));
    }

    @Test
    void checkAdminPass() {
        Assertions.assertFalse(pwd.checkAdminPass("this is not an admin password :DD"));
    }
}