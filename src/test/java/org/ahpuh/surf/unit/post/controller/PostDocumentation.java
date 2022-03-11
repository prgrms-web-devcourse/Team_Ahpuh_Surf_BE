package org.ahpuh.surf.unit.post.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class PostDocumentation {

    protected static RestDocumentationResultHandler createWithFile() {
        return document("post/createWithFile",
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
                ));
    }

    protected static RestDocumentationResultHandler createWithOnlyDto() {
        return document("post/createWithOnlyDto",
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
                ));
    }

    protected static RestDocumentationResultHandler updateWithFile() {
        return document("post/updateWithFile",
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
                ));
    }

    protected static RestDocumentationResultHandler updateWithOnlyDto() {
        return document("post/updateWithOnlyDto",
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
                ));
    }

    protected static RestDocumentationResultHandler readPost() {
        return document("post/readPost",
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
                ));
    }

    protected static RestDocumentationResultHandler delete() {
        return document("post/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ));
    }

    protected static RestDocumentationResultHandler makeFavorite() {
        return document("post/makeFavorite",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ));
    }

    protected static RestDocumentationResultHandler cancelFavorite() {
        return document("post/cancelFavorite",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("token")
                ),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ));
    }

    protected static RestDocumentationResultHandler getPostOfMonth() {
        return document("post/getPostOfMonth",
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
                ));
    }

    protected static RestDocumentationResultHandler getRecentScoreByAllPostsOfCategory() {
        return document("post/getRecentScoreByAllPostsOfCategory",
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
                ));
    }

    protected static RestDocumentationResultHandler getPostCountsOfYear() {
        return document("post/getPostCountsOfYear",
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
                ));
    }

    protected static RestDocumentationResultHandler getScoresOfCategory() {
        return document("post/getScoresOfCategory",
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
                ));
    }

    protected static RestDocumentationResultHandler recentAllPosts() {
        return document("post/recentAllPosts",
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
                ));
    }

    protected static RestDocumentationResultHandler followExplore() {
        return document("post/followExplore",
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
                ));
    }

    protected static RestDocumentationResultHandler getAllPostByUser() {
        return document("post/getAllPostByUser",
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
                ));
    }

    protected static RestDocumentationResultHandler getAllPostByCategory() {
        return document("post/getAllPostByCategory",
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
                ));
    }
}
