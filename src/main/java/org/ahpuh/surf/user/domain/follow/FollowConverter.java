package org.ahpuh.surf.user.domain.follow;

import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.dto.response.FollowUserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class FollowConverter {

    public Follow toEntity(final User user, final User followedUser) {
        return Follow.builder()
                .user(user)
                .followedUser(followedUser)
                .build();
    }

    public FollowUserResponseDto toFollowUserDto(final User user) {
        return FollowUserResponseDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .build();
    }

}
