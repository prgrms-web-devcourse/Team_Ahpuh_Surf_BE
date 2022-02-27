package org.ahpuh.surf.integration.like.controller;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.like.entity.Like;
import org.ahpuh.surf.like.repository.LikeRepository;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.controller.UserController;
import org.ahpuh.surf.user.dto.request.UserLoginRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;

    private User user1;
    private User user2;
    private String userToken1;
    private Post post1;
    private Long postId1;

    @BeforeEach
    void setUp() {
        // user1, user2 회원가입 후 userId 반환
        user1 = userRepository.save(User.builder()
                .email("user1@naver.com")
                .userName("name")
                .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                .build());
        user2 = userRepository.save(User.builder()
                .email("user2@naver.com")
                .userName("name")
                .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                .build());

        // user1 로그인 후 토큰 발급
        userToken1 = userController.login(UserLoginRequestDto.builder()
                        .email("user1@naver.com")
                        .password("testpw")
                        .build())
                .getBody()
                .getToken();

        // user2가 카테고리 생성
        final Category category1 = categoryRepository.save(Category.builder()
                .user(user2)
                .name("category 1")
                .colorCode("#000000")
                .build());

        // user2가 post 생성
        post1 = postRepository.save(Post.builder()
                .user(user2)
                .category(category1)
                .selectedDate(LocalDate.now())
                .content("content")
                .score(80)
                .build());
        postId1 = post1.getPostId();
    }

    @Test
    @DisplayName("게시글 좋아요를 할 수 있다.")
    @Transactional
    void testLike() throws Exception {
        // Given
        assertThat(likeRepository.findAll().size()).isEqualTo(0);

        // When
        mockMvc.perform(post("/api/v1/posts/{postId}/like", postId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userToken1))
                .andExpect(status().isOk())
                .andDo(print());

        // Then
        final List<Like> likes = likeRepository.findAll();
        assertAll("afterLikePost",
                () -> assertThat(likes.size()).isEqualTo(1),
                () -> assertThat(likes.get(0).getUser()).isEqualTo(user1),
                () -> assertThat(likes.get(0).getPost().getPostId()).isEqualTo(postId1)
        );

    }

    @Test
    @DisplayName("게시글 좋아요 취소를 할 수 있다.")
    @Transactional
    void testUnlike() throws Exception {
        // Given
        likeRepository.save(Like.builder()
                .user(user1)
                .post(post1)
                .build());

        final List<Like> likes = likeRepository.findAll();
        assertAll("beforeUnlikePost",
                () -> assertThat(likes.size()).isEqualTo(1),
                () -> assertThat(likes.get(0).getUser()).isEqualTo(user1),
                () -> assertThat(likes.get(0).getPost()).isEqualTo(post1)
        );

        // When
        mockMvc.perform(delete("/api/v1/posts/{postId}/unlike/{likeId}", postId1, likes.get(0).getLikeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userToken1))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Then
        assertThat(likeRepository.findAll().size()).isEqualTo(0);
    }

}