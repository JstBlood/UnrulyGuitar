package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Card;
import commons.CardList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

public class CardDetailsCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private Board board;
    @Mock
    private CardList cardList;
    @Mock
    private Card card;
    private CardDetailsCtrl cardDetailsCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cardDetailsCtrl = new CardDetailsCtrl(serverUtils, mainCtrl);
        board.key = "123";
        cardList.parentBoard = board;
        cardList.id = 1;
        card.parentCardList = cardList;
        card.id = Long.valueOf(1);
        cardDetailsCtrl.setCard(card);
    }

    @Test
    public void testPrepareServer() {
        cardDetailsCtrl.prepareServer();
    }

    @Test
    public void testRelink() {
        card.tasks = new ArrayList<>();
        cardDetailsCtrl.relink(card);
    }

    @Test
    public void testBack() {
        cardDetailsCtrl.back();
    }

    @Test
    public void testGenerateTask() {
        card.tasks = new ArrayList<>();
        cardDetailsCtrl.generateTask();
    }
}
