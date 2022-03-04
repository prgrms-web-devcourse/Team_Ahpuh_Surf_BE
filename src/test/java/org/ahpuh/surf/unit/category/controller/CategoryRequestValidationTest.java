package org.ahpuh.surf.unit.category.controller;

import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.unit.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryRequestValidationTest extends ControllerTest {

    private static final String TOKEN = "TestToken";

    @BeforeEach
    void setUp() {
        final JwtAuthenticationToken authentication = createJwtToken(1L, "testEmail");
        final SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication())
                .willReturn(authentication);
    }

    @DisplayName("CategoryCreateRequestDto @Valid")
    @Nested
    class CategoryCreateRequestDtoTest {

        @DisplayName("카테고리 이름은 최소 1, 최대 30 글자이다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "카테고리이름은최대30글자까지입니다.이것은31글자이구요..")
        void categoryName_Fail(final String categoryName) throws Exception {
            // Given
            final CategoryCreateRequestDto request = new CategoryCreateRequestDto(categoryName, "#000000");

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/categories")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("카테고리 색깔코드는 16진수의 헥스 컬러코드이다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "#",
                "#G00000",
                "#GGGGGG",
                "#0000000",
                "000000",
                "잘못된색깔코드"
        })
        void colorCode_Fail(final String colorCode) throws Exception {
            // Given
            final CategoryCreateRequestDto request = new CategoryCreateRequestDto("카테고리이름", colorCode);

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/categories")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @DisplayName("CategoryUpdateRequestDto @Valid")
    @Nested
    class CategoryUpdateRequestDtoTest {

        @DisplayName("카테고리 이름은 최소 1, 최대 30 글자이다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "카테고리이름은최대30글자까지입니다.이것은31글자이구요..")
        void categoryName_Fail(final String categoryName) throws Exception {
            // Given
            final CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(categoryName, true, "#000000");

            // When
            final ResultActions perform = mockMvc.perform(put("/api/v1/categories/{categoryId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("카테고리 공개여부는 null을 허용하지 않는다.")
        @ParameterizedTest
        @NullSource
        void isPublic_Fail(final Boolean isPublic) throws Exception {
            // Given
            final CategoryUpdateRequestDto request = new CategoryUpdateRequestDto("카테고리이름", isPublic, "#000000");

            // When
            final ResultActions perform = mockMvc.perform(put("/api/v1/categories/{categoryId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("카테고리 색깔코드는 16진수의 헥스 컬러코드이다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "#",
                "#G00000",
                "#GGGGGG",
                "#0000000",
                "000000",
                "잘못된색깔코드"
        })
        void colorCode_Fail(final String colorCode) throws Exception {
            // Given
            final CategoryUpdateRequestDto request = new CategoryUpdateRequestDto("카테고리이름", true, colorCode);

            // When
            final ResultActions perform = mockMvc.perform(put("/api/v1/categories/{categoryId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}
