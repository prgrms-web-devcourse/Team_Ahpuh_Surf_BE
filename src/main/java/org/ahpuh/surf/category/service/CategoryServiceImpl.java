package org.ahpuh.surf.category.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.converter.CategoryConverter;
import org.ahpuh.surf.category.dto.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.CategoryDetailResponseDto;
import org.ahpuh.surf.category.dto.CategoryResponseDto;
import org.ahpuh.surf.category.dto.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final CategoryConverter categoryConverter;

    @Override
    @Transactional
    public Long createCategory(final CategoryCreateRequestDto categoryDto) {
        final User user = userRepository.findById(categoryDto.getUserId())
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(categoryDto.getUserId()));
        final Category category = categoryConverter.toEntity(user, categoryDto);

        return categoryRepository.save(category).getId();
    }

    @Override
    @Transactional
    public Long updateCategory(final Long categoryId, final CategoryUpdateRequestDto categoryDto) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
        category.update(categoryDto.getName(), categoryDto.isPublic(), categoryDto.getColorCode());

        return category.getId();
    }

    @Override
    @Transactional
    public void deleteCategory(final Long categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
        category.delete();
    }

    @Override
    public List<CategoryResponseDto> findAllCategoryByUser(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Optional<List<Category>> categoryList = categoryRepository.findByUser(user);

        if (categoryList.isEmpty()) {
            return Collections.emptyList();
        }
        return categoryList.get().stream().map(categoryConverter::toCategoryResponseDto).toList();
    }

    @Override
    public List<CategoryDetailResponseDto> getCategoryDashboard(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Optional<List<Category>> categoryList = categoryRepository.findByUser(user);

        if (categoryList.isEmpty()) {
            return Collections.emptyList();
        }
        return categoryList.get().stream().map(categoryConverter::toCategoryDetailResponseDto).toList();
    }
}
