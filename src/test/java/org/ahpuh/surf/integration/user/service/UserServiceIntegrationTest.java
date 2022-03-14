package org.ahpuh.surf.integration.user.service;

import org.ahpuh.surf.common.exception.user.DuplicatedEmailException;
import org.ahpuh.surf.common.exception.user.InvalidPasswordException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.integration.IntegrationTest;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserRepository;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.ahpuh.surf.common.factory.MockFileFactory.createEmptyFile;
import static org.ahpuh.surf.common.factory.MockFileFactory.createMultipartFileImage;
import static org.ahpuh.surf.common.factory.MockUserFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class UserServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("유저 회원가입 테스트")
    @Nested
    class JoinTest {

        @DisplayName("유저는 회원가입을 할 수 있다.")
        @Test
        void joinSuccess() {
            // Given
            final UserJoinRequestDto joinRequestDto = createUserJoinRequestDto();

            // When
            userService.join(joinRequestDto);

            // Then
            assertThat(userRepository.findAll().size()).isEqualTo(1);
        }

        @DisplayName("중복된 이메일로 가입 시 예외가 발생한다 - 400 응답")
        @Test
        void duplicatedEmailException_400() {
            // Given
            userRepository.save(new User("test1@naver.com", "testpw", "userName"));
            final UserJoinRequestDto joinRequestDto = createUserJoinRequestDto();

            // When Then
            assertThatThrownBy(() -> userService.join(joinRequestDto))
                    .isInstanceOf(DuplicatedEmailException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                    .hasMessage("이미 가입된 이메일입니다.");
        }
    }

    @DisplayName("유저 로그인 테스트")
    @Nested
    class LoginTest {

        @DisplayName("유저는 가입된 이메일로")
        @Nested
        class VaildEmail {

            @DisplayName("로그인 할 수 있다.")
            @Test
            void loginSuccess() {
                // Given
                final String email = "test1@naver.com";
                final String password = "testpw";
                userRepository.save(new User(email, passwordEncoder.encode(password), "userName"));

                // When
                final User loginUser = userService.login(email, password);

                // Then
                assertThat(loginUser).isNotNull();
            }

            @DisplayName("로그인 시 틀린 비밀번호라면 예외가 발생한다 - 400 응답")
            @Test
            void invalidPasswordException_400() {
                // given
                final String email = "test1@naver.com";
                userRepository.save(new User(email, "validpw", "userName"));

                // When Then
                assertThatThrownBy(() -> userService.login(email, "invalidpw"))
                        .isInstanceOf(InvalidPasswordException.class)
                        .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST)
                        .hasMessage("잘못된 비밀번호입니다.");
            }
        }

        @DisplayName("가입되지 않은 이메일로 로그인 시 예외가 발생한다 - 404 응답")
        @Test
        void userNotFoundException_404() {
            // When Then
            assertThatThrownBy(() -> userService.login("invalidEmail", "testpw"))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");
        }
    }

    @DisplayName("유저 정보 조회 테스트")
    @Nested
    class GetUserInfoTest {

        @DisplayName("유저 Id로 유저 정보를 조회할 수 있다.")
        @Test
        void getUserInfoSuccess() {
            // Given
            final User user = userRepository.save(createMockUser());

            // When
            final UserFindInfoResponseDto response = userService.getUserInfo(user.getUserId());

            // Then
            assertThat(response).isNotNull();
        }

        @DisplayName("잘못된 Id로 회원 조회 시 예외가 발생한다 - 404 응답")
        @Test
        void userNotFoundException_404() {
            // When Then
            assertThatThrownBy(() -> userService.getUserInfo(2L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");
        }
    }

    @DisplayName("유저 정보 수정 테스트")
    @Nested
    class UpdateTest {

        @DisplayName("유저 프로필 사진과 유저 정보를 수정할 수 있다.")
        @Test
        void updateUserWithProfileImageSuccess() {
            // Given
            final User user = userRepository.save(createSavedUser());
            assertAll("유저 정보 변경전",
                    () -> assertThat(user.getUserName()).isEqualTo("mock"),
                    () -> assertThat(passwordEncoder.matches("testpw", user.getPassword())).isTrue(),
                    () -> assertThat(user.getUrl()).isNull(),
                    () -> assertThat(user.getAboutMe()).isNull(),
                    () -> assertThat(user.getAccountPublic()).isTrue(),
                    () -> assertThat(user.getProfilePhotoUrl()).isNull()
            );
            final UserUpdateRequestDto request = createUserUpdateRequestDto();
            final MockMultipartFile profileImage = createMultipartFileImage();

            // When
            userService.update(user.getUserId(), request, profileImage);

            // Then
            final User updatedUser = userRepository.getById(user.getUserId());
            assertAll("유저 정보 변경후",
                    () -> assertThat(updatedUser.getUserName()).isEqualTo("update"),
                    () -> assertThat(passwordEncoder.matches("update", updatedUser.getPassword())).isTrue(),
                    () -> assertThat(updatedUser.getUrl()).isEqualTo("update"),
                    () -> assertThat(updatedUser.getAboutMe()).isEqualTo("update"),
                    () -> assertThat(updatedUser.getAccountPublic()).isFalse(),
                    () -> assertThat(updatedUser.getProfilePhotoUrl()).isEqualTo("mock upload")
            );
        }

        @DisplayName("프로필 사진이 새로 첨부되지 않은 경우 회원 정보만 변경한다.")
        @Test
        void updateUserWithNoProfileImageSuccess() {
            // Given
            final User user = userRepository.save(createSavedUser());
            assertAll("유저 정보 변경전",
                    () -> assertThat(user.getUserName()).isEqualTo("mock"),
                    () -> assertThat(passwordEncoder.matches("testpw", user.getPassword())).isTrue(),
                    () -> assertThat(user.getUrl()).isNull(),
                    () -> assertThat(user.getAboutMe()).isNull(),
                    () -> assertThat(user.getAccountPublic()).isTrue(),
                    () -> assertThat(user.getProfilePhotoUrl()).isNull()
            );
            final UserUpdateRequestDto request = createUserUpdateRequestDto();
            final MockMultipartFile emptyImageFile = createEmptyFile();

            // When
            userService.update(user.getUserId(), request, emptyImageFile);

            // Then
            final User updatedUser = userRepository.getById(user.getUserId());
            assertAll("유저 정보 변경후",
                    () -> assertThat(updatedUser.getUserName()).isEqualTo("update"),
                    () -> assertThat(passwordEncoder.matches("update", updatedUser.getPassword())).isTrue(),
                    () -> assertThat(updatedUser.getUrl()).isEqualTo("update"),
                    () -> assertThat(updatedUser.getAboutMe()).isEqualTo("update"),
                    () -> assertThat(updatedUser.getAccountPublic()).isFalse(),
                    () -> assertThat(updatedUser.getProfilePhotoUrl()).isNull()
            );
        }
    }

    @DisplayName("회원 삭제 테스트")
    @Nested
    class DeleteTest {

        @DisplayName("유저를 삭제할 수 있다.")
        @Test
        void delete() {
            // Given
            final User user = userRepository.save(createMockUser());

            // When
            userService.delete(user.getUserId());

            // Then
            assertThat(userRepository.findAll().size()).isEqualTo(0);
        }
    }
}
