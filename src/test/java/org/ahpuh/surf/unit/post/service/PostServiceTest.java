package org.ahpuh.surf.unit.post.service;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryRepository;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.exception.category.CategoryNotFoundException;
import org.ahpuh.surf.common.exception.category.NoCategoryFromUserException;
import org.ahpuh.surf.common.exception.post.*;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.PostConverter;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.PostCountResponseDto;
import org.ahpuh.surf.post.dto.response.PostReadResponseDto;
import org.ahpuh.surf.post.dto.response.PostsOfMonthResponseDto;
import org.ahpuh.surf.post.dto.response.PostsRecentScoreResponseDto;
import org.ahpuh.surf.post.service.PostService;
import org.ahpuh.surf.s3.domain.FileStatus;
import org.ahpuh.surf.s3.domain.FileType;
import org.ahpuh.surf.s3.service.S3Service;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockFileFactory.createEmptyImageFile;
import static org.ahpuh.surf.common.factory.MockFileFactory.createMultipartFileImage1;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPostCountDto;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPostRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private PostConverter postConverter;

    @InjectMocks
    private PostService postService;

    @DisplayName("create 메소드는")
    @Nested
    class CreateMethod {

        @DisplayName("게시글을 생성할 수 있다. 파일 첨부 O")
        @Test
        void createPostWithFile_Success() throws IOException {
            // Given
            final User user = mock(User.class);
            final Category category = mock(Category.class);
            final Post post = mock(Post.class);
            final PostRequestDto request = createMockPostRequestDto();
            final MockMultipartFile imageFile = createMultipartFileImage1();
            final Optional<FileStatus> fileStatus = Optional.of(new FileStatus("url", FileType.FILE));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));
            given(user.getCategories())
                    .willReturn(List.of(category));
            given(category.getCategoryId())
                    .willReturn(1L);
            given(postConverter.toEntity(user, category, request))
                    .willReturn(post);
            given(s3Service.uploadPostFile(imageFile))
                    .willReturn(fileStatus);
            given(postRepository.save(post))
                    .willReturn(post);
            given(post.getPostId())
                    .willReturn(1L);

            // When
            final Long postId = postService.create(1L, request, imageFile);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postConverter, times(1))
                    .toEntity(any(User.class), any(Category.class), any(PostRequestDto.class));
            verify(s3Service, times(1))
                    .uploadPostFile(any(MultipartFile.class));
            verify(post, times(1))
                    .updateFile(any(FileStatus.class));
            verify(postRepository, times(1))
                    .save(any(Post.class));
            assertThat(postId).isEqualTo(1L);
        }

        @DisplayName("게시글을 생성할 수 있다. 파일 첨부 X")
        @Test
        void createPostWithNoFile_Success() throws IOException {
            // Given
            final User user = mock(User.class);
            final Category category = mock(Category.class);
            final Post post = mock(Post.class);
            final PostRequestDto request = createMockPostRequestDto();
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));
            given(user.getCategories())
                    .willReturn(List.of(category));
            given(category.getCategoryId())
                    .willReturn(1L);
            given(postConverter.toEntity(user, category, request))
                    .willReturn(post);
            given(postRepository.save(post))
                    .willReturn(post);
            given(post.getPostId())
                    .willReturn(1L);

            // When
            final Long postId = postService.create(1L, request, createEmptyImageFile());

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postConverter, times(1))
                    .toEntity(any(User.class), any(Category.class), any(PostRequestDto.class));
            verify(s3Service, times(0))
                    .uploadPostFile(any());
            verify(post, times(0))
                    .updateFile(any());
            verify(postRepository, times(1))
                    .save(any(Post.class));
            assertThat(postId).isEqualTo(1L);
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다.")
        @Test
        void userNotFoundException() throws IOException {
            // Given
            given(userRepository.findById(any()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.create(1L, mock(PostRequestDto.class), null))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(categoryRepository, times(0))
                    .findById(any());
            verify(postConverter, times(0))
                    .toEntity(any(), any(), any());
            verify(s3Service, times(0))
                    .uploadPostFile(any());
            verify(postRepository, times(0))
                    .save(any());
        }

        @DisplayName("해당 유저와 request의 카테고리가 다르면 예외가 발생한다.")
        @Test
        void noCategoryFromUserException() throws IOException {
            // Given
            final User user = mock(User.class);
            final Category category = mock(Category.class);
            final PostRequestDto request = mock(PostRequestDto.class);
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));
            given(user.getCategories())
                    .willReturn(List.of(category));
            given(category.getCategoryId())
                    .willReturn(2L);
            given(request.getCategoryId())
                    .willReturn(1L);

            // When
            assertThatThrownBy(() -> postService.create(1L, request, null))
                    .isInstanceOf(NoCategoryFromUserException.class)
                    .hasMessage("해당 유저의 카테고리가 아닙니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postConverter, times(0))
                    .toEntity(any(), any(), any());
            verify(s3Service, times(0))
                    .uploadPostFile(any());
            verify(postRepository, times(0))
                    .save(any());
        }
    }

    @DisplayName("update 메소드는")
    @Nested
    class UpdateMethod {

        @DisplayName("게시글 정보를 수정할 수 있다. 파일 첨부 O")
        @Test
        void updatePostWithFile_Success() throws IOException {
            // Given
            final Category category = mock(Category.class);
            final Post post = mock(Post.class);
            final PostRequestDto request = createMockPostRequestDto();
            final MockMultipartFile imageFile = createMultipartFileImage1();
            final Optional<FileStatus> fileStatus = Optional.of(new FileStatus("url", FileType.FILE));
            given(categoryRepository.findById(anyLong()))
                    .willReturn(Optional.of(category));
            given(postRepository.findById(any()))
                    .willReturn(Optional.of(post));
            given(s3Service.uploadPostFile(imageFile))
                    .willReturn(fileStatus);

            // When
            postService.update(1L, request, imageFile);

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(post, times(1))
                    .updatePost(any(Category.class), any(), anyString(), anyInt());
            verify(s3Service, times(1))
                    .uploadPostFile(any(MultipartFile.class));
            verify(post, times(1))
                    .updateFile(any(FileStatus.class));
        }

        @DisplayName("게시글 정보를 수정할 수 있다. 파일 첨부 X")
        @Test
        void updatePostWithNoFile_Success() throws IOException {
            // Given
            final Category category = mock(Category.class);
            final Post post = mock(Post.class);
            final PostRequestDto request = createMockPostRequestDto();
            given(categoryRepository.findById(any()))
                    .willReturn(Optional.of(category));
            given(postRepository.findById(any()))
                    .willReturn(Optional.of(post));

            // When
            postService.update(1L, request, createEmptyImageFile());

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(post, times(1))
                    .updatePost(any(Category.class), any(), anyString(), anyInt());
            verify(s3Service, times(0))
                    .uploadPostFile(any());
            verify(post, times(0))
                    .updateFile(any());
        }

        @DisplayName("존재하지 않는 카테고리 아이디가 입력되면 예외가 발생한다.")
        @Test
        void categoryNotFoundException() throws IOException {
            // Given
            given(categoryRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.update(1L, mock(PostRequestDto.class), null))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage("해당 카테고리를 찾을 수 없습니다.");

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(0))
                    .findById(any());
            verify(s3Service, times(0))
                    .uploadPostFile(any());
        }

        @DisplayName("존재하지 않는 게시글 아이디가 입력되면 예외가 발생한다.")
        @Test
        void PostNotFoundException() throws IOException {
            // Given
            given(categoryRepository.findById(anyLong()))
                    .willReturn(Optional.of(mock(Category.class)));
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.update(1L, mock(PostRequestDto.class), null))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("해당 게시글을 찾을 수 없습니다.");

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(s3Service, times(0))
                    .uploadPostFile(any());
        }
    }

    @DisplayName("readPost 메소드는")
    @Nested
    class ReadPostMethod {

        @DisplayName("게시글의 정보를 반환할 수 있다.")
        @Test
        void findPost_Success() {
            // Given
            final PostReadResponseDto responseDto = mock(PostReadResponseDto.class);
            given(postRepository.findPost(anyLong(), anyLong()))
                    .willReturn(Optional.of(responseDto));
            given(responseDto.likeCheck())
                    .willReturn(responseDto);

            // When
            final PostReadResponseDto response = postService.readPost(1L, 1L);

            // Then
            verify(postRepository, times(1))
                    .findPost(anyLong(), anyLong());
            verify(responseDto, times(1))
                    .likeCheck();
            assertThat(response).isEqualTo(responseDto);
        }

        @DisplayName("존재하지 않는 게시글 아이디가 입력되면 예외가 발생한다.")
        @Test
        void PostNotFoundException() {
            // Given
            given(postRepository.findPost(anyLong(), anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.readPost(1L, 1L))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("해당 게시글을 찾을 수 없습니다.");

            // Then
            verify(postRepository, times(1))
                    .findPost(anyLong(), anyLong());
        }
    }

    @DisplayName("delete 메소드는")
    @Nested
    class DeleteMethod {

        @DisplayName("게시글을 삭제할 수 있다.")
        @Test
        void deletePost() {
            // Given
            final User user = mock(User.class);
            final Post post = mock(Post.class);
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post));
            given(post.getUser())
                    .willReturn(user);
            given(user.getUserId())
                    .willReturn(1L);

            // When
            postService.delete(1L, 1L);

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .delete(any(Post.class));
        }

        @DisplayName("존재하지 않는 게시글 아이디가 입력되면 예외가 발생한다.")
        @Test
        void PostNotFoundException() {
            // Given
            given(postRepository.findById(any()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.delete(1L, 1L))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("해당 게시글을 찾을 수 없습니다.");

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(0))
                    .delete(any());
        }

        @DisplayName("해당 게시글을 작성하지 않은 유저가 게시글을 삭제하려고 하면 예외가 발생한다.")
        @Test
        void notMatchingPostByUserException() {
            // Given
            final User user = mock(User.class);
            final Post post = mock(Post.class);
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post));
            given(post.getUser())
                    .willReturn(user);
            given(user.getUserId())
                    .willReturn(2L);

            // When
            assertThatThrownBy(() -> postService.delete(1L, 1L))
                    .isInstanceOf(NotMatchingPostByUserException.class)
                    .hasMessage("다른 사용자의 게시글을 삭제할 수 없습니다.");

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(0))
                    .delete(any());
        }
    }

    @DisplayName("makeFavorite 메소드는")
    @Nested
    class MakeFavoriteMethod {

        @DisplayName("게시글을 즐겨찾기에 등록할 수 있다.")
        @Test
        void makeFavorite_Success() {
            // Given
            final Post post = mock(Post.class);
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post));
            given(post.getFavorite())
                    .willReturn(false);

            // When
            postService.makeFavorite(1L, 1L);

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(post, times(1))
                    .getFavorite();
            verify(post, times(1))
                    .updateFavorite(anyLong());
        }

        @DisplayName("존재하지 않는 게시글 아이디가 입력되면 예외가 발생한다.")
        @Test
        void PostNotFoundException() {
            // Given
            given(postRepository.findById(any()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.makeFavorite(1L, 1L))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("해당 게시글을 찾을 수 없습니다.");

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
        }

        @DisplayName("이미 즐겨찾기에 등록이 되어있는 경우 예외가 발생한다.")
        @Test
        void makeFavoriteFailException() {
            // Given
            final Post post = mock(Post.class);
            given(postRepository.findById(any()))
                    .willReturn(Optional.of(post));
            given(post.getFavorite())
                    .willReturn(true);

            // When
            assertThatThrownBy(() -> postService.makeFavorite(1L, 1L))
                    .isInstanceOf(MakeFavoriteFailException.class)
                    .hasMessage("이미 즐겨찾기에 추가된 게시글입니다.");

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(post, times(1))
                    .getFavorite();
            verify(post, times(0))
                    .updateFavorite(any());
        }
    }

    @DisplayName("cancelFavorite 메소드는")
    @Nested
    class CancelFavoriteMethod {

        @DisplayName("게시글을 즐겨찾기에서 취소할 수 있다.")
        @Test
        void cancelFavorite_Success() {
            // Given
            final Post post = mock(Post.class);
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(post));
            given(post.getFavorite())
                    .willReturn(true);

            // When
            postService.cancelFavorite(1L, 1L);

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(post, times(1))
                    .getFavorite();
            verify(post, times(1))
                    .updateFavorite(anyLong());
        }

        @DisplayName("존재하지 않는 게시글 아이디가 입력되면 예외가 발생한다.")
        @Test
        void PostNotFoundException() {
            // Given
            given(postRepository.findById(any()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.cancelFavorite(1L, 1L))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("해당 게시글을 찾을 수 없습니다.");

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
        }

        @DisplayName("즐겨찾기에 등록 되어있지 않은 경우 예외가 발생한다.")
        @Test
        void FavoriteInvalidRequestException() {
            // Given
            final Post post = mock(Post.class);
            given(postRepository.findById(any()))
                    .willReturn(Optional.of(post));
            given(post.getFavorite())
                    .willReturn(false);

            // When
            assertThatThrownBy(() -> postService.cancelFavorite(1L, 1L))
                    .isInstanceOf(CancelFavoriteFailException.class)
                    .hasMessage("이미 즐겨찾기 취소된 게시글입니다.");

            // Then
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(post, times(1))
                    .getFavorite();
            verify(post, times(0))
                    .updateFavorite(any());
        }
    }

    @DisplayName("getPostsOfMonth 메소드는")
    @Nested
    class GetPostsOfMonthMethod {

        @DisplayName("해당 년월에 해당하는 모든 게시글을 조회할 수 있다.")
        @Test
        void getPostsOfMonth_Success() {
            // Given
            given(postRepository.findPostsOfMonth(anyLong(), any(), any()))
                    .willReturn(List.of(mock(PostsOfMonthResponseDto.class)));

            // When
            postService.getPostsOfMonth(1L, 2022, 1);

            // Then
            verify(postRepository, times(1))
                    .findPostsOfMonth(any(), any(), any());
        }

        @DisplayName("해당 게시글이 없다면 빈 리스트를 반환한다.")
        @Test
        void getPostsOfMonth_EmptyList() {
            // Given
            given(postRepository.findPostsOfMonth(anyLong(), any(), any()))
                    .willReturn(List.of());

            // When
            final List<PostsOfMonthResponseDto> response = postService.getPostsOfMonth(1L, 2022, 1);

            // Then
            verify(postRepository, times(1))
                    .findPostsOfMonth(any(), any(), any());
            assertThat(response).isEqualTo(List.of());
        }

        @DisplayName("year 또는 month에 null이 입력되면 예외가 발생한다.")
        @ParameterizedTest
        @CsvSource({
                ",1",
                "2022,",
                ","
        })
        void invalidPeriodException_Null(final Integer year, final Integer month) {
            // When
            assertThatThrownBy(() -> postService.getPostsOfMonth(1L, year, month))
                    .isInstanceOf(InvalidPeriodException.class)
                    .hasMessage("기간이 잘못 입력되었습니다.");

            // Then
            verify(postRepository, times(0))
                    .findPostsOfMonth(any(), any(), any());
        }

        @DisplayName("month가 1~12의 숫자가 아니면 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {0, 13})
        void invalidPeriodException(final Integer month) {
            // When
            assertThatThrownBy(() -> postService.getPostsOfMonth(1L, 2022, month))
                    .isInstanceOf(InvalidPeriodException.class)
                    .hasMessage("기간이 잘못 입력되었습니다.");

            // Then
            verify(postRepository, times(0))
                    .findPostsOfMonth(any(), any(), any());
        }
    }

    @DisplayName("getRecentScore 메소드는")
    @Nested
    class GetRecentScoreMethod {

        @DisplayName("해당 카테고리의 가장 최근 게시물의 점수를 반환한다.")
        @Test
        void getRecentScore_Success() {
            // Given
            final Category category = mock(Category.class);
            final Post post = mock(Post.class);
            given(categoryRepository.findById(anyLong()))
                    .willReturn(Optional.of(category));
            given(postRepository.findTop1ByCategoryOrderBySelectedDateDesc(category))
                    .willReturn(Optional.of(post));
            given(post.getScore())
                    .willReturn(100);

            // When
            final PostsRecentScoreResponseDto response = postService.getRecentScore(1L);

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findTop1ByCategoryOrderBySelectedDateDesc(any(Category.class));
            assertThat(response.getRecentScore()).isEqualTo(100);
        }

        @DisplayName("해당 카테고리의 게시글이 없는 경우 null을 담은 dto를 반환한다.")
        @Test
        void findTop1ByCategoryOrderBySelectedDateDesc_Empty() {
            // Given
            final Category category = mock(Category.class);
            given(categoryRepository.findById(anyLong()))
                    .willReturn(Optional.of(category));
            given(postRepository.findTop1ByCategoryOrderBySelectedDateDesc(category))
                    .willReturn(Optional.empty());

            // When
            final PostsRecentScoreResponseDto response = postService.getRecentScore(1L);

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findTop1ByCategoryOrderBySelectedDateDesc(any(Category.class));
            assertThat(response.getRecentScore()).isNull();
        }

        @DisplayName("존재하지 않는 카테고리 아이디가 입력되면 예외가 발생한다.")
        @Test
        void categoryNotFoundException() {
            // Given
            given(categoryRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.getRecentScore(1L))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage("해당 카테고리를 찾을 수 없습니다.");

            // Then
            verify(categoryRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(0))
                    .findTop1ByCategoryOrderBySelectedDateDesc(any(Category.class));
        }
    }

    @DisplayName("getPostCountsOfYear 메소드는")
    @Nested
    class GetPostCountsOfYearMethod {

        @DisplayName("해당 년도의 각 날마다 작성한 게시글의 개수를 반환할 수 있다.")
        @Test
        void getPostCountsOfYear_Success() {
            // Given
            final User user = mock(User.class);
            final PostCountResponseDto responseDto = createMockPostCountDto();
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));
            given(postRepository.findEachDateAndCountOfYearByUser(2022, user))
                    .willReturn(List.of(responseDto, responseDto));

            // When
            final List<PostCountResponseDto> responseDtos = postService.getPostCountsOfYear(2022, 1L);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findEachDateAndCountOfYearByUser(anyInt(), any(User.class));
            assertThat(responseDtos).isEqualTo(List.of(responseDto, responseDto));
        }

        @DisplayName("작성한 게시글이 없는 경우 빈 리스트를 반환한다.")
        @Test
        void getPostCountsOfYear_EmptyList() {
            // Given
            final User user = mock(User.class);
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user));
            given(postRepository.findEachDateAndCountOfYearByUser(2022, user))
                    .willReturn(List.of());

            // When
            final List<PostCountResponseDto> responseDtos = postService.getPostCountsOfYear(2022, 1L);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findEachDateAndCountOfYearByUser(anyInt(), any(User.class));
            assertThat(responseDtos).isEqualTo(List.of());
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다.")
        @Test
        void userNotFoundException() {
            // Given
            given(userRepository.findById(any()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> postService.getPostCountsOfYear(2022, 1L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
        }
    }

    @DisplayName("getScoresOfCategoryByUser 메소드는")
    @Nested
    class GetScoresOfCategoryByUserMethod {

        @DisplayName("해당 유저의 1년치 게시글 점수를 조회할 수 있다.")
        @Test
        void getScoresOfCategoryByUser_Success() {
            // Given
            final List<CategorySimpleDto> responseDto = List.of(mock(CategorySimpleDto.class));
            given(postRepository.findAllScoreWithCategoryByUser(anyLong()))
                    .willReturn(responseDto);

            // When
            final List<CategorySimpleDto> response = postService.getScoresOfCategoryByUser(1L);

            // Then
            verify(postRepository, times(1))
                    .findAllScoreWithCategoryByUser(anyLong());
            assertThat(response).isEqualTo(responseDto);
        }

        @DisplayName("해당 유저의 게시글이 없다면 빈 리스트를 반환한다.")
        @Test
        void noPostReturnEmptyList() {
            // Given
            given(postRepository.findAllScoreWithCategoryByUser(anyLong()))
                    .willReturn(List.of());

            // When
            final List<CategorySimpleDto> response = postService.getScoresOfCategoryByUser(1L);

            // Then
            verify(postRepository, times(1))
                    .findAllScoreWithCategoryByUser(anyLong());
            assertThat(response).isEqualTo(List.of());
        }
    }
}
