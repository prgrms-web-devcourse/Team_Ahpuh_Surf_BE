package org.ahpuh.backend.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {

    private String token;

    private Long userId;

}
