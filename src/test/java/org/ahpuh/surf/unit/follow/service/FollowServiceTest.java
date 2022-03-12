package org.ahpuh.surf.unit.follow.service;

import org.ahpuh.surf.common.exception.follow.DuplicatedFollowingException;
import org.ahpuh.surf.common.exception.follow.FollowNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.follow.domain.FollowConverter;
import org.ahpuh.surf.follow.domain.repository.FollowRepository;
import org.ahpuh.surf.follow.dto.response.FollowUserResponseDto;
import org.ahpuh.surf.follow.service.FollowService;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowConverter followConverter;

    @InjectMocks
    private FollowService followService;

    @DisplayName("follow 메소드는")
    @Nested
    class FollowMethod {

        @DisplayName("유저1(source)이 유저2(target)를 팔로우 할 수 있다.")
        @Test
        void testFollow_Success() {
            // Given
            final Follow follow = mock(Follow.class);
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mock(User.class)));
            given(followRepository.existsBySourceAndTarget(any(User.class), any(User.class)))
                    .willReturn(false);
            given(followConverter.toEntity(any(User.class), any(User.class)))
                    .willReturn(follow);
            given(followRepository.save(any(Follow.class)))
                    .willReturn(follow);
            given(follow.getFollowId())
                    .willReturn(1L);

            // When
            followService.follow(1L, 2L);

            // Then
            verify(userRepository, times(2))
                    .findById(anyLong());
            verify(followRepository, times(1))
                    .existsBySourceAndTarget(any(User.class), any(User.class));
            verify(followConverter, times(1))
                    .toEntity(any(User.class), any(User.class));
            verify(followRepository, times(1))
                    .save(any(Follow.class));
        }

        @DisplayName("이미 팔로우 한 기록이 있으면 예외가 발생한다.")
        @Test
        void duplicatedFollowingException() {
            // Given
            given(userRepository.findById(any()))
                    .willReturn(Optional.of(mock(User.class)));
            given(followRepository.existsBySourceAndTarget(any(User.class), any(User.class)))
                    .willReturn(true);

            // When
            assertThatThrownBy(() -> followService.follow(1L, 2L))
                    .isInstanceOf(DuplicatedFollowingException.class)
                    .hasMessage("이미 팔로우 한 사용자입니다.");

            // Then
            verify(userRepository, times(2))
                    .findById(anyLong());
            verify(followRepository, times(1))
                    .existsBySourceAndTarget(any(User.class), any(User.class));
            verify(followConverter, times(0))
                    .toEntity(any(), any());
            verify(followRepository, times(0))
                    .save(any());
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다.")
        @Test
        void userNotFoundException() {
            // Given
            given(userRepository.findById(any()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> followService.follow(1L, 2L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(followRepository, times(0))
                    .existsBySourceAndTarget(any(), any());
            verify(followConverter, times(0))
                    .toEntity(any(), any());
            verify(followRepository, times(0))
                    .save(any());
        }
    }

    @DisplayName("unfollow 메소드는")
    @Nested
    class UnfollowMethod {

        @DisplayName("유저1(source)이 유저2(target)를 언팔로우 할 수 있다.")
        @Test
        void testUnfollow_Success() {
            // Given
            final Follow follow = mock(Follow.class);
            given(followRepository.findBySourceIdAndTargetId(anyLong(), anyLong()))
                    .willReturn(Optional.of(follow));

            // When
            followService.unfollow(1L, 2L);

            // Then
            verify(followRepository, times(1))
                    .findBySourceIdAndTargetId(anyLong(), anyLong());
            verify(followRepository, times(1))
                    .delete(any(Follow.class));
        }

        @DisplayName("팔로우한 기록이 없으면 예외가 발생한다.")
        @Test
        void followNotFoundException() {
            // Given
            given(followRepository.findBySourceIdAndTargetId(anyLong(), anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> followService.unfollow(1L, 2L))
                    .isInstanceOf(FollowNotFoundException.class)
                    .hasMessage("팔로우 한 기록이 없습니다.");

            // Then
            verify(followRepository, times(1))
                    .findBySourceIdAndTargetId(any(), any());
            verify(followRepository, times(0))
                    .delete(any());
        }
    }

    @DisplayName("findFollowerList 메소드는")
    @Nested
    class FindFollowerListMethod {

        @DisplayName("해당 유저의 팔로워들의 정보를 조회할 수 있다.")
        @Test
        void findFollowerList_Success() {
            // Given
            given(followRepository.findByTargetId(anyLong()))
                    .willReturn(List.of(mock(FollowUserResponseDto.class)));

            // When
            followService.findFollowerList(1L);

            // Then
            verify(followRepository, times(1))
                    .findByTargetId(anyLong());
        }

        @DisplayName("해당 유저의 팔로워가 없다면 빈 리스트를 반환한다.")
        @Test
        void noFollowerReturnEmptyList() {
            // Given
            given(followRepository.findByTargetId(anyLong()))
                    .willReturn(List.of());

            // When
            followService.findFollowerList(1L);

            // Then
            verify(followRepository, times(1))
                    .findByTargetId(anyLong());
        }
    }

    @DisplayName("findFollowingList 메소드는")
    @Nested
    class FindFollowingListMethod {

        @DisplayName("해당 유저가 팔로잉한 유저들의 정보를 조회할 수 있다.")
        @Test
        void findFollowingList_Success() {
            // Given
            given(followRepository.findBySourceId(anyLong()))
                    .willReturn(List.of(mock(FollowUserResponseDto.class)));

            // When
            followService.findFollowingList(1L);

            // Then
            verify(followRepository, times(1))
                    .findBySourceId(anyLong());
        }

        @DisplayName("해당 유저를 팔로잉한 유저가 없다면 빈 리스트를 반환한다.")
        @Test
        void noFollowerReturnEmptyList() {
            // Given
            given(followRepository.findBySourceId(anyLong()))
                    .willReturn(List.of());

            // When
            followService.findFollowingList(1L);

            // Then
            verify(followRepository, times(1))
                    .findBySourceId(anyLong());
        }
    }
}
