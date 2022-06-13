package org.ahpuh.surf.unit.category.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockUserFactory.createMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CategoryTest {

    @DisplayName("카테고리 정보를 수정할 수 있다.")
    @Test
    void updateCategoryTest() {
        // Given
        final User user = createMockUser();
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
}
