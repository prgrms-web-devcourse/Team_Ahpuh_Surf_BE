package org.ahpuh.surf.unit.category.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.common.exception.post.DuplicatedPostException;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.ahpuh.surf.common.factory.MockUserFactory.createSavedUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CategoryTest {

    @DisplayName("카테고리 정보를 수정할 수 있다.")
    @Test
    void updateCategoryTest() {
        // Given
        final User user = createSavedUser();
        final Category category = createMockCategory(user);

        // When
        category.update("update", false, "FFFFFF");

        // Then
        assertAll("카테고리 정보 수정 테스트",
                () -> assertThat(category.getName()).isEqualTo("update"),
                () -> assertThat(category.getIsPublic()).isFalse(),
                () -> assertThat(category.getColorCode()).isEqualTo("FFFFFF")
        );
    }

    @DisplayName("addPost 메소드는 이미 등록된 게시글이 다시 등록되면 예외가 발생한다.")
    @Test
    void duplicatedPostException() {
        // Given
        final User user = createMockUser();
        final Category category = createMockCategory(user);
        final Post post = createMockPost(user, category);

        // When Then
        assertThatThrownBy(() -> category.addPost(post))
                .isInstanceOf(DuplicatedPostException.class)
                .hasMessage("이미 등록된 게시글입니다.");
    }
}
