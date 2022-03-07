package org.ahpuh.surf.follow.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.common.exception.follow.FollowNotFoundException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.follow.domain.FollowConverter;
import org.ahpuh.surf.follow.domain.FollowRepository;
import org.ahpuh.surf.follow.dto.response.FollowResponseDto;
import org.ahpuh.surf.follow.dto.response.FollowUserResponseDto;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowConverter followConverter;

    @Transactional
    public FollowResponseDto follow(final Long userId, final Long targetId) {
        final User source = getUser(userId);
        final User target = getUser(targetId);
        final Long followId = followRepository.save(followConverter.toEntity(source, target))
                .getFollowId();

        return new FollowResponseDto(followId);
    }

    @Transactional
    public void unfollow(final Long userId, final Long targetId) {
        final User source = getUser(userId);
        final User target = getUser(targetId);
        final Follow followEntity = followRepository.findBySourceAndTarget(source, target)
                .orElseThrow(FollowNotFoundException::new);

        followRepository.delete(followEntity);
    }

    public List<FollowUserResponseDto> findFollowerList(final Long targetId) {
        final User userEntity = getUser(targetId);
        return followRepository.findByTarget(userEntity)
                .stream()
                .map(Follow::getSource)
                .map(followConverter::toFollowUserDto)
                .toList();
    }

    public List<FollowUserResponseDto> findFollowingList(final Long userId) {
        final User userEntity = getUser(userId);
        return followRepository.findBySource(userEntity)
                .stream()
                .map(Follow::getTarget)
                .map(followConverter::toFollowUserDto)
                .toList();
    }

    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
