package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.dto.response.UserLoginResponseDto;

public class MockUserFactory {

    public static User createMockUser() {
        return User.builder()
                .email("test1@naver.com")
                .password("testpw")
                .userName("mock")
                .build();
    }

    public static User createMockUser(final String email) {
        return User.builder()
                .email(email)
                .password("testpw")
                .userName("mock")
                .build();
    }

    public static User createMockUser(final String email, final String password, final String userName) {
        return User.builder()
                .email(email)
                .password(password)
                .userName(userName)
                .build();
    }

    public static UserJoinRequestDto createUserJoinRequestDto() {
        return UserJoinRequestDto.builder()
                .email("test1@naver.com")
                .password("testpw")
                .userName("mock")
                .build();
    }

    public static UserJoinRequestDto createUserJoinRequestDtoWithEmail(final String email) {
        return UserJoinRequestDto.builder()
                .email(email)
                .password("testpw")
                .userName("mock")
                .build();
    }

    public static UserJoinRequestDto createUserJoinRequestDtoWithUserName(final String userName) {
        return UserJoinRequestDto.builder()
                .email("test1@naver.com")
                .password("testpw")
                .userName(userName)
                .build();
    }

    public static UserLoginRequestDto createUserLoginRequestDto() {
        return UserLoginRequestDto.builder()
                .email("test1@naver.com")
                .password("testpw")
                .build();
    }

    public static UserLoginRequestDto createUserLoginRequestDto(final String email) {
        return UserLoginRequestDto.builder()
                .email(email)
                .password("testpw")
                .build();
    }

    public static UserLoginRequestDto createUserLoginRequestDto(final String email, final String password) {
        return UserLoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
    }

    public static UserLoginResponseDto createUserLoginResponseDto() {
        return UserLoginResponseDto.builder()
                .token("token")
                .userId(1L)
                .build();
    }

    public static UserFindInfoResponseDto createUserFindInfoDto() {
        return UserFindInfoResponseDto.builder()
                .userId(1L)
                .email("test1@naver.com")
                .userName("mock")
                .profilePhotoUrl(null)
                .aboutMe(null)
                .url(null)
                .followerCount(0)
                .followingCount(0)
                .accountPublic(true)
                .build();
    }

    public static UserUpdateRequestDto createUserUpdateRequestDto() {
        return UserUpdateRequestDto.builder()
                .userName("update")
                .password("update")
                .url("update")
                .aboutMe("update")
                .accountPublic(false)
                .build();
    }

    public static UserUpdateRequestDto createUserUpdateRequestDtoWithNoPassword() {
        return UserUpdateRequestDto.builder()
                .userName("update")
                .password(null)
                .url("update")
                .aboutMe("update")
                .accountPublic(false)
                .build();
    }
}
