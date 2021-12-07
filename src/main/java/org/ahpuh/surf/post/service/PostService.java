package org.ahpuh.surf.post.service;

import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostIdResponse;
import org.ahpuh.surf.post.dto.PostRequest;

public interface PostService {

    PostIdResponse create(PostRequest request);

    PostIdResponse update(Long postId, PostRequest request);

    PostDto readOne(Long postId);

    void delete(Long postID);

}
