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

    List<ExploreDto> findFollowingPosts(Long userId, Pageable page);

    List<ExploreDto> findNextFollowingPosts(Long userId, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

    List<PostCountDto> findAllDateAndCountBetween(int year, User user);

    List<PostScoreCategoryDto> findAllScoreWithCategoryByUser(User user);

    List<AllPostResponseDto> findAllPostResponse(Long userId, Long postUserId, Pageable page);

    List<AllPostResponseDto> findAllPostResponseByCursor(Long userId, Long postUserId, LocalDate selectedDate, LocalDateTime createdAt, Pageable page);

}
