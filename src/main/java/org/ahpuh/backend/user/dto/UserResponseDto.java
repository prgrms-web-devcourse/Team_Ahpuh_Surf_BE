package org.ahpuh.backend.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private String userId;

    private String email;

    private String userName;

    private String profileImg;

    private String aboutMe;

    private String url;

    private int followerCount;

    private int followingCount;

}
