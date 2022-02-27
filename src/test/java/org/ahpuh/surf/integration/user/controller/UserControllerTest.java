package org.ahpuh.surf.integration.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ahpuh.surf.user.controller.UserController;
import org.ahpuh.surf.user.dto.request.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.request.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.request.UserUpdateRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserController userController;

    private User user1;
    private Long userId1;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .email("test@naver.com")
                .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                .userName("user1")
                .build());
        userId1 = user1.getUserId();
    }

    @Test
    @DisplayName("회원가입을 할 수 있다.")
    @Transactional
    void testJoin() throws Exception {
        // Given
        final UserJoinRequestDto req = UserJoinRequestDto.builder()
                .email("test1@naver.com")
                .password("test111")
                .userName("name")
                .build();

        // When
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(print());

        // Then
        assertAll("userJoin",
                () -> Assertions.assertThat(userRepository.findAll().size()).isEqualTo(2),
                () -> assertThat(userRepository.findAll().get(1).getEmail()).isEqualTo("test1@naver.com")
        );
    }

    @Test
    @DisplayName("로그인 할 수 있다.")
    @Transactional
    void testLogin() throws Exception {
        // Given
        final UserLoginRequestDto req = UserLoginRequestDto.builder()
                .email("test@naver.com")
                .password("testpw")
                .build();

        // When Then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원정보를 조회할 수 있다.")
    @Transactional
    void testFindUserInfo() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}", userId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원정보를 수정할 수 있다.")
    @Transactional
    void testUpdateUser() throws Exception {
        // Given
        assertAll("beforeUpdate",
                () -> assertThat(user1.getUserName()).isEqualTo("user1"),
                () -> assertThat(user1.getAboutMe()).isNull(),
                () -> assertThat(user1.getUrl()).isNull(),
                () -> assertThat(user1.getAccountPublic()).isTrue()
        );

        final UserLoginRequestDto loginReq = UserLoginRequestDto.builder()
                .email("test@naver.com")
                .password("testpw")
                .build();
        final String token = Objects.requireNonNull(userController.login(loginReq).getBody()).getToken();

        // When
        final UserUpdateRequestDto updateReq = UserUpdateRequestDto.builder()
                .userName("수정된 name")
                .password(null)
                .url("내 블로그 주소")
                .aboutMe("수정된 소개글")
                .accountPublic(false)
                .build();
        final MockMultipartFile request = new MockMultipartFile(
                "request",
                "request.txt",
                "application/json",
                objectMapper.writeValueAsBytes(updateReq));

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "imagefile.jpeg",
                "image/jpeg",
                "<<jpeg data>>".getBytes());

        final MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/users");
        builder.with(requestMethod -> {
            requestMethod.setMethod("PUT");
            return requestMethod;
        });

        // 파일을 첨부하면 파일 url로 변경됨 (mock)
        mockMvc.perform(builder
                        .file(request)
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andDo(print());

        // Then
        final User user11 = userRepository.findAll().get(0);
        assertAll("afterUpdate",
                () -> assertThat(user11.getUserName()).isEqualTo("수정된 name"),
                () -> assertThat(user11.getUrl()).isEqualTo("내 블로그 주소"),
                () -> assertThat(user11.getAboutMe()).isEqualTo("수정된 소개글"),
                () -> assertThat(user11.getAccountPublic()).isFalse(),
                () -> assertThat(user11.getProfilePhotoUrl()).isEqualTo("mock upload")
        );

        // file을 첨부하지 않으면 파일 url을 변경하지 않음
        mockMvc.perform(builder
                        .file(request)
                        .file(new MockMultipartFile("file", null, null, new byte[0]))
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andDo(print());
        assertThat(user1.getProfilePhotoUrl()).isEqualTo("mock upload");

    }

    @Test
    @DisplayName("회원을 삭제(softDelete) 할 수 있다.")
    @Transactional
    void testDeleteUser() throws Exception {
        // Given
        final UserLoginRequestDto req = UserLoginRequestDto.builder()
                .email("test@naver.com")
                .password("testpw")
                .build();
        final String token = Objects.requireNonNull(userController.login(req).getBody()).getToken();

        // When
        mockMvc.perform(delete("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Then
        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

}