package org.ahpuh.backend.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserLoginRequestDto {

    private String email; // principal

    private String password; // credentials

}
