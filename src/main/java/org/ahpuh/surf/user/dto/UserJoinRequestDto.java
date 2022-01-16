package org.ahpuh.surf.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserJoinRequestDto {

    @Email(message = "Invalid email.")
    private String email;

    @NotBlank(message = "Password must be provided.")
    private String password;

    @NotBlank(message = "UserName must be provided.")
    @Size(max = 20, message = "UserName length must within 20.")
    private String userName;

}
