package org.ahpuh.surf.unit.user.controller;

import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.unit.ControllerTest;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserRequestValidationTest extends ControllerTest {

    @DisplayName("UserJoinRequestDto @Valid")
    @Nested
    class UserJoinRequestDtoTest {

        @DisplayName("이메일은 형식에 맞게 입력되어야한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "틀린이메일",
                "email",
                "@naver.com"
        })
        void email_Fail(final String email) throws Exception {
            // Given
            final UserJoinRequestDto request = new UserJoinRequestDto(email, "password", "userName");

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("비밀번호는 빈 값을 제외하고 입력되어야한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void password_Fail(final String password) throws Exception {
            // Given
            final UserJoinRequestDto request = new UserJoinRequestDto("email@naver.com", password, "userName");

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("유저 이름은 빈 값을 허용하지 않고, 최대 20자까지 입력되어야한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "유저이름은최대20자까지가능합니다이건21",
        })
        void userName_Fail(final String userName) throws Exception {
            // Given
            final UserJoinRequestDto request = new UserJoinRequestDto("email@naver.com", "password", userName);

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @DisplayName("UserLoginRequestDto @Valid")
    @Nested
    class UserLoginRequestDtoTest {

        @DisplayName("이메일은 형식에 맞게 입력되어야한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "틀린이메일",
                "email",
                "@naver.com"
        })
        void email_Fail(final String email) throws Exception {
            // Given
            final UserLoginRequestDto request = new UserLoginRequestDto(email, "password");

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("비밀번호는 빈 값을 제외하고 입력되어야한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void password_Fail(final String password) throws Exception {
            // Given
            final UserLoginRequestDto request = new UserLoginRequestDto("email@naver.com", password);

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @DisplayName("UserUpdateRequestDto @Valid")
    @Nested
    class UserUpdateRequestDtoTest {

        private static final String TOKEN = "TestToken";

        @BeforeEach
        void setUp() {
            final JwtAuthenticationToken authentication = createJwtToken(1L, "testEmail@naver.com");
            final SecurityContext securityContext = mock(SecurityContext.class);
            SecurityContextHolder.setContext(securityContext);

            given(securityContext.getAuthentication())
                    .willReturn(authentication);
        }

        @DisplayName("유저 이름은 빈 값을 허용하지 않고, 최대 20자까지 입력되어야한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "유저이름은최대20자까지가능합니다이건21",
        })
        void userName_Fail(final String userName) throws Exception {
            // Given
            final UserUpdateRequestDto request =
                    new UserUpdateRequestDto(userName, "password", "url", "aboutMe", true);

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/users")
                    .file(new MockMultipartFile(
                            "request",
                            "request.txt",
                            "application/json",
                            objectMapper.writeValueAsBytes(request)))
                    .with(requestMethod -> {
                        requestMethod.setMethod("PUT");
                        return requestMethod;
                    })
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("accountPublic은 필수로 입력되어야한다.")
        @ParameterizedTest
        @NullSource
        void accountPublic_Fail(final Boolean accountPublic) throws Exception {
            // Given
            final UserUpdateRequestDto request =
                    new UserUpdateRequestDto("userName", null, null, null, accountPublic);

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/users")
                    .file(new MockMultipartFile(
                            "request",
                            "request.txt",
                            "application/json",
                            objectMapper.writeValueAsBytes(request)))
                    .with(requestMethod -> {
                        requestMethod.setMethod("PUT");
                        return requestMethod;
                    })
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}
