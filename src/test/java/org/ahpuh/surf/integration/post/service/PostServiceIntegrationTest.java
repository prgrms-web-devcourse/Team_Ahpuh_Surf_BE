package org.ahpuh.surf.integration.post.service;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
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

    @DisplayName("게시글 생성 테스트")
    @Nested
    class CreatePostTest {

        @DisplayName("파일을 첨부하여 게시글을 생성할 수 있다.")
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

        @DisplayName("파일을 첨부하지 않고 게시글을 생성할 수 있다.")
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

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다 - 404 응답")
        @Test
        void userNotFoundException_404() {
            // When Then
            assertThatThrownBy(() -> postService.create(1L, null, null))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");
        }

        @DisplayName("해당 유저와 request의 카테고리가 다르면 예외가 발생한다 - 400 응답")
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
                    .hasMessage("해당 유저의 카테고리가 아닙니다.");
        }
    }

    @DisplayName("게시글 수정 테스트")
    @Nested
    class UpdatePostTest {

        @DisplayName("파일을 첨부하여 게시글 정보를 수정할 수 있다.")
        @Test
        void updatePostWithFileSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            entityManager.persist(createMockPost(user, category));

            final List<Post> allPosts = postRepository.findAll();
            assertAll("게시글 수정전",
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
            assertAll("게시글 수정후",
                    () -> assertThat(updatedPosts.size()).isEqualTo(1),
                    () -> assertThat(updatedPosts.get(0).getContent()).isEqualTo("update"),
                    () -> assertThat(updatedPosts.get(0).getFileUrl()).isEqualTo("mock upload")
            );
        }

        @DisplayName("파일을 첨부하지 않고 게시글 정보를 수정할 수 있다.")
        @Test
        void updatePostWithNoFileSuccess() {
            // Given
            final User user = saveUser();
            final Category category = saveCategory(user);
            entityManager.persist(createMockPost(user, category));

            final List<Post> allPosts = postRepository.findAll();
            assertAll("게시글 수정전",
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
            assertAll("게시글 수정후",
                    () -> assertThat(updatedPosts.size()).isEqualTo(1),
                    () -> assertThat(updatedPosts.get(0).getContent()).isEqualTo("update"),
                    () -> assertThat(updatedPosts.get(0).getFileUrl()).isNull()
            );
        }

        @DisplayName("존재하지 않는 게시글 아이디가 입력되면 예외가 발생한다 - 404 응답")
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
                    .hasMessage("해당 게시글을 찾을 수 없습니다.");
        }
    }

    @DisplayName("해당 게시글 정보 조회 테스트")
    @Nested
    class ReadPostTest {

        @DisplayName("게시글의 정보를 반환할 수 있다.")
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

    @DisplayName("게시글 삭제 테스트")
    @Nested
    class DeletePostTest {

        @DisplayName("내 게시글을 삭제할 수 있다.")
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

        @DisplayName("해당 게시글을 작성하지 않은 유저가 게시글을 삭제하려고 하면 예외가 발생한다 - 400 응답")
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
                    .hasMessage("다른 사용자의 게시글을 삭제할 수 없습니다.");
        }
    }

    @DisplayName("내 게시글 즐겨찾기 등록 테스트")
    @Nested
    class MakeFavoriteTest {

        @DisplayName("게시글을 즐겨찾기에 등록할 수 있다.")
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

        @DisplayName("이미 즐겨찾기에 등록이 되어있는 경우 예외가 발생한다 - 400 응답")
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
                    .hasMessage("이미 즐겨찾기에 추가된 게시글입니다.");
        }
    }

    @DisplayName("내 게시글 즐겨찾기 취소 테스트")
    @Nested
    class CancelFavoriteTest {

        @DisplayName("내 게시글을 즐겨찾기에서 취소할 수 있다.")
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

        @DisplayName("즐겨찾기에 등록 되어있지 않은 경우 예외가 발생한다 - 400 응답")
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
                    .hasMessage("이미 즐겨찾기 취소된 게시글입니다.");
        }
    }

    @DisplayName("내 한달 게시글 정보 조회 테스트")
    @Nested
    class GetPostsOfMonthTest {

        @DisplayName("해당 년월에 해당하는 모든 게시글을 조회할 수 있다.")
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

        @DisplayName("해당 게시글이 없다면 빈 리스트를 반환한다.")
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

    @DisplayName("해당 카테고리의 최신 게시글 점수 조회 테스트")
    @Nested
    class GetRecentScoreTest {

        @DisplayName("해당 카테고리의 가장 최근 게시물의 점수를 반환할 수 있다.")
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

        @DisplayName("해당 카테고리의 게시글이 없는 경우 null을 담은 dto를 반환한다.")
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

    @DisplayName("해당 유저의 일년치 게시글 개수 조회 테스트")
    @Nested
    class GetPostCountsOfYearTest {

        @DisplayName("해당 년도의 각 날마다 작성한 게시글의 개수를 반환할 수 있다.")
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

        @DisplayName("해당 유저가 작성한 게시글이 없는 경우 빈 리스트를 반환한다.")
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

    @DisplayName("해당 유저의 카테고리별 게시글 점수 조회 테스트")
    @Nested
    class GetScoresOfCategoryByUserTest {

        @DisplayName("해당 유저의 카테고리별 1년치 게시글 점수를 조회할 수 있다.")
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

        @DisplayName("해당 유저의 게시글이 없다면 빈 리스트를 반환한다.")
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
