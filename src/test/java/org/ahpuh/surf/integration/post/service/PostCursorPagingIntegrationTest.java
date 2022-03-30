package org.ahpuh.surf.integration.post.service;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.repository.CategoryRepository;
import org.ahpuh.surf.common.cursor.CursorResult;
import org.ahpuh.surf.common.exception.post.PostNotFoundException;
import org.ahpuh.surf.integration.IntegrationTest;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.dto.response.AllPostResponseDto;
import org.ahpuh.surf.post.dto.response.ExploreResponseDto;
import org.ahpuh.surf.post.dto.response.RecentPostResponseDto;
import org.ahpuh.surf.post.service.PostService;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockFollowFactory.createMockFollow;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPostWithSelectedDate;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PostCursorPagingIntegrationTest extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("전체 최신 게시글 둘러보기 테스트")
    @Nested
    class RecentAllPostsTest {

        @DisplayName("cursorId가 0일 때")
        @Nested
        class CursorIdIsZero {

            @DisplayName("게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getRecentPosts_HasNextTrue() {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, 11);

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(user.getUserId(), 0L);

                // Then
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getRecentPosts_HasNextFalse(final int postCount) {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, postCount);

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(1L, 0L);

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("게시글이 없으면 빈 리스트를 반환하고 hasNext를 false로 표시한다.")
            @Test
            void noPostReturnEmptyList_HasNextFalse() {
                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(1L, 0L);

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(0)
                );
            }
        }

        @DisplayName("cursorId가 이전에 조회한 게시글들 중 마지막 게시글의 postId일 때")
        @Nested
        class CursorIdIsNotZero {

            @DisplayName("다음 페이지의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getRecentPostsByCursor_HasNextTrue() {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, 21);

                final Post cursor = postRepository.findAll().get(11);

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(user.getUserId(), cursor.getPostId());

                // Then
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("다음 페이지의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getRecentPostsByCursor_HasNextFalse(final int postCount) {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, postCount + 10);
                entityManager.clear();

                final Post cursor = postRepository.findAll().get(postCount);

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(user.getUserId(), cursor.getPostId());

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("커서에 해당하는 게시글이 없으면 예외를 발생시킨다 - 404 응답")
            @Test
            void postNotFoundException_404() {
                // Given
                final User user = saveUser();

                // When Then
                assertThatThrownBy(() -> postService.recentAllPosts(user.getUserId(), 1L))
                        .isInstanceOf(PostNotFoundException.class)
                        .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                        .hasMessage("해당 게시글을 찾을 수 없습니다.");
            }
        }
    }

    @DisplayName("내가 팔로잉한 유저들의 전체 게시글 둘러보기 테스트")
    @Nested
    class FollowExploreTest {

        User follower;

        @BeforeEach
        void setUp() {
            entityManager.persist(createMockUser("following@naver.com"));
            follower = userRepository.findAll()
                    .stream()
                    .filter(user -> user.getEmail().equals("following@naver.com"))
                    .findFirst()
                    .orElseThrow();
        }

        @DisplayName("cursorId가 0일 때")
        @Nested
        class CursorIdIsZero {

            @DisplayName("팔로우한 유저들의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getFollowExplorePosts_HasNextTrue() {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, 11);
                entityManager.persist(createMockFollow(follower, user));

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(follower.getUserId(), 0L);

                // Then
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("팔로우한 유저들의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getFollowExplorePosts_HasNextFalse(final int postCount) {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, postCount);
                entityManager.persist(createMockFollow(follower, user));

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(follower.getUserId(), 0L);

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("팔로우한 유저들의 게시글이 없으면 빈 리스트를 반환하고 hasNext를 false로 표시한다.")
            @Test
            void noPostReturnEmptyList_HasNextFalse() {
                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(1L, 0L);

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(0)
                );
            }
        }

        @DisplayName("cursorId가 이전에 조회한 게시글들 중 마지막 게시글의 postId일 때")
        @Nested
        class CursorIdIsNotZero {

            @DisplayName("다음 페이지의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getFollowExplorePostsByCursor_HasNextTrue() {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, 21);
                entityManager.persist(createMockFollow(follower, user));

                final Post cursor = postRepository.findAll().get(11);

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(follower.getUserId(), cursor.getPostId());

                // Then
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("다음 페이지의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getFollowExplorePostsByCursor_HasNextFalse(final int postCount) {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, postCount + 10);
                entityManager.persist(createMockFollow(follower, user));
                entityManager.clear();

                final Post cursor = postRepository.findAll().get(postCount);

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(follower.getUserId(), cursor.getPostId());

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("커서에 해당하는 게시글이 없으면 예외를 발생시킨다 - 404 응답")
            @Test
            void postNotFoundException_404() {
                // Given
                final User user = saveUser();

                // When Then
                assertThatThrownBy(() -> postService.followExplore(user.getUserId(), 1L))
                        .isInstanceOf(PostNotFoundException.class)
                        .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                        .hasMessage("해당 게시글을 찾을 수 없습니다.");
            }
        }
    }

    @DisplayName("해당 유저의 모든 게시글 정보 조회 테스트")
    @Nested
    class GetAllPostByUserTest {

        @DisplayName("cursorId가 0일 때")
        @Nested
        class CursorIdIsZero {

            @DisplayName("해당 유저의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getAllPostOfUser_HasNextTrue() {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, 11);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(user.getUserId(), user.getUserId(), 0L);

                // Then
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("해당 유저의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getAllPostOfUser_HasNextFalse(final int postCount) {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, postCount);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(user.getUserId(), user.getUserId(), 0L);

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("해당 유저 또는 게시글이 없으면 빈 리스트를 반환하고 hasNext를 false로 표시한다.")
            @Test
            void noPostReturnEmptyList_HasNextFalse() {
                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(1L, 1L, 0L);

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(0)
                );
            }
        }

        @DisplayName("cursorId가 이전에 조회한 게시글들 중 마지막 게시글의 postId일 때")
        @Nested
        class CursorIdIsNotZero {

            @DisplayName("다음 페이지의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getAllPostOfUserByCursor_HasNextTrue() {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, 21);

                final Post cursor = postRepository.findAll().get(11);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(user.getUserId(), user.getUserId(), cursor.getPostId());

                // Then
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("다음 페이지의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getAllPostOfUserByCursor_HasNextFalse(final int postCount) {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, postCount + 10);
                entityManager.clear();

                final Post cursor = postRepository.findAll().get(postCount);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(user.getUserId(), user.getUserId(), cursor.getPostId());

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("커서에 해당하는 게시글이 없으면 예외를 발생시킨다.")
            @Test
            void postNotFoundException() {
                // When Then
                assertThatThrownBy(() -> postService.getAllPostByUser(1L, 2L, 30L))
                        .isInstanceOf(PostNotFoundException.class)
                        .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                        .hasMessage("해당 게시글을 찾을 수 없습니다.");
            }
        }
    }

    @DisplayName("해당 카테고리의 모든 게시글 정보 조회 테스트")
    @Nested
    class GetAllPostByCategoryTest {

        @DisplayName("cursorId가 0일 때")
        @Nested
        class CursorIdIsZero {

            @DisplayName("해당 카테고리의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getAllPostOfUser_HasNextTrue() {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, 11);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(user.getUserId(), category.getCategoryId(), 0L);

                // Then
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("해당 카테고리의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getAllPostOfCategory_HasNextFalse(final int postCount) {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, postCount);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(user.getUserId(), category.getCategoryId(), 0L);

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("해당 카테고리 또는 게시글이 없으면 빈 리스트를 반환하고 hasNext를 false로 표시한다.")
            @Test
            void getAllPostOfCategory_EmptyList_HasNextFalse() {
                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(1L, 1L, 0L);

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(0)
                );
            }
        }

        @DisplayName("cursorId가 이전에 조회한 게시글들 중 마지막 게시글의 postId일 때")
        @Nested
        class CursorIdIsNotZero {

            @DisplayName("다음 페이지의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getAllPostOfCategoryByCursor_HasNextTrue() {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, 21);

                final Post cursor = postRepository.findAll().get(11);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(user.getUserId(), category.getCategoryId(), cursor.getPostId());

                // Then
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("다음 페이지의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getAllPostOfCategoryByCursor_HasNextFalse(final int postCount) {
                // Given
                final User user = saveUser();
                final Category category = saveCategory(user);
                savePostsWithCount(user, category, postCount + 10);
                entityManager.clear();

                final Post cursor = postRepository.findAll().get(postCount);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(user.getUserId(), category.getCategoryId(), cursor.getPostId());

                // Then
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("커서에 해당하는 게시글이 없으면 예외를 발생시킨다.")
            @Test
            void postNotFoundException() {
                // When Then
                assertThatThrownBy(() -> postService.getAllPostByCategory(1L, 1L, 30L))
                        .isInstanceOf(PostNotFoundException.class)
                        .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                        .hasMessage("해당 게시글을 찾을 수 없습니다.");
            }
        }
    }

    private void savePostsWithCount(final User user, final Category category, final int count) {
        for (int i = 0; i < count; i++) {
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, i + 1)));
        }
    }

    private Category saveCategory(final User user) {
        entityManager.persist(createMockCategory(user));
        return categoryRepository.findAll().get(0);
    }

    private User saveUser() {
        entityManager.persist(createMockUser());
        return userRepository.findAll().get(0);
    }
}
