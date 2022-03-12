package org.ahpuh.surf.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ahpuh.surf.category.controller.CategoryController;
import org.ahpuh.surf.category.service.CategoryService;
import org.ahpuh.surf.config.JwtConfig;
import org.ahpuh.surf.follow.controller.FollowController;
import org.ahpuh.surf.follow.service.FollowService;
import org.ahpuh.surf.post.controller.PostController;
import org.ahpuh.surf.post.service.PostService;
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
@WebMvcTest({
        UserController.class,
        CategoryController.class,
        PostController.class,
        FollowController.class
})
public abstract class ControllerTest {

    @MockBean
    protected UserService userService;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected PostService postService;

    @MockBean
    protected FollowService followService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
