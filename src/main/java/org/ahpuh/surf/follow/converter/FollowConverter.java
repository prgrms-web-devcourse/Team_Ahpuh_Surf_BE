package org.ahpuh.surf.follow.converter;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.follow.dto.FollowUserDto;
import org.ahpuh.surf.follow.entity.Follow;
import org.ahpuh.surf.user.entity.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowConverter {

    public Follow toEntity(final User user, final User followedUser) {
        return Follow.builder()
                .user(user)
                .followedUser(followedUser)
                .build();
    }

    public FollowUserDto toFollowUserDto(final User user) {
        return FollowUserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .build();
    }

}
