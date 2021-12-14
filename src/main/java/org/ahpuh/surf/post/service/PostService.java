package org.ahpuh.surf.post.service;

import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostRequestDto;

public interface PostService {

    Long create(Long userId, PostRequestDto request);

    Long update(Long postId, PostRequestDto request);

    PostDto readOne(Long postId);

    void delete(Long postID);

    Long clickFavorite(final Long userId, final Long postId);

}
