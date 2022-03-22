package org.ahpuh.surf.acceptance.category;

import org.ahpuh.surf.acceptance.AcceptanceTest;
import org.ahpuh.surf.common.fixture.AfterLoginAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.ahpuh.surf.common.fixture.TUser.USER_1;
import static org.hamcrest.Matchers.is;

public class CategoryControllerTest extends AcceptanceTest {

    @DisplayName("카테고리 생성")
    @Nested
    class 카테고리_생성 {

        @Test
        void 카테고리_생성_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료();

            // When
            action.카테고리_생성_요청();

            // Then
            USER_1.response.statusCode(201);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "카테고리이름은최대30글자까지입니다.이것은31글자이구요..")
        void 카테고리명_최소1_최대30자_실패(final String categoryName) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료();

            // When
            action.카테고리_생성_요청_name(categoryName);

            // Then
            USER_1.response.statusCode(400);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "#",
                "#G00000",
                "#GGGGGG",
                "#0000000",
                "000000",
                "잘못된색깔코드"})
        void 카테고리_색깔코드_실패(final String colorCode) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료();

            // When
            action.카테고리_생성_요청_colorCode(colorCode);

            // Then
            USER_1.response.statusCode(400);
        }
    }

    @DisplayName("카테고리 수정")
    @Nested
    class 카테고리_수정 {

        @Test
        void 카테고리_수정_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.카테고리_수정_요청();

            // Then
            USER_1.response.statusCode(200);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "카테고리이름은최대30글자까지입니다.이것은31글자이구요..")
        void 카테고리명_최소1_최대30자_실패(final String categoryName) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.카테고리_수정_요청_name(categoryName);

            // Then
            USER_1.response.statusCode(400);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "#",
                "#G00000",
                "#GGGGGG",
                "#0000000",
                "000000",
                "잘못된색깔코드"})
        void 카테고리_색깔코드_실패(final String colorCode) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.카테고리_수정_요청_colorCode(colorCode);

            // Then
            USER_1.response.statusCode(400);
        }
    }

    @DisplayName("카테고리 삭제")
    @Nested
    class 카테고리_삭제 {

        @Test
        void 카테고리_삭제_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.카테고리_삭제_요청();

            // Then
            USER_1.response.statusCode(204);
        }
    }

    @DisplayName("내 모든 카테고리 조회")
    @Nested
    class 내_모든_카테고리_조회 {

        @Test
        void 내_모든_카테고리_조회_성공() {
            // Given : 카테고리 2개 생성
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .카테고리_생성_완료();

            // When
            action.내_모든_카테고리_조회_요청();

            // Then
            USER_1.response.statusCode(200)
                    .body("size()", is(2));
        }
    }

    @DisplayName("내 모든 카테고리 각각의 게시글 개수 및 평균점수 조회")
    @Nested
    class 내_모든_카테고리_각각의_게시글_개수_및_평균점수_조회 {

        @Test
        void 내_모든_카테고리_각각의_게시글_개수_및_평균점수_조회_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료()
                    .게시글_생성_완료(100)
                    .게시글_생성_완료(90);

            // When
            action.내_모든_카테고리_각각의_게시글_개수_및_평균점수_조회();

            // Then
            USER_1.response.statusCode(200)
                    .body("size()", is(1))
                    .body("[0].postCount", is(2))
                    .body("[0].averageScore", is(95));
        }
    }
}
