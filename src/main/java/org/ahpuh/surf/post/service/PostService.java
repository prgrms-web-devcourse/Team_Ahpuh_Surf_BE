package org.ahpuh.surf.post.service;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.response.CursorResult;
import org.ahpuh.surf.common.s3.S3ServiceImpl.FileStatus;
import org.ahpuh.surf.post.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostService {

    Long create(Long userId, PostRequestDto request, FileStatus fileStatus);

    Long update(Long postId, PostRequestDto request, FileStatus fileStatus);

    PostDto readOne(Long myId, Long postId);

    void delete(Long postID);

    Long clickFavorite(Long userId, Long postId);

    List<PostCountDto> getCountsPerDayWithYear(int year, Long userId);

    List<CategorySimpleDto> getScoresWithCategoryByUserId(Long userId);

    CursorResult<ExploreDto> followingExplore(Long userId, Long cursorId, Pageable page);

    List<PostResponseDto> getPost(Long userId, Integer year, Integer month);

    CursorResult<AllPostResponseDto> getAllPost(Long myId, Long userId, Long cursorId, Pageable page);

    CursorResult<AllPostResponseDto> getAllPostByCategory(Long myId, Long userId, Long categoryId, Long cursorId, Pageable page);

    int getRecentScore(Long categoryId);

    List<ExploreDto> recentAllPosts(Long myId);

}
