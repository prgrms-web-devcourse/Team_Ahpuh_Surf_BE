package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.PostCountResponseDto;
import org.ahpuh.surf.user.domain.User;

import java.time.LocalDate;

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

    public static PostCountResponseDto createMockPostCountDto() {
        return PostCountResponseDto.builder()
                .date(LocalDate.now())
                .count(3L)
                .build();
    }
}
