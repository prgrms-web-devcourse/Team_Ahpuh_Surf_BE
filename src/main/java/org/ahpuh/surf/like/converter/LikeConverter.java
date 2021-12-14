package org.ahpuh.surf.like.converter;

import org.ahpuh.surf.like.entity.Like;
import org.springframework.stereotype.Component;

@Component
public class LikeConverter {

    public Like toEntity(final Long userId, final Long postId) {
        return Like.builder()
                .userId(userId)
                .postId(postId)
                .build();
    }

}
