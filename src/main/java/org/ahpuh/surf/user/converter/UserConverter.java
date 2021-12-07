package org.ahpuh.surf.user.converter;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.UserLoginDto;
import org.ahpuh.surf.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder bCryptEncoder;

    public UserLoginDto toUserLoginDto(final User user) {
        return UserLoginDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .permission(user.getPermission())
                .build();
    }

    public User toEntity(final UserJoinRequestDto dto) {
        final User user = User.builder()
                .email(dto.getEmail())
                .userName(dto.getUserName())
                .password(bCryptEncoder.encode(dto.getPassword()))
                .build();
        user.setPermission("RULE_USER");
        return user;
    }

}
