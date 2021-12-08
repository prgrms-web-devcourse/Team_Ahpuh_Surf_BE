package org.ahpuh.surf.post.service;

import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostIdResponseDto;
import org.ahpuh.surf.post.dto.PostRequestDto;

public interface PostService {

    PostIdResponseDto create(PostRequestDto request);

    PostIdResponseDto update(Long postId, PostRequestDto request);

    PostDto readOne(Long postId);

    void delete(Long postID);

}
