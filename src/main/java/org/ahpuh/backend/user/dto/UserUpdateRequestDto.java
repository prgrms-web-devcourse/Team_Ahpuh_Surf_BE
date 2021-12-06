package org.ahpuh.backend.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    private String userName;

    private String password;

    private String profilePhotoUrl;

    private String url;

    private String aboutMe;

    private Boolean accountPublic;

}