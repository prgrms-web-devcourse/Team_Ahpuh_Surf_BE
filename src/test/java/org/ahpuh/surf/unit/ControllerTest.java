package org.ahpuh.surf.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ahpuh.surf.config.JwtConfig;
import org.ahpuh.surf.user.controller.UserController;
import org.ahpuh.surf.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@Import(JwtConfig.class)
@ActiveProfiles("test")
@WebMvcTest(UserController.class)
public abstract class ControllerTest {

    @MockBean
    protected UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
