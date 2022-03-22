package org.ahpuh.surf.common.fixture;

import io.restassured.http.Method;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.ahpuh.surf.common.factory.MockFollowFactory.createMockFollowRequestDtoWithTargetId;

public class FollowAction {

    private final TUser user;

    public FollowAction(final TUser user) {
        this.user = user;
    }

    public void 팔로우_요청(final TUser target) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createMockFollowRequestDtoWithTargetId(target.userId))
                .request(Method.POST, "/api/v1/follow")
                .then();
    }

    public void 언팔로우_요청(final TUser target) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.DELETE, "/api/v1/follow/{userId}", target.userId)
                .then();
    }

    public void 해당_유저의_팔로워_조회_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.GET, "/api/v1/users/{userId}/followers", user.userId)
                .then();
    }

    public void 해당_유저가_팔로잉한_유저_조회_요청() {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.GET, "/api/v1/users/{userId}/following", user.userId)
                .then();
    }
}
