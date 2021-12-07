package org.ahpuh.surf.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserJoinRequestDto {

    private String email;

    private String userName;

    private String password;

}
