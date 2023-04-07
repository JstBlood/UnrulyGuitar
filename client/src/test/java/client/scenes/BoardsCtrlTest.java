package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BoardsCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private Board board;
    private BoardsCtrl boardsCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        boardsCtrl = new BoardsCtrl(serverUtils, mainCtrl);
    }

    @Test
    public void testCreate() {
        boardsCtrl.create();
    }

    @Test
    public void testBack() {
        boardsCtrl.back();
    }

    @Test
    public void testShowHelp() {
        boardsCtrl.showHelp();
    }
}
