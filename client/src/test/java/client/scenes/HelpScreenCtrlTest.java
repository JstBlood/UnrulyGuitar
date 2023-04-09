package client.scenes;

import client.utils.ServerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HelpScreenCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    private HelpScreenCtrl helpScreenCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        helpScreenCtrl = new HelpScreenCtrl(serverUtils, mainCtrl);
    }

    @Test
    public void testBackLogon() {
        helpScreenCtrl.setPrevScene("logon");
        helpScreenCtrl.back();
    }

    @Test
    public void testBackBoards() {
        helpScreenCtrl.setPrevScene("boards");
        helpScreenCtrl.back();
    }

    @Test
    public void testBackBoardOverview() {
        helpScreenCtrl.setPrevScene("boardOverview");
        helpScreenCtrl.back();
    }

    @Test
    public void testBackBoardSettings() {
        helpScreenCtrl.setPrevScene("boardSettings");
        helpScreenCtrl.back();
    }

    @Test
    public void setPrevScene() {
        helpScreenCtrl.setPrevScene("test");
    }
}
