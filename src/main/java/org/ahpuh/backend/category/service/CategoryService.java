package org.ahpuh.backend.category.service;

import org.ahpuh.backend.category.dto.CategoryCreateRequestDto;
import org.ahpuh.backend.category.dto.CategoryResponseDto;
import org.ahpuh.backend.category.dto.CategoryUpdateRequestDto;

import java.util.List;

public interface CategoryService {

    Long createCategory(CategoryCreateRequestDto categoryDto);

    Long updateCategory(Long categoryId, CategoryUpdateRequestDto categoryDto);

    void deleteCategory(Long categoryId);

    List<CategoryResponseDto> findAllCategoryByUser(Long userId);

    // Todo: 해당 사용자의 카테고리 정보

    // Todo: 카테고리별 게시글 전체 조회

    // Todo: 일년치 카테고리별 게시글 점수 조회
}
