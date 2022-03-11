package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.cursor.CursorResult;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.dto.PostScoreDto;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.*;
import org.ahpuh.surf.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MockPostFactory {

    public static Post createMockPost(final User user, final Category category) {
        return Post.builder()
                .user(user)
                .category(category)
                .selectedDate(LocalDate.now())
                .content("postContent")
                .score(100)
                .build();
    }

    public static Post createMockPostWithContent(final User user, final Category category, final String content) {
        return Post.builder()
                .user(user)
                .category(category)
                .selectedDate(LocalDate.of(2022, 1, 1))
                .content(content)
                .score(100)
                .build();
    }

    public static Post createMockPostWithScore(final User user, final Category category, final int score) {
        return Post.builder()
                .user(user)
                .category(category)
                .selectedDate(LocalDate.of(2022, 1, 1))
                .content("content")
                .score(score)
                .build();
    }

    public static Post createMockPostWithSelectedDate(final User user, final Category category, final LocalDate selectedDate) {
        return Post.builder()
                .user(user)
                .category(category)
                .selectedDate(selectedDate)
                .content("postContent")
                .score(100)
                .build();
    }

    public static PostRequestDto createMockPostRequestDto() {
        return PostRequestDto.builder()
                .categoryId(1L)
                .selectedDate(LocalDate.now().toString())
                .content("content")
                .score(100)
                .build();
    }

    public static PostReadResponseDto createMockPostReadResponseDto() {
        return PostReadResponseDto.builder()
                .postId(1L)
                .userId(1L)
                .categoryId(1L)
                .selectedDate(LocalDate.now())
                .content("content")
                .score(100)
                .imageUrl("imageUrl")
                .fileUrl("fileUrl")
                .createdAt(LocalDateTime.now())
                .favorite(false)
                .likeId(1L)
                .build()
                .likeCheck();
    }

    public static List<PostsOfMonthResponseDto> createMockPostsOfMonthResponseDtos() {
        final PostsOfMonthResponseDto response = PostsOfMonthResponseDto.builder()
                .categoryName("categoryName")
                .colorCode("#000000")
                .postId(1L)
                .content("content")
                .score(100)
                .imageUrl("imageUrl")
                .fileUrl("fileUrl")
                .selectedDate(LocalDate.of(2022, 2, 1))
                .build();
        return List.of(response, response);
    }

    public static PostsRecentScoreResponseDto createMockPostsRecentScoreResponseDto() {
        return new PostsRecentScoreResponseDto(100);
    }

    public static List<PostCountResponseDto> createMockPostCountResponseDto() {
        final PostCountResponseDto response = PostCountResponseDto.builder()
                .date(LocalDate.now())
                .count(3L)
                .build();
        return List.of(response, response);
    }

    public static List<CategorySimpleDto> createMockCategorySimpleDto() {
        final CategorySimpleDto response = CategorySimpleDto.builder()
                .categoryId(1L)
                .categoryName("categoryName")
                .colorCode("#000000")
                .postScores(
                        List.of(PostScoreDto.builder()
                                .selectedDate(LocalDate.now())
                                .score(100)
                                .build()))
                .build();
        return List.of(response, response);
    }

    public static CursorResult<RecentPostResponseDto> createMockRecentAllPosts() {
        final RecentPostResponseDto response = RecentPostResponseDto.builder()
                .userId(1L)
                .userName("userName")
                .profilePhotoUrl("profilePhotoUrl")
                .followId(1L)
                .categoryName("categoryName")
                .colorCode("#000000")
                .postId(1L)
                .content("content")
                .score(100)
                .imageUrl("imageUrl")
                .fileUrl("fileUrl")
                .selectedDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .likeId(1L)
                .build();
        response.likeCheck();
        return new CursorResult<>(List.of(response, response), false);
    }

    public static CursorResult<ExploreResponseDto> createMockExploreResponseDto() {
        final ExploreResponseDto response = ExploreResponseDto.builder()
                .userId(1L)
                .userName("userName")
                .profilePhotoUrl("profilePhotoUrl")
                .categoryName("categoryName")
                .colorCode("#000000")
                .postId(1L)
                .content("content")
                .score(100)
                .imageUrl("imageUrl")
                .fileUrl("fileUrl")
                .selectedDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .likeId(1L)
                .build();
        response.likeCheck();
        return new CursorResult<>(List.of(response, response), false);
    }

    public static CursorResult<AllPostResponseDto> createMockAllPostResponseDto() {
        final AllPostResponseDto response = AllPostResponseDto.builder()
                .categoryName("categoryName")
                .colorCode("#000000")
                .postId(1L)
                .content("content")
                .score(100)
                .imageUrl("imageUrl")
                .fileUrl("fileUrl")
                .selectedDate(LocalDate.now())
                .likeId(1L)
                .build();
        response.likeCheck();
        return new CursorResult<>(List.of(response, response), false);
    }
}
