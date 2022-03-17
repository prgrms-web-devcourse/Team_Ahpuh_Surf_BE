package org.ahpuh.surf.common.fixture;

import io.restassured.http.Method;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.ahpuh.surf.common.factory.MockUserFactory.*;
import static org.ahpuh.surf.common.mapper.DtoObjectMapper.mapToString;

public enum TUser {
    USER_1("user1"),
    USER_2("user2"),
    USER_3("user3"),
    USER_4("user4"),
    USER_5("user5");

    public String email;
    public String userName;
    public ValidatableResponse response;

    TUser(final String userName) {
        this.userName = userName;
        this.email = userName + "@naver.com";
    }

    public void 회원가입_요청() {
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserJoinRequestDtoWithEmail(email))
                .request(Method.POST, "/api/v1/users")
                .then();
    }

    public void 회원가입_요청(final String email) {
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserJoinRequestDtoWithEmail(email))
                .request(Method.POST, "/api/v1/users")
                .then();
    }

    public void 회원가입_완료() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserJoinRequestDtoWithEmail(email))
                .request(Method.POST, "/api/v1/users");
    }

    public void 회원가입_하지_않음() {}

    public void 로그인_요청() {
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserLoginRequestDto(email))
                .request(Method.POST, "/api/v1/users/login")
                .then();
    }

    public void 로그인_요청(final String password) {
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserLoginRequestDto(email, password))
                .request(Method.POST, "/api/v1/users/login")
                .then();
    }

    public void 유저조회_요청(final String token, final Long userId) {
        this.response = given()
                .header(HttpHeaders.AUTHORIZATION, token)
                .request(Method.GET, "/api/v1/users/{userId}", userId)
                .then();
    }

    public void 유저정보_수정_요청_With_File(final String token, final File file) {
        this.response = given()
                .header(HttpHeaders.AUTHORIZATION, token)
                .multiPart("file",
                        file,
                        MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("request",
                        mapToString(createUserUpdateRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/users")
                .then();
    }

    public void 유저정보_수정_요청_No_File(final String token) {
        this.response = given()
                .header(HttpHeaders.AUTHORIZATION, token)
                .multiPart("request",
                        mapToString(createUserUpdateRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/users")
                .then();
    }

    public void 회원탈퇴_요청(final String token) {
        this.response = given()
                .header(HttpHeaders.AUTHORIZATION, token)
                .request(Method.DELETE, "/api/v1/users")
                .then();
    }
}
