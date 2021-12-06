package org.ahpuh.backend.post.service;

import org.ahpuh.backend.post.dto.PostDto;
import org.ahpuh.backend.post.dto.PostIdResponse;
import org.ahpuh.backend.post.dto.PostRequest;

public interface PostService {

    PostIdResponse create(PostRequest request);

    PostIdResponse update(Long postId, PostRequest request);

    PostDto readOne(Long postId);

    void delete(Long postID);

}
