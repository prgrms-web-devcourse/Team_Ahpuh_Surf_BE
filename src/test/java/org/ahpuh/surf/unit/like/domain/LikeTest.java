package org.ahpuh.surf.unit.like.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LikeTest {

    @DisplayName("Like 엔티티를 builder로 생성하면 해당 유저와 게시글의 Like 리스트에 add 된다.")
    @Test
    void likeBuilderAddTest() {
        // Given
        final User user = createMockUser();
        final Category category = createMockCategory(user);
        final Post post = createMockPost(user, category);

        // When
        Like.builder()
                .user(user)
                .post(post)
                .build();

        // Then
        assertAll("유저와 게시글의 Like 리스트에 자동 add 되는지 테스트",
                () -> assertThat(user.getLikes().size()).isEqualTo(1),
                () -> assertThat(post.getLikes().size()).isEqualTo(1)
        );
    }
}
