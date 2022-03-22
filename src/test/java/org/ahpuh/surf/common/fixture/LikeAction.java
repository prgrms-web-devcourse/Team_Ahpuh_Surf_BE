package org.ahpuh.surf.common.fixture;

import io.restassured.http.Method;
import org.apache.http.HttpHeaders;

import static io.restassured.RestAssured.given;

public class LikeAction {

    private final TUser user;

    public LikeAction(final TUser user) {
        this.user = user;
    }

    public void 게시글_좋아요_요청(final Long postId) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.POST, "/api/v1/posts/{postId}/like", postId)
                .then();
    }

    public void 게시글_좋아요_취소_요청(final Long postId, final Long likeId) {
        user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.DELETE, "/api/v1/posts/{postId}/unlike/{likeId}", postId, likeId)
                .then();
    }
}
