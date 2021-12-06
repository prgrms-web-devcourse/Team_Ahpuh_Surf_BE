package org.ahpuh.backend.post.converter;

import org.ahpuh.backend.category.entity.Category;
import org.ahpuh.backend.post.dto.PostDto;
import org.ahpuh.backend.post.entity.Post;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PostConverter {

    public static Post toEntity(final Category category, final String selectedDate, final String title,
                                final String content, final int score, final String fileUrl) {
        return Post.builder()
                .category(category)
                .selectedDate(LocalDate.parse(selectedDate)) // yyyy-mm-dd
                .title(title)
                .content(content)
                .score(score)
                .fileUrl(fileUrl)
                .build();
    }

    public static PostDto toDto(final Post post) {
        return PostDto.builder()
                .postId(post.getId())
                .categoryId(post.getCategory().getId())
                .selectedDate(post.getSelectedDate().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .score(post.getScore())
                .fileUrl(post.getFileUrl())
                .build();
    }

}
