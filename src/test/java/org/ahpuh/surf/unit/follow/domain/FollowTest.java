package org.ahpuh.surf.unit.follow.domain;

import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class FollowTest {

    @DisplayName("팔로우 엔티티를 builder로 생성하면 해당 유저의 팔로우 리스트에 add 된다.")
    @Test
    void followBuilderAddTest() {
        // Given
        final User user1 = createMockUser("test1@naver.com");
        final User user2 = createMockUser("test2@naver.com");

        // When
        Follow.builder()
                .source(user1)
                .target(user2)
                .build();

        // Then
        assertAll("유저의 팔로우 리스트에 자동 add 되는지 테스트",
                () -> assertThat(user1.getFollowing().size()).isEqualTo(1),
                () -> assertThat(user2.getFollowers().size()).isEqualTo(1)
        );
    }
}
