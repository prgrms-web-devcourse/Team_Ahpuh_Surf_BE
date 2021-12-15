package org.ahpuh.surf.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.PostCountDto;
import org.ahpuh.surf.post.dto.QFollowingPostDto;
import org.ahpuh.surf.post.dto.QPostCountDto;
import org.ahpuh.surf.user.entity.User;

import java.time.LocalDate;
import java.util.List;

import static org.ahpuh.surf.follow.entity.QFollow.follow;
import static org.ahpuh.surf.post.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FollowingPostDto> followingPosts(final Long userId) {
        return queryFactory
                .select(new QFollowingPostDto(
                        post.user.userId.as("userId"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.postId.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate,
                        post.updatedAt.as("updatedAt")
                ))
                .from(post)
                .leftJoin(follow).on(follow.user.userId.eq(userId))
                .where(follow.followedUser.userId.eq(post.user.userId))
                .groupBy(post.postId, follow.followId)
                .orderBy(post.updatedAt.desc())
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
                        post.user.eq(user))
                .groupBy(post.selectedDate)
                .orderBy(post.selectedDate.asc())
                .fetch()
                ;
    }

    @Override
    public List<CategorySimpleDto> findAllScoreWithCategoryByUser(final User user) {
        // TODO: query 수정
        /*return queryFactory
                .select(new QCategorySimpleDto(
                        post.category.categoryId.as("categoryId"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(new QPostScoreDto(post.selectedDate, post.score))
                                        .from(post)
                                        .where(post.category.eq()),
                                "posts")
                ))
                .from(post)
                .where(post.selectedDate.after(LocalDate.now().minusYears(1)),  // 현재부터 1년 전
                        post.user.eq(user))
                .groupBy(post.category)
                .fetch()
                ;*/
        return null;
    }

}
