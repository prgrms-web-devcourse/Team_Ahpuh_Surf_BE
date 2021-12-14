package org.ahpuh.surf.post.repository;

import org.ahpuh.surf.post.dto.FollowingPostDto;

import java.util.List;

public interface PostRepositoryQuerydsl {

    List<FollowingPostDto> followingPosts(Long userId);

}
