package client.scenes;

import client.utils.ServerUtils;
import commons.ColorPreset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ColorPresetCtrlTest {
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private ColorPreset colorPreset;
    private ColorPresetCtrl colorPresetCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        colorPresetCtrl = new ColorPresetCtrl(serverUtils, mainCtrl, colorPreset, null);
    }

    @Test
    public void testDelete() {
        colorPreset.id = 1;
        colorPresetCtrl.delete();
    }
}
