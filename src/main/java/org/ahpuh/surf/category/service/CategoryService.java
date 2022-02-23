package org.ahpuh.surf.category.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.converter.CategoryConverter;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryResponseDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.category.repository.CategoryRepository;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryConverter categoryConverter;

    @Transactional
    public Long createCategory(final Long userId, final CategoryCreateRequestDto categoryDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Category category = categoryConverter.toEntity(user, categoryDto);

        return categoryRepository.save(category).getCategoryId();
    }

    @Transactional
    public Long updateCategory(final Long categoryId, final CategoryUpdateRequestDto categoryDto) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
        category.update(categoryDto.getName(), categoryDto.getIsPublic(), categoryDto.getColorCode());

        return category.getCategoryId();
    }

    @Transactional
    public void deleteCategory(final Long categoryId) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
        categoryRepository.delete(category);
    }

    public List<CategoryResponseDto> findAllCategoryByUser(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final List<Category> categoryList = categoryRepository.findByUser(user);

        return categoryList.stream()
                .map(categoryConverter::toCategoryResponseDto)
                .toList();
    }

    public List<CategoryDetailResponseDto> getCategoryDashboard(final Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final List<Category> categoryList = categoryRepository.findByUser(user);

        return categoryList.stream()
                .map((Category category) -> categoryConverter.toCategoryDetailResponseDto(category, (int) getAverageScore(category)))
                .toList();
    }

    private double getAverageScore(final Category category) {
        return postRepository.findByCategory(category).stream()
                .mapToInt(Post::getScore)
                .average().orElse(0);
    }
}
