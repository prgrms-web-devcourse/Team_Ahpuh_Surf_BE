package org.ahpuh.surf.acceptance.post;

import org.ahpuh.surf.acceptance.AcceptanceTest;
import org.ahpuh.surf.common.fixture.AfterLoginAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.time.LocalDate;

import static org.ahpuh.surf.common.factory.MockFileFactory.createTextFile;
import static org.ahpuh.surf.common.fixture.TUser.USER_1;
import static org.ahpuh.surf.common.fixture.TUser.USER_2;
import static org.hamcrest.Matchers.is;

public class PostAcceptanceTest extends AcceptanceTest {

    @DisplayName("게시글 생성")
    @Nested
    class 게시글_생성 {

        @Test
        void 게시글_생성_파일_첨부O_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();
            final File file = createTextFile();

            // When
            action.게시글_생성_요청_With_File(file);

            // Then
            USER_1.response.statusCode(201);
        }

        @Test
        void 게시글_생성_파일_첨부X_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.게시글_생성_요청_No_File();

            // Then
            USER_1.response.statusCode(201);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 게시글_지정날짜_필수입력(final String selectedDate) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.게시글_생성_요청_selectedDate(selectedDate);

            // Then
            USER_1.response.statusCode(400);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 게시글_내용_필수입력(final String content) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.게시글_생성_요청_content(content);

            // Then
            USER_1.response.statusCode(400);
        }

        @Test
        void 게시글_내용_최대500자() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.게시글_생성_요청_content("a".repeat(501));

            // Then
            USER_1.response.statusCode(400);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 101})
        void 게시글_성장점수_최소0_최대100(final int score) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료().카테고리_생성_완료();

            // When
            action.게시글_생성_요청_score(score);

            // Then
            USER_1.response.statusCode(400);
        }
    }

    @DisplayName("게시글 수정")
    @Nested
    class 게시글_수정 {

        @Test
        void 게시글_수정_파일_첨부O_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();
            final File file = createTextFile();

            // When
            action.게시글_수정_요청_With_File(file);

            // Then
            USER_1.response.statusCode(200);
        }

        @Test
        void 게시글_수정_파일_첨부X_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            action.게시글_수정_요청_No_File();

            // Then
            USER_1.response.statusCode(200);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 게시글_지정날짜_필수입력(final String selectedDate) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            action.게시글_수정_요청_selectedDate(selectedDate);

            // Then
            USER_1.response.statusCode(400);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 게시글_내용_필수입력(final String content) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            action.게시글_수정_요청_content(content);

            // Then
            USER_1.response.statusCode(400);
        }

        @Test
        void 게시글_내용_최대500자() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            action.게시글_수정_요청_content("a".repeat(501));

            // Then
            USER_1.response.statusCode(400);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 101})
        void 게시글_성장점수_최소0_최대100(final int score) {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            action.게시글_수정_요청_score(score);

            // Then
            USER_1.response.statusCode(400);
        }
    }

    @DisplayName("게시글 조회")
    @Nested
    class 게시글_조회 {

        @Test
        void 게시글_조회_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            action.게시글_조회_요청();

            // Then
            USER_1.response.statusCode(200);
        }
    }

    @DisplayName("게시글 삭제")
    @Nested
    class 게시글_삭제 {

        @Test
        void 게시글_삭제_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            action.게시글_삭제_요청();

            // Then
            USER_1.response.statusCode(204);
        }
    }

    @DisplayName("내 게시글 즐겨찾기 추가")
    @Nested
    class 내_게시글_즐겨찾기_추가 {

        @Test
        void 내_게시글_즐겨찾기_추가_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료();

            // When
            action.즐겨찾기_추가_요청();

            // Then
            USER_1.response.statusCode(200);
        }
    }

    @DisplayName("내 게시글 즐겨찾기 삭제")
    @Nested
    class 내_게시글_즐겨찾기_삭제 {

        @Test
        void 내_게시글_즐겨찾기_삭제_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료()
                    .즐겨찾기_추가_완료();

            // When
            action.즐겨찾기_삭제_요청();

            // Then
            USER_1.response.statusCode(204);
        }
    }

    @DisplayName("특정 Month의 모든 내 게시글 조회")
    @Nested
    class 특정_Month_모든_게시글_조회 {

        @Test
        void 특정_Month_모든_게시글_조회_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료()
                    .게시글_생성_완료();
            final LocalDate now = LocalDate.now();
            final int year = now.getYear();
            final int month = now.getMonth().getValue();

            // When
            action.특정_Month_모든_게시글_조회_요청(year, month);

            // Then
            USER_1.response.statusCode(200)
                    .body("size()", is(2));
        }
    }

    @DisplayName("해당 카테고리의 최신 게시글 점수 조회")
    @Nested
    class 해당_카테고리의_최신_게시글_점수_조회 {

        @Test
        void 해당_카테고리의_최신_게시글_점수_조회_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료_score(100);

            // When
            action.해당_카테고리의_최신_게시글_점수_조회_요청();

            // Then
            USER_1.response.statusCode(200)
                    .body("recentScore", is(100));
        }
    }

    @DisplayName("특정 Year의 날짜별 게시글 개수 조회")
    @Nested
    class 특정_Year의_날짜별_게시글_개수_조회 {

        @Test
        void 특정_Year의_날짜별_게시글_개수_조회_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료_selectedDate(
                            LocalDate.of(2022, 3, 1).toString())
                    .게시글_생성_완료_selectedDate(
                            LocalDate.of(2022, 3, 1).toString())
                    .게시글_생성_완료_selectedDate(
                            LocalDate.of(2022, 3, 2).toString());

            // When
            action.특정_Year의_날짜별_게시글_개수_조회_요청(2022);

            // Then
            USER_1.response.statusCode(200)
                    .body("[0].date", is("2022-03-01"))
                    .body("[0].count", is(2))
                    .body("[1].date", is("2022-03-02"))
                    .body("[1].count", is(1));
        }
    }

    @DisplayName("해당 유저의 카테고리별 게시글 점수 조회")
    @Nested
    class 해당_유저의_카테고리별_게시글_점수_조회 {

        @Test
        void 해당_유저의_카테고리별_게시글_점수_조회_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료_selectedDate(
                            LocalDate.of(2022, 3, 1).toString())
                    .게시글_생성_완료_selectedDate(
                            LocalDate.of(2022, 3, 2).toString());

            // When
            action.해당_유저의_카테고리별_게시글_점수_조회_요청();

            // Then
            USER_1.response.statusCode(200)
                    .body("size()", is(1))
                    .body("[0].categoryId", is(1))
                    .body("[0].postScores[0].selectedDate", is("2022-03-01"))
                    .body("[0].postScores[0].score", is(100))
                    .body("[0].postScores[1].selectedDate", is("2022-03-02"))
                    .body("[0].postScores[1].score", is(100));
        }
    }

    @DisplayName("전체 최신 게시글 둘러보기")
    @Nested
    class 전체_최신_게시글_둘러보기 {

        @Test
        void 전체_최신_게시글_둘러보기_성공() {
            // Given
            final AfterLoginAction action = USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료()
                    .게시글_생성_완료();

            // When
            action.전체_최신_게시글_둘러보기_요청();

            // Then
            USER_1.response.statusCode(200)
                    .body("size()", is(2));
        }
    }

    @DisplayName("내가 팔로우한 유저의 게시글 둘러보기")
    @Nested
    class 내가_팔로우한_유저의_게시글_둘러보기 {

        @Test
        void 내가_팔로우한_유저의_게시글_둘러보기_성공() {
            // Given
            USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료()
                    .게시글_생성_완료();
            final AfterLoginAction action = USER_2.로그인_완료()
                    .팔로우_완료(USER_1);

            // When
            action.내가_팔로우한_유저의_게시글_둘러보기_요청();

            // Then
            USER_2.response.statusCode(200)
                    .body("size()", is(2));
        }
    }

    @DisplayName("해당 유저의 전체 게시글 조회")
    @Nested
    class 해당_유저의_전체_게시글_조회 {

        @Test
        void 해당_유저의_전체_게시글_조회_성공() {
            // Given
            USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료()
                    .게시글_생성_완료();

            // When
            USER_2.로그인_완료()
                    .해당_유저의_전체_게시글_조회_요청(USER_1);

            // Then
            USER_2.response.statusCode(200)
                    .body("size()", is(2));
        }
    }

    @DisplayName("해당 카테고리의 전체 게시글 조회")
    @Nested
    class 해당_카테고리의_전체_게시글_조회 {

        @Test
        void 해당_카테고리의_전체_게시글_조회_성공() {
            // Given
            USER_1.로그인_완료()
                    .카테고리_생성_완료()
                    .게시글_생성_완료()
                    .게시글_생성_완료();

            // When
            USER_2.로그인_완료()
                    .해당_카테고리의_전체_게시글_조회_요청(1L);

            // Then
            USER_2.response.statusCode(200)
                    .body("size()", is(2));
        }
    }
}
