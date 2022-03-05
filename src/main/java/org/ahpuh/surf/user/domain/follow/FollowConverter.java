package org.ahpuh.surf.user.domain.follow;

import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.dto.response.FollowUserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class FollowConverter {

    public Follow toEntity(final User source, final User target) {
        return Follow.builder()
                .source(source)
                .target(target)
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
