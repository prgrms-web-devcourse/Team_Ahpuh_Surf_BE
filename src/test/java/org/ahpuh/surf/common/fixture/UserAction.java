package org.ahpuh.surf.common.fixture;

import io.restassured.http.Method;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.ahpuh.surf.common.factory.MockUserFactory.createUserUpdateRequestDto;
import static org.ahpuh.surf.common.mapper.DtoObjectMapper.mapToString;

public class UserAction {

    private final TUser user;

    public UserAction(final TUser user) {
        this.user = user;
    }

    public void 유저조회_요청(final Long userId) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.GET, "/api/v1/users/{userId}", userId)
                .then();
    }

    public void 유저정보_수정_요청_With_File(final File file) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
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
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createUserUpdateRequestDto()),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.PUT, "/api/v1/users")
                .then();
    }

    public void 회원탈퇴_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.DELETE, "/api/v1/users")
                .then();
    }
}
