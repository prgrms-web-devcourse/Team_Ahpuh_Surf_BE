package org.ahpuh.surf.acceptance.user;

import org.ahpuh.surf.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static org.ahpuh.surf.common.factory.MockFileFactory.createImageFile;
import static org.ahpuh.surf.common.factory.MockFileFactory.invalidFile;
import static org.ahpuh.surf.common.fixture.TUser.USER_1;
import static org.ahpuh.surf.common.fixture.TUser.USER_2;

public class UserControllerTest extends AcceptanceTest {

    @DisplayName("회원가입")
    @Nested
    class 회원가입 {

        @Test
        void 회원가입_성공() {
            // When
            USER_1.회원가입_요청();

            // Then
            USER_1.response.statusCode(201);
        }

        @Test
        void 중복된_이메일_실패() {
            // Given
            USER_1.회원가입_완료();
            final String duplicatedEmail = "user1@naver.com";

            // When
            USER_2.회원가입_요청(duplicatedEmail);

            // Then
            USER_2.response.statusCode(400);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "invalidEmail",
                "user1@",
                "@naver.com"
        })
        void 잘못된_이메일_형식_실패(final String invalidEmail) {
            // When
            USER_1.회원가입_요청(invalidEmail);

            // Then
            USER_1.response.statusCode(400);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "userName은_최대_20자까지입니다.")
        void 유저_이름_최대20자_실패(final String invalidUserName) {
            // When
            USER_1.회원가입_요청_name(invalidUserName);

            // Then
            USER_1.response.statusCode(400);
        }
    }

    @DisplayName("로그인")
    @Nested
    class 로그인 {

        @Test
        void 로그인_성공() {
            // Given
            USER_1.회원가입_완료();

            // When
            USER_1.로그인_요청();

            // Then
            USER_1.response.statusCode(200);
        }

        @Test
        void 존재하지_않는_유저_실패() {
            // Given
            USER_1.회원가입_하지_않음();

            // When
            USER_1.로그인_요청();

            // Then
            USER_1.response.statusCode(404);
        }

        @Test
        void 틀린_비밀번호_실패() {
            // Given
            USER_1.회원가입_완료();

            // When
            USER_1.로그인_요청("invalidPassword");

            // Then
            USER_1.response.statusCode(400);
        }
    }

    @DisplayName("유저정보 조회")
    @Nested
    class 유저정보_조회 {

        @Test
        void 유저정보_조회_성공() {
            // Given
            USER_1.로그인_완료();

            // When
            USER_1.유저조회_요청(1L);

            // Then
            USER_1.response.statusCode(200);
        }

        @Test
        void 존재하지_않는_유저_아이디_실패() {
            // Given
            USER_1.로그인_완료();
            final Long invalidUserId = 2L;

            // When
            USER_1.유저조회_요청(invalidUserId);

            // Then
            USER_1.response.statusCode(404);
        }
    }

    @DisplayName("유저정보 수정")
    @Nested
    class 유저정보_수정 {

        @Test
        void 유저정보_수정_프로필이미지_첨부O_성공() {
            // Given
            USER_1.로그인_완료();
            final File profileImage = createImageFile();

            // When
            USER_1.유저정보_수정_요청_With_File(profileImage);

            // Then
            USER_1.response.statusCode(200);
        }

        @Test
        void 유저정보_수정_프로필이미지_첨부X_성공() {
            // Given
            USER_1.로그인_완료();

            // When
            USER_1.유저정보_수정_요청_No_File();

            // Then
            USER_1.response.statusCode(200);
        }

        @Test
        void 유저정보_수정_잘못된_File_실패() {
            // Given
            USER_1.로그인_완료();

            // When
            USER_1.유저정보_수정_요청_With_File(invalidFile());

            // Then
            USER_1.response.statusCode(400);
        }
    }

    @DisplayName("유저삭제")
    @Nested
    class 유저삭제 {

        @Test
        void 유저삭제_성공() {
            // Given
            USER_1.로그인_완료();

            // When
            USER_1.회원탈퇴_요청();

            // Then
            USER_1.response.statusCode(204);
        }
    }
}
