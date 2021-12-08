package org.ahpuh.surf.category.converter;

import org.ahpuh.surf.category.dto.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.CategoryResponseDto;
import org.ahpuh.surf.category.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    // Todo: user추가
    public Category toEntity(final CategoryCreateRequestDto createRequestDto) {
        return Category.builder()
                .name(createRequestDto.getName())
                .colorCode(createRequestDto.getColorCode())
                .build();
    }

    public CategoryResponseDto toCategoryResponseDto(final Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .isPublic(category.isPublic())
                .colorCode(category.getColorCode())
                .build();
    }
}
