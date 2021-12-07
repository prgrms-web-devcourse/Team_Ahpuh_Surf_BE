package org.ahpuh.backend.category.converter;

import org.ahpuh.backend.category.dto.CategoryCreateRequestDto;
import org.ahpuh.backend.category.dto.CategoryResponseDto;
import org.ahpuh.backend.category.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    // Todo: user추가
    public Category toEntity(CategoryCreateRequestDto dto) {
        return Category.builder()
                .name(dto.getName())
                .isPublic(dto.isPublic())
                .colorCode(dto.getColorCode())
                .build();
    }

    public CategoryResponseDto toCategoryResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .isPublic(category.isPublic())
                .colorCode(category.getColorCode())
                .build();
    }
}
