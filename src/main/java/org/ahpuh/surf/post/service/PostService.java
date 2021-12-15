package org.ahpuh.surf.post.service;

import org.ahpuh.surf.common.response.CursorResult;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostIdResponse;
import org.ahpuh.surf.post.dto.PostRequest;
import org.ahpuh.surf.post.dto.PostResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    PostIdResponse create(PostRequest request);

    PostIdResponse update(Long postId, PostRequest request);

    PostDto readOne(Long postId);

    void delete(Long postID);

    List<PostResponseDto> getPost(Long userId, Integer year, Integer month);

    CursorResult<PostResponseDto> getAllPost(Long userId, Long cursorId, Pageable page);

    CursorResult<PostResponseDto> getAllPostByCategory(Long userId, Long categoryId, Long cursorId, Pageable page);

}
