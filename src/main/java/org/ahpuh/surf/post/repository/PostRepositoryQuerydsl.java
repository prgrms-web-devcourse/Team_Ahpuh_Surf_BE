package org.ahpuh.surf.post.repository;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.user.entity.User;

import java.util.List;

public interface PostRepositoryQuerydsl {

    List<FollowingPostDto> followingPosts(Long userId);

    List<PostCountDto> findAllDateAndCountBetween(int year);

    List<CategorySimpleDto> findAllScoreWithCategoryByUser(User user);

}
