package client.scenes;

import client.utils.ServerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AddCardListCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    private AddCardListCtrl addCardListCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        addCardListCtrl = new AddCardListCtrl(serverUtils, mainCtrl);
    }

    @Test
    public void test() {

    }
}
