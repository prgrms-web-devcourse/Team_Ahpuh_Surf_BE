package org.ahpuh.surf.like.converter;

import org.ahpuh.surf.like.entity.Like;
import org.ahpuh.surf.post.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class LikeConverter {

    public Like toEntity(final Long userId, final Post post) {
        return Like.builder()
                .userId(userId)
                .post(post)
                .build();
    }

}
