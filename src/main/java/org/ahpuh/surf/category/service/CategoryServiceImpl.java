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
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.post.repository.PostRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CategoryConverter categoryConverter;

    @Override
    @Transactional
    public Long createCategory(final Long userId, final CategoryCreateRequestDto categoryDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityExceptionHandler.UserNotFound(userId));
        final Category category = categoryConverter.toEntity(user, categoryDto);

        return categoryRepository.save(category).getCategoryId();
    }

    @Override
    @Transactional
    public Long updateCategory(final Long categoryId, final CategoryUpdateRequestDto categoryDto) {
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> EntityExceptionHandler.CategoryNotFound(categoryId));
        category.update(categoryDto.getName(), categoryDto.getIsPublic(), categoryDto.getColorCode());

        return category.getCategoryId();
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
        final List<Category> categoryList = categoryRepository.findByUser(user);

        return categoryList.stream()
                .map(categoryConverter::toCategoryResponseDto)
                .toList();
    }

    @Override
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
