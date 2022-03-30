package org.ahpuh.surf.category.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.domain.CategoryConverter;
import org.ahpuh.surf.category.domain.repository.CategoryRepository;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.dto.response.AllCategoryByUserResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.common.exception.category.CategoryNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryConverter categoryConverter;

    @Transactional
    public Long createCategory(final Long userId, final CategoryCreateRequestDto categoryDto) {
        final User user = getUser(userId);
        final Category category = categoryConverter.toEntity(user, categoryDto);
        return categoryRepository.save(category)
                .getCategoryId();
    }

    @Transactional
    public void updateCategory(final Long categoryId, final CategoryUpdateRequestDto categoryDto) {
        final Category category = getCategory(categoryId);
        category.update(categoryDto.getName(), categoryDto.getIsPublic(), categoryDto.getColorCode());
    }

    @Transactional
    public void deleteCategory(final Long categoryId) {
        final Category category = getCategory(categoryId);
        categoryRepository.delete(category);
    }

    public List<AllCategoryByUserResponseDto> findAllCategoryByUser(final Long userId) {
        final User user = getUser(userId);
        final List<Category> categoryList = categoryRepository.findByUser(user);

        return categoryList.stream()
                .map(categoryConverter::toCategoryResponseDto)
                .toList();
    }

    public List<CategoryDetailResponseDto> getCategoryDashboard(final Long userId) {
        getUser(userId);
        return categoryRepository.getCategoryDashboard(userId);
    }

    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private Category getCategory(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
    }
}
