package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.user.domain.User;

public class MockFollowFactory {

    public static Follow createMockFollow(final User source, final User target) {
        return Follow.builder()
                .source(source)
                .target(target)
                .build();
    }
}
