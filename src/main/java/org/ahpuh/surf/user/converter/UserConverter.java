package org.ahpuh.surf.user.converter;

import lombok.RequiredArgsConstructor;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder bCryptEncoder;

    public User toEntity(final UserJoinRequestDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .password(bCryptEncoder.encode(dto.getPassword()))
                .userName(dto.getUserName())
                .build();
    }

    public UserFindInfoResponseDto toUserFindInfoResponseDto(final User userEntity, final long followingCount, final long followerCount) {
        return UserFindInfoResponseDto.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmail())
                .userName(userEntity.getUserName())
                .profilePhotoUrl(userEntity.getProfilePhotoUrl())
                .aboutMe(userEntity.getAboutMe())
                .url(userEntity.getUrl())
                .followingCount(followingCount)
                .followerCount(followerCount)
                .accountPublic(userEntity.getAccountPublic())
                .build();
    }
}
