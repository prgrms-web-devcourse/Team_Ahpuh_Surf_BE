package org.ahpuh.surf.follow.domain.repository;

import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.follow.dto.response.FollowUserResponseDto;

import java.util.List;
import java.util.Optional;

public interface FollowRepositoryQuerydsl {

    Optional<Follow> findBySourceIdAndTargetId(Long sourceId, Long targetId);

    List<FollowUserResponseDto> findBySourceId(Long sourceId);

    List<FollowUserResponseDto> findByTargetId(Long targetId);

}
