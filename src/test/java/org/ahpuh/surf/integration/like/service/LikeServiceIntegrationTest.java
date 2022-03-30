package org.ahpuh.surf.integration.like.service;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.like.DuplicatedLikeException;
import org.ahpuh.surf.common.exception.like.LikeNotFoundException;
import org.ahpuh.surf.common.exception.post.PostNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.integration.IntegrationTest;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.like.domain.LikeRepository;
import org.ahpuh.surf.like.service.LikeService;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockLikeFactory.createMockLike;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LikeServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @DisplayName("좋아요 테스트")
    @Nested
    class LikeTest {

        @DisplayName("유저가 해당 게시글을 좋아요 할 수 있다.")
        @Test
        void likeSuccess() {
            // Given
            saveTwoUser();
            final Category category1 = saveCategory(user1);
            final Post post1 = savePost(user1, category1);

            // When
            likeService.like(user2.getUserId(), post1.getPostId());

            // Then
            assertThat(likeRepository.findAll().size()).isEqualTo(1);
        }

        @DisplayName("이미 좋아요 한 기록이 있으면 예외가 발생한다 - 400 응답")
        @Test
        void duplicatedLikeException_400() {
            // Given
            saveTwoUser();
            final Category category1 = saveCategory(user1);
            final Post post1 = savePost(user1, category1);
            likeRepository.save(createMockLike(user2, post1));

            // When Then
            assertThatThrownBy(() -> likeService.like(user2.getUserId(), post1.getPostId()))
                    .isInstanceOf(DuplicatedLikeException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("이미 좋아요를 누른 게시글입니다.");
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다 - 404 응답")
        @Test
        void userNotFoundException_404() {
            // When Then
            assertThatThrownBy(() -> likeService.like(1L, 1L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");
        }

        @DisplayName("존재하지 않는 게시글 아이디가 입력되면 예외가 발생한다 - 404 응답")
        @Test
        void postNotFoundException_404() {
            // Given
            final User user = saveUser();

            // When Then
            assertThatThrownBy(() -> likeService.like(user.getUserId(), 1L))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("해당 게시글을 찾을 수 없습니다.");
        }
    }

    @DisplayName("좋아요 취소 테스트")
    @Nested
    class UnlikeTest {

        @DisplayName("유저가 좋아요한 게시글을 좋아요 취소할 수 있다.")
        @Test
        void unlikeSuccess() {
            // Given
            saveTwoUser();
            final Category category1 = saveCategory(user1);
            final Post post1 = savePost(user1, category1);
            final Like like = likeRepository.save(createMockLike(user2, post1));

            // When
            likeService.unlike(like.getLikeId());

            // Then
            assertThat(likeRepository.findAll().size()).isEqualTo(0);
        }

        @DisplayName("좋아요한 기록이 없으면 예외가 발생한다 - 404 응답")
        @Test
        void likeNotFoundException_404() {
            // Given
            saveTwoUser();
            final Category category1 = saveCategory(user1);
            savePost(user1, category1);

            // When Then
            assertThatThrownBy(() -> likeService.unlike(1L))
                    .isInstanceOf(LikeNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("좋아요 한 기록이 없습니다.");
        }
    }

    private void saveTwoUser() {
        entityManager.persist(createMockUser("user1@naver.com"));
        entityManager.persist(createMockUser("user2@naver.com"));
        final List<User> allUsers = userRepository.findAll();
        allUsers.forEach(user -> {
            if (user.getEmail().equals("user1@naver.com")) {
                user1 = user;
            }
            if (user.getEmail().equals("user2@naver.com")) {
                user2 = user;
            }
        });
    }

    private User saveUser() {
        entityManager.persist(createMockUser());
        return userRepository.findAll().get(0);
    }

    private Category saveCategory(final User user) {
        entityManager.persist(createMockCategory(user));
        return categoryRepository.findAll().get(0);
    }

    private Post savePost(final User user, final Category category) {
        entityManager.persist(createMockPost(user, category));
        return postRepository.findAll().get(0);
    }
}
