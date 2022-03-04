package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.dto.response.AllCategoryByUserResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryCreateResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryUpdateResponseDto;
import org.ahpuh.surf.user.domain.User;

public class MockCategoryFactory {

    public static Category createMockCategory(final User user) {
        return Category.builder()
                .user(user)
                .name("categoryName")
                .colorCode("#000000")
                .build();
    }

    public static CategoryCreateRequestDto createMockCategoryCreateRequestDto() {
        return CategoryCreateRequestDto.builder()
                .name("categoryName")
                .colorCode("#000000")
                .build();
    }

    public static CategoryCreateResponseDto createMockCategoryCreateResponseDto() {
        return new CategoryCreateResponseDto(1L);
    }

    public static CategoryUpdateRequestDto createMockCategoryUpdateRequestDto() {
        return CategoryUpdateRequestDto.builder()
                .name("categoryName")
                .colorCode("#000000")
                .isPublic(true)
                .build();
    }

    public static CategoryUpdateResponseDto createMockCategoryUpdateResponseDto() {
        return new CategoryUpdateResponseDto(1L);
    }

    public static AllCategoryByUserResponseDto createMockAllCategoryByUserResponseDto() {
        return AllCategoryByUserResponseDto.builder()
                .categoryId(1L)
                .name("categoryName")
                .colorCode("#000000")
                .isPublic(true)
                .build();
    }

    public static CategoryDetailResponseDto createMockCategoryDetailResponseDto() {
        return CategoryDetailResponseDto.builder()
                .categoryId(1L)
                .name("categoryName")
                .colorCode("#000000")
                .isPublic(true)
                .averageScore(90)
                .postCount(3)
                .build();
    }
}
