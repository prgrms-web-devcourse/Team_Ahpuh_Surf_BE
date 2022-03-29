package org.ahpuh.surf.unit.post.service;

import org.ahpuh.surf.common.cursor.CursorResult;
import org.ahpuh.surf.common.exception.post.PostNotFoundException;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.post.dto.response.AllPostResponseDto;
import org.ahpuh.surf.post.dto.response.ExploreResponseDto;
import org.ahpuh.surf.post.dto.response.RecentPostResponseDto;
import org.ahpuh.surf.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostCursorPagingTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @DisplayName("recentAllPosts 메소드는")
    @Nested
    class RecentAllPostsMethod {

        @DisplayName("cursorId가 0일 때")
        @Nested
        class CursorIdIsZero {

            @DisplayName("게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getRecentPosts_HasNextTrue() {
                // Given
                final List<RecentPostResponseDto> responseDtos = new ArrayList<>();
                final RecentPostResponseDto postDto = mock(RecentPostResponseDto.class);
                for (int i = 0; i < 11; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findAllRecentPost(anyLong(), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(1L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllRecentPost(anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllRecentPostByCursor(any(), any(), any(), any());
                verify(postDto, times(10))
                        .likeCheck();
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
                final List<RecentPostResponseDto> responseDtos = new ArrayList<>();
                final RecentPostResponseDto postDto = mock(RecentPostResponseDto.class);
                for (int i = 0; i < postCount; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findAllRecentPost(anyLong(), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(1L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllRecentPost(anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllRecentPostByCursor(any(), any(), any(), any());
                verify(postDto, times(postCount))
                        .likeCheck();
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("게시글이 없으면 빈 리스트를 반환하고 hasNext를 false로 표시한다.")
            @Test
            void getRecentPosts_EmptyList_HasNextFalse() {
                // Given
                given(postRepository.findAllRecentPost(anyLong(), any(PageRequest.class)))
                        .willReturn(List.of());

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(1L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllRecentPost(anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllRecentPostByCursor(any(), any(), any(), any());
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(0)
                );
            }
        }

        @DisplayName("cursorId가 이전에 조회한 게시글들 중 마지막 게시글의 postId일 때")
        @Nested
        class CursorIdIsNotZero {

            @DisplayName("게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getRecentPostsByCursor_HasNextTrue() {
                // Given
                final Post post = mock(Post.class);
                final List<RecentPostResponseDto> responseDtos = new ArrayList<>();
                final RecentPostResponseDto postDto = mock(RecentPostResponseDto.class);
                for (int i = 0; i < 11; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.of(post));
                given(post.getSelectedDate())
                        .willReturn(LocalDate.of(2022, 1, 1));
                given(postRepository.findAllRecentPostByCursor(anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(1L, 30L);

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllRecentPost(any(), any());
                verify(postRepository, times(1))
                        .findAllRecentPostByCursor(anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class));
                verify(postDto, times(10))
                        .likeCheck();
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getRecentPostsByCursor_HasNextFalse(final int postCount) {
                // Given
                final Post post = mock(Post.class);
                final List<RecentPostResponseDto> responseDtos = new ArrayList<>();
                final RecentPostResponseDto postDto = mock(RecentPostResponseDto.class);
                for (int i = 0; i < postCount; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.of(post));
                given(post.getSelectedDate())
                        .willReturn(LocalDate.of(2022, 1, 1));
                given(postRepository.findAllRecentPostByCursor(anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<RecentPostResponseDto> response = postService.recentAllPosts(1L, 30L);

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllRecentPost(any(), any());
                verify(postRepository, times(1))
                        .findAllRecentPostByCursor(anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class));
                verify(postDto, times(postCount))
                        .likeCheck();
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("커서에 해당하는 게시글이 없으면 예외를 발생시킨다.")
            @Test
            void postNotFoundException() {
                // Given
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.empty());

                // When
                assertThatThrownBy(() -> postService.recentAllPosts(1L, 30L))
                        .isInstanceOf(PostNotFoundException.class)
                        .hasMessage("해당 게시글을 찾을 수 없습니다.");

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllRecentPost(any(), any());
                verify(postRepository, times(0))
                        .findAllRecentPostByCursor(any(), any(), any(), any());
            }
        }
    }

    @DisplayName("followExplore 메소드는")
    @Nested
    class FollowExploreMethod {

        @DisplayName("cursorId가 0일 때")
        @Nested
        class CursorIdIsZero {

            @DisplayName("팔로우한 유저들의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getFollowExplorePosts_HasNextTrue() {
                // Given
                final List<ExploreResponseDto> responseDtos = new ArrayList<>();
                final ExploreResponseDto postDto = mock(ExploreResponseDto.class);
                for (int i = 0; i < 11; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findFollowingPosts(anyLong(), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(1L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findFollowingPosts(anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findFollowingPostsByCursor(any(), any(), any(), any());
                verify(postDto, times(10))
                        .likeCheck();
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
                final List<ExploreResponseDto> responseDtos = new ArrayList<>();
                final ExploreResponseDto postDto = mock(ExploreResponseDto.class);
                for (int i = 0; i < postCount; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findFollowingPosts(anyLong(), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(1L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findFollowingPosts(anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findFollowingPostsByCursor(any(), any(), any(), any());
                verify(postDto, times(postCount))
                        .likeCheck();
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("팔로우한 유저들의 게시글이 없으면 빈 리스트를 반환하고 hasNext를 false로 표시한다.")
            @Test
            void getFollowExplorePosts_EmptyList_HasNextFalse() {
                // Given
                given(postRepository.findFollowingPosts(anyLong(), any(PageRequest.class)))
                        .willReturn(List.of());

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(1L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findFollowingPosts(anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findFollowingPostsByCursor(any(), any(), any(), any());
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(0)
                );
            }
        }

        @DisplayName("cursorId가 이전에 조회한 게시글들 중 마지막 게시글의 postId일 때")
        @Nested
        class CursorIdIsNotZero {

            @DisplayName("팔로우한 유저들의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getFollowExplorePostsByCursor_HasNextTrue() {
                // Given
                final Post post = mock(Post.class);
                final List<ExploreResponseDto> responseDtos = new ArrayList<>();
                final ExploreResponseDto postDto = mock(ExploreResponseDto.class);
                for (int i = 0; i < 11; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.of(post));
                given(post.getSelectedDate())
                        .willReturn(LocalDate.of(2022, 1, 1));
                given(postRepository.findFollowingPostsByCursor(anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(1L, 30L);

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findFollowingPosts(any(), any());
                verify(postRepository, times(1))
                        .findFollowingPostsByCursor(anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class));
                verify(postDto, times(10))
                        .likeCheck();
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("팔로우한 유저들의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getFollowExplorePostsByCursor_HasNextFalse(final int postCount) {
                // Given
                final Post post = mock(Post.class);
                final List<ExploreResponseDto> responseDtos = new ArrayList<>();
                final ExploreResponseDto postDto = mock(ExploreResponseDto.class);
                for (int i = 0; i < postCount; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.of(post));
                given(post.getSelectedDate())
                        .willReturn(LocalDate.of(2022, 1, 1));
                given(postRepository.findFollowingPostsByCursor(anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<ExploreResponseDto> response = postService.followExplore(1L, 30L);

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findFollowingPosts(any(), any());
                verify(postRepository, times(1))
                        .findFollowingPostsByCursor(anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class));
                verify(postDto, times(postCount))
                        .likeCheck();
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("커서에 해당하는 게시글이 없으면 예외를 발생시킨다.")
            @Test
            void postNotFoundException() {
                // Given
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.empty());

                // When
                assertThatThrownBy(() -> postService.followExplore(1L, 30L))
                        .isInstanceOf(PostNotFoundException.class)
                        .hasMessage("해당 게시글을 찾을 수 없습니다.");

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findFollowingPosts(any(), any());
                verify(postRepository, times(0))
                        .findFollowingPostsByCursor(any(), any(), any(), any());
            }
        }
    }

    @DisplayName("getAllPostByUser 메소드는")
    @Nested
    class GetAllPostByUserMethod {

        @DisplayName("cursorId가 0일 때")
        @Nested
        class CursorIdIsZero {

            @DisplayName("해당 유저의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getAllPostOfUser_HasNextTrue() {
                // Given
                final List<AllPostResponseDto> responseDtos = new ArrayList<>();
                final AllPostResponseDto postDto = mock(AllPostResponseDto.class);
                for (int i = 0; i < 11; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findAllPostOfUser(anyLong(), anyLong(), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(1L, 2L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllPostOfUser(anyLong(), anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllPostOfUserByCursor(any(), any(), any(), any(), any());
                verify(postDto, times(10))
                        .likeCheck();
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
                final List<AllPostResponseDto> responseDtos = new ArrayList<>();
                final AllPostResponseDto postDto = mock(AllPostResponseDto.class);
                for (int i = 0; i < postCount; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findAllPostOfUser(anyLong(), anyLong(), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(1L, 2L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllPostOfUser(anyLong(), anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllPostOfUserByCursor(any(), any(), any(), any(), any());
                verify(postDto, times(postCount))
                        .likeCheck();
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("해당 유저 또는 게시글이 없으면 빈 리스트를 반환하고 hasNext를 false로 표시한다.")
            @Test
            void getAllPostOfUser_EmptyList_HasNextFalse() {
                // Given
                given(postRepository.findAllPostOfUser(anyLong(), anyLong(), any(PageRequest.class)))
                        .willReturn(List.of());

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(1L, 2L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllPostOfUser(anyLong(), anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllPostOfUserByCursor(any(), any(), any(), any(), any());
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(0)
                );
            }
        }

        @DisplayName("cursorId가 이전에 조회한 게시글들 중 마지막 게시글의 postId일 때")
        @Nested
        class CursorIdIsNotZero {

            @DisplayName("해당 유저의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getAllPostOfUserByCursor_HasNextTrue() {
                // Given
                final Post post = mock(Post.class);
                final List<AllPostResponseDto> responseDtos = new ArrayList<>();
                final AllPostResponseDto postDto = mock(AllPostResponseDto.class);
                for (int i = 0; i < 11; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.of(post));
                given(post.getSelectedDate())
                        .willReturn(LocalDate.of(2022, 1, 1));
                given(postRepository.findAllPostOfUserByCursor(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(1L, 2L, 30L);

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllPostOfCategory(any(), any(), any());
                verify(postRepository, times(1))
                        .findAllPostOfUserByCursor(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class));
                verify(postDto, times(10))
                        .likeCheck();
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("해당 유저의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getAllPostOfUserByCursor_HasNextFalse(final int postCount) {
                // Given
                final Post post = mock(Post.class);
                final List<AllPostResponseDto> responseDtos = new ArrayList<>();
                final AllPostResponseDto postDto = mock(AllPostResponseDto.class);
                for (int i = 0; i < postCount; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.of(post));
                given(post.getSelectedDate())
                        .willReturn(LocalDate.of(2022, 1, 1));
                given(postRepository.findAllPostOfUserByCursor(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByUser(1L, 2L, 30L);

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllPostOfCategory(any(), any(), any());
                verify(postRepository, times(1))
                        .findAllPostOfUserByCursor(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class));
                verify(postDto, times(postCount))
                        .likeCheck();
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("해당 게시글이 없으면 예외를 발생시킨다.")
            @Test
            void postNotFoundException() {
                // Given
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.empty());

                // When
                assertThatThrownBy(() -> postService.getAllPostByUser(1L, 2L, 30L))
                        .isInstanceOf(PostNotFoundException.class)
                        .hasMessage("해당 게시글을 찾을 수 없습니다.");

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllPostOfCategory(any(), any(), any());
                verify(postRepository, times(0))
                        .findAllPostOfUserByCursor(any(), any(), any(), any(), any());
            }
        }
    }

    @DisplayName("getAllPostByCategory 메소드는")
    @Nested
    class GetAllPostByCategoryMethod {

        @DisplayName("cursorId가 0일 때")
        @Nested
        class CursorIdIsZero {

            @DisplayName("해당 카테고리의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getAllPostOfUser_HasNextTrue() {
                // Given
                final List<AllPostResponseDto> responseDtos = new ArrayList<>();
                final AllPostResponseDto postDto = mock(AllPostResponseDto.class);
                for (int i = 0; i < 11; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findAllPostOfCategory(anyLong(), anyLong(), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(1L, 2L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllPostOfCategory(anyLong(), anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllPostOfCategoryByCursor(any(), any(), any(), any(), any());
                verify(postDto, times(10))
                        .likeCheck();
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
                final List<AllPostResponseDto> responseDtos = new ArrayList<>();
                final AllPostResponseDto postDto = mock(AllPostResponseDto.class);
                for (int i = 0; i < postCount; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findAllPostOfCategory(anyLong(), anyLong(), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(1L, 2L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllPostOfCategory(anyLong(), anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllPostOfCategoryByCursor(any(), any(), any(), any(), any());
                verify(postDto, times(postCount))
                        .likeCheck();
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("해당 카테고리 또는 게시글이 없으면 빈 리스트를 반환하고 hasNext를 false로 표시한다.")
            @Test
            void getAllPostOfCategory_EmptyList_HasNextFalse() {
                // Given
                given(postRepository.findAllPostOfCategory(anyLong(), anyLong(), any(PageRequest.class)))
                        .willReturn(List.of());

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(1L, 2L, 0L);

                // Then
                verify(postRepository, times(0))
                        .findById(any());
                verify(postRepository, times(1))
                        .findAllPostOfCategory(anyLong(), anyLong(), any(PageRequest.class));
                verify(postRepository, times(0))
                        .findAllPostOfCategoryByCursor(any(), any(), any(), any(), any());
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(0)
                );
            }
        }

        @DisplayName("cursorId가 이전에 조회한 게시글들 중 마지막 게시글의 postId일 때")
        @Nested
        class CursorIdIsNotZero {

            @DisplayName("해당 카테고리의 게시글이 11개 이상이면 최신 10개의 게시글을 조회하고 hasNext를 true로 표시한다.")
            @Test
            void getAllPostOfCategoryByCursor_HasNextTrue() {
                // Given
                final Post post = mock(Post.class);
                final List<AllPostResponseDto> responseDtos = new ArrayList<>();
                final AllPostResponseDto postDto = mock(AllPostResponseDto.class);
                for (int i = 0; i < 11; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.of(post));
                given(post.getSelectedDate())
                        .willReturn(LocalDate.of(2022, 1, 1));
                given(postRepository.findAllPostOfCategoryByCursor(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(1L, 2L, 30L);

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllPostOfCategory(any(), any(), any());
                verify(postRepository, times(1))
                        .findAllPostOfCategoryByCursor(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class));
                verify(postDto, times(10))
                        .likeCheck();
                assertAll("게시글 10개 조회, hasNext = true",
                        () -> assertThat(response.hasNext()).isTrue(),
                        () -> assertThat(response.values().size()).isEqualTo(10)
                );
            }

            @DisplayName("해당 카테고리의 게시글이 1 ~ 10개면 게시글을 조회하고 hasNext를 false로 표시한다.")
            @ParameterizedTest
            @ValueSource(ints = {1, 5, 10})
            void getAllPostOfCategoryByCursor_HasNextFalse(final int postCount) {
                // Given
                final Post post = mock(Post.class);
                final List<AllPostResponseDto> responseDtos = new ArrayList<>();
                final AllPostResponseDto postDto = mock(AllPostResponseDto.class);
                for (int i = 0; i < postCount; i++) {
                    responseDtos.add(postDto);
                }
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.of(post));
                given(post.getSelectedDate())
                        .willReturn(LocalDate.of(2022, 1, 1));
                given(postRepository.findAllPostOfCategoryByCursor(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class)))
                        .willReturn(responseDtos);

                // When
                final CursorResult<AllPostResponseDto> response = postService.getAllPostByCategory(1L, 2L, 30L);

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllPostOfCategory(any(), any(), any());
                verify(postRepository, times(1))
                        .findAllPostOfCategoryByCursor(anyLong(), anyLong(), anyLong(), any(LocalDate.class), any(PageRequest.class));
                verify(postDto, times(postCount))
                        .likeCheck();
                assertAll("게시글 조회, hasNext = false",
                        () -> assertThat(response.hasNext()).isFalse(),
                        () -> assertThat(response.values().size()).isEqualTo(postCount)
                );
            }

            @DisplayName("해당 게시글이 없으면 예외를 발생시킨다.")
            @Test
            void postNotFoundException() {
                // Given
                given(postRepository.findById(anyLong()))
                        .willReturn(Optional.empty());

                // When
                assertThatThrownBy(() -> postService.getAllPostByCategory(1L, 2L, 30L))
                        .isInstanceOf(PostNotFoundException.class)
                        .hasMessage("해당 게시글을 찾을 수 없습니다.");

                // Then
                verify(postRepository, times(1))
                        .findById(anyLong());
                verify(postRepository, times(0))
                        .findAllPostOfCategory(any(), any(), any());
                verify(postRepository, times(0))
                        .findAllPostOfCategoryByCursor(any(), any(), any(), any(), any());
            }
        }
    }
}
