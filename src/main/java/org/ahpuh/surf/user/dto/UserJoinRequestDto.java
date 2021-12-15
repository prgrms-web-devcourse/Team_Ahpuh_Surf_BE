package org.ahpuh.surf.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserJoinRequestDto {

    @Email(message = "email must be provided.")
    private String email;

    @NotBlank(message = "password must be provided.")
    private String password;

    @NotBlank(message = "userName must be provided.")
    private String userName;

}
