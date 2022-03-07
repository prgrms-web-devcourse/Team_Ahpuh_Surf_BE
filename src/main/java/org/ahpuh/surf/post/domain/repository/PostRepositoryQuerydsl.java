package org.ahpuh.surf.post.domain.repository;

import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.post.dto.response.AllPostResponseDto;
import org.ahpuh.surf.post.dto.response.ExploreResponseDto;
import org.ahpuh.surf.post.dto.response.PostCountResponseDto;
import org.ahpuh.surf.post.dto.response.RecentPostResponseDto;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryQuerydsl {

    List<PostCountResponseDto> findAllDateAndCountBetween(int year, User user);

    List<PostScoreCategoryDto> findAllScoreWithCategoryByUser(User user);

    List<RecentPostResponseDto> findAllRecentPost(Long userId, Pageable page);

    List<RecentPostResponseDto> findAllRecentPostByCursor(Long userId, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

    List<ExploreResponseDto> findFollowingPosts(Long userId, Pageable page);

    List<ExploreResponseDto> findFollowingPostsByCursor(Long userId, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

    List<AllPostResponseDto> findAllPostOfUser(Long userId, Long postUserId, Pageable page);

    List<AllPostResponseDto> findAllPostOfUserByCursor(Long userId, Long postUserId, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

    List<AllPostResponseDto> findAllPostOfCategory(Long userId, Long categoryId, Pageable page);

    List<AllPostResponseDto> findAllPostOfCategoryByCursor(Long userId, Long categoryId, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

}
