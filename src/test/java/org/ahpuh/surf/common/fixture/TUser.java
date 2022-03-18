package org.ahpuh.surf.common.fixture;

import io.restassured.http.Method;
import io.restassured.response.ValidatableResponse;
import org.ahpuh.surf.user.dto.response.UserLoginResponseDto;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.ahpuh.surf.common.factory.MockUserFactory.*;
import static org.ahpuh.surf.common.mapper.DtoObjectMapper.mapToString;

public enum TUser {
    USER_1(1L, "user1"),
    USER_2(2L, "user2"),
    USER_3(3L, "user3"),
    USER_4(4L, "user4"),
    USER_5(5L, "user5");

    public Long userId;
    public String email;
    public String userName;
    public String token;
    public ValidatableResponse response;

    TUser(final Long userId, final String userName) {
        this.userId = userId;
        this.userName = userName;
        this.email = userName + "@naver.com";
    }

    public void 회원가입_요청() {
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserJoinRequestDtoWithEmail(this.email))
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

    public void 회원가입_요청_name(final String userName) {
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserJoinRequestDtoWithUserName(userName))
                .request(Method.POST, "/api/v1/users")
                .then();
    }

    public void 회원가입_완료() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserJoinRequestDtoWithEmail(this.email))
                .request(Method.POST, "/api/v1/users");
    }

    public void 회원가입_하지_않음() {
    }

    public void 로그인_요청() {
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserLoginRequestDto(this.email))
                .request(Method.POST, "/api/v1/users/login")
                .then();
    }

    public void 로그인_요청(final String password) {
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserLoginRequestDto(this.email, password))
                .request(Method.POST, "/api/v1/users/login")
                .then();
    }

    public AfterLoginAction 로그인_완료() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserJoinRequestDtoWithEmail(this.email))
                .request(Method.POST, "/api/v1/users");
        this.token = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createUserLoginRequestDto(this.email))
                .request(Method.POST, "/api/v1/users/login")
                .then().statusCode(200)
                .extract().as(UserLoginResponseDto.class)
                .getToken();
        return new AfterLoginAction(this);
    }

    public void 유저조회_요청(final Long userId) {
        this.response = given()
                .header(HttpHeaders.AUTHORIZATION, this.token)
                .request(Method.GET, "/api/v1/users/{userId}", userId)
                .then();
    }

    public void 유저정보_수정_요청_With_File(final File file) {
        this.response = given()
                .header(HttpHeaders.AUTHORIZATION, this.token)
                .multiPart("file",
                        file,
                        MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("request",
                        mapToString(createUserUpdateRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/users")
                .then();
    }

    public void 유저정보_수정_요청_No_File() {
        this.response = given()
                .header(HttpHeaders.AUTHORIZATION, this.token)
                .multiPart("request",
                        mapToString(createUserUpdateRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/users")
                .then();
    }

    public void 회원탈퇴_요청() {
        this.response = given()
                .header(HttpHeaders.AUTHORIZATION, this.token)
                .request(Method.DELETE, "/api/v1/users")
                .then();
    }
}
