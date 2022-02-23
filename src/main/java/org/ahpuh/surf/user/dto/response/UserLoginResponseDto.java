package org.ahpuh.surf.user.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {

    private String token;

    private Long userId;

}
