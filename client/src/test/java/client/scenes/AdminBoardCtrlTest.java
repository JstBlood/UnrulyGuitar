package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AdminBoardCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private Board board;
    private AdminBoardCtrl adminBoardCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adminBoardCtrl = new AdminBoardCtrl(serverUtils, mainCtrl, board);
    }

    @Test
    public void test() {

    }
}
