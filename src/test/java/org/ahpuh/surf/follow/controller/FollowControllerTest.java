package org.ahpuh.surf.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ahpuh.surf.follow.repository.FollowRepository;
import org.ahpuh.surf.user.controller.UserController;
import org.ahpuh.surf.user.dto.UserLoginRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private FollowController followController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserController userController;

    private Long userId1;
    private Long userId2;
    private Long userId3;
    private String token;

    @BeforeEach
    void setUp() {
        userId1 = userRepository.save(User.builder()
                        .email("user1@naver.com")
                        .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                        .build())
                .getUserId();
        userId2 = userRepository.save(User.builder()
                        .email("user2@naver.com")
                        .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                        .build())
                .getUserId();
        userId3 = userRepository.save(User.builder()
                        .email("user3@naver.com")
                        .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                        .build())
                .getUserId();

        final UserLoginRequestDto userJoinRequest = UserLoginRequestDto.builder()
                .email("user1@naver.com")
                .password("testpw")
                .build();
        token = userController.login(userJoinRequest)
                .getBody()
                .getToken();
    }

    @Test
    @Order(1)
    @DisplayName("팔로우를 할 수 있다.")
    @Transactional
    void testFollow() throws Exception {
        // Given
        final User user1 = userRepository.getById(userId1);
        final User user2 = userRepository.getById(userId2);

        assertAll("beforeFollow",
                () -> assertThat(user1.getEmail(), is("user1@naver.com")),
                () -> assertThat(user1.getFollowedUsers().size(), is(0)),
                () -> assertThat(user2.getEmail(), is("user2@naver.com")),
                () -> assertThat(user2.getFollowingUsers().size(), is(0))
        );

        // When
        mockMvc.perform(post("/api/v1/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userId2))
                        .header("token", token))
                .andExpect(status().isCreated())
                .andDo(print());

        // Then
        assertAll("afterFollow",
                () -> assertThat(user1.getFollowedUsers().size(), is(1)),
                () -> assertThat(user1.getFollowedUsers().get(0).getUser().getUserId(), is(userId1)),
                () -> assertThat(user1.getFollowedUsers().get(0).getFollowedUser().getUserId(), is(userId2)),
                () -> assertThat(user2.getFollowingUsers().size(), is(1)),
                () -> assertThat(user2.getFollowingUsers().get(0).getUser().getUserId(), is(userId1)),
                () -> assertThat(user2.getFollowingUsers().get(0).getFollowedUser().getUserId(), is(userId2))
        );
    }

    @Test
    @Order(2)
    @DisplayName("언팔로우를 할 수 있다.")
    @Transactional
    void testUnfollow() throws Exception {
        // Given
        mockMvc.perform(post("/api/v1/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userId2))
                        .header("token", token))
                .andExpect(status().isCreated())
                .andDo(print());

        assertAll("beforeFollow",
                () -> assertThat(userRepository.getById(userId1).getFollowedUsers().size(), is(1)),
                () -> assertThat(userRepository.getById(userId2).getFollowingUsers().size(), is(1)),
                () -> assertThat(followRepository.findAll().size(), is(1))
        );
        final Long followid = followRepository.findAll().get(0).getFollowId();

        // When
        mockMvc.perform(delete("/api/v1/follow/{followId}", followid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("token", token))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Then
        assertAll("afterFollow",
                () -> assertThat(userRepository.getById(userId1).getFollowedUsers().size(), is(0)),
                () -> assertThat(userRepository.getById(userId2).getFollowingUsers().size(), is(0)),
                () -> assertThat(followRepository.findAll().size(), is(0))
        );
    }

}