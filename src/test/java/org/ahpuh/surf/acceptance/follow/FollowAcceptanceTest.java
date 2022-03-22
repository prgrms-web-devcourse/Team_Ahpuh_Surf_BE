package org.ahpuh.surf.acceptance.follow;

import org.ahpuh.surf.acceptance.AcceptanceTest;
import org.ahpuh.surf.common.fixture.AfterLoginAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.ahpuh.surf.common.fixture.TUser.*;
import static org.hamcrest.Matchers.is;

public class FollowAcceptanceTest extends AcceptanceTest {

    @DisplayName("팔로우")
    @Nested
    class 팔로우 {

        @Test
        void 팔로우_성공() {
            // Given
            USER_1.회원가입_완료();

            // When
            USER_2.로그인_완료().팔로우_요청(USER_1);

            // Then
            USER_2.response.statusCode(201);
        }
    }

    @DisplayName("언팔로우")
    @Nested
    class 언팔로우 {

        @Test
        void 언팔로우_성공() {
            // Given
            USER_1.회원가입_완료();
            final AfterLoginAction action = USER_2.로그인_완료().팔로우_완료(USER_1);

            // When
            action.언팔로우_요청(USER_1);

            // Then
            USER_2.response.statusCode(204);
        }
    }

    @DisplayName("해당 유저의 팔로워 조회")
    @Nested
    class 해당_유저의_팔로워_조회 {

        @Test
        void 해당_유저의_팔로워_조회_성공() {
            // Given
            USER_1.회원가입_완료();
            USER_2.로그인_완료().팔로우_완료(USER_1);
            USER_3.로그인_완료().팔로우_완료(USER_1);
            USER_4.로그인_완료().팔로우_완료(USER_1);

            // When
            USER_1.로그인_완료().해당_유저의_팔로워_조회_요청();

            // Then
            USER_1.response.statusCode(200)
                    .body("size()", is(3))
                    .body("[0].userId", is(4))
                    .body("[1].userId", is(3))
                    .body("[2].userId", is(2));
        }
    }

    @DisplayName("해당 유저가 팔로잉한 유저 조회")
    @Nested
    class 해당_유저가_팔로잉한_유저_조회 {

        @Test
        void 해당_유저가_팔로잉한_유저_조회_성공() {
            // Given
            USER_1.회원가입_완료();
            USER_2.회원가입_완료();
            USER_3.회원가입_완료();
            final AfterLoginAction action = USER_4.로그인_완료()
                    .팔로우_완료(USER_1)
                    .팔로우_완료(USER_2)
                    .팔로우_완료(USER_3);

            // When
            action.해당_유저가_팔로잉한_유저_조회_요청();

            // Then
            USER_4.response.statusCode(200)
                    .body("size()", is(3))
                    .body("[0].userId", is(3))
                    .body("[1].userId", is(2))
                    .body("[2].userId", is(1));
        }
    }
}
