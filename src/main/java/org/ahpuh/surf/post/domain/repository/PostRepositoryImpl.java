package org.ahpuh.surf.post.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.post.dto.*;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.ahpuh.surf.follow.entity.QFollow.follow;
import static org.ahpuh.surf.post.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ExploreDto> findFollowingPosts(final Long userId, final Pageable page) {
        return queryFactory
                .select(new QExploreDto(
                        post.user.userId.as("userId"),
                        post.user.userName.as("userName"),
                        post.user.profilePhotoUrl.as("profilePhotoUrl"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate,
                        post.createdAt.as("createdAt")
                ))
                .from(post)
                .leftJoin(follow).on(follow.user.userId.eq(userId))
                .where(
                        follow.followedUser.userId.eq(post.user.userId),
                        post.isDeleted.eq(false)
                )
                .groupBy(post.postId, follow.followId)
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<ExploreDto> findNextFollowingPosts(final Long userId, final LocalDate selectedDate, final LocalDateTime createdAt, final Pageable page) {
        return queryFactory
                .select(new QExploreDto(
                        post.user.userId.as("userId"),
                        post.user.userName.as("userName"),
                        post.user.profilePhotoUrl.as("profilePhotoUrl"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate,
                        post.createdAt.as("createdAt")
                ))
                .from(post)
                .leftJoin(follow).on(follow.user.userId.eq(userId))
                .where(
                        follow.followedUser.userId.eq(post.user.userId),
                        post.isDeleted.eq(false),
                        post.selectedDate.loe(selectedDate),
                        post.createdAt.before(createdAt)
                )
                .groupBy(post.postId, follow.followId)
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<PostCountDto> findAllDateAndCountBetween(final int year, final User user) {
        return queryFactory
                .select(new QPostCountDto(
                        post.selectedDate.as("date"),
                        post.selectedDate.count().as("count")))
                .from(post)
                .where(post.selectedDate.between(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31)),
                        post.user.eq(user), post.isDeleted.eq(false))
                .groupBy(post.selectedDate)
                .orderBy(post.selectedDate.asc())
                .fetch();
    }

    @Override
    public List<PostScoreCategoryDto> findAllScoreWithCategoryByUser(final User user) {
        return queryFactory
                .select(new QPostScoreCategoryDto(
                        post.category.as("category"),
                        post.selectedDate.as("selectedDate"),
                        post.score.as("score")
                ))
                .from(post)
                .where(post.user.eq(user), post.isDeleted.eq(false))
                .orderBy(post.category.categoryId.asc(), post.selectedDate.asc())
                .fetch();
    }

}
