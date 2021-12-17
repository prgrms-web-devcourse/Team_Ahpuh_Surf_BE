package org.ahpuh.surf.common.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

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

    public static IllegalArgumentException FollowNotFound() {
        return new IllegalArgumentException("삭제하려는 팔로우 기록이 없습니다.");
    }

    public static IllegalArgumentException FollowingNotFound() {
        return new IllegalArgumentException("삭제하려는 팔로우 기록이 없습니다.");
    }

    public static IllegalArgumentException LikeNotFound(final Long likeId) {
        return new IllegalArgumentException("좋아요한 기록이 없습니다." + likeId);
    }

    public static IllegalArgumentException UserNotMatching(final Long userId, final Long requestUserId) {
        return new IllegalArgumentException(
                MessageFormat.format("로그인한 회원 id {0}와 요청한 회원의 id {1}가 일치하지 않습니다.", userId, requestUserId)
        );
    }
}
