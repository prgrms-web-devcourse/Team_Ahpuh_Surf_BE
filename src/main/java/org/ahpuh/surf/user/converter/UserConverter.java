package org.ahpuh.surf.user.converter;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder bCryptEncoder;

    public User toEntity(final UserJoinRequestDto dto) {
        final User user = User.builder()
                .email(dto.getEmail())
                .userName(dto.getUserName())
                .password(bCryptEncoder.encode(dto.getPassword()))
                .build();
        user.setPermission("ROLE_USER");
        return user;
    }

}
