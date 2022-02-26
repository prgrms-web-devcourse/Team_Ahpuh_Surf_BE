package org.ahpuh.surf.post.converter;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.common.s3.S3ServiceImpl.FileStatus;
import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.post.dto.PostScoreDto;
import org.ahpuh.surf.post.dto.RecentPostDto;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.AllPostResponseDto;
import org.ahpuh.surf.post.dto.response.PostReadResponseDto;
import org.ahpuh.surf.post.dto.response.PostResponseDto;
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
                .build();
    }

    public PostReadResponseDto toPostReadResponseDto(final Post post, final Long myId) {
        final PostReadResponseDto dto = PostReadResponseDto.builder()
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
        post.getLikes()
                .stream()
                .filter(like -> like.getUser().getUserId().equals(myId))
                .findFirst()
                .ifPresent(likeEntity -> dto.setLiked(likeEntity.getLikeId()));
        return dto;
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

    public AllPostResponseDto toAllPostResponseDto(final Post post, final Long myId) {
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
        post.getLikes()
                .stream()
                .filter(like -> like.getUser().getUserId().equals(myId))
                .findFirst()
                .ifPresent(likeEntity -> allPostResponseDto.setLiked(likeEntity.getLikeId()));
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

    public RecentPostDto toRecentAllPosts(final Post post, final User me) {
        final RecentPostDto recentPostDto = RecentPostDto.builder()
                .userId(post.getUser().getUserId())
                .userName(post.getUser().getUserName())
                .profilePhotoUrl(post.getUser().getProfilePhotoUrl())
                .categoryName(post.getCategory().getName())
                .colorCode(post.getCategory().getColorCode())
                .postId(post.getPostId())
                .content(post.getContent())
                .score(post.getScore())
                .selectedDate(post.getSelectedDate())
                .createdAt(post.getCreatedAt())
                .build();
        post.getLikes()
                .stream()
                .filter(like -> like.getUser().equals(me))
                .findFirst()
                .ifPresent(like -> recentPostDto.setLiked(like.getLikeId()));
        if (post.getUser()
                .getFollowers()
                .stream()
                .anyMatch(follow -> follow.getUser().equals(me))) {
            recentPostDto.checkFollowed();
        }
        return recentPostDto;
    }
}
