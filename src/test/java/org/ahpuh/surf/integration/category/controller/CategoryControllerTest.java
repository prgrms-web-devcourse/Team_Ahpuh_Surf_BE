package org.ahpuh.surf.integration.category.controller;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.integration.IntegrationControllerTest;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.user.domain.Permission;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.ahpuh.surf.user.dto.response.UserLoginResponseDto;
import org.ahpuh.surf.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends IntegrationControllerTest {

    private User user;
    private Category category;
    private String token;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@naver.com")
                .userName("test")
                .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                .build();
        user.setPermission(Permission.ROLE_USER);
        userRepository.save(user);
        category = categoryRepository.save(Category.builder()
                .user(user)
                .name("test")
                .colorCode("#e7f5ff")
                .build());
        postRepository.save(Post.builder()
                .user(user)
                .category(category)
                .content("post1")
                .selectedDate(LocalDate.now())
                .score(88).build());

        final UserLoginResponseDto loginResponse = userService.authenticate(user.getEmail(), "testpw");
        token = loginResponse.getToken();
    }

    @Test
    @DisplayName("카테고리를 생성할 수 있다.")
    void createCategory() throws Exception {
        final CategoryCreateRequestDto req = CategoryCreateRequestDto.builder()
                .name("suebeen")
                .colorCode("#d0ebff")   // TODO: 예외 테스트
                .build();

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(print());

    }

    @Test
    @DisplayName("카테고리를 수정 할 수 있다.")
    void updateCategory() throws Exception {
        final CategoryUpdateRequestDto req = CategoryUpdateRequestDto.builder()
                .name("update")
                .isPublic(false)
                .colorCode("#d0ebdf")
                .build();

        mockMvc.perform(put("/api/v1/categories/{categoryId}", category.getCategoryId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리를 삭제할 수 있다.")
    void deleteCategory() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/{categoryId}", category.getCategoryId())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("유저의 모든 카테고리 정보를 조회할 수 있다.")
    void findAllCategoryByUser() throws Exception {
        mockMvc.perform(get("/api/v1/categories", user.getUserId())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("유저의 대시보드를 조회할 수 있다.")
    void getCategoryDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/categories/dashboard")
                        .param("userId", String.valueOf(user.getUserId()))
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
