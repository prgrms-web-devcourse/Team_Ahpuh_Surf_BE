package org.ahpuh.surf.category.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.converter.CategoryConverter;
import org.ahpuh.surf.category.dto.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.CategoryResponseDto;
import org.ahpuh.surf.category.dto.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
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
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> findAllCategoryByUser(final Long userId) {
        return null;
    }
}
