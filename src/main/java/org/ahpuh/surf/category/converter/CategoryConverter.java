package org.ahpuh.surf.category.converter;

import org.ahpuh.surf.category.dto.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.CategoryDetailResponseDto;
import org.ahpuh.surf.category.dto.CategoryResponseDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.user.entity.User;
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

    public CategoryResponseDto toCategoryResponseDto(final Category category) {
        return CategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .isPublic(category.getIsPublic())
                .colorCode(category.getColorCode())
                .recentScore(category.getRecentScore())
                .build();
    }

    public CategoryDetailResponseDto toCategoryDetailResponseDto(final Category category) {
        return CategoryDetailResponseDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .averageScore(category.getAverageScore())
                .isPublic(category.getIsPublic())
                .colorCode(category.getColorCode())
                .postCount(category.getPostCount())
                .build();
    }

}
