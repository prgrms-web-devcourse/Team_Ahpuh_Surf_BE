package org.ahpuh.surf.post.service;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.post.dto.PostDto;
import org.ahpuh.surf.post.dto.PostRequestDto;

import java.util.List;

public interface PostService {

    Long create(Long userId, PostRequestDto request);

    Long update(Long postId, PostRequestDto request);

    PostDto readOne(Long postId);

    void delete(Long postID);

    Long clickFavorite(final Long userId, final Long postId);

    List<FollowingPostDto> explore(Long userId);

    List<PostCountDto> getCountsPerDayWithYear(int year, Long userId);

    List<CategorySimpleDto> getScoresWithCategoryByUserId(Long userId);

}
