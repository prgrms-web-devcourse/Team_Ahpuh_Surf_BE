package org.ahpuh.surf.integration.follow.service;

import org.ahpuh.surf.common.exception.follow.DuplicatedFollowingException;
import org.ahpuh.surf.common.exception.follow.FollowNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.follow.domain.repository.FollowRepository;
import org.ahpuh.surf.follow.dto.response.FollowUserResponseDto;
import org.ahpuh.surf.follow.service.FollowService;
import org.ahpuh.surf.integration.IntegrationTest;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.ahpuh.surf.common.factory.MockFollowFactory.createMockFollow;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class FollowServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("팔로우 테스트")
    @Nested
    class FollowTest {

        @DisplayName("유저1(source)이 유저2(target)를 팔로우 할 수 있다.")
        @Test
        void followSuccess() {
            // Given
            final User user1 = saveUser("user1@naver.com");
            final User user2 = saveUser("user2@naver.com");

            // When
            followService.follow(user1.getUserId(), user2.getUserId());

            // Then
            assertThat(followRepository.findAll().size()).isEqualTo(1);
        }

        @DisplayName("이미 팔로우 한 기록이 있으면 예외가 발생한다 - 400 응답")
        @Test
        void duplicatedFollowingException_400() {
            // Given
            final User user1 = saveUser("user1@naver.com");
            final User user2 = saveUser("user2@naver.com");
            followRepository.save(createMockFollow(user1, user2));

            // When Then
            assertThatThrownBy(() -> followService.follow(user1.getUserId(), user2.getUserId()))
                    .isInstanceOf(DuplicatedFollowingException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("이미 팔로우 한 사용자입니다.");
        }

        @DisplayName("존재하지 않는 유저 아이디가 입력되면 예외가 발생한다 - 404 응답")
        @Test
        void userNotFoundException_404() {
            // When Then
            assertThatThrownBy(() -> followService.follow(1L, 2L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");
        }
    }

    @DisplayName("언팔로우 테스트")
    @Nested
    class UnfollowTest {

        @DisplayName("유저1(source)이 유저2(target)를 언팔로우 할 수 있다.")
        @Test
        void unfollowSuccess() {
            // Given
            final User user1 = saveUser("user1@naver.com");
            final User user2 = saveUser("user2@naver.com");
            followRepository.save(createMockFollow(user1, user2));
            assertThat(followRepository.findAll().size()).isEqualTo(1);

            // When
            followService.unfollow(user1.getUserId(), user2.getUserId());

            // Then
            assertThat(followRepository.findAll().size()).isEqualTo(0);
        }

        @DisplayName("팔로우한 기록이 없으면 예외가 발생한다 - 404 응답")
        @Test
        void followNotFoundException_404() {
            // Given
            final User user1 = saveUser("user1@naver.com");
            final User user2 = saveUser("user2@naver.com");

            // When Then
            assertThatThrownBy(() -> followService.unfollow(user1.getUserId(), user2.getUserId()))
                    .isInstanceOf(FollowNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("팔로우 한 기록이 없습니다.");
        }
    }

    @DisplayName("해당 유저의 팔로워 정보 전체 조회 테스트")
    @Nested
    class FindFollowerListTest {

        @DisplayName("해당 유저의 팔로워들의 정보를 조회할 수 있다.")
        @Test
        void findFollowerListSuccess() {
            // Given
            final User user1 = saveUser("user1@naver.com");
            final User user2 = saveUser("user2@naver.com");
            final User user3 = saveUser("user3@naver.com");
            followRepository.save(createMockFollow(user1, user3));
            followRepository.save(createMockFollow(user2, user3));

            // When
            final List<FollowUserResponseDto> response = followService.findFollowerList(user3.getUserId());

            // Then
            assertAll(
                    () -> assertThat(response.size()).isEqualTo(2),
                    () -> assertThat(response.get(0).getUserId()).isEqualTo(user2.getUserId()),
                    () -> assertThat(response.get(1).getUserId()).isEqualTo(user1.getUserId())
            );
        }

        @DisplayName("해당 유저의 팔로워가 없다면 빈 리스트를 반환한다.")
        @Test
        void noFollowerReturnEmptyList() {
            // Given
            final User user1 = saveUser("user1@naver.com");

            // When
            final List<FollowUserResponseDto> response = followService.findFollowerList(user1.getUserId());

            // Then
            assertThat(response.size()).isEqualTo(0);
        }
    }

    @DisplayName("해당 유저가 팔로잉한 유저 정보 전체 조회 테스트")
    @Nested
    class FindFollowingListTest {

        @DisplayName("해당 유저가 팔로잉한 유저들의 정보를 조회할 수 있다.")
        @Test
        void findFollowingListSuccess() {
            // Given
            final User user1 = saveUser("user1@naver.com");
            final User user2 = saveUser("user2@naver.com");
            final User user3 = saveUser("user3@naver.com");
            followRepository.save(createMockFollow(user1, user2));
            followRepository.save(createMockFollow(user1, user3));

            // When
            final List<FollowUserResponseDto> response = followService.findFollowingList(user1.getUserId());

            // Then
            assertAll(
                    () -> assertThat(response.size()).isEqualTo(2),
                    () -> assertThat(response.get(0).getUserId()).isEqualTo(user3.getUserId()),
                    () -> assertThat(response.get(1).getUserId()).isEqualTo(user2.getUserId())
            );
        }

        @DisplayName("해당 유저를 팔로잉한 유저가 없다면 빈 리스트를 반환한다.")
        @Test
        void noFollowerReturnEmptyList() {
            // Given
            final User user1 = saveUser("user1@naver.com");

            // When
            final List<FollowUserResponseDto> response = followService.findFollowingList(user1.getUserId());

            // Then
            assertThat(response.size()).isEqualTo(0);
        }
    }

    private User saveUser(final String email) {
        entityManager.persist(createMockUser(email));

        return userRepository.findAll()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow();
    }
}
