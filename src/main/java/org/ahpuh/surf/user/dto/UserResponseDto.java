package org.ahpuh.surf.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long userId;

    private String email;

    private String userName;

    private String profilePhotoUrl;

    private String aboutMe;

    private String url;

    private int followerCount;

    private int followingCount;

}
