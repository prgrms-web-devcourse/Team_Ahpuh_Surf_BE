package org.ahpuh.surf.unit.user.service;

import org.ahpuh.surf.common.exception.user.DuplicatedEmailException;
import org.ahpuh.surf.common.exception.user.InvalidPasswordException;
import org.ahpuh.surf.common.exception.user.UserNotFoundException;
import org.ahpuh.surf.s3.service.S3Service;
import org.ahpuh.surf.user.domain.User;
import org.ahpuh.surf.user.domain.UserConverter;
import org.ahpuh.surf.user.domain.UserRepository;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static org.ahpuh.surf.common.factory.MockFileFactory.createEmptyImageFile;
import static org.ahpuh.surf.common.factory.MockUserFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private UserConverter userConverter;

    @DisplayName("login 메소드는")
    @Nested
    class LoginMethod {

        @DisplayName("가입된 이메일로")
        @Nested
        class VaildEmail {

            private final User validUser = createMockUser();
            private final String validEmail = validUser.getEmail();
            private final String validPassword = validUser.getPassword();

            @DisplayName("로그인 할 수 있다.")
            @Test
            void emailLoginSuccess() {
                // Given
                final User mockUser = mock(User.class);
                given(userRepository.findByEmail(validEmail))
                        .willReturn(Optional.of(mockUser));

                // When
                final User loginUser = userService.login(validEmail, validPassword);

                // Then
                assertThat(loginUser).isSameAs(mockUser);
                verify(userRepository, times(1))
                        .findByEmail(validEmail);
                verify(mockUser, times(1))
                        .checkPassword(any(), anyString());
            }

            @DisplayName("로그인 시 틀린 비밀번호라면 예외가 발생한다.")
            @Test
            void invalidPasswordLoginFail() {
                // given
                given(userRepository.findByEmail(validEmail))
                        .willReturn(Optional.of(validUser));

                // When
                assertThatThrownBy(() -> userService.login(validEmail, "invalidPassword"))
                        .isInstanceOf(InvalidPasswordException.class)
                        .hasMessage("잘못된 비밀번호입니다.");

                // Then
                verify(userRepository, times(1))
                        .findByEmail(validEmail);
            }
        }

        @DisplayName("가입되지 않은 이메일로 로그인 시 예외가 발생한다.")
        @Test
        void invalidEmailLoginFail() {
            // given
            given(userRepository.findByEmail(anyString()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> userService.login("invalidEmail", any()))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findByEmail("invalidEmail");
        }
    }

    @DisplayName("join 메소드는")
    @Nested
    class JoinMethod {

        private UserJoinRequestDto joinRequestDto;

        @BeforeEach
        void setUp() {
            joinRequestDto = createUserJoinRequestDto();
        }

        @DisplayName("중복되지 않은 이메일로 가입할 수 있다.")
        @Test
        void validEmailJoinSuccess() {
            // Given
            final User mockUser = createMockUser();
            given(userRepository.existsByEmail(joinRequestDto.getEmail()))
                    .willReturn(false);
            given(userConverter.toEntity(joinRequestDto))
                    .willReturn(mockUser);
            given(userRepository.save(mockUser))
                    .willReturn(createSavedUser());

            // When
            userService.join(joinRequestDto);

            // Then
            verify(userRepository, times(1))
                    .existsByEmail(joinRequestDto.getEmail());
            verify(userRepository, times(1))
                    .save(mockUser);
            verify(userConverter, times(1))
                    .toEntity(joinRequestDto);
        }

        @DisplayName("중복된 이메일로 가입 시 예외가 발생한다.")
        @Test
        void invalidEmailJoinFail() {
            // Given
            given(userRepository.existsByEmail(joinRequestDto.getEmail()))
                    .willReturn(true);

            // When
            assertThatThrownBy(() -> userService.join(joinRequestDto))
                    .isInstanceOf(DuplicatedEmailException.class)
                    .hasMessage("이미 가입된 이메일입니다.");

            // Then
            verify(userRepository, times(1))
                    .existsByEmail(joinRequestDto.getEmail());
        }
    }

    @DisplayName("getUserInfo 메소드는")
    @Nested
    class GetUserInfoMethod {

        @DisplayName("유효한 userId로 유저Dto를 반환할 수 있다.")
        @Test
        void validUserId_Success() {
            // Given
            final User mockUser = createMockUser();
            final UserFindInfoResponseDto mockUserDto = createUserFindInfoDto();

            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));
            given(userConverter.toUserFindInfoResponseDto(any(), eq(0L), eq(0L)))
                    .willReturn(mockUserDto);

            // When
            final UserFindInfoResponseDto returnDto = userService.getUserInfo(1L);

            // Then
            assertThat(returnDto).isSameAs(mockUserDto);
            verify(userRepository, times(1))
                    .findById(1L);
            verify(userConverter, times(1))
                    .toUserFindInfoResponseDto(mockUser, 0L, 0L);
        }

        @DisplayName("잘못된 아이디의 회원 조회 시 예외가 발생한다.")
        @Test
        void invalidUserIdFail() {
            // Given
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> userService.getUserInfo(1L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("해당 유저를 찾을 수 없습니다.");

            // Then
            verify(userRepository, times(1))
                    .findById(1L);
        }
    }

    @DisplayName("update 메소드는")
    @Nested
    class UpdateMethod {

        @DisplayName("사진 파일이 첨부된 경우")
        @Nested
        class WithImageFile {

            @DisplayName("사진을 업로드하고 유저 정보를 변경한다.")
            @Test
            void uploadFileAndUpdateUser() throws IOException {
                // Given
                final MockMultipartFile file = mock(MockMultipartFile.class);
                given(s3Service.uploadUserImage(file))
                        .willReturn(Optional.of("imageUrl"));
                final User mockUser = mock(User.class);
                given(userRepository.findById(anyLong()))
                        .willReturn(Optional.of(mockUser));

                // When
                userService.update(1L, createUserUpdateRequestDto(), file);

                // Then
                verify(s3Service, times(1))
                        .uploadUserImage(file);
                verify(userRepository, times(1))
                        .findById(anyLong());
                verify(mockUser, times(1))
                        .update(any(PasswordEncoder.class), any(UserUpdateRequestDto.class), any());
            }
        }

        @DisplayName("사진 파일이 첨부되지 않은 경우")
        @Nested
        class WithNoImageFile {

            @DisplayName("유저 정보를 변경한다.")
            @Test
            void UpdateUser() throws IOException {
                // Given
                final User mockUser = mock(User.class);
                given(userRepository.findById(anyLong()))
                        .willReturn(Optional.of(mockUser));

                // When
                userService.update(1L, createUserUpdateRequestDto(), createEmptyImageFile());

                // Then
                verify(s3Service, times(0))
                        .uploadUserImage(any());
                verify(userRepository, times(1))
                        .findById(anyLong());
                verify(mockUser, times(1))
                        .update(any(PasswordEncoder.class), any(UserUpdateRequestDto.class), any());
            }
        }
    }

    @DisplayName("delete 메소드는")
    @Nested
    class DeleteMethod {

        @DisplayName("유저를 삭제한다.")
        @Test
        void delete() {
            // Given
            final User mockUser = mock(User.class);
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(mockUser));

            // When
            userService.delete(1L);

            // Then
            verify(userRepository, times(1))
                    .findById(anyLong());
            verify(userRepository, times(1))
                    .delete(mockUser);
        }
    }
}
