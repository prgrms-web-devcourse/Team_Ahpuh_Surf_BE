package org.ahpuh.surf.acceptance;

import io.restassured.RestAssured;
import org.ahpuh.surf.jwt.Claims;
import org.ahpuh.surf.jwt.Jwt;
import org.ahpuh.surf.user.domain.Permission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private Jwt jwt;

    @LocalServerPort
    private int port;

    protected String TOKEN;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }

    @BeforeEach
    void setToken() {
        final String[] roles = new String[]{Permission.ROLE_USER.getRole()};
        TOKEN = jwt.sign(Claims.from(1L, "test1@naver.com", roles));
    }

    @AfterEach
    void cleanUpDatabase() {
        databaseCleaner.cleanUp();
    }
}
