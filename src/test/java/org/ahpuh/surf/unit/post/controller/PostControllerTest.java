package org.ahpuh.surf.unit.post.controller;

import org.ahpuh.surf.category.dto.CategorySimpleDto;
import org.ahpuh.surf.common.cursor.CursorResult;
import org.ahpuh.surf.jwt.JwtAuthenticationToken;
import org.ahpuh.surf.post.dto.request.PostRequestDto;
import org.ahpuh.surf.post.dto.response.*;
import org.ahpuh.surf.unit.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.ahpuh.surf.common.factory.MockFileFactory.createMultipartFileImage1;
import static org.ahpuh.surf.common.factory.MockJwtFactory.createJwtToken;
import static org.ahpuh.surf.common.factory.MockPostFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerTest extends ControllerTest {

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

        @DisplayName("createPost 요청은")
        @Nested
        class CreatePostRequest {

            @DisplayName("게시글을 생성할 수 있다_MultipartFile O")
            @Test
            void testCreatePost_WithMultipartFile() throws Exception {
                // Given
                final PostRequestDto request = createMockPostRequestDto();
                final MockMultipartFile file = createMultipartFileImage1();

                // When
                final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts")
                        .file(file)
                        .file("request", objectMapper.writeValueAsBytes(request))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN));

                // Then
                perform.andExpect(status().isCreated())
                        .andDo(print());
                verify(postService, times(1))
                        .create(anyLong(), any(PostRequestDto.class), any(MockMultipartFile.class));

                perform.andDo(document("post/createWithFile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                        ),
                        requestPartBody("file"),
                        requestPartFields("request",
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 id"),
                                fieldWithPath("selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location")
                        )));
            }

            @DisplayName("게시글을 생성할 수 있다_MultipartFile X")
            @Test
            void testCreatePost_NoMultipartFile() throws Exception {
                // Given
                final PostRequestDto request = createMockPostRequestDto();

                // When
                final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts")
                        .file("request", objectMapper.writeValueAsBytes(request))
                        .header(HttpHeaders.AUTHORIZATION, TOKEN));

                // Then
                perform.andExpect(status().isCreated())
                        .andDo(print());
                verify(postService, times(1))
                        .create(anyLong(), any(PostRequestDto.class), any());

                perform.andDo(document("post/createOnlyDto",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                        ),
                        requestPartFields("request",
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 id"),
                                fieldWithPath("selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location")
                        )));
            }
        }

        @DisplayName("updatePost 요청은")
        @Nested
        class UpdatePostRequest {

            @DisplayName("게시글을 수정할 수 있다_MultipartFile O")
            @Test
            void testUpdatePost_WithMultipartFile() throws Exception {
                // Given
                final PostRequestDto request = createMockPostRequestDto();
                final MockMultipartFile file = createMultipartFileImage1();

                // When
                final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts/{postId}", 1L)
                        .file(file)
                        .file("request", objectMapper.writeValueAsBytes(request))
                        .with(requestMethod -> {
                            requestMethod.setMethod("PUT");
                            return requestMethod;
                        })
                        .header(HttpHeaders.AUTHORIZATION, TOKEN));

                // Then
                perform.andExpect(status().isOk())
                        .andDo(print());
                verify(postService, times(1))
                        .update(anyLong(), any(PostRequestDto.class), any(MockMultipartFile.class));

                perform.andDo(document("post/updateWithFile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                        ),
                        requestPartBody("file"),
                        requestPartFields("request",
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 id"),
                                fieldWithPath("selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                        )));
            }

            @DisplayName("게시글을 수정할 수 있다_MultipartFile X")
            @Test
            void testUpdatePost_NoMultipartFile() throws Exception {
                // Given
                final PostRequestDto request = createMockPostRequestDto();

                // When
                final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts/{postId}", 1L)
                        .file("request", objectMapper.writeValueAsBytes(request))
                        .with(requestMethod -> {
                            requestMethod.setMethod("PUT");
                            return requestMethod;
                        })
                        .header(HttpHeaders.AUTHORIZATION, TOKEN));

                // Then
                perform.andExpect(status().isOk())
                        .andDo(print());
                verify(postService, times(1))
                        .update(anyLong(), any(PostRequestDto.class), any());

                perform.andDo(document("post/updateOnlyDto",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                        ),
                        requestPartFields("request",
                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 id"),
                                fieldWithPath("selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                        )));
            }
        }

        @DisplayName("해당 게시글을 상세 조회할 수 있다.")
        @Test
        void testReadPost() throws Exception {
            // Given
            final PostReadResponseDto response = createMockPostReadResponseDto();
            given(postService.readPost(any(), any()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/{postId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .readPost(anyLong(), anyLong());

            perform.andDo(document("post/readPost",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    pathParameters(
                            parameterWithName("postId").description("게시글 id")
                    ),
                    responseFields(
                            fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 id"),
                            fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id"),
                            fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 id"),
                            fieldWithPath("selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜"),
                            fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                            fieldWithPath("score").type(JsonFieldType.NUMBER).description("게시글 성장 점수"),
                            fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("이미지 주소"),
                            fieldWithPath("fileUrl").type(JsonFieldType.STRING).description("파일 주소"),
                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 생성 시간"),
                            fieldWithPath("favorite").type(JsonFieldType.BOOLEAN).description("게시글 즐겨찾기 여부"),
                            fieldWithPath("likeId").type(JsonFieldType.NUMBER).description("게시글 좋아요 id"),
                            fieldWithPath("isLiked").type(JsonFieldType.BOOLEAN).description("게시글 좋아요 여부")
                    )));
        }

        @DisplayName("게시글을 삭제할 수 있다.")
        @Test
        void testDeletePost() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(delete("/api/v1/posts/{postId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isNoContent())
                    .andDo(print());
            verify(postService, times(1))
                    .delete(anyLong(), anyLong());

            perform.andDo(document("post/delete",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    pathParameters(
                            parameterWithName("postId").description("게시글 id")
                    )));
        }

        @DisplayName("내 게시글을 즐겨찾기에 추가할 수 있다.")
        @Test
        void testMakeFavorite() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(post("/api/v1/posts/{postId}/favorite", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .makeFavorite(anyLong(), anyLong());

            perform.andDo(document("post/makeFavorite",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    pathParameters(
                            parameterWithName("postId").description("게시글 id")
                    )));
        }

        @DisplayName("내 게시글을 즐겨찾기에서 삭제할 수 있다.")
        @Test
        void testCancelFavorite() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(delete("/api/v1/posts/{postId}/favorite", 1L)
                    .header(HttpHeaders.AUTHORIZATION, TOKEN));

            // Then
            perform.andExpect(status().isNoContent())
                    .andDo(print());
            verify(postService, times(1))
                    .cancelFavorite(anyLong(), anyLong());

            perform.andDo(document("post/cancelFavorite",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    pathParameters(
                            parameterWithName("postId").description("게시글 id")
                    )));
        }

        @DisplayName("해당 월의 모든 게시글을 조회할 수 있다.")
        @Test
        void testGetPostOfMonth() throws Exception {
            // Given
            final List<PostsOfMonthResponseDto> response = createMockPostsOfMonthResponseDtos();
            given(postService.getPostsOfMonth(anyLong(), anyInt(), anyInt()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/month")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("year", "2022")
                    .param("month", "2"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .getPostsOfMonth(anyLong(), anyInt(), anyInt());

            perform.andDo(document("post/getPostOfMonth",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestParameters(
                            parameterWithName("year").description("년도"),
                            parameterWithName("month").description("월")
                    ),
                    responseFields(
                            fieldWithPath("[].categoryName").type(JsonFieldType.STRING).description("카테고리명")
                                    .optional(),
                            fieldWithPath("[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                    .optional(),
                            fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("게시글 id")
                                    .optional(),
                            fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글 내용")
                                    .optional(),
                            fieldWithPath("[].score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                                    .optional(),
                            fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("이미지 주소")
                                    .optional(),
                            fieldWithPath("[].fileUrl").type(JsonFieldType.STRING).description("파일 주소")
                                    .optional(),
                            fieldWithPath("[].selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜")
                                    .optional()
                    )));
        }

        @DisplayName("해당 카테고리의 최신 게시글 점수를 조회할 수 있다.")
        @Test
        void testGetRecentScoreByAllPostsOfCategory() throws Exception {
            // Given
            final PostsRecentScoreResponseDto response = createMockPostsRecentScoreResponseDto();
            given(postService.getRecentScore(anyLong()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/recentScore")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("categoryId", "1"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .getRecentScore(anyLong());

            perform.andDo(document("post/getRecentScoreByAllPostsOfCategory",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestParameters(
                            parameterWithName("categoryId").description("카테고리 id")
                    ),
                    responseFields(
                            fieldWithPath("recentScore").type(JsonFieldType.NUMBER).description("최근 게시글 점수")
                                    .optional()
                    )));
        }

        @DisplayName("해당 년도의 각 날마다 게시글 개수를 조회할 수 있다.")
        @Test
        void testGetPostCountsOfYear() throws Exception {
            // Given
            final List<PostCountResponseDto> response = createMockPostCountResponseDto();
            given(postService.getPostCountsOfYear(anyInt(), anyLong()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/calendarGraph")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("year", "2022")
                    .param("userId", "1"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .getPostCountsOfYear(anyInt(), anyLong());

            perform.andDo(document("post/getPostCountsOfYear",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestParameters(
                            parameterWithName("year").description("해당 년도"),
                            parameterWithName("userId").description("유저 id")
                    ),
                    responseFields(
                            fieldWithPath("[].date").type(JsonFieldType.STRING).description("날짜")
                                    .optional(),
                            fieldWithPath("[].count").type(JsonFieldType.NUMBER).description("게시글 개수")
                                    .optional()
                    )));
        }

        @DisplayName("해당 유저의 각 카테고리마다 게시글들의 점수를 조회할 수 있다.")
        @Test
        void testGetScoresOfCategory() throws Exception {
            // Given
            final List<CategorySimpleDto> response = createMockCategorySimpleDto();
            given(postService.getScoresOfCategoryByUser(anyLong()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/score")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("userId", "1"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .getScoresOfCategoryByUser(anyLong());

            perform.andDo(document("post/getScoresOfCategory",
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
                            fieldWithPath("[].categoryName").type(JsonFieldType.STRING).description("카테고리명")
                                    .optional(),
                            fieldWithPath("[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                    .optional(),
                            fieldWithPath("[].postScores[].selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜")
                                    .optional(),
                            fieldWithPath("[].postScores[].score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                                    .optional()
                    )));
        }

        @DisplayName("모든 게시글을 최신순으로 조회할 수 있다.")
        @Test
        void testRecentAllPosts() throws Exception {
            // Given
            final CursorResult<RecentPostResponseDto> response = createMockRecentAllPosts();
            given(postService.recentAllPosts(anyLong(), anyLong()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/recent")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("cursorId", "1"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .recentAllPosts(anyLong(), anyLong());

            perform.andDo(document("post/recentAllPosts",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestParameters(
                            parameterWithName("cursorId").description("커서 id -> 처음 조회할 때는 0 입력, 다음 페이지를 조회할 때는 이전 페이지 마지막 게시글의 postId를 cursorId로 입력")
                    ),
                    responseFields(
                            fieldWithPath("values[].userId").type(JsonFieldType.NUMBER).description("유저 id")
                                    .optional(),
                            fieldWithPath("values[].userName").type(JsonFieldType.STRING).description("유저 이름")
                                    .optional(),
                            fieldWithPath("values[].profilePhotoUrl").type(JsonFieldType.STRING).description("유저 프로필사진 url 주소")
                                    .optional(),
                            fieldWithPath("values[].followId").type(JsonFieldType.NUMBER).description("팔로우 id")
                                    .optional(),
                            fieldWithPath("values[].categoryName").type(JsonFieldType.STRING).description("카테고리명")
                                    .optional(),
                            fieldWithPath("values[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                    .optional(),
                            fieldWithPath("values[].postId").type(JsonFieldType.NUMBER).description("게시글 id")
                                    .optional(),
                            fieldWithPath("values[].content").type(JsonFieldType.STRING).description("게시글 내용")
                                    .optional(),
                            fieldWithPath("values[].score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                                    .optional(),
                            fieldWithPath("values[].imageUrl").type(JsonFieldType.STRING).description("이미지 주소")
                                    .optional(),
                            fieldWithPath("values[].fileUrl").type(JsonFieldType.STRING).description("파일 주소")
                                    .optional(),
                            fieldWithPath("values[].selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜")
                                    .optional(),
                            fieldWithPath("values[].createdAt").type(JsonFieldType.STRING).description("게시글 생성 시간")
                                    .optional(),
                            fieldWithPath("values[].likeId").type(JsonFieldType.NUMBER).description("게시글 좋아요 id")
                                    .optional(),
                            fieldWithPath("values[].isLiked").type(JsonFieldType.BOOLEAN).description("게시글 좋아요 여부")
                                    .optional(),
                            fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                    )));
        }

        @DisplayName("내가 팔로우한 모든 유저의 게시글을 최신순으로 조회할 수 있다.")
        @Test
        void testFollowExplore() throws Exception {
            // Given
            final CursorResult<ExploreResponseDto> response = createMockExploreResponseDto();
            given(postService.followExplore(anyLong(), anyLong()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/follow/posts")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("cursorId", "1"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .followExplore(anyLong(), anyLong());

            perform.andDo(document("post/followExplore",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestParameters(
                            parameterWithName("cursorId").description("커서 id -> 처음 조회할 때는 0 입력, 다음 페이지를 조회할 때는 이전 페이지 마지막 게시글의 postId를 cursorId로 입력")
                    ),
                    responseFields(
                            fieldWithPath("values[].userId").type(JsonFieldType.NUMBER).description("유저 id")
                                    .optional(),
                            fieldWithPath("values[].userName").type(JsonFieldType.STRING).description("유저 이름")
                                    .optional(),
                            fieldWithPath("values[].profilePhotoUrl").type(JsonFieldType.STRING).description("유저 프로필사진 url 주소")
                                    .optional(),
                            fieldWithPath("values[].categoryName").type(JsonFieldType.STRING).description("카테고리명")
                                    .optional(),
                            fieldWithPath("values[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                    .optional(),
                            fieldWithPath("values[].postId").type(JsonFieldType.NUMBER).description("게시글 id")
                                    .optional(),
                            fieldWithPath("values[].content").type(JsonFieldType.STRING).description("게시글 내용")
                                    .optional(),
                            fieldWithPath("values[].score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                                    .optional(),
                            fieldWithPath("values[].imageUrl").type(JsonFieldType.STRING).description("이미지 주소")
                                    .optional(),
                            fieldWithPath("values[].fileUrl").type(JsonFieldType.STRING).description("파일 주소")
                                    .optional(),
                            fieldWithPath("values[].selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜")
                                    .optional(),
                            fieldWithPath("values[].createdAt").type(JsonFieldType.STRING).description("게시글 생성 시간")
                                    .optional(),
                            fieldWithPath("values[].likeId").type(JsonFieldType.NUMBER).description("게시글 좋아요 id")
                                    .optional(),
                            fieldWithPath("values[].isLiked").type(JsonFieldType.BOOLEAN).description("게시글 좋아요 여부")
                                    .optional(),
                            fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                    )));
        }

        @DisplayName("해당 유저의 모든 게시글을 최신순으로 조회할 수 있다.")
        @Test
        void testGetAllPostByUser() throws Exception {
            // Given
            final CursorResult<AllPostResponseDto> response = createMockAllPostResponseDto();
            given(postService.getAllPostByUser(anyLong(), anyLong(), anyLong()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/all")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("userId", "1")
                    .param("cursorId", "1"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .getAllPostByUser(anyLong(), anyLong(), anyLong());

            perform.andDo(document("post/getAllPostByUser",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestParameters(
                            parameterWithName("userId").description("유저 id"),
                            parameterWithName("cursorId").description("커서 id -> 처음 조회할 때는 0 입력, 다음 페이지를 조회할 때는 이전 페이지 마지막 게시글의 postId를 cursorId로 입력")
                    ),
                    responseFields(
                            fieldWithPath("values[].categoryName").type(JsonFieldType.STRING).description("카테고리명")
                                    .optional(),
                            fieldWithPath("values[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                    .optional(),
                            fieldWithPath("values[].postId").type(JsonFieldType.NUMBER).description("게시글 id")
                                    .optional(),
                            fieldWithPath("values[].content").type(JsonFieldType.STRING).description("게시글 내용")
                                    .optional(),
                            fieldWithPath("values[].score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                                    .optional(),
                            fieldWithPath("values[].imageUrl").type(JsonFieldType.STRING).description("이미지 주소")
                                    .optional(),
                            fieldWithPath("values[].fileUrl").type(JsonFieldType.STRING).description("파일 주소")
                                    .optional(),
                            fieldWithPath("values[].selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜")
                                    .optional(),
                            fieldWithPath("values[].likeId").type(JsonFieldType.NUMBER).description("게시글 좋아요 id")
                                    .optional(),
                            fieldWithPath("values[].isLiked").type(JsonFieldType.BOOLEAN).description("게시글 좋아요 여부")
                                    .optional(),
                            fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                    )));
        }

        @DisplayName("해당 카테고리의 모든 게시글을 최신순으로 조회할 수 있다.")
        @Test
        void testGetAllPostByCategory() throws Exception {
            // Given
            final CursorResult<AllPostResponseDto> response = createMockAllPostResponseDto();
            given(postService.getAllPostByCategory(anyLong(), anyLong(), anyLong()))
                    .willReturn(response);

            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts")
                    .header(HttpHeaders.AUTHORIZATION, TOKEN)
                    .param("categoryId", "1")
                    .param("cursorId", "1"));

            // Then
            perform.andExpect(status().isOk())
                    .andDo(print());
            verify(postService, times(1))
                    .getAllPostByCategory(anyLong(), anyLong(), anyLong());

            perform.andDo(document("post/getAllPostByCategory",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                    ),
                    requestParameters(
                            parameterWithName("categoryId").description("카테고리 id"),
                            parameterWithName("cursorId").description("커서 id -> 처음 조회할 때는 0 입력, 다음 페이지를 조회할 때는 이전 페이지 마지막 게시글의 postId를 cursorId로 입력")
                    ),
                    responseFields(
                            fieldWithPath("values[].categoryName").type(JsonFieldType.STRING).description("카테고리명")
                                    .optional(),
                            fieldWithPath("values[].colorCode").type(JsonFieldType.STRING).description("지정 색깔코드")
                                    .optional(),
                            fieldWithPath("values[].postId").type(JsonFieldType.NUMBER).description("게시글 id")
                                    .optional(),
                            fieldWithPath("values[].content").type(JsonFieldType.STRING).description("게시글 내용")
                                    .optional(),
                            fieldWithPath("values[].score").type(JsonFieldType.NUMBER).description("게시글 성장 점수")
                                    .optional(),
                            fieldWithPath("values[].imageUrl").type(JsonFieldType.STRING).description("이미지 주소")
                                    .optional(),
                            fieldWithPath("values[].fileUrl").type(JsonFieldType.STRING).description("파일 주소")
                                    .optional(),
                            fieldWithPath("values[].selectedDate").type(JsonFieldType.STRING).description("게시글 지정 날짜")
                                    .optional(),
                            fieldWithPath("values[].likeId").type(JsonFieldType.NUMBER).description("게시글 좋아요 id")
                                    .optional(),
                            fieldWithPath("values[].isLiked").type(JsonFieldType.BOOLEAN).description("게시글 좋아요 여부")
                                    .optional(),
                            fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                    )));
        }
    }

    @DisplayName("비로그인 상태로")
    @Nested
    class NotLoginYet {

        @DisplayName("게시글을 생성할 수 없다.")
        @Test
        void testCreatePost_Fail() throws Exception {
            // Given
            final PostRequestDto request = createMockPostRequestDto();
            final MockMultipartFile file = createMultipartFileImage1();

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts")
                    .file(file)
                    .file("request", objectMapper.writeValueAsBytes(request)));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .create(any(), any(), any());
        }

        @DisplayName("게시글을 수정할 수 없다.")
        @Test
        void testUpdatePost_Fail() throws Exception {
            // Given
            final PostRequestDto request = createMockPostRequestDto();
            final MockMultipartFile file = createMultipartFileImage1();

            // When
            final ResultActions perform = mockMvc.perform(multipart("/api/v1/posts/{postId}", 1L)
                    .file(file)
                    .file("request", objectMapper.writeValueAsBytes(request))
                    .with(requestMethod -> {
                        requestMethod.setMethod("PUT");
                        return requestMethod;
                    }));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .update(any(), any(), any());
        }

        @DisplayName("해당 게시글을 상세 조회할 수 없다.")
        @Test
        void testReadPost_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    get("/api/v1/posts/{postId}", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .readPost(any(), any());
        }

        @DisplayName("게시글을 삭제할 수 없다.")
        @Test
        void testDeletePost_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    delete("/api/v1/posts/{postId}", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .delete(any(), any());
        }

        @DisplayName("내 게시글을 즐겨찾기에 추가할 수 없다.")
        @Test
        void testMakeFavorite_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    post("/api/v1/posts/{postId}/favorite", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .makeFavorite(any(), any());
        }

        @DisplayName("내 게시글을 즐겨찾기에서 삭제할 수 없다.")
        @Test
        void testCancelFavorite_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(
                    delete("/api/v1/posts/{postId}/favorite", 1L));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .cancelFavorite(any(), any());
        }

        @DisplayName("해당 월의 모든 게시글을 조회할 수 없다.")
        @Test
        void testGetPostOfMonth_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/month")
                    .param("year", "2022")
                    .param("month", "2"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .getPostsOfMonth(any(), any(), any());
        }

        @DisplayName("해당 카테고리의 최신 게시글 점수를 조회할 수 없다.")
        @Test
        void testGetRecentScoreByAllPostsOfCategory_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/recentScore")
                    .param("categoryId", "1"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .getRecentScore(any());
        }

        @DisplayName("해당 년도의 각 날마다 게시글 개수를 조회할 수 없다.")
        @Test
        void testGetPostCountsOfYear_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/calendarGraph")
                    .param("year", "2022")
                    .param("userId", "1"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .getPostCountsOfYear(anyInt(), any());
        }

        @DisplayName("해당 유저의 각 카테고리마다 게시글들의 점수를 조회할 수 없다.")
        @Test
        void testGetScoresOfCategory_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/score")
                    .param("userId", "1"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .getScoresOfCategoryByUser(any());
        }

        @DisplayName("모든 게시글을 최신순으로 조회할 수 없다.")
        @Test
        void testRecentAllPosts_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/recent")
                    .param("cursorId", "1"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .recentAllPosts(any(), any());
        }

        @DisplayName("내가 팔로우한 모든 유저의 게시글을 최신순으로 조회할 수 없다.")
        @Test
        void testFollowExplore_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/follow/posts")
                    .param("cursorId", "1"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .followExplore(any(), any());
        }

        @DisplayName("해당 유저의 모든 게시글을 최신순으로 조회할 수 없다.")
        @Test
        void testGetAllPostByUser_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts/all")
                    .param("userId", "1")
                    .param("cursorId", "1"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .getAllPostByUser(any(), any(), any());
        }

        @DisplayName("해당 카테고리의 모든 게시글을 최신순으로 조회할 수 없다.")
        @Test
        void testGetAllPostByCategory_Fail() throws Exception {
            // When
            final ResultActions perform = mockMvc.perform(get("/api/v1/posts")
                    .param("categoryId", "1")
                    .param("cursorId", "1"));

            // Then
            perform.andExpect(status().isForbidden())
                    .andDo(print());
            verify(postService, times(0))
                    .getAllPostByCategory(any(), any(), any());
        }
    }
}
