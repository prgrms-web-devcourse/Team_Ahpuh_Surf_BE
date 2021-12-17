package org.ahpuh.surf.user.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    private String userName;

    private String password;

    private String url;

    private String aboutMe;

    @NotNull
    private Boolean accountPublic;

}