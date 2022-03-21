package org.ahpuh.surf.common.fixture;

import io.restassured.http.Method;
import org.ahpuh.surf.category.dto.request.CategoryCreateRequestDto;
import org.ahpuh.surf.category.dto.request.CategoryUpdateRequestDto;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategoryCreateRequestDto;
import static org.ahpuh.surf.common.factory.MockCategoryFactory.createMockCategoryUpdateRequestDto;

public class CategoryAction {

    private final TUser user;

    public CategoryAction(final TUser user) {
        this.user = user;
    }

    public void 카테고리_생성_요청() {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createMockCategoryCreateRequestDto())
                .request(Method.POST, "/api/v1/categories")
                .then();
    }

    public void 카테고리_생성_요청_name(final String categoryName) {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new CategoryCreateRequestDto(categoryName, "#000000"))
                .request(Method.POST, "/api/v1/categories")
                .then();
    }

    public void 카테고리_생성_요청_colorCode(final String colorCode) {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new CategoryCreateRequestDto("categoryName", colorCode))
                .request(Method.POST, "/api/v1/categories")
                .then();
    }

    public void 카테고리_생성_완료() {
        user.response = given().header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createMockCategoryCreateRequestDto())
                .request(Method.POST, "/api/v1/categories")
                .then();
    }

    public void 카테고리_수정_요청() {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createMockCategoryUpdateRequestDto())
                .request(Method.PUT, "/api/v1/categories/{categoryId}", 1L)
                .then();
    }

    public void 카테고리_수정_요청_name(final String categoryName) {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new CategoryUpdateRequestDto(categoryName, true, "#000000"))
                .request(Method.PUT, "/api/v1/categories/{categoryId}", 1L)
                .then();
    }

    public void 카테고리_수정_요청_colorCode(final String colorCode) {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new CategoryUpdateRequestDto("categoryName", true, colorCode))
                .request(Method.PUT, "/api/v1/categories/{categoryId}", 1L)
                .then();
    }

    public void 카테고리_삭제_요청() {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.DELETE, "/api/v1/categories/{categoryId}", 1L)
                .then();
    }

    public void 내_모든_카테고리_조회_요청() {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .request(Method.GET, "/api/v1/categories")
                .then();
    }

    public void 내_모든_카테고리_각각의_게시글_개수_및_평균점수_조회() {
        this.user.response = given()
                .header(HttpHeaders.AUTHORIZATION, user.token)
                .param("userId", user.userId)
                .request(Method.GET, "/api/v1/categories/dashboard")
                .then();
    }
}
