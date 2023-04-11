package client.scenes;

import client.utils.ServerUtils;
import commons.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CardCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private Card card;
    private CardCtrl cardCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cardCtrl = new CardCtrl(serverUtils, mainCtrl, card, null);
    }

    @Test
    public void testDelete() {
        card.id = Long.valueOf(1);
        cardCtrl.delete();
    }
}
