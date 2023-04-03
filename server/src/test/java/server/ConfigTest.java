package server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.springframework.util.ObjectUtils.isEmpty;

public class ConfigTest {

    Config config = new Config();

    @Test
    public void configTest() {
        var actual = config.getRandom();

        Assertions.assertFalse(actual == null || isEmpty(actual));
    }

}
