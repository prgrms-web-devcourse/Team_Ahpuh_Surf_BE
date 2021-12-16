package org.ahpuh.surf.post.repository;

import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.user.entity.User;

import java.util.List;

public interface PostRepositoryQuerydsl {

    List<FollowingPostDto> findFollowingPosts(Long userId);

    List<PostCountDto> findAllDateAndCountBetween(int year, User user);

    List<PostScoreCategoryDto> findAllScoreWithCategoryByUser(User user);

}
