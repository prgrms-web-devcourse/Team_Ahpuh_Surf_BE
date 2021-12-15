package org.ahpuh.surf.post.converter;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostRequestDto;
import org.ahpuh.surf.post.dto.PostRequest;
import org.ahpuh.surf.post.dto.PostResponseDto;
import org.ahpuh.surf.post.entity.Post;
import org.ahpuh.surf.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PostConverter {

    public static Post toEntity(final User user, final Category category, final PostRequestDto request) {
        return Post.builder()
                .user(user)
                .category(category)
                .selectedDate(LocalDate.parse(request.getSelectedDate())) // yyyy-mm-dd
                .content(request.getContent())
                .score(request.getScore())
                .fileUrl(request.getFileUrl())
                .build();
    }

    public static PostDto toDto(final Post post) {
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

    public static PostResponseDto toPostResponseDto(final Post post, final Category category) {
        return PostResponseDto.builder()
                .categoryName(category.getName())
                .colorCode(category.getColorCode())
                .postId(post.getId())
                .content(post.getContent())
                .score(post.getScore())
                .fileUrl(post.getFileUrl())
                .selectedDate(post.getSelectedDate().toString())
                .build();
    }

}
