package org.ahpuh.surf.common.fixture;

import io.restassured.http.Method;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.ahpuh.surf.common.factory.MockPostFactory.*;
import static org.ahpuh.surf.common.mapper.DtoObjectMapper.mapToString;

public class PostAction {

    private final TUser user;

    public PostAction(final TUser user) {
        this.user = user;
    }

    public void 게시글_생성_요청_With_File(final File file) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("file",
                        file,
                        MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("request",
                        mapToString(createMockPostRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.POST, "/api/v1/posts")
                .then();
    }

    public void 게시글_생성_요청_No_File() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.POST, "/api/v1/posts")
                .then();
    }

    public void 게시글_생성_요청_selectedDate(final String selectedDate) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDtoWithSelectedDate(selectedDate)),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.POST, "/api/v1/posts")
                .then();
    }

    public void 게시글_생성_요청_content(final String content) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDtoWithContent(content)),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.POST, "/api/v1/posts")
                .then();
    }

    public void 게시글_생성_요청_score(final int score) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDtoWithScore(score)),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.POST, "/api/v1/posts")
                .then();
    }

    public void 게시글_수정_요청_With_File(final File file) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("file",
                        file,
                        MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("request",
                        mapToString(createMockPostRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/posts/{postId}", 1L)
                .then();
    }

    public void 게시글_수정_요청_No_File() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/posts/{postId}", 1L)
                .then();
    }

    public void 게시글_수정_요청_selectedDate(final String selectedDate) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDtoWithSelectedDate(selectedDate)),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/posts/{postId}", 1L)
                .then();
    }

    public void 게시글_수정_요청_content(final String content) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDtoWithContent(content)),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/posts/{postId}", 1L)
                .then();
    }

    public void 게시글_수정_요청_score(final int score) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDtoWithScore(score)),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/posts/{postId}", 1L)
                .then();
    }

    public void 게시글_조회_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.GET, "/api/v1/posts/{postId}", 1L)
                .then();
    }

    public void 게시글_삭제_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.DELETE, "/api/v1/posts/{postId}", 1L)
                .then();
    }

    public void 즐겨찾기_추가_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.POST, "/api/v1/posts/{postId}/favorite", 1L)
                .then();
    }

    public void 즐겨찾기_삭제_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.DELETE, "/api/v1/posts/{postId}/favorite", 1L)
                .then();
    }

    public void 특정_Month_모든_게시글_조회_요청(final int year, final int month) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("year", year)
                .param("month", month)
                .request(Method.GET, "/api/v1/posts/month")
                .then();
    }

    public void 해당_카테고리의_최신_게시글_점수_조회_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("categoryId", 1L)
                .request(Method.GET, "/api/v1/recentScore")
                .then();
    }

    public void 특정_Year의_날짜별_게시글_개수_조회_요청(final int year) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("year", year)
                .param("userId", 1L)
                .request(Method.GET, "/api/v1/posts/calendarGraph")
                .then();
    }

    public void 해당_유저의_카테고리별_게시글_점수_조회_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("userId", 1L)
                .request(Method.GET, "/api/v1/posts/score")
                .then();
    }

    public void 전체_최신_게시글_둘러보기_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("cursorId", 0)
                .request(Method.GET, "/api/v1/posts/recent")
                .then();
    }

    public void 내가_팔로우한_유저의_게시글_둘러보기_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("cursorId", 0)
                .request(Method.GET, "/api/v1/follow/posts")
                .then();
    }

    public void 해당_유저의_전체_게시글_조회_요청(final TUser user) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("userId", user.userId)
                .param("cursorId", 0)
                .request(Method.GET, "/api/v1/posts/all")
                .then();
    }

    public void 해당_카테고리의_전체_게시글_조회_요청(final Long categoryId) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("categoryId", categoryId)
                .param("cursorId", 0)
                .request(Method.GET, "/api/v1/posts")
                .then();
    }
}
