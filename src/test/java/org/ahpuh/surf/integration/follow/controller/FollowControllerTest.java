package org.ahpuh.surf.integration.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ahpuh.surf.follow.entity.Follow;
import org.ahpuh.surf.follow.repository.FollowRepository;
import org.ahpuh.surf.user.controller.UserController;
import org.ahpuh.surf.user.dto.request.UserLoginRequestDto;
import org.ahpuh.surf.user.entity.User;
import org.ahpuh.surf.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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
    private UserRepository userRepository;
    @Autowired
    private UserController userController;

    private User user1;
    private User user2;
    private User user3;
    private Long userId1;
    private Long userId2;
    private String token;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder()
                .email("user1@naver.com")
                .userName("name")
                .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                .build());
        userId1 = user1.getUserId();
        user2 = userRepository.save(User.builder()
                .email("user2@naver.com")
                .userName("name")
                .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                .build());
        userId2 = user2.getUserId();
        user3 = userRepository.save(User.builder()
                .email("user3@naver.com")
                .userName("name")
                .password("$2a$10$1dmE40BM1RD2lUg.9ss24eGs.4.iNYq1PwXzqKBfIXNRbKCKliqbG") // testpw
                .build());

        final UserLoginRequestDto userJoinRequest = UserLoginRequestDto.builder()
                .email("user1@naver.com")
                .password("testpw")
                .build();
        token = userController.login(userJoinRequest)
                .getBody()
                .getToken();
    }

    @Test
    @DisplayName("팔로우를 할 수 있다.")
    @Transactional
    void testFollow() throws Exception {
        // When
        mockMvc.perform(post("/api/v1/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userId2))
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isCreated())
                .andDo(print());

        // Then
        final List<Follow> allFollow = followRepository.findAll();
        assertAll("afterFollow",
                () -> assertThat(allFollow.size()).isEqualTo(1),
                () -> assertThat(allFollow.get(0).getUser()).isEqualTo(user1),
                () -> assertThat(allFollow.get(0).getFollowedUser()).isEqualTo(user2)
        );
    }

    @Test
    @DisplayName("언팔로우를 할 수 있다.")
    @Transactional
    void testUnfollow() throws Exception {
        // Given
        followRepository.save(Follow.builder()
                .user(user1)
                .followedUser(user2)
                .build());

        final List<Follow> follows = followRepository.findAll();
        assertAll("beforeUnfollow",
                () -> assertThat(follows.size()).isEqualTo(1),
                () -> assertThat(follows.get(0).getUser()).isEqualTo(user1),
                () -> assertThat(follows.get(0).getFollowedUser()).isEqualTo(user2)
        );

        // When
        mockMvc.perform(delete("/api/v1/follow/{userId}", userId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Then
        assertThat(followRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 user를 팔로우 한 user 목록을 조회할 수 있다.")
    @Transactional
    void testFindFollowerList() throws Exception {
        // Given
        followRepository.save(Follow.builder()
                .user(user1)
                .followedUser(user2)
                .build());
        followRepository.save(Follow.builder()
                .user(user1)
                .followedUser(user3)
                .build());

        final List<Follow> allFollow = followRepository.findAll();
        assertAll("user1이 user2, user3을 팔로우",
                () -> assertThat(allFollow.size()).isEqualTo(2),
                () -> assertThat(allFollow.get(0).getUser()).isEqualTo(user1),
                () -> assertThat(allFollow.get(0).getFollowedUser()).isEqualTo(user2),
                () -> assertThat(allFollow.get(1).getUser()).isEqualTo(user1),
                () -> assertThat(allFollow.get(1).getFollowedUser()).isEqualTo(user3)
        );

        // When, Then
        mockMvc.perform(get("/api/v1/users/{userId}/followers", userId2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("특정 user가 팔로잉 한 user 목록을 조회할 수 있다.")
    @Transactional
    void testFollowingList() throws Exception {
        // Given
        followRepository.save(Follow.builder()
                .user(user1)
                .followedUser(user2)
                .build());
        followRepository.save(Follow.builder()
                .user(user1)
                .followedUser(user3)
                .build());

        final List<Follow> allFollow = followRepository.findAll();
        assertAll("user1이 user2, user3을 팔로우",
                () -> assertThat(allFollow.size()).isEqualTo(2),
                () -> assertThat(allFollow.get(0).getUser()).isEqualTo(user1),
                () -> assertThat(allFollow.get(0).getFollowedUser()).isEqualTo(user2),
                () -> assertThat(allFollow.get(1).getUser()).isEqualTo(user1),
                () -> assertThat(allFollow.get(1).getFollowedUser()).isEqualTo(user3)
        );

        // When, Then
        mockMvc.perform(get("/api/v1/users/{userId}/following", userId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

}