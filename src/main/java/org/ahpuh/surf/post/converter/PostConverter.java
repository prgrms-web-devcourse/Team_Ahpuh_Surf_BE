package org.ahpuh.surf.post.converter;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.common.s3.S3Service.FileStatus;
import org.ahpuh.surf.like.entity.Like;
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

    public Post toEntity(final User user, final Category category, final PostRequestDto request, final FileStatus fileStatus) {
        Post postEntity = Post.builder()
                .user(user)
                .category(category)
                .selectedDate(LocalDate.parse(request.getSelectedDate())) // yyyy-mm-dd
                .content(request.getContent())
                .score(request.getScore())
                .build();
        if (fileStatus != null) {
            postEntity.editFile(fileStatus);
        }
        return postEntity;
    }

    public PostDto toDto(final Post post, final Optional<Like> like) {
        final PostDto dto = PostDto.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .categoryId(post.getCategory().getCategoryId())
                .selectedDate(post.getSelectedDate().toString())
                .content(post.getContent())
                .score(post.getScore())
                .imageUrl(post.getImageUrl())
                .fileUrl(post.getFileUrl())
                .favorite(post.getFavorite())
                .createdAt(post.getCreatedAt().toString())
                .build();
        like.ifPresent(likeEntity -> dto.setLiked(likeEntity.getLikeId()));
        return dto;
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

    public AllPostResponseDto toAllPostResponseDto(final Post post, final Optional<Like> like) {
        final AllPostResponseDto allPostResponseDto = AllPostResponseDto.builder()
                .categoryName(post.getCategory().getName())
                .colorCode(post.getCategory().getColorCode())
                .postId(post.getPostId())
                .content(post.getContent())
                .score(post.getScore())
                .imageUrl(post.getImageUrl())
                .fileUrl(post.getFileUrl())
                .selectedDate(post.getSelectedDate().toString())
                .build();
        like.ifPresent(likeEntity -> allPostResponseDto.setLiked(likeEntity.getLikeId()));
        return allPostResponseDto;
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
