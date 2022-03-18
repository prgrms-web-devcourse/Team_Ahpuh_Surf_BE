package org.ahpuh.surf.follow.domain;

import org.ahpuh.surf.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class FollowConverter {

    public Follow toEntity(final User source, final User target) {
        return Follow.builder()
                .source(source)
                .target(target)
                .build();
    }
}
