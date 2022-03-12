package org.ahpuh.surf.unit.follow.controller;

import org.ahpuh.surf.common.factory.MockFollowFactory;
import org.ahpuh.surf.follow.dto.request.FollowRequestDto;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.unit.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import static org.ahpuh.surf.common.factory.MockFollowFactory.createMockFollowResponseDto;
import static org.ahpuh.surf.common.factory.MockFollowFactory.createMockFollowUserResponseDtos;
import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FollowControllerTest extends ControllerTest {

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

        @DisplayName("팔로우를 할 수 있다.")
        @Test
        void testFollow() throws Exception {
            // Given
            final FollowRequestDto request = MockFollowFactory.createMockFollowRequestDto();
            given(followService.follow(anyLong(), anyLong()))
                    .willReturn(createMockFollowResponseDto());

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/follow")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isCreated())
                    .andDo(print());
            verify(followService, times(1))
                    .follow(anyLong(), anyLong());

            // RestDocs
            perform.andDo(FollowDocumentation.follow());
        }

        @DisplayName("언팔로우를 할 수 있다.")
        @Test
        void testUnfollow() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(delete("/api/v1/follow/{userId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isNoContent())
                    .andDo(print());
            verify(followService, times(1))
                    .unfollow(anyLong(), anyLong());

            // RestDocs
            perform.andDo(FollowDocumentation.unfollow());
        }

        @DisplayName("팔로워들의 정보를 조회할 수 있다.")
        @Test
        void testFindFollowersList() throws Exception {
            // Given
            given(followService.findFollowerList(anyLong()))
                    .willReturn(createMockFollowUserResponseDtos());

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/users/{userId}/followers", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(followService, times(1))
                    .findFollowerList(anyLong());

            // RestDocs
            perform.andDo(FollowDocumentation.findFollowersList());
        }

        @DisplayName("해당 유저가 팔로우한 유저들의 정보를 조회할 수 있다.")
        @Test
        void testFindFollowingList() throws Exception {
            // Given
            given(followService.findFollowingList(anyLong()))
                    .willReturn(createMockFollowUserResponseDtos());

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/users/{userId}/following", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(followService, times(1))
                    .findFollowingList(anyLong());

            // RestDocs
            perform.andDo(FollowDocumentation.findFollowingList());
        }
    }

    @DisplayName("비로그인 상태로")
    @Nested
    class NotLoginYet {

        @DisplayName("팔로우를 할 수 없다.")
        @Test
        void testFollow_Fail() throws Exception {
            // Given
            final FollowRequestDto request = MockFollowFactory.createMockFollowRequestDto();

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/follow")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(followService, times(0))
                    .follow(any(), any());
        }

        @DisplayName("언팔로우를 할 수 없다.")
        @Test
        void testUnfollow_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    delete("/api/v1/follow/{userId}", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(followService, times(0))
                    .unfollow(any(), any());
        }

        @DisplayName("팔로워들의 정보를 조회할 수 없다.")
        @Test
        void testFindFollowersList_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    get("/api/v1/users/{userId}/followers", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(followService, times(0))
                    .findFollowerList(any());
        }

        @DisplayName("해당 유저가 팔로우한 유저들의 정보를 조회할 수 없다.")
        @Test
        void testFindFollowingList_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    get("/api/v1/users/{userId}/following", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(followService, times(0))
                    .findFollowingList(any());
        }
    }
}
