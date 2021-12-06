package org.ahpuh.backend.category.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.backend.category.converter.CategoryConverter;
import org.ahpuh.backend.category.dto.CategoryCreateRequestDto;
import org.ahpuh.backend.category.dto.CategoryResponseDto;
import org.ahpuh.backend.category.dto.CategoryUpdateRequestDto;
import org.ahpuh.backend.category.entity.Category;
import org.ahpuh.backend.category.repository.CategoryRepository;
import org.ahpuh.backend.common.exception.EntityExceptionSuppliers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryConverter categoryConverter;

    @Override
    @Transactional
    public Long createCategory(CategoryCreateRequestDto categoryDto) {
        return categoryRepository.save(categoryConverter.toEntity(categoryDto)).getId();
    }

    @Override
    @Transactional
    public Long updateCategory(Long categoryId, CategoryUpdateRequestDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(EntityExceptionSuppliers.CategoryNotFound);
        category.update(categoryDto.getName(), categoryDto.isPublic(), categoryDto.getColorCode());
        return category.getId();
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(EntityExceptionSuppliers.CategoryNotFound);
        category.delete();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> findAllCategoryByUser(Long userId) {
        return null;
    }
}
