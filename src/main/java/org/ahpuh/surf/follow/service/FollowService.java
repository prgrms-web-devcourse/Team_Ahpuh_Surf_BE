package org.ahpuh.surf.follow.service;

import org.ahpuh.surf.follow.dto.FollowUserDto;

import java.util.List;

public interface FollowService {

    Long follow(Long userId, Long followUserId);

    void unfollow(Long followId);

    List<FollowUserDto> findFollowingList(Long userId);

    List<FollowUserDto> findFollowList(Long userId);

}
