package org.ahpuh.surf.common.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityExceptionHandler {

    public static IllegalArgumentException CategoryNotFound(final Long categoryId) {
        return new IllegalArgumentException("Category with given id not found. Invalid id is " + categoryId);
    }

    ;

    public static IllegalArgumentException UserNotFound(final Long userId) {
        return new IllegalArgumentException("User with given id not found. Invalid id is " + userId);
    }

    ;

    public static IllegalArgumentException PostNotFound(final Long postId) {
        return new IllegalArgumentException("Post with given id not found. Invalid id is " + postId);
    }

    ;

}
