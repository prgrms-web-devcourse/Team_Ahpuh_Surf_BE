package org.ahpuh.surf.unit.follow.domain;

import org.ahpuh.surf.config.QuerydslConfig;
import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.follow.domain.repository.FollowRepository;
import org.ahpuh.surf.follow.dto.response.FollowUserResponseDto;
import org.ahpuh.surf.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockFollowFactory.createMockFollow;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(QuerydslConfig.class)
@DataJpaTest
public class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("save 메소드는")
    @Nested
    class SaveMethod {

        @DisplayName("팔로우를 등록할 수 있다.")
        @Test
        void saveFollow_Success() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            final Follow follow = createMockFollow(user1, user2);

            // When
            followRepository.save(follow);

            // Then
            final List<Follow> follows = followRepository.findAll();
            assertAll("팔로우 등록 테스트",
                    () -> assertThat(follows.size()).isEqualTo(1),
                    () -> assertThat(follows.get(0).getSource().getEmail()).isEqualTo("user1@naver.com"),
                    () -> assertThat(follows.get(0).getTarget().getEmail()).isEqualTo("user2@naver.com")
            );
        }

        @DisplayName("팔로우를 등록하면 해당 유저에 연관관계가 매핑된다.")
        @Test
        void followMappingTest() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            final Follow follow = createMockFollow(user1, user2);

            // When
            followRepository.save(follow);

            // Then
            final List<Follow> follows = followRepository.findAll();
            assertAll("팔로우 연관관계 매핑 테스트",
                    () -> assertThat(follows.size()).isEqualTo(1),
                    () -> assertThat(follows.get(0).getSource().getFollowing().size()).isEqualTo(1),
                    () -> assertThat(follows.get(0).getSource().getFollowing().get(0).getTarget().getEmail()).isEqualTo("user2@naver.com"),
                    () -> assertThat(follows.get(0).getTarget().getFollowers().size()).isEqualTo(1),
                    () -> assertThat(follows.get(0).getTarget().getFollowers().get(0).getSource().getEmail()).isEqualTo("user1@naver.com")
            );
        }
    }

    @DisplayName("delete 메소드는")
    @Nested
    class DeleteMethod {

        @DisplayName("팔로우를 삭제할 수 있다.")
        @Test
        void deleteFollowTest() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            followRepository.save(createMockFollow(user1, user2));

            final List<Follow> allFollow = followRepository.findAll();
            assertThat(allFollow.size()).isEqualTo(1);

            // When
            followRepository.delete(allFollow.get(0));
            testEntityManager.flush();
            testEntityManager.clear();

            // Then
            assertThat(followRepository.findAll().size()).isEqualTo(0);
        }
    }

    @DisplayName("existsBySourceAndTarget 메소드는")
    @Nested
    class ExistsBySourceAndTargetMethod {

        @DisplayName("source -> target 팔로우 기록이 있다면 true를 반환한다.")
        @Test
        void testExistCheck_True() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            followRepository.save(createMockFollow(user1, user2));

            final List<Follow> allFollow = followRepository.findAll();
            assertThat(allFollow.size()).isEqualTo(1);

            // When
            final boolean existCheck = followRepository.existsBySourceAndTarget(user1, user2);

            // Then
            assertThat(existCheck).isTrue();
        }

        @DisplayName("팔로우 기록이 없다면 false를 반환한다.")
        @Test
        void testExistCheck_False() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));

            // When
            final boolean existCheck = followRepository.existsBySourceAndTarget(user1, user2);

            // Then
            assertThat(existCheck).isFalse();
        }
    }

    @DisplayName("findBySourceIdAndTargetId 메소드는")
    @Nested
    class FindBySourceIdAndTargetIdMethod {

        @DisplayName("sourceId와 targetId에 해당하는 팔로우 기록을 조회할 수 있다.")
        @Test
        void findBySourceIdAndTargetId_Success() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            followRepository.save(createMockFollow(user1, user2));

            // When
            final Optional<Follow> follow = followRepository.findBySourceIdAndTargetId(user1.getUserId(), user2.getUserId());

            // Then
            assertAll("팔로우 기록 확인",
                    () -> assertThat(follow).isNotEmpty(),
                    () -> assertThat(follow.get().getSource()).isEqualTo(user1),
                    () -> assertThat(follow.get().getTarget()).isEqualTo(user2)
            );
        }

        @DisplayName("sourceId와 targetId에 해당하는 팔로우 기록이 없다면 optional.empty()를 반환한다.")
        @Test
        void findBySourceIdAndTargetId_Empty() {
            // When
            final Optional<Follow> follow = followRepository.findBySourceIdAndTargetId(1L, 2L);

            // Then
            assertThat(follow).isEmpty();
        }
    }

    @DisplayName("findBySourceId 메소드는")
    @Nested
    class FindBySourceIdMethod {

        @DisplayName("해당 유저가 팔로우한 유저들의 정보를 조회할 수 있다.")
        @Test
        void findBySourceId_Success() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            followRepository.save(createMockFollow(user1, user2));

            // When
            final List<FollowUserResponseDto> followUserDtos = followRepository.findBySourceId(user1.getUserId());

            // Then
            assertAll("팔로우한 유저 정보 확인",
                    () -> assertThat(followUserDtos.size()).isEqualTo(1),
                    () -> assertThat(followUserDtos.get(0).getUserId()).isEqualTo(user2.getUserId()),
                    () -> assertThat(followUserDtos.get(0).getUserName()).isEqualTo(user2.getUserName()),
                    () -> assertThat(followUserDtos.get(0).getProfilePhotoUrl()).isEqualTo(user2.getProfilePhotoUrl())
            );
        }

        @DisplayName("해당 유저가 팔로우한 유저가 없다면 빈 리스트를 반환한다.")
        @Test
        void findBySourceId_EmptyList() {
            // When
            final List<FollowUserResponseDto> followUserDtos = followRepository.findBySourceId(1L);

            // Then
            assertThat(followUserDtos).isEqualTo(List.of());
        }
    }

    @DisplayName("findByTargetId 메소드는")
    @Nested
    class FindByTargetIdMethod {

        @DisplayName("해당 유저의 팔로워들의 정보를 조회할 수 있다.")
        @Test
        void findByTargetId_Success() {
            // Given
            final User user1 = testEntityManager.persist(createMockUser("user1@naver.com"));
            final User user2 = testEntityManager.persist(createMockUser("user2@naver.com"));
            followRepository.save(createMockFollow(user1, user2));

            // When
            final List<FollowUserResponseDto> followUserDtos = followRepository.findByTargetId(user2.getUserId());

            // Then
            assertAll("팔로우한 유저 정보 확인",
                    () -> assertThat(followUserDtos.size()).isEqualTo(1),
                    () -> assertThat(followUserDtos.get(0).getUserId()).isEqualTo(user1.getUserId()),
                    () -> assertThat(followUserDtos.get(0).getUserName()).isEqualTo(user1.getUserName()),
                    () -> assertThat(followUserDtos.get(0).getProfilePhotoUrl()).isEqualTo(user1.getProfilePhotoUrl())
            );
        }

        @DisplayName("해당 유저의 팔로워가 없다면 빈 리스트를 반환한다.")
        @Test
        void findByTargetId_EmptyList() {
            // When
            final List<FollowUserResponseDto> followUserDtos = followRepository.findByTargetId(1L);

            // Then
            assertThat(followUserDtos).isEqualTo(List.of());
        }
    }
}
