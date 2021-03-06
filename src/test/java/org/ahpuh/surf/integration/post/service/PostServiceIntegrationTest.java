package org.ahpuh.surf.integration.post.service;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.repository.CategoryRepository;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.exception.category.NoCategoryFromUserException;
import org.ahpuh.surf.common.exception.post.CancelFavoriteFailException;
import org.ahpuh.surf.common.exception.post.MakeFavoriteFailException;
import org.ahpuh.surf.common.exception.post.NotMatchingPostByUserException;
import org.ahpuh.surf.common.exception.post.PostNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.integration.IntegrationTest;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.PostCountResponseDto;
import org.ahpuh.surf.post.dto.response.PostReadResponseDto;
import org.ahpuh.surf.post.dto.response.PostsOfMonthResponseDto;
import org.ahpuh.surf.post.dto.response.PostsRecentScoreResponseDto;
import org.ahpuh.surf.post.service.PostService;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockFileFactory.createEmptyFile;
import static org.ahpuh.surf.common.factory.MockFileFactory.createMultipartFileText;
import static org.ahpuh.surf.common.factory.MockPostFactory.*;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PostServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("????????? ?????? ?????????")
    @Nested
    class CreatePostTest {

        @DisplayName("????????? ???????????? ???????????? ????????? ??? ??????.")
        @Test
        void createPostWithFileSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);

            final PostRequestDto request = createMockPostRequestDto(category.getCategoryId());
            final MockMultipartFile testFile = createMultipartFileText();

            // When
            postService.create(user.getUserId(), request, testFile);

            // Then
            final List<Post> allPosts = postRepository.findAll();
            assertAll(
                    () -> assertThat(allPosts.size()).isEqualTo(1),
                    () -> assertThat(allPosts.get(0).getFileUrl()).isEqualTo("mock upload")
            );
        }

        @DisplayName("????????? ???????????? ?????? ???????????? ????????? ??? ??????.")
        @Test
        void createPostWithNoFileSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);

            final PostRequestDto request = createMockPostRequestDto(category.getCategoryId());
            final MockMultipartFile emptyFile = createEmptyFile();

            // When
            postService.create(user.getUserId(), request, emptyFile);

            // Then
            final List<Post> allPosts = postRepository.findAll();
            assertAll(
                    () -> assertThat(allPosts.size()).isEqualTo(1),
                    () -> assertThat(allPosts.get(0).getFileUrl()).isNull()
            );
        }

        @DisplayName("???????????? ?????? ?????? ???????????? ???????????? ????????? ???????????? - 404 ??????")
        @Test
        void userNotFoundException_404() {
            // When Then
            assertThatThrownBy(() -> postService.create(1L, null, null))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("?????? ????????? ?????? ??? ????????????.");
        }

        @DisplayName("?????? ????????? request??? ??????????????? ????????? ????????? ???????????? - 400 ??????")
        @Test
        void noCategoryFromUserException_400() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);

            final PostRequestDto request = createMockPostRequestDto(category.getCategoryId() + 1);
            final MockMultipartFile emptyFile = createEmptyFile();

            // When Then
            assertThatThrownBy(() -> postService.create(user.getUserId(), request, emptyFile))
                    .isInstanceOf(NoCategoryFromUserException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("?????? ????????? ??????????????? ????????????.");
        }
    }

    @DisplayName("????????? ?????? ?????????")
    @Nested
    class UpdatePostTest {

        @DisplayName("????????? ???????????? ????????? ????????? ????????? ??? ??????.")
        @Test
        void updatePostWithFileSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            entityManager.persist(createMockPost(user, category));

            final List<Post> allPosts = postRepository.findAll();
            assertAll("????????? ?????????",
                    () -> assertThat(allPosts.size()).isEqualTo(1),
                    () -> assertThat(allPosts.get(0).getContent()).isEqualTo("postContent"),
                    () -> assertThat(allPosts.get(0).getFileUrl()).isNull()
            );

            final PostRequestDto request = createMockPostUpdateRequestDto(category.getCategoryId());
            final MockMultipartFile textFile = createMultipartFileText();

            // When
            postService.update(allPosts.get(0).getPostId(), request, textFile);

            // Then
            entityManager.flush();
            entityManager.clear();
            final List<Post> updatedPosts = postRepository.findAll();
            assertAll("????????? ?????????",
                    () -> assertThat(updatedPosts.size()).isEqualTo(1),
                    () -> assertThat(updatedPosts.get(0).getContent()).isEqualTo("update"),
                    () -> assertThat(updatedPosts.get(0).getFileUrl()).isEqualTo("mock upload")
            );
        }

        @DisplayName("????????? ???????????? ?????? ????????? ????????? ????????? ??? ??????.")
        @Test
        void updatePostWithNoFileSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            entityManager.persist(createMockPost(user, category));

            final List<Post> allPosts = postRepository.findAll();
            assertAll("????????? ?????????",
                    () -> assertThat(allPosts.size()).isEqualTo(1),
                    () -> assertThat(allPosts.get(0).getContent()).isEqualTo("postContent")
            );

            final PostRequestDto request = createMockPostUpdateRequestDto(category.getCategoryId());
            final MockMultipartFile emptyFile = createEmptyFile();

            // When
            postService.update(allPosts.get(0).getPostId(), request, emptyFile);

            // Then
            entityManager.flush();
            entityManager.clear();
            final List<Post> updatedPosts = postRepository.findAll();
            assertAll("????????? ?????????",
                    () -> assertThat(updatedPosts.size()).isEqualTo(1),
                    () -> assertThat(updatedPosts.get(0).getContent()).isEqualTo("update"),
                    () -> assertThat(updatedPosts.get(0).getFileUrl()).isNull()
            );
        }

        @DisplayName("???????????? ?????? ????????? ???????????? ???????????? ????????? ???????????? - 404 ??????")
        @Test
        void postNotFoundException_404() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);

            final PostRequestDto request = createMockPostRequestDto(category.getCategoryId());

            // When Then
            assertThatThrownBy(() -> postService.update(1L, request, null))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("?????? ???????????? ?????? ??? ????????????.");
        }
    }

    @DisplayName("?????? ????????? ?????? ?????? ?????????")
    @Nested
    class ReadPostTest {

        @DisplayName("???????????? ????????? ????????? ??? ??????.")
        @Test
        void readPostSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            final Post post = savePost(user, category);

            // When
            final PostReadResponseDto response = postService.readPost(user.getUserId(), post.getPostId());

            // Then
            assertThat(response.getPostId()).isEqualTo(post.getPostId());
        }
    }

    @DisplayName("????????? ?????? ?????????")
    @Nested
    class DeletePostTest {

        @DisplayName("??? ???????????? ????????? ??? ??????.")
        @Test
        void deletePostSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            final Post post = savePost(user, category);

            // When
            postService.delete(user.getUserId(), post.getPostId());

            // Then
            assertThat(postRepository.findAll().size()).isEqualTo(0);
        }

        @DisplayName("?????? ???????????? ???????????? ?????? ????????? ???????????? ??????????????? ?????? ????????? ???????????? - 400 ??????")
        @Test
        void notMatchingPostByUserException() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            final Post post = savePost(user, category);

            // When Then
            assertThatThrownBy(() -> postService.delete(user.getUserId() + 1, post.getPostId()))
                    .isInstanceOf(NotMatchingPostByUserException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("?????? ???????????? ???????????? ????????? ??? ????????????.");
        }
    }

    @DisplayName("??? ????????? ???????????? ?????? ?????????")
    @Nested
    class MakeFavoriteTest {

        @DisplayName("???????????? ??????????????? ????????? ??? ??????.")
        @Test
        void makeFavoriteSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            final Post post = savePost(user, category);

            // When
            postService.makeFavorite(user.getUserId(), post.getPostId());

            // Then
            assertThat(postRepository.findAll().get(0).getFavorite()).isTrue();
        }

        @DisplayName("?????? ??????????????? ????????? ???????????? ?????? ????????? ???????????? - 400 ??????")
        @Test
        void makeFavoriteFailException_400() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            final Post post = savePost(user, category);

            post.updateFavorite(user.getUserId());

            // When Then
            assertThatThrownBy(() -> postService.makeFavorite(user.getUserId(), post.getPostId()))
                    .isInstanceOf(MakeFavoriteFailException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("?????? ??????????????? ????????? ??????????????????.");
        }
    }

    @DisplayName("??? ????????? ???????????? ?????? ?????????")
    @Nested
    class CancelFavoriteTest {

        @DisplayName("??? ???????????? ?????????????????? ????????? ??? ??????.")
        @Test
        void cancelFavoriteSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            final Post post = savePost(user, category);

            post.updateFavorite(user.getUserId());

            // When
            postService.cancelFavorite(user.getUserId(), post.getPostId());

            // Then
            assertThat(postRepository.findAll().get(0).getFavorite()).isFalse();
        }

        @DisplayName("??????????????? ?????? ???????????? ?????? ?????? ????????? ???????????? - 400 ??????")
        @Test
        void favoriteInvalidRequestException() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            final Post post = savePost(user, category);

            // When Then
            assertThatThrownBy(() -> postService.cancelFavorite(user.getUserId(), post.getPostId()))
                    .isInstanceOf(CancelFavoriteFailException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("?????? ???????????? ????????? ??????????????????.");
        }
    }

    @DisplayName("??? ?????? ????????? ?????? ?????? ?????????")
    @Nested
    class GetPostsOfMonthTest {

        @DisplayName("?????? ????????? ???????????? ?????? ???????????? ????????? ??? ??????.")
        @Test
        void getPostsOfMonthSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 1)));
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 2)));
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 3)));

            // When
            final List<PostsOfMonthResponseDto> response = postService.getPostsOfMonth(user.getUserId(), 2022, 1);

            // Then
            assertAll(
                    () -> assertThat(response.size()).isEqualTo(3),
                    () -> assertThat(response.get(0).getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 3)),
                    () -> assertThat(response.get(1).getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 2)),
                    () -> assertThat(response.get(2).getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 1))
            );
        }

        @DisplayName("?????? ???????????? ????????? ??? ???????????? ????????????.")
        @Test
        void noPostReturnEmptyList() {
            // Given
            final User user = saveUser();

            // When
            final List<PostsOfMonthResponseDto> response = postService.getPostsOfMonth(user.getUserId(), 2022, 1);

            // Then
            assertThat(response.size()).isEqualTo(0);
        }
    }

    @DisplayName("?????? ??????????????? ?????? ????????? ?????? ?????? ?????????")
    @Nested
    class GetRecentScoreTest {

        @DisplayName("?????? ??????????????? ?????? ?????? ???????????? ????????? ????????? ??? ??????.")
        @Test
        void getRecentScoreSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            entityManager.persist(createMockPostWithScore(user, category, 85));

            // When
            final PostsRecentScoreResponseDto response = postService.getRecentScore(category.getCategoryId());

            // Then
            assertThat(response.getRecentScore()).isEqualTo(85);
        }

        @DisplayName("?????? ??????????????? ???????????? ?????? ?????? null??? ?????? dto??? ????????????.")
        @Test
        void noPostOfCategoryReturnNullDto() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);

            // When
            final PostsRecentScoreResponseDto response = postService.getRecentScore(category.getCategoryId());

            // Then
            assertThat(response.getRecentScore()).isNull();
        }
    }

    @DisplayName("?????? ????????? ????????? ????????? ?????? ?????? ?????????")
    @Nested
    class GetPostCountsOfYearTest {

        @DisplayName("?????? ????????? ??? ????????? ????????? ???????????? ????????? ????????? ??? ??????.")
        @Test
        void getPostCountsOfYearSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 1)));
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 1)));
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 2, 1)));

            // When
            final List<PostCountResponseDto> response = postService.getPostCountsOfYear(2022, user.getUserId());

            // Then
            assertAll(
                    () -> assertThat(response.size()).isEqualTo(2),
                    () -> assertThat(response.get(0).getDate()).isEqualTo(LocalDate.of(2022, 1, 1)),
                    () -> assertThat(response.get(0).getCount()).isEqualTo(2),
                    () -> assertThat(response.get(1).getDate()).isEqualTo(LocalDate.of(2022, 2, 1)),
                    () -> assertThat(response.get(1).getCount()).isEqualTo(1)
            );
        }

        @DisplayName("?????? ????????? ????????? ???????????? ?????? ?????? ??? ???????????? ????????????.")
        @Test
        void noPostReturnEmptyList() {
            // Given
            final User user = saveUser();

            // When
            final List<PostCountResponseDto> response = postService.getPostCountsOfYear(2022, user.getUserId());

            // Then
            assertThat(response.size()).isEqualTo(0);
        }
    }

    @DisplayName("?????? ????????? ??????????????? ????????? ?????? ?????? ?????????")
    @Nested
    class GetScoresOfCategoryByUserTest {

        @DisplayName("?????? ????????? ??????????????? 1?????? ????????? ????????? ????????? ??? ??????.")
        @Test
        void getScoresOfCategoryByUserSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 1)));
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 2)));
            entityManager.persist(
                    createMockPostWithSelectedDate(user, category, LocalDate.of(2022, 1, 3)));

            // When
            final List<CategorySimpleDto> response = postService.getScoresOfCategoryByUser(user.getUserId());

            // Then
            assertAll(
                    () -> assertThat(response.size()).isEqualTo(1),
                    () -> assertThat(response.get(0).getCategoryId()).isEqualTo(category.getCategoryId()),
                    () -> assertThat(response.get(0).getPostScores().size()).isEqualTo(3),
                    () -> assertThat(response.get(0).getPostScores().get(0).getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 1)),
                    () -> assertThat(response.get(0).getPostScores().get(1).getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 2)),
                    () -> assertThat(response.get(0).getPostScores().get(2).getSelectedDate()).isEqualTo(LocalDate.of(2022, 1, 3)));
        }

        @DisplayName("?????? ????????? ???????????? ????????? ??? ???????????? ????????????.")
        @Test
        void noPostReturnEmptyList() {
            // Given
            final User user = saveUser();

            // When
            final List<CategorySimpleDto> response = postService.getScoresOfCategoryByUser(user.getUserId());

            // Then
            assertThat(response.size()).isEqualTo(0);
        }
    }

    private Post savePost(final User user, final Category category) {
        entityManager.persist(createMockPost(user, category));
        return postRepository.findAll().get(0);
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
