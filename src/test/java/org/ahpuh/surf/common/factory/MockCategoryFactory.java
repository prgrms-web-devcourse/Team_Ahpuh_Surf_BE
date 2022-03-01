package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.user.domain.User;

public class MockCategoryFactory {

    public static Category createMockCategory(final User user) {
        return Category.builder()
                .user(user)
                .name("categoryName")
                .colorCode("000000")
                .build();
    }

}
