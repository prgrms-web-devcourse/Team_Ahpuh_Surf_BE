package org.ahpuh.surf.unit.like.service;

import org.ahpuh.surf.common.exception.like.DuplicatedLikeException;
import org.ahpuh.surf.common.exception.like.LikeNotFoundException;
import org.ahpuh.surf.common.exception.post.PostNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.like.domain.LikeConverter;
import org.ahpuh.surf.like.domain.LikeRepository;
import org.ahpuh.surf.like.service.LikeService;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.domain.repository.PostRepository;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeConverter likeConverter;

    @InjectMocks
    private LikeService likeService;

    @DisplayName("like 메소드는")
    @Nested
    class LikeMethod {

        @DisplayName("해당 유저가 게시글을 좋아요 할 수 있다.")
        @Test
        void testLike_Success() {
            // Given
            final Like like = mock(Like.class);
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mock(User.class)));
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(mock(Post.class)));
            given(likeRepository.existsByUserAndPost(any(User.class), any(Post.class)))
                    .willReturn(false);
            given(likeConverter.toEntity(any(User.class), any(Post.class)))
                    .willReturn(like);
            given(likeRepository.save(any(Like.class)))
                    .willReturn(like);
            given(like.getLikeId())
                    .willReturn(1L);

            // When
            likeService.like(1L, 1L);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(likeRepository, times(1))
                    .existsByUserAndPost(any(User.class), any(Post.class));
            verify(likeConverter, times(1))
                    .toEntity(any(User.class), any(Post.class));
            verify(likeRepository, times(1))
                    .save(any(Like.class));
        }

        @DisplayName("이미 좋아요 한 기록이 있으면 예외가 발생한다.")
        @Test
        void duplicatedLikeException() {
            // Given
            given(userRepository.findById(any()))
                    .willReturn(Optional.of(mock(User.class)));
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.of(mock(Post.class)));
            given(likeRepository.existsByUserAndPost(any(User.class), any(Post.class)))
                    .willReturn(true);

            // When
            assertThatThrownBy(() -> likeService.like(1L, 1L))
                    .isInstanceOf(DuplicatedLikeException.class)
                    .hasMessage("이미 좋아요를 누른 게시글입니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(likeRepository, times(1))
                    .existsByUserAndPost(any(User.class), any(Post.class));
            verify(likeConverter, times(0))
                    .toEntity(any(), any());
            verify(likeRepository, times(0))
                    .save(any());
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다.")
        @Test
        void userNotFoundException() {
            // Given
            given(userRepository.findById(any()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> likeService.like(1L, 1L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(0))
                    .findById(any());
            verify(likeRepository, times(0))
                    .existsByUserAndPost(any(), any());
            verify(likeConverter, times(0))
                    .toEntity(any(), any());
            verify(likeRepository, times(0))
                    .save(any());
        }

        @DisplayName("존재하지 않는 게시글 아이디가 입력되면 예외가 발생한다.")
        @Test
        void postNotFoundException() {
            // Given
            given(userRepository.findById(any()))
                    .willReturn(Optional.of(mock(User.class)));
            given(postRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> likeService.like(1L, 1L))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("해당 게시글을 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(postRepository, times(1))
                    .findById(anyLong());
            verify(likeRepository, times(0))
                    .existsByUserAndPost(any(), any());
            verify(likeConverter, times(0))
                    .toEntity(any(), any());
            verify(likeRepository, times(0))
                    .save(any());
        }
    }

    @DisplayName("unlike 메소드는")
    @Nested
    class UnlikeMethod {

        @DisplayName("해당 유저가 좋아요한 게시글을 취소할 수 있다.")
        @Test
        void testUnlike_Success() {
            // Given
            final Like like = mock(Like.class);
            given(likeRepository.findById(anyLong()))
                    .willReturn(Optional.of(like));

            // When
            likeService.unlike(1L);

            // Then
            verify(likeRepository, times(1))
                    .findById(anyLong());
            verify(likeRepository, times(1))
                    .delete(any(Like.class));
        }

        @DisplayName("좋아요한 기록이 없으면 예외가 발생한다.")
        @Test
        void likeNotFoundException() {
            // Given
            given(likeRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> likeService.unlike(1L))
                    .isInstanceOf(LikeNotFoundException.class)
                    .hasMessage("좋아요 한 기록이 없습니다.");

            // Then
            verify(likeRepository, times(1))
                    .findById(anyLong());
            verify(likeRepository, times(0))
                    .delete(any());
        }
    }
}
