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
