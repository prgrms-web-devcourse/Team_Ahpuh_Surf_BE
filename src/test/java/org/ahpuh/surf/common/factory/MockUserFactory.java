package org.ahpuh.surf.common.factory;

import org.ahpuh.surf.user.domain.Permission;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.dto.response.UserLoginResponseDto;

import java.util.ArrayList;

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

    public static User createSavedUser() {
        return new User(1L,
                "mock",
                "test1@naver.com",
                "$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG", //testpw
                null,
                null,
                null,
                true,
                Permission.ROLE_USER,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static User createSavedUserWithProfileImage() {
        return new User(1L,
                "mock",
                "test1@naver.com",
                "$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG", //testpw
                "profilePhoto",
                null,
                null,
                true,
                Permission.ROLE_USER,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static UserJoinRequestDto createUserJoinRequestDto() {
        return UserJoinRequestDto.builder()
                .email("test1@naver.com")
                .password("testpw")
                .userName("mock")
                .build();
    }

    public static UserLoginRequestDto createUserLoginRequestDto() {
        return UserLoginRequestDto.builder()
                .email("test1@naver.com")
                .password("testpw")
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
