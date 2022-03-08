package org.ahpuh.surf.post.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.exception.category.CategoryNotFoundException;
import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.post.dto.PostScoreDto;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.PostResponseDto;
import org.ahpuh.surf.user.domain.User;
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
                .build();
    }

    public PostResponseDto toPostResponseDto(final Post post, final Category category) {
        return PostResponseDto.builder()
                .categoryName(category.getName())
                .colorCode(category.getColorCode())
                .postId(post.getPostId())
                .content(post.getContent())
                .score(post.getScore())
                .imageUrl(post.getImageUrl())
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
                                        .build()));
            } else {
                throw new CategoryNotFoundException();
            }
        });

        return categorySimpleDtos;
    }
}
