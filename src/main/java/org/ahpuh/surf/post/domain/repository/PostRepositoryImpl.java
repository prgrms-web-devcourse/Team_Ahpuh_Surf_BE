package org.ahpuh.surf.post.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.post.dto.PostScoreCategoryDto;
import org.ahpuh.surf.post.dto.QPostScoreCategoryDto;
import org.ahpuh.surf.post.dto.response.*;
import org.ahpuh.surf.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.ahpuh.surf.follow.domain.QFollow.follow;
import static org.ahpuh.surf.like.domain.QLike.like;
import static org.ahpuh.surf.post.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PostReadResponseDto> findPost(final Long postId, final Long userId) {
        return Optional.ofNullable(queryFactory
                .select(new QPostReadResponseDto(
                        post.postId.as("postId"),
                        post.user.userId.as("userId"),
                        post.category.categoryId.as("categoryId"),
                        post.selectedDate.as("selectedDate"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.createdAt.as("createdAt"),
                        post.favorite.as("favorite"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .where(post.postId.eq(postId))
                .fetchOne());
    }

    @Override
    public List<PostCountResponseDto> findAllDateAndCountBetween(final int year, final User user) {
        return queryFactory
                .select(new QPostCountResponseDto(
                        post.selectedDate.as("date"),
                        post.selectedDate.count().as("count")
                ))
                .from(post)
                .where(post.selectedDate.between(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31)),
                        post.user.eq(user))
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
                .where(post.user.eq(user))
                .orderBy(post.category.categoryId.asc(), post.selectedDate.asc())
                .fetch();
    }

    @Override
    public List<RecentPostResponseDto> findAllRecentPost(final Long userId, final Pageable page) {
        return queryFactory
                .select(new QRecentPostResponseDto(
                        post.user.userId.as("userId"),
                        post.user.userName.as("userName"),
                        post.user.profilePhotoUrl.as("profilePhotoUrl"),
                        follow.followId.as("followId"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate.as("selectedDate"),
                        post.createdAt.as("createdAt"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .leftJoin(follow).on(follow.source.userId.eq(userId).and(follow.target.userId.eq(post.user.userId)))
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<RecentPostResponseDto> findAllRecentPostByCursor(final Long userId, final LocalDate selectedDate, final LocalDateTime createdAt, final Pageable page) {
        return queryFactory
                .select(new QRecentPostResponseDto(
                        post.user.userId.as("userId"),
                        post.user.userName.as("userName"),
                        post.user.profilePhotoUrl.as("profilePhotoUrl"),
                        follow.followId.as("followId"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate,
                        post.createdAt.as("createdAt"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .leftJoin(follow).on(follow.source.userId.eq(userId).and(follow.target.userId.eq(post.user.userId)))
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .where((post.selectedDate.before(selectedDate))
                        .or(post.selectedDate.eq(selectedDate).and(post.createdAt.before(createdAt))))
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<ExploreResponseDto> findFollowingPosts(final Long userId, final Pageable page) {
        return queryFactory
                .select(new QExploreResponseDto(
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
                        post.selectedDate.as("selectedDate"),
                        post.createdAt.as("createdAt"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .leftJoin(follow).on(follow.source.userId.eq(userId))
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .where(follow.target.userId.eq(post.user.userId))
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<ExploreResponseDto> findFollowingPostsByCursor(final Long userId, final LocalDate selectedDate, final LocalDateTime createdAt, final Pageable page) {
        return queryFactory
                .select(new QExploreResponseDto(
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
                        post.createdAt.as("createdAt"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .leftJoin(follow).on(follow.source.userId.eq(userId))
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .where(follow.target.userId.eq(post.user.userId)
                        .and(post.selectedDate.before(selectedDate))
                        .or(post.selectedDate.eq(selectedDate).and(post.createdAt.before(createdAt))))
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<AllPostResponseDto> findAllPostOfCategory(final Long userId, final Long categoryId, final Pageable page) {
        return queryFactory
                .select(new QAllPostResponseDto(
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate.as("selectedDate"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .where(post.category.categoryId.eq(categoryId))
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<AllPostResponseDto> findAllPostOfCategoryByCursor(final Long userId, final Long categoryId, final LocalDate selectedDate, final LocalDateTime createdAt, final Pageable page) {
        return queryFactory
                .select(new QAllPostResponseDto(
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate.as("selectedDate"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .where(post.category.categoryId.eq(categoryId)
                        .and(post.selectedDate.before(selectedDate))
                        .or(post.selectedDate.eq(selectedDate).and(post.createdAt.before(createdAt))))
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<AllPostResponseDto> findAllPostOfUser(final Long userId, final Long postUserId, final Pageable page) {
        return queryFactory
                .select(new QAllPostResponseDto(
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate.as("selectedDate"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .where(post.user.userId.eq(postUserId))
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public List<AllPostResponseDto> findAllPostOfUserByCursor(final Long userId, final Long postUserId, final LocalDate selectedDate, final LocalDateTime createdAt, final Pageable page) {
        return queryFactory
                .select(new QAllPostResponseDto(
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.imageUrl.as("imageUrl"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate.as("selectedDate"),
                        like.likeId.as("likeId")
                ))
                .from(post)
                .where(post.user.userId.eq(postUserId)
                        .and(post.selectedDate.before(selectedDate))
                        .or(post.selectedDate.eq(selectedDate).and(post.createdAt.before(createdAt))))
                .leftJoin(like).on(like.user.userId.eq(userId).and(post.postId.eq(like.post.postId)))
                .orderBy(post.selectedDate.desc(), post.createdAt.desc())
                .limit(page.getPageSize())
                .fetch();
    }
}
