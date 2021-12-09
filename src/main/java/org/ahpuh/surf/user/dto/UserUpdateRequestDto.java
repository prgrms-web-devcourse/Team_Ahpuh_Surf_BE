package org.ahpuh.surf.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    private String userName;

    @NotBlank(message = "password must be provided.")
    private String password;

    private String profilePhotoUrl;

    private String url;

    private String aboutMe;

    @NotNull
    private Boolean accountPublic;

}