package org.ahpuh.surf.follow.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.follow.dto.response.FollowUserResponseDto;
import org.ahpuh.surf.follow.dto.response.QFollowUserResponseDto;

import java.util.List;
import java.util.Optional;

import static org.ahpuh.surf.follow.domain.QFollow.follow;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Follow> findBySourceIdAndTargetId(final Long sourceId, final Long targetId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(follow)
                .where(follow.source.userId.eq(sourceId)
                        .and(follow.target.userId.eq(targetId)))
                .fetchOne());
    }

    @Override
    public List<FollowUserResponseDto> findBySourceId(final Long sourceId) {
        return queryFactory
                .select(new QFollowUserResponseDto(
                        follow.target.userId.as("userId"),
                        follow.target.userName.as("userName"),
                        follow.target.profilePhotoUrl.as("profilePhotoUrl")))
                .from(follow)
                .where(follow.source.userId.eq(sourceId))
                .orderBy(follow.followId.desc())
                .fetch();
    }

    @Override
    public List<FollowUserResponseDto> findByTargetId(final Long targetId) {
        return queryFactory
                .select(new QFollowUserResponseDto(
                        follow.source.userId.as("userId"),
                        follow.source.userName.as("userName"),
                        follow.source.profilePhotoUrl.as("profilePhotoUrl")))
                .from(follow)
                .where(follow.target.userId.eq(targetId))
                .orderBy(follow.followId.desc())
                .fetch();
    }
}
