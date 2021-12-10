package org.ahpuh.surf.post.converter;

import org.ahpuh.surf.category.entity.Category;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostRequestDto;
import org.ahpuh.surf.post.entity.Post;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PostConverter {

    public static Post toEntity(final Category category, final PostRequestDto request) {
        return Post.builder()
                .category(category)
                .selectedDate(LocalDate.parse(request.getSelectedDate())) // yyyy-mm-dd
                .content(request.getContent())
                .score(request.getScore())
                .fileUrl(request.getFileUrl())
                .build();
    }

    public static PostDto toDto(final Post post) {
        return PostDto.builder()
                .postId(post.getId())
                .categoryId(post.getCategory().getCategoryId())
                .selectedDate(post.getSelectedDate().toString())
                .content(post.getContent())
                .score(post.getScore())
                .fileUrl(post.getFileUrl())
                .build();
    }

}
