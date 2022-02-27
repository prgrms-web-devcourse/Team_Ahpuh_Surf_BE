package org.ahpuh.surf.like.converter;

import org.ahpuh.surf.like.entity.Like;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class LikeConverter {

    public Like toEntity(final User user, final Post post) {
        return Like.builder()
                .user(user)
                .post(post)
                .build();
    }

}
