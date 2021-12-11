package org.ahpuh.surf.follow.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.follow.converter.FollowConverter;
import org.ahpuh.surf.follow.repository.FollowRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import static org.ahpuh.surf.common.exception.EntityExceptionHandler.UserNotFound;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    private final FollowConverter followConverter;

    private final UserRepository userRepository;

    @Override
    public Long follow(final Long userId, final Long followUserId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        final User followedUser = userRepository.findById(followUserId)
                .orElseThrow(() -> UserNotFound(followUserId));

        return followRepository.save(followConverter.toEntity(user, followedUser))
                .getFollowId();
    }
}
