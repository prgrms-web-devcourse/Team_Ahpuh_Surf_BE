package org.ahpuh.surf.unit.user.domain;

import org.ahpuh.surf.category.domain.Category;
import org.ahpuh.surf.common.exception.category.DuplicatedCategoryException;
import org.ahpuh.surf.common.exception.follow.DuplicatedFollowingException;
import org.ahpuh.surf.common.exception.like.DuplicatedLikeException;
import org.ahpuh.surf.common.exception.post.DuplicatedPostException;
import org.ahpuh.surf.common.exception.user.InvalidPasswordException;
import org.ahpuh.surf.follow.domain.Follow;
import org.ahpuh.surf.like.domain.Like;
import org.ahpuh.surf.post.domain.Post;
import org.ahpuh.surf.user.domain.Permission;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategory;
import static org.ahpuh.surf.common.factory.MockLikeFactory.createMockLike;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPost;
import static org.ahpuh.surf.common.factory.MockUserFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UserTest {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = createSavedUser();
    }

    @DisplayName("checkPassword 메소드는")
    @Nested
    class CheckPasswordMethod {

        @DisplayName("올바른 비밀번호를 검증할 수 있다.")
        @Test
        void validPasswordCheck() {
            // When
            final boolean checkResult = mockUser.checkPassword(passwordEncoder, "testpw");

            // Then
            assertThat(checkResult).isTrue();
        }

        @DisplayName("잘못된 비밀번호 또는 null, empty 값에 예외를 던진다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "", " ", "invalidPassword"
        })
        void invalidPasswordCheck(final String password) {
            // When Then
            assertThatThrownBy(() -> mockUser.checkPassword(passwordEncoder, password))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessage("잘못된 비밀번호입니다.");
        }
    }

    @DisplayName("setPermission 메소드는")
    @Nested
    class SetPermissionMethod {

        @DisplayName("유저의 권한을 변경할 수 있다.")
        @Test
        void changeUserPermission() {
            // When
            mockUser.setPermission(Permission.ROLE_ADMIN);

            // Then
            assertThat(mockUser.getPermission()).isEqualTo(Permission.ROLE_ADMIN);
        }
    }

    @DisplayName("update 메소드는")
    @Nested
    class UpdateMethod {

        @DisplayName("비밀번호, 프로필이미지를 포함한 유저 정보를 수정할 수 있다.")
        @Test
        void updateUser_WIthPasswordAndProfileImage() {
            // Given
            final UserUpdateRequestDto updateRequest = createUserUpdateRequestDto();
            final Optional<String> profilePhotoUrl = Optional.of("update");

            // When
            mockUser.update(passwordEncoder, updateRequest, profilePhotoUrl);

            // Then
            assertAll("수정된 유저 정보 검증",
                    () -> assertThat(mockUser.getUserName()).isEqualTo("update"),
                    () -> assertThat(mockUser.getUrl()).isEqualTo("update"),
                    () -> assertThat(mockUser.getAboutMe()).isEqualTo("update"),
                    () -> assertThat(mockUser.getAccountPublic()).isFalse(),
                    () -> assertThat(passwordEncoder.matches("update", mockUser.getPassword())).isTrue(),
                    () -> assertThat(mockUser.getProfilePhotoUrl()).isEqualTo("update")
            );
        }

        @DisplayName("프로필이미지를 제외하고 비밀번호를 포함한 유저 정보를 수정할 수 있다.")
        @Test
        void updateUser_WIthPassword_NoProfileImage() {
            // Given
            final User savedUser = createSavedUserWithProfileImage();
            final UserUpdateRequestDto updateRequest = createUserUpdateRequestDto();
            final Optional<String> profilePhotoUrl = Optional.empty();

            // When
            savedUser.update(passwordEncoder, updateRequest, profilePhotoUrl);

            // Then
            assertAll("수정된 유저 정보 검증_프로필이미지 X",
                    () -> assertThat(savedUser.getUserName()).isEqualTo("update"),
                    () -> assertThat(savedUser.getUrl()).isEqualTo("update"),
                    () -> assertThat(savedUser.getAboutMe()).isEqualTo("update"),
                    () -> assertThat(savedUser.getAccountPublic()).isFalse(),
                    () -> assertThat(passwordEncoder.matches("update", savedUser.getPassword())).isTrue(),
                    () -> assertThat(savedUser.getProfilePhotoUrl()).isEqualTo("profilePhoto")
            );
        }

        @DisplayName("비밀번호를 제외하고 프로필이미지를 포함한 유저 정보를 수정할 수 있다.")
        @Test
        void updateUser_WIthProfileImage_NoPassword() {
            // Given
            final User savedUser = createSavedUserWithProfileImage();
            final UserUpdateRequestDto updateRequest = createUserUpdateRequestDtoWithNoPassword();
            final Optional<String> profilePhotoUrl = Optional.of("update");

            // When
            savedUser.update(passwordEncoder, updateRequest, profilePhotoUrl);

            // Then
            assertAll("수정된 유저 정보 검증_비밀번호 X",
                    () -> assertThat(savedUser.getUserName()).isEqualTo("update"),
                    () -> assertThat(savedUser.getUrl()).isEqualTo("update"),
                    () -> assertThat(savedUser.getAboutMe()).isEqualTo("update"),
                    () -> assertThat(savedUser.getAccountPublic()).isFalse(),
                    () -> assertThat(passwordEncoder.matches("testpw", savedUser.getPassword())).isTrue(),
                    () -> assertThat(savedUser.getProfilePhotoUrl()).isEqualTo("update")
            );
        }

        @DisplayName("비밀번호와 프로필이미지는 변경하지 않고 나머지 유저 정보를 수정할 수 있다.")
        @Test
        void updateUser_NoPasswordAndProfileImage() {
            // Given
            final User savedUser = createSavedUserWithProfileImage();
            final UserUpdateRequestDto updateRequest = createUserUpdateRequestDtoWithNoPassword();
            final Optional<String> profilePhotoUrl = Optional.empty();

            // When
            savedUser.update(passwordEncoder, updateRequest, profilePhotoUrl);

            // Then
            assertAll("수정된 유저 정보 검증_비밀번호 X",
                    () -> assertThat(savedUser.getUserName()).isEqualTo("update"),
                    () -> assertThat(savedUser.getUrl()).isEqualTo("update"),
                    () -> assertThat(savedUser.getAboutMe()).isEqualTo("update"),
                    () -> assertThat(savedUser.getAccountPublic()).isFalse(),
                    () -> assertThat(passwordEncoder.matches("testpw", savedUser.getPassword())).isTrue(),
                    () -> assertThat(savedUser.getProfilePhotoUrl()).isEqualTo("profilePhoto")
            );
        }
    }

    @DisplayName("연관관계 매핑 예외")
    @Nested
    class EntityMappingExceptions {

        @DisplayName("addCategory 메소드는 이미 등록된 카테고리가 다시 등록되면 예외가 발생한다.")
        @Test
        void duplicatedCategoryException() {
            // Given
            final User user = createMockUser();
            final Category category = createMockCategory(user);

            // When Then
            assertThatThrownBy(() -> user.addCategory(category))
                    .isInstanceOf(DuplicatedCategoryException.class)
                    .hasMessage("이미 등록된 카테고리입니다.");
        }

        @DisplayName("addPost 메소드는 이미 등록된 게시글이 다시 등록되면 예외가 발생한다.")
        @Test
        void duplicatedPostException() {
            // Given
            final User user = createMockUser();
            final Category category = createMockCategory(user);
            final Post post = createMockPost(user, category);

            // When Then
            assertThatThrownBy(() -> user.addPost(post))
                    .isInstanceOf(DuplicatedPostException.class)
                    .hasMessage("이미 등록된 게시글입니다.");
        }

        @DisplayName("addFollowing 메소드는 이미 팔로우 한 유저가 다시 팔로우하면 예외가 발생한다.")
        @Test
        void duplicatedFollowingException_Following() {
            // Given
            final User user1 = createMockUser("test1@naver.com");
            final User user2 = createMockUser("test2@naver.com");
            final Follow follow = Follow.builder()
                    .source(user1)
                    .target(user2)
                    .build();

            // When Then
            assertThatThrownBy(() -> user1.addFollowing(follow))
                    .isInstanceOf(DuplicatedFollowingException.class)
                    .hasMessage("이미 팔로우 한 사용자입니다.");
        }

        @DisplayName("addFollowers 메소드는 이미 팔로우 한 유저를 다시 팔로우하면 예외가 발생한다.")
        @Test
        void duplicatedFollowingException_Followers() {
            // Given
            final User user1 = createMockUser("test1@naver.com");
            final User user2 = createMockUser("test2@naver.com");
            final Follow follow = Follow.builder()
                    .source(user1)
                    .target(user2)
                    .build();

            // When Then
            assertThatThrownBy(() -> user2.addFollowers(follow))
                    .isInstanceOf(DuplicatedFollowingException.class)
                    .hasMessage("이미 팔로우 한 사용자입니다.");
        }

        @DisplayName("addLike 메소드는 이미 좋아요를 누른 게시글을 다시 좋아요하면 예외가 발생한다.")
        @Test
        void duplicatedLikeException() {
            // Given
            final User user = createMockUser();
            final Category category = createMockCategory(user);
            final Post post = createMockPost(user, category);
            final Like like = createMockLike(user, post);

            // When Then
            assertThatThrownBy(() -> user.addLike(like))
                    .isInstanceOf(DuplicatedLikeException.class)
                    .hasMessage("이미 좋아요를 누른 게시글입니다.");
        }
    }
}
