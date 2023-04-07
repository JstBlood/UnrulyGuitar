package client.scenes;

import client.utils.ServerUtils;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TagCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private Tag mockTag;
    private TagCtrl tagCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tagCtrl = new TagCtrl(serverUtils, mainCtrl, mockTag);
    }

    @Test
    public void test() {
    }
}
