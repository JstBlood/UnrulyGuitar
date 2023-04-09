package client.scenes;

import client.utils.ServerUtils;
import commons.CardList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CardListCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private CardList cardList;
    private CardListCtrl cardListCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cardListCtrl = new CardListCtrl(serverUtils, mainCtrl, cardList);
    }

    @Test
    public void testDeleteCardList() {
        cardList.id = 1;
        cardListCtrl.deleteCardList();
    }
}
