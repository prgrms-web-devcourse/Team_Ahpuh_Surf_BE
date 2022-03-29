package org.ahpuh.surf.post.domain.repository;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.post.dto.response.*;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryQuerydsl {

    Optional<PostReadResponseDto> findPost(Long postId, Long userId);

    List<PostsOfMonthResponseDto> findPostsOfMonth(Long userId, LocalDate startDate, LocalDate endDate);

    List<PostCountResponseDto> findEachDateAndCountOfYearByUser(int year, User user);

    List<CategorySimpleDto> findAllScoreWithCategoryByUser(Long userId);

    List<RecentPostResponseDto> findAllRecentPost(Long userId, Pageable page);

    List<RecentPostResponseDto> findAllRecentPostByCursor(Long userId, Long cursorPostId, LocalDate selectedDate, Pageable page);

    List<ExploreResponseDto> findFollowingPosts(Long userId, Pageable page);

    List<ExploreResponseDto> findFollowingPostsByCursor(Long userId, Long cursorPostId, LocalDate selectedDate, Pageable page);

    List<AllPostResponseDto> findAllPostOfUser(Long userId, Long postUserId, Pageable page);

    List<AllPostResponseDto> findAllPostOfUserByCursor(Long userId, Long postUserId, Long cursorPostId, LocalDate selectedDate, Pageable page);

    List<AllPostResponseDto> findAllPostOfCategory(Long userId, Long categoryId, Pageable page);

    List<AllPostResponseDto> findAllPostOfCategoryByCursor(Long userId, Long categoryId, Long cursorPostId, LocalDate selectedDate, Pageable page);

}
