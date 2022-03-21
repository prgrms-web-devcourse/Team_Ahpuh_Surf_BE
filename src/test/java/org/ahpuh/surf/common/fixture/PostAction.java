package org.ahpuh.surf.common.fixture;

import io.restassured.http.Method;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.ahpuh.surf.common.factory.MockPostFactory.createMockPostRequestDtoWithScore;
import static org.ahpuh.surf.common.mapper.DtoObjectMapper.mapToString;

public class PostAction {

    private final TUser user;

    public PostAction(final TUser user) {
        this.user = user;
    }

    public void 게시글_생성_완료(final int postScore) {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .multiPart("request",
                        mapToString(createMockPostRequestDtoWithScore(postScore)),
                        MediaType.APPLICATION_JSON_VALUE)
                .request(Method.POST, "/api/v1/posts")
                .then();
    }
}
