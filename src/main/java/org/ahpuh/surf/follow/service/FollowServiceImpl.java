package org.ahpuh.surf.follow.service;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.follow.converter.FollowConverter;
import org.ahpuh.surf.follow.entity.Follow;
import org.ahpuh.surf.follow.repository.FollowRepository;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.ahpuh.surf.common.exception.EntityExceptionHandler.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    private final FollowConverter followConverter;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long follow(final Long userId, final Long followUserId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFound(userId));
        final User followedUser = userRepository.findById(followUserId)
                .orElseThrow(() -> UserNotFound(followUserId));

        return followRepository.save(followConverter.toEntity(user, followedUser))
                .getFollowId();
    }

    @Override
    @Transactional
    public void unfollow(final Long followId) {
        final Follow followEntity = followRepository.findById(followId)
                .orElseThrow(() -> FollowNotFound(followId));
        final User user = followEntity.getUser();
        final User followedUser = followEntity.getFollowedUser();

        if (!user.getFollowedUsers().remove(followEntity)) {
            throw FollowingNotFound();
        }
        if (!followedUser.getFollowingUsers().remove(followEntity)) {
            throw FollowingNotFound();
        }

        followRepository.deleteById(followId);
    }

}
