package org.ahpuh.backend.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ahpuh.backend.user.dto.UserJoinRequestDto;
import org.ahpuh.backend.user.dto.UserLoginRequestDto;
import org.ahpuh.backend.user.repository.UserRepository;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입을 할 수 있다.")
    @Transactional
    void join() throws Exception {
        final UserJoinRequestDto req = UserJoinRequestDto.builder()
                .email("test1@naver.com")
                .userName("최승은1")
                .password("test111")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(print());

        assertAll("userJoin",
                () -> assertThat(userRepository.findAll().size(), is(1)),
                () -> assertThat(userRepository.findAll().get(0).getEmail(), is("test1@naver.com")),
                () -> assertThat(userRepository.findAll().get(0).getUserName(), is("최승은1"))
        );
    }

    @Test
    @DisplayName("로그인을 할 수 있다.")
    @Transactional
    void login() throws Exception {
        // Given
        final UserJoinRequestDto joinReq = UserJoinRequestDto.builder()
                .email("test1@naver.com")
                .userName("최승은1")
                .password("test111")
                .build();
        userController.join(joinReq);

        // When
        final UserLoginRequestDto req = UserLoginRequestDto.builder()
                .email("test1@naver.com")
                .password("test111")
                .build();

        // Then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(print());
    }

}