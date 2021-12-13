package org.ahpuh.surf.user.converter;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.user.dto.UserDto;
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
                .password(bCryptEncoder.encode(dto.getPassword()))
                .build();
        return user;
    }

    public UserDto toUserDto(final User userEntity) {
        return UserDto.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmail())
                .userName(userEntity.getUserName())
                .profilePhotoUrl(userEntity.getProfilePhotoUrl())
                .aboutMe(userEntity.getAboutMe())
                .url(userEntity.getUrl())
                .followerCount(userEntity.getFollowers().size())
                .followingCount(userEntity.getFollowing().size())
                .build();
    }

}
