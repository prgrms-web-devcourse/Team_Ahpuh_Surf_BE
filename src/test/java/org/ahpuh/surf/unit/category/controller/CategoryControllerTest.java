package org.ahpuh.surf.unit.category.controller;

import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.ahpuh.surf.category.dto.response.AllCategoryByUserResponseDto;
import org.ahpuh.surf.category.dto.response.CategoryDetailResponseDto;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.unit.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.ahpuh.surf.common.factory.MockCategoryFactory.*;
import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryControllerTest extends ControllerTest {

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

        @DisplayName("카테고리를 생성할 수 있다.")
        @Test
        void testCreateCategory() throws Exception {
            // Given
            final CategoryCreateRequestDto request = createMockCategoryCreateRequestDto();

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/categories")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isCreated())
                    .andDo(print());
            verify(categoryService, times(1))
                    .createCategory(anyLong(), any(CategoryCreateRequestDto.class));

            perform.andDo(document("category/create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestFields(
                            fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리명"),
                            fieldWithPath("colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                    ),
                    responseHeaders(
                            headerWithName(HttpHeaders.LOCATION).description("location")
                    )));
        }

        @DisplayName("카테고리를 수정할 수 있다.")
        @Test
        void testUpdateCategory() throws Exception {
            // Given
            final CategoryUpdateRequestDto request = createMockCategoryUpdateRequestDto();

            // When
            final ResultActions perform = mockMvc.perform(put("/api/v1/categories/{categoryId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(categoryService, times(1))
                    .updateCategory(anyLong(), any(CategoryUpdateRequestDto.class));

            perform.andDo(document("category/update",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestFields(
                            fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리명"),
                            fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("카테고리 공개여부"),
                            fieldWithPath("colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                    )));
        }

        @DisplayName("카테고리를 삭제할 수 있다.")
        @Test
        void testDeleteCategory() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(delete("/api/v1/categories/{categoryId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isNoContent())
                    .andDo(print());
            verify(categoryService, times(1))
                    .deleteCategory(anyLong());

            perform.andDo(document("category/delete",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    pathParameters(
                            parameterWithName("categoryId").description("카테고리 id")
                    )));
        }

        @DisplayName("내 모든 카테고리를 반환할 수 있다.")
        @Test
        void testFindAllCategoryByUser() throws Exception {
            // Given
            final AllCategoryByUserResponseDto response = createMockAllCategoryByUserResponseDto();
            given(categoryService.findAllCategoryByUser(anyLong()))
                    .willReturn(List.of(response, response));

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/categories")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(categoryService, times(1))
                    .findAllCategoryByUser(anyLong());

            perform.andDo(document("category/findAll",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    responseFields(
                            fieldWithPath("[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 id")
                                    .optional(),
                            fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리명")
                                    .optional(),
                            fieldWithPath("[].isPublic").type(JsonFieldType.BOOLEAN).description("카테고리 공개여부")
                                    .optional(),
                            fieldWithPath("[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                    .optional()
                    )));
        }

        @DisplayName("해당 유저의 모든 카테고리 평균점수, 게시글 개수를 조회할 수 있다.")
        @Test
        void testGetCategoryDashboard() throws Exception {
            // Given
            final CategoryDetailResponseDto response = createMockCategoryDetailResponseDto();
            given(categoryService.getCategoryDashboard(anyLong()))
                    .willReturn(List.of(response, response));

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/categories/dashboard")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("userId", "1"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(categoryService, times(1))
                    .getCategoryDashboard(anyLong());

            perform.andDo(document("category/getDashboardInfo",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestParameters(
                            parameterWithName("userId").description("유저 id")
                    ),
                    responseFields(
                            fieldWithPath("[].categoryId").type(JsonFieldType.NUMBER).description("카테고리 id")
                                    .optional(),
                            fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리명")
                                    .optional(),
                            fieldWithPath("[].isPublic").type(JsonFieldType.BOOLEAN).description("카테고리 공개여부")
                                    .optional(),
                            fieldWithPath("[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                    .optional(),
                            fieldWithPath("[].averageScore").type(JsonFieldType.NUMBER).description("해당 카테고리의 게시글 평균점수")
                                    .optional(),
                            fieldWithPath("[].postCount").type(JsonFieldType.NUMBER).description("해당 카테고리의 게시글 갯수")
                                    .optional()
                    )));
        }
    }

    @DisplayName("비로그인 상태로")
    @Nested
    class NotLoginYet {

        @DisplayName("카테고리를 생성할 수 없다.")
        @Test
        void testCreateCategory_Fail() throws Exception {
            // Given
            final CategoryCreateRequestDto request = createMockCategoryCreateRequestDto();

            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(categoryService, times(0))
                    .createCategory(any(), any());
        }

        @DisplayName("카테고리를 수정할 수 없다.")
        @Test
        void testUpdateCategory_Fail() throws Exception {
            // Given
            final CategoryUpdateRequestDto request = createMockCategoryUpdateRequestDto();

            // When
            final ResultActions perform = mockMvc.perform(put("/api/v1/categories/{categoryId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(categoryService, times(0))
                    .updateCategory(any(), any());
        }

        @DisplayName("카테고리를 삭제할 수 없다.")
        @Test
        void testDeleteCategory_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    delete("/api/v1/categories/{categoryId}", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(categoryService, times(0))
                    .deleteCategory(any());
        }

        @DisplayName("내 모든 카테고리를 반환할 수 없다.")
        @Test
        void testFindAllCategoryByUser_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    get("/api/v1/categories"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(categoryService, times(0))
                    .findAllCategoryByUser(any());
        }

        @DisplayName("해당 유저의 모든 카테고리 평균점수, 게시글 개수를 조회할 수 없다.")
        @Test
        void testGetCategoryDashboard_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/categories/dashboard")
                    .param("userId", "1"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(categoryService, times(0))
                    .getCategoryDashboard(any());
        }
    }
}
