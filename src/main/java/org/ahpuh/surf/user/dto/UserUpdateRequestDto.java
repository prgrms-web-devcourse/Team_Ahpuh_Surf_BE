package org.ahpuh.surf.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    @NotBlank(message = "UserName must be provided.")
    @Size(max = 20, message = "UserName length must within 20.")
    private String userName;

    private String password;

    private String url;

    private String aboutMe;

    @NotNull
    private Boolean accountPublic;

}