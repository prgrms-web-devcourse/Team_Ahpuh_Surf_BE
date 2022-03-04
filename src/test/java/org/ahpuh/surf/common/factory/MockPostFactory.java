package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.post.domain.Post;
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

    public static Post createMockScoredPost(final User user, final Category category, final int score) {
        return new Post(user, category, null, null, score);
    }
}
