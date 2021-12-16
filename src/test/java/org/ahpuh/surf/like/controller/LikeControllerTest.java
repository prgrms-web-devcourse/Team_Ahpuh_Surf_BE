package org.ahpuh.surf.like.controller;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.like.entity.Like;
import org.ahpuh.surf.like.repository.LikeRepository;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.controller.UserController;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.UserLoginRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class LikeControllerTest {

    Long userId1;
    Long userId2;
    String userToken1;
    Long postId1;
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

    @BeforeEach
    void setUp() {
        // user1, user2 회원가입 후 userId 반환
        userId1 = userController.join(UserJoinRequestDto.builder()
                        .email("test1@naver.com")
                        .userName("name")
                        .password("test1")
                        .build())
                .getBody();
        userId2 = userController.join(UserJoinRequestDto.builder()
                        .email("test2@naver.com")
                        .userName("name")
                        .password("test2")
                        .build())
                .getBody();

        // user1 로그인 후 토큰 발급
        userToken1 = userController.login(UserLoginRequestDto.builder()
                        .email("test1@naver.com")
                        .password("test1")
                        .build())
                .getBody()
                .getToken();

        final User user2 = userRepository.getById(userId2);

        // user2가 카테고리 생성
        final Category category1 = categoryRepository.save(Category.builder()
                .user(user2)
                .name("category 1")
                .build());

        // user2가 post 생성
        postId1 = postRepository.save(Post.builder()
                        .user(user2)
                        .category(category1)
                        .selectedDate(LocalDate.now())
                        .content("content")
                        .score(80)
                        .build())
                .getPostId();
    }

    @Test
    @DisplayName("게시글 좋아요를 할 수 있다.")
    @Transactional
    void testLike() throws Exception {
        // Given
        assertThat(likeRepository.findAll().size(), is(0));

        // When
        mockMvc.perform(post("/api/v1/posts/{postId}/like", postId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", userToken1))
                .andExpect(status().isOk())
                .andDo(print());

        // Then
        final List<Like> likes = likeRepository.findAll();
        assertAll("afterLikePost",
                () -> assertThat(likes.size(), is(1)),
                () -> assertThat(likes.get(0).getUserId(), is(userId1)),
                () -> assertThat(likes.get(0).getPost().getPostId(), is(postId1))
        );

    }

    @Test
    @DisplayName("게시글 좋아요 취소를 할 수 있다.")
    @Transactional
    void testUnlike() throws Exception {
        // Given
        assertThat(likeRepository.findAll().size(), is(0));

        mockMvc.perform(post("/api/v1/posts/{postId}/like", postId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", userToken1))
                .andExpect(status().isOk())
                .andDo(print());

        final List<Like> likes = likeRepository.findAll();
        assertAll("beforeUnlikePost",
                () -> assertThat(likes.size(), is(1)),
                () -> assertThat(likes.get(0).getUserId(), is(userId1)),
                () -> assertThat(likes.get(0).getPost().getPostId(), is(postId1))
        );

        // When
        mockMvc.perform(delete("/api/v1/posts/{postId}/unlike/{likeId}", postId1, likes.get(0).getLikeId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", userToken1))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Then
        assertThat(likeRepository.findAll().size(), is(0));
    }

}