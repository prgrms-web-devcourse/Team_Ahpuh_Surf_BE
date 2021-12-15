package org.ahpuh.surf.post.service;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.common.response.CursorResult;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostRequestDto;

import java.util.List;
import org.ahpuh.surf.post.dto.PostResponseDto;
import org.springframework.data.domain.Pageable;


public interface PostService {

    Long create(Long userId, PostRequestDto request);

    Long update(Long postId, PostRequestDto request);

    PostDto readOne(Long postId);

    void delete(Long postID);

    Long clickFavorite(final Long userId, final Long postId);

    List<PostCountDto> getCountsPerDayWithYear(int year, Long userId);

    List<CategorySimpleDto> getScoresWithCategoryByUserId(Long userId);

    List<FollowingPostDto> explore(Long userId);

    List<PostResponseDto> getPost(Long userId, Integer year, Integer month);

    CursorResult<PostResponseDto> getAllPost(Long userId, Long cursorId, Pageable page);

    CursorResult<PostResponseDto> getAllPostByCategory(Long userId, Long categoryId, Long cursorId, Pageable page);

    int getRecentScore(Long categoryId);
}
