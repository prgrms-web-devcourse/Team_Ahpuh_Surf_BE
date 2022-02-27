package org.ahpuh.surf.user.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.common.exception.EntityExceptionHandler;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.ahpuh.surf.user.domain.follow.Follow;
import org.ahpuh.surf.user.domain.follow.FollowConverter;
import org.ahpuh.surf.user.domain.follow.FollowRepository;
import org.ahpuh.surf.user.dto.response.FollowResponseDto;
import org.ahpuh.surf.user.dto.response.FollowUserResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.ahpuh.surf.common.exception.EntityExceptionHandler.UserNotFound;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowConverter followConverter;

    @Transactional
    public FollowResponseDto follow(final Long userId, final Long followUserId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        final User followedUser = userRepository.findById(followUserId)
                .orElseThrow(() -> UserNotFound(followUserId));

        final Long followId = followRepository.save(followConverter.toEntity(user, followedUser))
                .getFollowId();

        return new FollowResponseDto(followId);
    }

    @Transactional
    public void unfollow(final Long myId, final Long userId) {
        final User me = userRepository.findById(myId)
                .orElseThrow(() -> UserNotFound(myId));
        final User followedUser = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));

        final Follow followEntity = followRepository.findByUserAndFollowedUser(me, followedUser)
                .orElseThrow(EntityExceptionHandler::FollowNotFound);

        followRepository.delete(followEntity);
    }

    public List<FollowUserResponseDto> findFollowerList(final Long userId) {
        final User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        return followRepository.findByFollowedUser(userEntity)
                .stream()
                .map(Follow::getUser)
                .map(followConverter::toFollowUserDto)
                .toList();
    }

    public List<FollowUserResponseDto> findFollowingList(final Long userId) {
        final User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        return followRepository.findByUser(userEntity)
                .stream()
                .map(Follow::getFollowedUser)
                .map(followConverter::toFollowUserDto)
                .toList();
    }
}
