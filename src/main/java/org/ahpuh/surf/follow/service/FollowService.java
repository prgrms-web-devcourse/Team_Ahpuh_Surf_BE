package org.ahpuh.surf.follow.service;

import org.ahpuh.surf.follow.dto.FollowUserDto;

import java.util.List;

public interface FollowService {

    Long follow(Long userId, Long followUserId);

    void unfollow(Long myId, Long userId);

    List<FollowUserDto> findFollowerList(Long userId);

    List<FollowUserDto> findFollowingList(Long userId);

}
