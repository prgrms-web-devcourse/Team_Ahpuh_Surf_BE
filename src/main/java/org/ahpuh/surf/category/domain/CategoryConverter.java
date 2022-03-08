package org.ahpuh.surf.category.domain;

import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.response.AllCategoryByUserResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    public Category toEntity(final User user, final CategoryCreateRequestDto createRequestDto) {
        return Category.builder()
                .user(user)
                .name(createRequestDto.getName())
                .colorCode(createRequestDto.getColorCode())
                .build();
    }

    public AllCategoryByUserResponseDto toCategoryResponseDto(final Category category) {
        return AllCategoryByUserResponseDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .isPublic(category.getIsPublic())
                .colorCode(category.getColorCode())
                .build();
    }

    public CategoryDetailResponseDto toCategoryDetailResponseDto(final Category category, final int averageScore) {
        return CategoryDetailResponseDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .isPublic(category.getIsPublic())
                .colorCode(category.getColorCode())
                .postCount(category.getPostCount())
                .averageScore(averageScore)
                .build();
    }
}
