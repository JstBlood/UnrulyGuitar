package server.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import server.ConfigTest;
import server.helpers.TestAuthService;

@SpringBootTest
@Import(ConfigTest.class)
class AdminControllerTest {
    @Autowired
    private AdminController sut;

    @Autowired
    private TestAuthService pwd;

    @Test
    void cannotShutdownWithoutAdminPrivilages() {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, sut.shutdown("","").getStatusCode());
    }
}