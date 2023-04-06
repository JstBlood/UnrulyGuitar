package client.scenes;

import client.utils.ServerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LogonCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    private LogonCtrl logonCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        logonCtrl = new LogonCtrl(serverUtils, mainCtrl);
    }

    @Test
    public void testShowHelp() {
        logonCtrl.showHelp();
    }

}
