package org.ahpuh.surf.unit.user.controller;

import org.ahpuh.surf.common.factory.MockFileFactory;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.ahpuh.surf.common.factory.MockUserFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
            final JwtAuthenticationToken authentication = createJwtToken(1L, "testEmail");
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

            given(userService.findUser(anyLong()))
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
                    .findUser(anyLong());
            assertThat(responseBody).isEqualTo(objectMapper.writeValueAsString(response));

            perform.andDo(document("user/findUserInfo",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    pathParameters(
                            parameterWithName("userId").description("유저 id")
                    ),
                    responseFields(
                            fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id"),
                            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름"),
                            fieldWithPath("profilePhotoUrl").type(JsonFieldType.STRING).description("유저 프로필사진 url 주소")
                                    .optional(),
                            fieldWithPath("aboutMe").type(JsonFieldType.STRING).description("유저 자기소개 문구")
                                    .optional(),
                            fieldWithPath("url").type(JsonFieldType.STRING).description("유저 컨택 url 주소")
                                    .optional(),
                            fieldWithPath("followerCount").type(JsonFieldType.NUMBER).description("팔로워 수"),
                            fieldWithPath("followingCount").type(JsonFieldType.NUMBER).description("팔로잉 수"),
                            fieldWithPath("accountPublic").type(JsonFieldType.BOOLEAN).description("계정 공개여부")
                    )));
        }

        @DisplayName("내 회원정보를 수정할 수 있다_유저 프로필 이미지 파일 첨부 O")
        @Test
        void updateUserInfo_MultipartFile_Success() throws Exception {
            // Given
            final UserUpdateRequestDto request = createUserUpdateRequestDto();
            final MockMultipartFile file = MockFileFactory.createMultipartFileImage1();

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/users")
                    .file(file)
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
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(userService, times(1))
                    .update(anyLong(), any(UserUpdateRequestDto.class), any(MockMultipartFile.class));

            perform.andDo(document("user/updateUserWithImage",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestPartBody("file"),
                    requestPartFields("request",
                            fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름"),
                            fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                            fieldWithPath("url").type(JsonFieldType.STRING).optional()
                                    .description("유저 컨택 url 주소"),
                            fieldWithPath("aboutMe").type(JsonFieldType.STRING).optional()
                                    .description("유저 자기소개 문구"),
                            fieldWithPath("accountPublic").type(JsonFieldType.BOOLEAN).description("계정 공개여부")
                    )));
        }

        @DisplayName("내 회원정보를 수정할 수 있다_유저 프로필 이미지 파일 첨부 X")
        @Test
        void updateUserInfo_OnlyDto_Success() throws Exception {
            // Given
            final UserUpdateRequestDto request = createUserUpdateRequestDto();

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
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(userService, times(1))
                    .update(anyLong(), any(UserUpdateRequestDto.class), any());

            perform.andDo(document("user/updateUserOnlyDto",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestPartFields("request",
                            fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름"),
                            fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                            fieldWithPath("url").type(JsonFieldType.STRING).optional()
                                    .description("유저 컨택 url 주소"),
                            fieldWithPath("aboutMe").type(JsonFieldType.STRING).optional()
                                    .description("유저 자기소개 문구"),
                            fieldWithPath("accountPublic").type(JsonFieldType.BOOLEAN).description("계정 공개여부")
                    )));
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

            perform.andDo(document("user/deleteUser",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    )));
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

            perform.andDo(document("user/join",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                            fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름")
                    ),
                    responseHeaders(
                            headerWithName(HttpHeaders.LOCATION).description("location")
                    )));
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

            perform.andDo(document("user/login",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                            fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                    ),
                    responseFields(
                            fieldWithPath("token").type(JsonFieldType.STRING).description("토큰"),
                            fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id")
                    )));
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
                    .findUser(anyLong());
        }

        @DisplayName("내 회원정보를 수정할 수 없다.")
        @Test
        void updateUserInfo_Fail() throws Exception {
            // Given
            final UserUpdateRequestDto request = createUserUpdateRequestDto();
            final MockMultipartFile file = MockFileFactory.createMultipartFileImage1();

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/users")
                    .file(file)
                    .file(new MockMultipartFile(
                            "request",
                            "request.txt",
                            "application/json",
                            objectMapper.writeValueAsBytes(request)))
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
