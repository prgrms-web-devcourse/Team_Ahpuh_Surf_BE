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

    public void 팔로우_완료(final TUser target) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createMockFollowRequestDtoWithTargetId(target.userId))
                .request(Method.POST, "/api/v1/follow")
                .then();
    }
}
