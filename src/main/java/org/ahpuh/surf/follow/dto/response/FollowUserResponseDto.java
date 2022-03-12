package org.ahpuh.surf.follow.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowUserResponseDto {

    private Long userId;
    private String userName;
    private String profilePhotoUrl;

    @Builder
    @QueryProjection
    public FollowUserResponseDto(final Long userId, final String userName, final String profilePhotoUrl) {
        this.userId = userId;
        this.userName = userName;
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
