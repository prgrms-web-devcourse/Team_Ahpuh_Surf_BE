package org.ahpuh.surf.like.service;

public interface LikeService {

    Long like(Long userId, Long postId);

    void unlike(Long likeId);

}
