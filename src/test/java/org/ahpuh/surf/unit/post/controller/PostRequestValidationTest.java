package org.ahpuh.surf.unit.post.controller;

import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.unit.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostRequestValidationTest extends ControllerTest {

    private static final String TOKEN = "TestToken";

    @BeforeEach
    void setUp() {
        final JwtAuthenticationToken authentication = createJwtToken(1L, "testEmail");
        final SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);
    }

    @DisplayName("PostRequestDto @Valid")
    @Nested
    class PostRequestDtoValidTest {

        @DisplayName("카테고리 id는 null을 허용하지 않는다.")
        @ParameterizedTest
        @NullSource
        void categoryId_Fail(final Long categoryId) throws Exception {
            // Given
            final PostRequestDto request = new PostRequestDto(
                    categoryId,
                    LocalDate.now().toString(),
                    "content",
                    100);

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts")
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("selectedDate는 null과 blank를 허용하지 않는다.")
        @ParameterizedTest
        @NullAndEmptySource
        void categoryName_Fail(final String selectedDate) throws Exception {
            // Given
            final PostRequestDto request = new PostRequestDto(
                    1L,
                    selectedDate,
                    "content",
                    100);

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts")
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("게시글 내용은 null과 blank를 허용하지 않는다.")
        @ParameterizedTest
        @NullAndEmptySource
        void content_Fail(final String content) throws Exception {
            // Given
            final PostRequestDto request = new PostRequestDto(
                    1L,
                    LocalDate.now().toString(),
                    content,
                    100);

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts")
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("게시글 내용은 최대 500자까지 허용한다.")
        @Test
        void contentLength_Fail() throws Exception {
            // Given
            final PostRequestDto request = new PostRequestDto(
                    1L,
                    LocalDate.now().toString(),
                    "a".repeat(501),
                    100);

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts")
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("게시글 성장 점수는 0 ~ 100까지 허용한다.")
        @ParameterizedTest
        @ValueSource(ints = {-1, 101})
        void score_Fail(final int score) throws Exception {
            // Given
            final PostRequestDto request = new PostRequestDto(
                    1L,
                    LocalDate.now().toString(),
                    "content",
                    score);

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts")
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}
