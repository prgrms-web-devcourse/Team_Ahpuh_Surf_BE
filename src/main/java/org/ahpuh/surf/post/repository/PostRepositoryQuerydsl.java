package org.ahpuh.surf.post.repository;

import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryQuerydsl {

    List<FollowingPostDto> findFollowingPosts(Long userId, Pageable page);

    List<FollowingPostDto> findNextFollowingPosts(Long userId, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

    List<PostCountDto> findAllDateAndCountBetween(int year, User user);

    List<PostScoreCategoryDto> findAllScoreWithCategoryByUser(User user);

}
