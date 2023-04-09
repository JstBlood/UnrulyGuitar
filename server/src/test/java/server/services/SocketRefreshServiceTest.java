package server.services;

import commons.Board;
import commons.Card;
import commons.CardList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import server.ConfigTest;
import server.helpers.TestSimpMessaging;


@SpringBootTest
@Import(ConfigTest.class)
class SocketRefreshServiceTest {
    @Autowired
    @Qualifier("socketRefreshService")
    private SocketRefreshService sockets;
    @Autowired
    private TestSimpMessaging simp;

    private Board SOME_BOARD = new Board("asd", "abc");
    private CardList SOME_CARDLIST = new CardList("asd", SOME_BOARD);
    private Card SOME_CARD = new Card("asd", "abc", SOME_CARDLIST);


    @BeforeEach
    void setUp() {
        simp.clear();
    }

    @Test
    void broadcast() {
        sockets.broadcast(SOME_BOARD);
        Assertions.assertEquals(simp.getPaths().get(0), "/topic/board/asd");
        Assertions.assertEquals(((Board)simp.getSent().get(0)).key, "asd");
    }

    @Test
    void broadcastRelist() {
        sockets.broadcastRelist();
        Assertions.assertEquals(simp.getPaths().get(0), "/topic/relist/");
    }

    @Test
    void broadcastRemoval() {
        sockets.broadcastRemoval(SOME_BOARD);
        Assertions.assertEquals(simp.getPaths().get(0), "/topic/board/asd/deletion");
    }

    @Test
    void broadcastRemovalCard() {
        sockets.broadcastRemoval(SOME_CARD);
        Assertions.assertEquals(simp.getPaths().get(0), "/topic/card/" + SOME_CARD.id + "/deletion");
    }

    @Test
    void broadcastRemovalCardList() {
        sockets.broadcastRemoval(SOME_CARDLIST);
        Assertions.assertEquals(simp.getPaths().get(0), "/topic/cardlist/" + SOME_CARDLIST.id + "/deletion");
    }
}