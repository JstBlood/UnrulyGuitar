package commons;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileDataTest {

    @Test
    public void testEmptyConstructor() {
        var q = new FileData();
        Assertions.assertNull(q.name);
        Assertions.assertNull(q.type);
        Assertions.assertNull(q.getFileData());
    }

    @Test
    public void testConstructor() {
        var q = new FileData("img12", "jpeg", null);
        Assertions.assertEquals("img12", q.name);
        Assertions.assertEquals("jpeg", q.type);
        Assertions.assertNull(q.getFileData());
    }

    @Test
    public void getFileData() {
        byte[] b = new byte[12];
        var q = new FileData("img12", "jpeg", b);
        Assertions.assertEquals(b, q.getFileData());
    }
}
