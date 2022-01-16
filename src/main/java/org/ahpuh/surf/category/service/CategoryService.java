package org.ahpuh.surf.category.service;

import org.ahpuh.surf.category.dto.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.CategoryDetailResponseDto;
import org.ahpuh.surf.category.dto.CategoryResponseDto;
import org.ahpuh.surf.category.dto.CategoryUpdateRequestDto;

import java.util.List;

public interface CategoryService {

    Long createCategory(Long userId, CategoryCreateRequestDto categoryDto);

    Long updateCategory(Long categoryId, CategoryUpdateRequestDto categoryDto);

    void deleteCategory(Long categoryId);

    List<CategoryResponseDto> findAllCategoryByUser(Long userId);

    List<CategoryDetailResponseDto> getCategoryDashboard(Long userId);

}
