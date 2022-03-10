package org.ahpuh.surf.post.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PostConverter {

    public Post toEntity(final User user, final Category category, final PostRequestDto request) {
        return Post.builder()
                .user(user)
                .category(category)
                .selectedDate(LocalDate.parse(request.getSelectedDate()))
                .content(request.getContent())
                .score(request.getScore())
                .build();
    }
}
