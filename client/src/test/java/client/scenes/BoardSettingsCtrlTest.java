package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BoardSettingsCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private Board board;
    private BoardSettingsCtrl boardSettingsCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        boardSettingsCtrl = new BoardSettingsCtrl(serverUtils, mainCtrl);
        boardSettingsCtrl.setBoard(board);
    }

    @Test
    public void testAddTag() {
        boardSettingsCtrl.addTag();
    }

    @Test
    public void testAddPreset() {
        boardSettingsCtrl.addPreset();
    }

    @Test
    public void testShowHelp() {
        boardSettingsCtrl.showHelp();
    }
}
