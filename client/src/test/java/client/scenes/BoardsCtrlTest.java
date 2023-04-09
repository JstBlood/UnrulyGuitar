package client.scenes;

import java.util.Random;

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
    private Random rand;
    private BoardsCtrl boardsCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rand = new Random();
        boardsCtrl = new BoardsCtrl(serverUtils, mainCtrl, rand);
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
