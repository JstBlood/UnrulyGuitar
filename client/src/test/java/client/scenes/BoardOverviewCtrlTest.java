package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BoardOverviewCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    private Board board = new Board("123", "title");
    private BoardOverviewCtrl boardOverviewCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        boardOverviewCtrl = new BoardOverviewCtrl(serverUtils, mainCtrl);
        boardOverviewCtrl.setBoard(board);
    }

    @Test
    public void testPrepareServer() {
        boardOverviewCtrl.prepareServer();
    }

    @Test
    public void testPrepareLongPolling() {
        boardOverviewCtrl.prepareLongPolling();
    }

    @Test
    public void testPerformRelink() {
        boardOverviewCtrl.relink(board);
    }

    @Test
    public void testGetBoard() {
        Assertions.assertEquals(board, boardOverviewCtrl.getBoard());
    }

    @Test
    public void testAddCardList() {
        boardOverviewCtrl.addCardList();
    }

    @Test
    public void testRemoveBoard() {
        boardOverviewCtrl.removeBoard();
    }
    

    @Test
    public void testShowHelp() {
        boardOverviewCtrl.showHelp();
    }

    @Test
    public void testOpenSettings() {
        boardOverviewCtrl.openSettings();
    }

    @Test
    public void testBack() {
        boardOverviewCtrl.back();
    }

    @Test
    public void testStop() {
        boardOverviewCtrl.stop();
    }

}
