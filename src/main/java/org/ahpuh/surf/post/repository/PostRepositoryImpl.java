package org.ahpuh.surf.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.ahpuh.surf.post.dto.FollowingPostDto;
import org.ahpuh.surf.post.dto.QFollowingPostDto;

import javax.persistence.EntityManager;
import java.util.List;

import static org.ahpuh.surf.follow.entity.QFollow.follow;
import static org.ahpuh.surf.post.entity.QPost.post;

public class PostRepositoryImpl implements PostRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(final EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<FollowingPostDto> followingPosts(final Long userId) {
        return queryFactory
                .select(new QFollowingPostDto(
                        post.user.userId.as("userId"),
                        post.category.name.as("categoryName"),
                        post.category.colorCode.as("colorCode"),
                        post.id.as("postId"),
                        post.content.as("content"),
                        post.score.as("score"),
                        post.fileUrl.as("fileUrl"),
                        post.selectedDate,
                        post.updatedAt.as("updatedAt")
                ))
                .from(post)
                .leftJoin(follow).on(follow.user.userId.eq(userId))
                .where(follow.followedUser.userId.eq(post.user.userId))
                .groupBy(post.id, follow.followId)
                .orderBy(post.updatedAt.desc())
                .fetch();
    }

}
