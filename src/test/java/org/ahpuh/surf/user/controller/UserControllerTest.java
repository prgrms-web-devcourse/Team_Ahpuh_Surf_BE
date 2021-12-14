package org.ahpuh.surf.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.UserLoginRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    Long userId1;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserController userController;

    @BeforeEach
    void setUp() {
        userId1 = userRepository.save(User.builder()
                        .email("test@naver.com")
                        .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                        .userName("name")
                        .build())
                .getUserId();
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
                () -> assertThat(userRepository.findAll().size(), is(2)),
                () -> assertThat(userRepository.findAll().get(1).getEmail(), is("test1@naver.com"))
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

//    @Test
//    @DisplayName("회원정보를 수정할 수 있다.")
//    @Transactional
//    void testUpdateUser() throws Exception {
//        // Given
//        final UserLoginRequestDto req = UserLoginRequestDto.builder()
//                .email("test@naver.com")
//                .password("testpw")
//                .build();
//        final String token = userController.login(req).getBody().getToken();
//
//        final User user = userRepository.findById(userId1).get();
//
//        assertAll("beforeUpdate",
//                () -> assertThat(user.getUserName(), is("name")),
//                () -> assertThat(user.getAboutMe(), is(nullValue())),
//                () -> assertThat(user.getAccountPublic(), is(true))
//        );
//
//        final UserUpdateRequestDto request = UserUpdateRequestDto.builder()
//                .userName("수정된 name")
//                .password(null)
//                .url("내 블로그 주소")
//                .aboutMe("수정된 소개글")
//                .accountPublic(false)
//                .build();
//
//        // When
//        mockMvc.perform(put("/api/v1/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//                        .header("token", token))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//        // Then
//        assertAll("userUpdate",
//                () -> assertThat(user.getUserName(), is("수정된 name")),
//                () -> assertThat(user.getProfilePhotoUrl(), is(nullValue())),
//                () -> assertThat(user.getAboutMe(), is("수정된 소개글")),
//                () -> assertThat(user.getAccountPublic(), is(false))
//        );
//    }

    @Test
    @DisplayName("회원을 삭제(softDelete) 할 수 있다.")
    @Transactional
    void testDeleteUser() throws Exception {
        // Given
        final UserLoginRequestDto req = UserLoginRequestDto.builder()
                .email("test@naver.com")
                .password("testpw")
                .build();
        final String token = userController.login(req).getBody().getToken();

        // When
        mockMvc.perform(delete("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", token))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Then
        assertThat(userRepository.findAll().size(), is(0));
    }

}