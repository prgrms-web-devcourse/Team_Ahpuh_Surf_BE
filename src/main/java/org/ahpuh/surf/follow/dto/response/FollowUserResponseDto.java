package org.ahpuh.surf.follow.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class FollowUserResponseDto {

    private Long userId;

    private String userName;

    private String profilePhotoUrl;

}