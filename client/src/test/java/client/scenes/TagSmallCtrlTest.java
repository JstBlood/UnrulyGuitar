package client.scenes;

import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TagSmallCtrlTest {
    @Mock
    private Tag tag;
    private TagSmallCtrl tagSmallCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tagSmallCtrl = new TagSmallCtrl(tag);
    }

    @Test
    public void test() {
    }
}
