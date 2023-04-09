package commons;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ColorPresetTest {
    @Test
    public void testEquals() {
        var c1 = new ColorPreset();
        var c2 = new ColorPreset();
        Assertions.assertEquals(c1, c2);
    }

    @Test
    public void testToString() {
        var c = new ColorPreset();
        Assertions.assertTrue(c.toString().contains("background"));
    }
}
