package org.ahpuh.surf.common.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityExceptionHandler {

    public static IllegalArgumentException CategoryNotFound(final Long categoryId) {
        return new IllegalArgumentException("Category with given id not found. Invalid id is " + categoryId);
    }

    public static IllegalArgumentException UserNotFound(final Long userId) {
        return new IllegalArgumentException("User with given id not found. Invalid id is " + userId);
    }

    public static IllegalArgumentException UserNotFound(final String email) {
        return new IllegalArgumentException("User with given email not found. Invalid email is " + email);
    }

    public static IllegalArgumentException PostNotFound(final Long postId) {
        return new IllegalArgumentException("Post with given id not found. Invalid id is " + postId);
    }

    public static IllegalArgumentException FollowNotFound(final Long followId) {
        return new IllegalArgumentException("No Follow for id : " + followId);
    }

    public static IllegalArgumentException FollowingNotFound() {
        return new IllegalArgumentException("삭제하려는 팔로우 기록이 없습니다.");
    }

}
