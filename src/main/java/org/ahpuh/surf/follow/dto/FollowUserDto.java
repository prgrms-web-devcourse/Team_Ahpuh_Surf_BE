package org.ahpuh.surf.follow.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class FollowUserDto {

    private Long userId;

    private String userName;

    private String profilePhotoUrl;

}
