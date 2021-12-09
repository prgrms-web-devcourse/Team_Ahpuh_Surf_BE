package org.ahpuh.surf.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ahpuh.surf.user.dto.UserJoinRequestDto;
import org.ahpuh.surf.user.dto.UserLoginRequestDto;
import org.ahpuh.surf.user.dto.UserUpdateRequestDto;
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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
class UserControllerTest {

    Long userId1;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        final User userEntity = User.builder()
                .email("test@naver.com")
                .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                .build();
        userEntity.setPermission("ROLE_USER");
        userId1 = userRepository.save(userEntity).getUserId();
    }

    @Test
    @DisplayName("회원가입을 할 수 있다.")
    @Transactional
    void testJoin() throws Exception {
        final UserJoinRequestDto req = UserJoinRequestDto.builder()
                .email("test1@naver.com")
                .password("test111")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(print());

        assertAll("userJoin",
                () -> assertThat(userRepository.findAll().size(), is(2)),
                () -> assertThat(userRepository.findAll().get(1).getEmail(), is("test1@naver.com"))
        );
    }

    @Test
    @DisplayName("로그인 할 수 있다.")
    @Transactional
    void testLogin() throws Exception {
        final UserLoginRequestDto req = UserLoginRequestDto.builder()
                .email("test@naver.com")
                .password("testpw")
                .build();

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
        final User user = userRepository.findById(userId1).get();

        assertThat(user.getAboutMe(), is(nullValue()));
        assertThat(user.getAccountPublic(), is(true));

        final UserUpdateRequestDto request = UserUpdateRequestDto.builder()
                .userName(user.getUserName())
                .password(user.getPassword())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .url("내 블로그 주소")
                .aboutMe("수정된 소개글")
                .accountPublic(false)
                .build();

        mockMvc.perform(put("/api/v1/users/{userId}", userId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());

        assertAll("userUpdate",
                () -> assertThat(user.getProfilePhotoUrl(), is(nullValue())),
                () -> assertThat(user.getAboutMe(), is("수정된 소개글")),
                () -> assertThat(user.getAccountPublic(), is(false))
        );
    }

    @Test
    @DisplayName("회원을 삭제(softDelete) 할 수 있다.")
    @Transactional
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{userId}", userId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        assertThat(userRepository.findAll().size(), is(0));
    }

}