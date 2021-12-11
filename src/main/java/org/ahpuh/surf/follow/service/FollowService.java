package org.ahpuh.surf.follow.service;

public interface FollowService {

    Long follow(Long userId, Long followUserId);

    void unfollow(Long followId);

}
