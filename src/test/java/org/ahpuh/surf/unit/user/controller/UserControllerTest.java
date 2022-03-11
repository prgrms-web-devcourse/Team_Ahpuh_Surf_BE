package org.ahpuh.surf.unit.user.controller;

import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.unit.ControllerTest;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.dto.response.UserFindInfoResponseDto;
import org.ahpuh.surf.user.dto.response.UserLoginResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import static org.ahpuh.surf.common.factory.MockFileFactory.createMultipartFileImage1;
import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.ahpuh.surf.common.factory.MockUserFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends ControllerTest {

    @DisplayName("로그인을 한 상태로")
    @Nested
    class AfterLogin {

        private static final String TOKEN = "TestToken";

        @BeforeEach
        void setUp() {
            final JwtAuthenticationToken authentication = createJwtToken(1L, "testEmail@naver.com");
            final SecurityContext securityContext = mock(SecurityContext.class);
            SecurityContextHolder.setContext(securityContext);

            given(securityContext.getAuthentication())
                    .willReturn(authentication);
        }

        @DisplayName("회원정보를 조회할 수 있다.")
        @Test
        void findUserInfo() throws Exception {
            // Given
            final UserFindInfoResponseDto response = createUserFindInfoDto();

            given(userService.getUserInfo(anyLong()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/users/{userId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            final String responseBody = perform.andExpect(status().isOk())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            verify(userService, times(1))
                    .getUserInfo(anyLong());
            assertThat(responseBody).isEqualTo(objectMapper.writeValueAsString(response));

            // RestDocs
            perform.andDo(UserDocumentation.findUserInfo());
        }

        @DisplayName("내 회원정보를 수정할 수 있다_유저 프로필 이미지 파일 첨부 O")
        @Test
        void updateUserInfo_MultipartFile_Success() throws Exception {
            // Given
            final UserUpdateRequestDto request = createUserUpdateRequestDto();
            final MockMultipartFile file = createMultipartFileImage1();

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/users")
                    .file(file)
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .with(requestMethod -> {
                        requestMethod.setMethod("PUT");
                        return requestMethod;
                    })
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(userService, times(1))
                    .update(anyLong(), any(UserUpdateRequestDto.class), any(MockMultipartFile.class));

            // RestDocs
            perform.andDo(UserDocumentation.updateUserWithImage());
        }

        @DisplayName("내 회원정보를 수정할 수 있다_유저 프로필 이미지 파일 첨부 X")
        @Test
        void updateUserInfo_OnlyDto_Success() throws Exception {
            // Given
            final UserUpdateRequestDto request = createUserUpdateRequestDto();

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/users")
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .with(requestMethod -> {
                        requestMethod.setMethod("PUT");
                        return requestMethod;
                    })
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(userService, times(1))
                    .update(anyLong(), any(UserUpdateRequestDto.class), any());

            // RestDocs
            perform.andDo(UserDocumentation.updateUserWithOnlyDto());
        }

        @DisplayName("회원을 삭제할 수 있다.")
        @Test
        void testDeleteUser() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(delete("/api/v1/users")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isNoContent())
                    .andDo(print());
            verify(userService, times(1))
                    .delete(anyLong());

            // RestDocs
            perform.andDo(UserDocumentation.deleteUser());
        }
    }

    @DisplayName("비로그인 상태로")
    @Nested
    class NotLoginYet {

        @DisplayName("회원가입을 할 수 있다.")
        @Test
        void join_Success() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createUserJoinRequestDto())));

            // Then
            perform.andExpect(status().isCreated())
                    .andDo(print());

            verify(userService, times(1))
                    .join(any(UserJoinRequestDto.class));

            // RestDocs
            perform.andDo(UserDocumentation.join());
        }

        @DisplayName("로그인을 할 수 있다.")
        @Test
        void login_Success() throws Exception {
            // Given
            final UserLoginRequestDto request = createUserLoginRequestDto();
            final UserLoginResponseDto response = createUserLoginResponseDto();

            given(userService.authenticate(request.getEmail(), request.getPassword()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            final String responseBody = perform.andExpect(status().isOk())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            verify(userService, times(1))
                    .authenticate(request.getEmail(), request.getPassword());
            assertThat(responseBody).isEqualTo(objectMapper.writeValueAsString(response));

            // RestDocs
            perform.andDo(UserDocumentation.login());
        }

        @DisplayName("회원정보를 조회할 수 없다.")
        @Test
        void findUserInfo_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    get("/api/v1/users/{userId}", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(userService, times(0))
                    .getUserInfo(anyLong());
        }

        @DisplayName("내 회원정보를 수정할 수 없다.")
        @Test
        void updateUserInfo_Fail() throws Exception {
            // Given
            final UserUpdateRequestDto request = createUserUpdateRequestDto();
            final MockMultipartFile file = createMultipartFileImage1();

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/users")
                    .file(file)
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .with(requestMethod -> {
                        requestMethod.setMethod("PUT");
                        return requestMethod;
                    }));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(userService, times(0))
                    .update(any(), any(), any());
        }

        @DisplayName("회원을 삭제할 수 없다.")
        @Test
        void deleteUser_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    delete("/api/v1/users"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(userService, times(0))
                    .delete(any());
        }
    }
}
