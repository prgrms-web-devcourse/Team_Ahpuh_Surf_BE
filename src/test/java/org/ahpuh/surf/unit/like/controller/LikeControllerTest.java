package org.ahpuh.surf.unit.like.controller;

import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.unit.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.ahpuh.surf.common.factory.MockLikeFactory.createMockLikeResponseDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LikeControllerTest extends ControllerTest {

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

        @DisplayName("좋아요를 할 수 있다.")
        @Test
        void testLike() throws Exception {
            // Given
            given(likeService.like(anyLong(), anyLong()))
                    .willReturn(createMockLikeResponseDto());

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/posts/{postId}/like", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(likeService, times(1))
                    .like(anyLong(), anyLong());

            // RestDocs
            perform.andDo(LikeDocumentation.like());
        }

        @DisplayName("언팔로우를 할 수 있다.")
        @Test
        void testUnlike() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(delete("/api/v1/posts/{postId}/unlike/{likeId}", 1L, 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isNoContent())
                    .andDo(print());
            verify(likeService, times(1))
                    .unlike(anyLong());

            // RestDocs
            perform.andDo(LikeDocumentation.unlike());
        }
    }

    @DisplayName("비로그인 상태로")
    @Nested
    class NotLoginYet {

        @DisplayName("좋아요를 할 수 없다.")
        @Test
        void testLike_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    post("/api/v1/posts/{postId}/like", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(likeService, times(0))
                    .like(any(), any());
        }

        @DisplayName("언팔로우를 할 수 없다.")
        @Test
        void testUnlike_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    delete("/api/v1/posts/{postId}/like/{likeId}", 1L, 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(likeService, times(0))
                    .unlike(any());
        }
    }
}
