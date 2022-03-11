package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.User;

public class MockLikeFactory {

    public static Like createMockLike(final User user, final Post post) {
        return Like.builder()
                .user(user)
                .post(post)
                .build();
    }
}
