package org.ahpuh.surf.post.converter;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.post.dto.*;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostConverter {

    public Post toEntity(final User user, final Category category, final PostRequestDto request) {
        return Post.builder()
                .user(user)
                .category(category)
                .selectedDate(LocalDate.parse(request.getSelectedDate())) // yyyy-mm-dd
                .content(request.getContent())
                .score(request.getScore())
                .fileUrl(request.getFileUrl())
                .build();
    }

    public PostDto toDto(final Post post) {
        return PostDto.builder()
                .postId(post.getPostId())
                .categoryId(post.getCategory().getCategoryId())
                .selectedDate(post.getSelectedDate().toString())
                .content(post.getContent())
                .score(post.getScore())
                .fileUrl(post.getFileUrl())
                .favorite(post.getFavorite())
                .createdAt(post.getCreatedAt().toString())
                .build();
    }

    public PostResponseDto toPostResponseDto(final Post post, final Category category) {
        return PostResponseDto.builder()
                .categoryName(category.getName())
                .colorCode(category.getColorCode())
                .postId(post.getPostId())
                .content(post.getContent())
                .score(post.getScore())
                .fileUrl(post.getFileUrl())
                .selectedDate(post.getSelectedDate().toString())
                .build();
    }

    public List<CategorySimpleDto> sortPostScoresByCategory(
            final List<PostScoreCategoryDto> posts,
            final List<Category> categories) {

        final List<CategorySimpleDto> categorySimpleDtos = categories.stream()
                .map(category -> new CategorySimpleDto(
                        category.getCategoryId(),
                        category.getName(),
                        category.getColorCode(),
                        new ArrayList<>()))
                .collect(Collectors.toList());

        posts.forEach(postScoreCategoryDto -> {
            final Category category = postScoreCategoryDto.getCategory();
            if (categories.contains(category)) {
                categorySimpleDtos.stream()
                        .filter(categorySimpleDto -> categorySimpleDto.getCategoryId().equals(category.getCategoryId()))
                        .findFirst()
                        .map(categorySimpleDto -> categorySimpleDto.getPostScores()
                                .add(PostScoreDto.builder()
                                        .x(postScoreCategoryDto.getSelectedDate())
                                        .y(postScoreCategoryDto.getScore())
                                        .build())
                        );
            } else {
                throw EntityExceptionHandler.CategoryNotFound(category.getCategoryId());
            }

        });

        categorySimpleDtos.removeIf(categorySimpleDto -> categorySimpleDto.getPostScores().size() == 0);

        return categorySimpleDtos;
    }

}
